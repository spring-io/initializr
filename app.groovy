package app

@Grab("spring-boot-starter-actuator")
@Grab("org.codehaus.groovy:groovy-ant:2.3.2")

@Configuration
@EnableConfigurationProperties(Projects)
@Controller
@Log
class MainController {

  @Value('${info.home:http://localhost:8080/}')
  private String home

  @Value('${info.spring-boot.version:1.0.1.RELEASE}')
  private String bootVersion

  @Value('${TMPDIR:.}')
  private String tmpdir

  @Autowired
  private Reactor reactor

  @Autowired
  private Projects projects

  @RequestMapping("/")
  @ResponseBody
  String home() {
    def model = [:]
    // sort lists
    model["styles"] = projects.styles.sort { it.name }
    model["types"] = projects.types.sort { it.name }
    template "home.html", model
  }

  @RequestMapping("/spring")
  @ResponseBody
  ResponseEntity<byte[]> spring() {
    File download = new File(tmpdir, "spring.zip")
    if (!download.exists()) {
      log.info("Creating: "  + download)
      new AntBuilder().zip(destfile: download) { 
        zipfileset(dir:".", includes:"spring/bin/**", filemode:"775")
        zipfileset(dir:".", includes:"spring/**", excludes:"spring/bin/**")
      }
    }
    log.info("Downloading: "  + download)
    new ResponseEntity<byte[]>(download.bytes, ["Content-Type":"application/zip"] as HttpHeaders, HttpStatus.OK)
  }

  @RequestMapping(value="/starter.tgz", produces="application/x-compress")
  @ResponseBody
  ResponseEntity<byte[]> springTgz(PomRequest request) {

    File dir = File.createTempFile("tmp","",new File(tmpdir));
    def tempFiles = getProjectFiles(dir, request)

    File download = new File(tmpdir, dir.name + ".tgz")
    log.info("Creating: "  + download)
    tempFiles << download
    
    new AntBuilder().tar(destfile: download, compression: "gzip") { 
      zipfileset(dir:dir, includes:"**")
    }
    log.info("Downloading: "  + download)
    def result = new ResponseEntity<byte[]>(download.bytes, ["Content-Type":"application/x-compress"] as HttpHeaders, HttpStatus.OK)

    reactor.notify("tempfiles", Event.wrap(tempFiles))

    result
  }

  @RequestMapping("/starter.zip")
  @ResponseBody
  ResponseEntity<byte[]> springZip(PomRequest request) {

    File dir = File.createTempFile("tmp","",new File(tmpdir));
    def tempFiles = getProjectFiles(dir, request)

    File download = new File(tmpdir, dir.name + ".zip")
    log.info("Creating: "  + download)
    tempFiles << download
    
    new AntBuilder().zip(destfile: download) { 
      zipfileset(dir:dir, includes:"**")
    }
    log.info("Downloading: "  + download)
    def result = new ResponseEntity<byte[]>(download.bytes, ["Content-Type":"application/zip"] as HttpHeaders, HttpStatus.OK)

    reactor.notify("tempfiles", Event.wrap(tempFiles))

    result
  }

  def getProjectFiles(File dir, PomRequest request) { 

    def tempFiles = []

    def model = [:]
    tempFiles << dir
    dir.delete()
    dir.mkdirs()

    String pom = new String(pom(request, model).body)
    new File(dir, "pom.xml").write(pom)

    String gradle = new String(gradle(request, model).body)
    new File(dir, "build.gradle").write(gradle)

    File src = new File(new File(dir, "src/main/java"),request.packageName.replace(".", "/"))
    src.mkdirs()
    write(src, "Application.java", model)
    
    File test = new File(new File(dir, "src/test/java"),request.packageName.replace(".", "/"))
    test.mkdirs()
    if (model.styles.contains("-web")) { 
      model.testAnnotations = "@WebAppConfiguration\n"
      model.testImports = "import org.springframework.test.context.web.WebAppConfiguration;\n"
    } else { 
      model.testAnnotations = ""
      model.testImports = ""
    }
    write(test, "ApplicationTests.java", model)

    File resources = new File(dir, "src/main/resources")
    resources.mkdirs()
    new File(resources, "application.properties").write("")

    tempFiles

  }

  def write(File src, String name, def model) { 
    log.info("Creating: "  + src + "/" + name)
    def body = template name, model
    new File(src, name).write(body)
  }

  @RequestMapping("/pom")
  @ResponseBody
  ResponseEntity<byte[]> pom(PomRequest request, Map model) {
    model.bootVersion = bootVersion
    new ResponseEntity<byte[]>(render("starter-pom.xml", request, model), ["Content-Type":"application/octet-stream"] as HttpHeaders, HttpStatus.OK)

  }

  @RequestMapping("/build")
  @ResponseBody
  ResponseEntity<byte[]> gradle(PomRequest request, Map model) {
    model.bootVersion = bootVersion
    new ResponseEntity<byte[]>(render("starter-build.gradle", request, model), ["Content-Type":"application/octet-stream"] as HttpHeaders, HttpStatus.OK)
  }
   
  byte[] render(String path, PomRequest request, Map model) {

    def style = request.style
    log.info("Styles requested: " + style)

    def type = request.type
    log.info("Type requested: " + type)

    model.groupId = request.groupId
    model.artifactId = request.artifactId
    model.version = request.version
    model.name = request.name
    model.description = request.description
    model.packageName = request.packageName

    if (style==null || style.size()==0) { 
      style = [""]
    }
    if (!style.class.isArray() && !(style instanceof Collection)) {
      style = [style]
    }
    style = style.collect{ it=="jpa" ? "data-jpa" : it }
    model["styles"] = style.collect{ it=="" ? "" : "-" + it }

    log.info("Model: " + model)

    def body = template path, model
    body
  }

}

@Configuration
@EnableReactor
class ReactorConfiguration {

	@Bean
	public Reactor rootReactor(reactor.core.Environment reactorEnvironment) {
		return reactorEnvironment.getRootReactor();
	}

}

@Consumer
@Log
class TemporaryFileCleaner {

  @Autowired
  Reactor reactor

  @Selector('tempfiles')
  void clean(def tempFiles) { 
    log.info "Tempfiles: " + tempFiles
    if (tempFiles) { 
      tempFiles.each {
        File file = it as File
        if (file.directory) { 
          file.deleteDir()
        } else {
          file.delete()
        }
      }
    }
  }

}

class PomRequest { 
  def style = []

  String name = "demo"
  String type = "starter"
  String description = "Demo project for Spring Boot"
  String groupId = "org.test"
  String artifactId
  String version = "0.0.1-SNAPSHOT"
  String packageName
  String getArtifactId() {
    artifactId == null ? name : artifactId
  }
  String getPackageName() {
    packageName == null ? name.replace('-', '.') : packageName
  }
}

@ConfigurationProperties(prefix='projects', ignoreUnknownFields=false)
class Projects {
  List<Map<String,Object>> styles
  List<Map<String,Object>> types  
}