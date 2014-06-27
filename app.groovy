package app

@Grab('spring-boot-starter-actuator')
@Grab('org.codehaus.groovy:groovy-ant:2.3.2')

@Controller
@Log
class MainController {

  @Value('${info.home:http://localhost:8080/}')
  private String home

  @Value('${info.spring-boot.version}')
  private String bootVersion

  @Value('${TMPDIR:.}')
  private String tmpdir

  @Autowired
  private Reactor reactor

  @Autowired
  private Projects projects

  @ModelAttribute
  PomRequest pomRequest() {
    PomRequest request = new PomRequest()
    request.bootVersion = bootVersion
    request
  }

  @RequestMapping(value='/')
  @ResponseBody
  Projects projects() {
    projects
  }

  @RequestMapping(value='/', produces='text/html')
  @ResponseBody
  String home() {
    def model = [:]
    projects.properties.each { model[it.key] = it.value }
    template 'home.html', model
  }

  @RequestMapping('/spring')
  @ResponseBody
  ResponseEntity<byte[]> spring() {
    File download = new File(tmpdir, 'spring.zip')
    if (!download.exists()) {
      log.info('Creating: '  + download)
      new AntBuilder().zip(destfile: download) { 
        zipfileset(dir:'.', includes:'spring/bin/**', filemode:'775')
        zipfileset(dir:'.', includes:'spring/**', excludes:'spring/bin/**')
      }
    }
    log.info('Uploading: '  + download)
    new ResponseEntity<byte[]>(download.bytes, ['Content-Type':'application/zip'] as HttpHeaders, HttpStatus.OK)
  }

  @RequestMapping(value='/starter.tgz', produces='application/x-compress')
  @ResponseBody
  ResponseEntity<byte[]> springTgz(PomRequest request) {

    File dir = getProjectFiles(request)

    File download = new File(tmpdir, dir.name + '.tgz')
    addTempFile(dir.name, download)
    
    new AntBuilder().tar(destfile: download, compression: 'gzip') { 
      zipfileset(dir:dir, includes:'**')
    }
    log.info("Uploading: ${download} (${download.bytes.length} bytes)")
    def result = new ResponseEntity<byte[]>(download.bytes, ['Content-Type':'application/x-compress'] as HttpHeaders, HttpStatus.OK)

    cleanTempFiles(dir.name)

    result
  }

  @RequestMapping('/starter.zip')
  @ResponseBody
  ResponseEntity<byte[]> springZip(PomRequest request) {

    def dir = getProjectFiles(request)

    File download = new File(tmpdir, dir.name + '.zip')
    addTempFile(dir.name, download)
    
    new AntBuilder().zip(destfile: download) { 
      zipfileset(dir:dir, includes:'**')
    }
    log.info("Uploading: ${download} (${download.bytes.length} bytes)")
    def result = new ResponseEntity<byte[]>(download.bytes, ['Content-Type':'application/zip'] as HttpHeaders, HttpStatus.OK)

    cleanTempFiles(dir.name)

    result
  }

  private void addTempFile(String group, File file) {
    reactor.notify('/temp/' + group, Event.wrap(file))
  }

  private void cleanTempFiles(String group) {
    reactor.notify('/clean/' + group)
  }

  def getProjectFiles(PomRequest request) {

    def model = [:]

    File dir = File.createTempFile('tmp','',new File(tmpdir));
    addTempFile(dir.name, dir)
    dir.delete()
    dir.mkdirs()

    if (request.type.contains('gradle')) {
      String gradle = new String(gradle(request, model).body)
      new File(dir, 'build.gradle').write(gradle)
    } else { 
      String pom = new String(pom(request, model).body)
      new File(dir, 'pom.xml').write(pom)
    }

    String language = request.language

    File src = new File(new File(dir, 'src/main/' + language),request.packageName.replace('.', '/'))
    src.mkdirs()
    write(src, 'Application.' + language, model)

    if (request.packaging=='war') {
      write(src, 'ServletInitializer.' + language, model)
    }
    
    File test = new File(new File(dir, 'src/test/' + language),request.packageName.replace('.', '/'))
    test.mkdirs()
    if (model.styles.contains('-web')) { 
      model.testAnnotations = '@WebAppConfiguration\n'
      model.testImports = 'import org.springframework.test.context.web.WebAppConfiguration;\n'
    } else { 
      model.testAnnotations = ''
      model.testImports = ''
    }
    write(test, 'ApplicationTests.' + language, model)

    File resources = new File(dir, 'src/main/resources')
    resources.mkdirs()
    new File(resources, 'application.properties').write('')

    if (request.isWebStyle()) {
      new File(dir, 'src/main/resources/templates').mkdirs()
      new File(dir, 'src/main/resources/static').mkdirs()
    }

    dir

  }

  def write(File src, String name, def model) {
    String tmpl = name.endsWith('.groovy') ? name + '.tmpl' : name
    def body = template tmpl, model
    new File(src, name).write(body)
  }

  @RequestMapping('/pom')
  @ResponseBody
  ResponseEntity<byte[]> pom(PomRequest request, Map model) {
    model.bootVersion = request.bootVersion
    new ResponseEntity<byte[]>(render('starter-pom.xml', request, model), ['Content-Type':'application/octet-stream'] as HttpHeaders, HttpStatus.OK)

  }

  @RequestMapping('/build')
  @ResponseBody
  ResponseEntity<byte[]> gradle(PomRequest request, Map model) {
    model.bootVersion = request.bootVersion
    new ResponseEntity<byte[]>(render('starter-build.gradle', request, model), ['Content-Type':'application/octet-stream'] as HttpHeaders, HttpStatus.OK)
  }
   
  byte[] render(String path, PomRequest request, Map model) {
    if (request.packaging=='war' && !request.isWebStyle()) { 
      request.style << 'web'
    }
    log.info("Styles requested: ${request.style}, Type requested: ${request.type}")
    request.properties.each { model[it.key] = it.value }
    model.styles = fixStyles(request.style)
    template path, model
  }

  private def fixStyles(def style) {
    if (style==null || style.size()==0) { 
      style = ['']
    }
    if (!style.class.isArray() && !(style instanceof Collection)) {
      style = [style]
    }
    style = style.collect{ it=='jpa' ? 'data-jpa' : it }
    style.collect{ it=='' ? '' : '-' + it }
  }

}

@EnableReactor
@Consumer
@Log
class TemporaryFileCleaner {

  @Autowired
  Reactor reactor

  private Map files = [:].withDefault { [] }

  @Selector(value='/temp/{stem}', type=SelectorType.URI)
  void add(Event<File> file) {
    String stem = file.headers.stem
    files[stem] << file.data
  }

  @Selector(value='/clean/{stem}', type=SelectorType.URI)
  void clean(Event<File> event) {
    String stem = event.headers.stem
    def tempFiles = files.remove(stem)
    log.fine 'Tempfiles: ' + tempFiles
    tempFiles.each { File file ->
      if (file.directory) { 
        file.deleteDir()
      } else {
        file.delete()
      }
    }
  }

}

class PomRequest { 
  def style = []

  String name = 'demo'
  String type = 'starter'
  String description = 'Demo project for Spring Boot'
  String groupId = 'org.test'
  String artifactId
  String version = '0.0.1-SNAPSHOT'
  String bootVersion
  String packaging = 'jar'
  String language = 'java'
  String packageName
  String javaVersion = '1.7'
  String getArtifactId() {
    artifactId == null ? name : artifactId
  }
  String getPackageName() {
    packageName == null ? name.replace('-', '.') : packageName
  }
  boolean isWebStyle() {
    style.any { webStyle(it) }
  }
  private boolean webStyle(String style) {
    style.contains('web') || style.contains('thymeleaf') || style.contains('freemarker') || style.contains('velocity') || style.contains('groovy-template')
  }
}

@Component
@ConfigurationProperties(prefix='projects', ignoreUnknownFields=false)
class Projects {
  List<Style> styles
  List<Type> types
  List<Packaging> packagings
  List<JavaVersion> javaVersions
  List<Language> languages
  List<BootVersion> bootVersions
  static class Language { 
    String name
    String value
    boolean selected
  }
  static class JavaVersion { 
    String value
    boolean selected
  }
  static class Packaging { 
    String name
    String value
    boolean selected
  }
  static class Type {
    String name
    String action
    String value
    boolean selected
  }
  static class BootVersion {
    String value
    boolean selected    
  }
  static class Style {
	String name
	List<Map<String,Object>> starters
  }
}