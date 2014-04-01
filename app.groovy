package app

@Grab("spring-boot-starter-actuator")
@Grab("org.codehaus.groovy:groovy-ant:2.1.6")

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
    model["styles"] = [[name:"Web", value:"web"]]
    model["styles"] << [name:"Thymeleaf", value:"thymeleaf"]
    model["styles"] << [name:"Actuator", value:"actuator"]
    model["styles"] << [name:"Security", value:"security"]
    model["styles"] << [name:"Batch", value:"batch"]
    model["styles"] << [name:"JDBC", value:"jdbc"]
    model["styles"] << [name:"Integration", value:"integration"]
    model["styles"] << [name:"JMS", value:"jms"]
    model["styles"] << [name:"AMQP", value:"amqp"]
    model["styles"] << [name:"AOP", value:"aop"]
    model["styles"] << [name:"JPA", value:"data-jpa"]
    model["styles"] << [name:"MongoDB", value:"data-mongodb"]
    model["styles"] << [name:"Redis", value:"redis"]
    model["styles"] << [name:"Rest Repositories", value:"data-rest"]
    model["styles"] << [name:"Remote Shell", value:"shell-remote"]
    model["styles"] << [name:"Mobile", value:"mobile"]
    model["types"] = [[name:"Maven POM", value:"pom.xml", selected: false], [name:"Maven Project", value:"starter.zip", selected: true], [name:"Gradle Build", value:"build.gradle", selected: false]]

   // sort lists
    model["styles"] = model["styles"].sort { it.name }
    model["types"] = model["types"].sort { it.name }
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

  def write(File src, String name, def model) { 
    log.info("Creating: "  + src + "/" + name)
    def body = template name, model
    new File(src, name).write(body)
  }

  @RequestMapping("/pom")
  @ResponseBody
  ResponseEntity<byte[]> pom(PomRequest request, Map model) {
    new ResponseEntity<byte[]>(render("starter-pom.xml", request, model), ["Content-Type":"application/octet-stream"] as HttpHeaders, HttpStatus.OK)

  }

  @RequestMapping("/build")
  @ResponseBody
  ResponseEntity<byte[]> gradle(PomRequest request, Map model) {
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
import reactor.core.Reactor
import reactor.function.Consumer
import reactor.event.selector.Selectors
import reactor.event.Event
@Grab("reactor-core")
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
