package app

@Grab("org.springframework.boot:spring-boot-starter-actuator:0.5.0.BUILD-SNAPSHOT")
@Grab("org.codehaus.groovy:groovy-ant:2.1.6")
@Grab("org.codehaus.groovy.modules.http-builder:http-builder:0.5.2")
@Grab(group='net.sf.json-lib', module='json-lib', version='2.3', classifier='jdk15')
import groovyx.net.http.*

@Controller
@Log
class MainController {

  @Value('${info.home:http://localhost:8080/}')
  private String home

  @Value('${TMPDIR:.}')
  private String tmpdir

  @Autowired
  private Reactor reactor

  @RequestMapping("/")
  @ResponseBody
  String home() {
    def model = [:]
    model["styles"] = [[name:"Standard", value:""]]
    model["styles"] << [name:"Web", value:"web"]
    model["styles"] << [name:"Actuator", value:"actuator"]
    model["styles"] << [name:"Batch", value:"batch"]
    model["styles"] << [name:"JPA", value:"jpa"]
    model["types"] = [[name:"Maven POM", value:"pom", selected: true], [name:"Maven Project", value:"pomproject", selected: false]]
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

  @RequestMapping("/starter")
  @ResponseBody
  ResponseEntity<byte[]> spring(PomRequest request) {

    def tempFiles = []

    def model = [:]
    String pom = new String(pom(request, model).body)
    File dir = File.createTempFile("tmp","",new File(tmpdir));
    tempFiles << dir
    dir.delete()
    dir.mkdirs()
    new File(dir, "pom.xml").write(pom)

    File src = new File(new File(dir, "src/main/java"),request.packageName.replace(".", "/"))
    src.mkdirs()

    def body = template "Application.java", model
    log.info("Creating: "  + src + "/Application.java")
    new File(src, "Application.java").write(body)
    
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

  @RequestMapping("/pom")
  @ResponseBody
  ResponseEntity<byte[]> pom(PomRequest request, Map model) {

    def style = request.style
    log.info("Styles requested: " + style)

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
    model["styles"] = style.collect{ it=="" ? "" : "-" + it }

    log.info("Model: " + model)

    def body = template "starter-pom.xml", model
    new ResponseEntity<byte[]>(body, ["Content-Type":"application/octet-stream"] as HttpHeaders, HttpStatus.OK)
  }

}

import reactor.spring.context.ConsumerBeanPostProcessor;
@Configuration
@EnableReactor
class ReactorConfiguration {

	@Bean
	public reactor.core.Environment reactorEnvironment() {
		return new reactor.core.Environment(); // TODO: use Spring Environment to configure?
	}

	@Bean
	public Reactor rootReactor() {
		return reactorEnvironment().getRootReactor();
	}

}

@Component
@Log
class TemporaryFileCleaner {

  @Autowired
  Reactor reactor

  @PostConstruct
  void init() { 
		reactor.on(Selectors.$("tempfiles"), [
			accept: { event ->
                       def tempFiles = event.data
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
		] as Consumer)
  }

}

class PomRequest { 
  def style = []

  String name = "demo"
  String description = "Demo project for Spring Boot"
  String groupId = "org.test"
  String artifactId
  String version = "0.0.1.SNAPSHOT"
  String packageName
  String getArtifactId() {
    artifactId == null ? name : artifactId
  }
  String getPackageName() {
    packageName == null ? name.replace('-', '.') : packageName
  }
}
