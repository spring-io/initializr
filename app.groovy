@Grab("org.springframework.bootstrap:spring-bootstrap-actuator:0.5.0.BUILD-SNAPSHOT")
@Grab("org.codehaus.groovy:groovy-ant:2.1.3")

@Controller
@Log
class MainController {

  @Value('${info.home:http://localhost:8080/}')
  private String home

  @Value('${TMPDIR:.}')
  private String tmpdir

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

  @RequestMapping("/installer")
  @ResponseBody
  String installer(@RequestHeader(required=false) String host) {
    template "installer.sh", [host: host!=null ? host : home]
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

  @RequestMapping("/pom")
  @ResponseBody
  ResponseEntity<String> pom(PomRequest request) {

    def style = request.style
    log.info("Styles requested: " + style)

    def model = [:]
    model.groupId = request.groupId
    model.artifactId = request.artifactId
    model.version = request.version
    model.name = request.name
    model.description = request.description
    model.packageName = request.packageName

    if (style==null || style.size()==0) { 
      style = [""]
    }
    model["styles"] = style.collect{ it=="" ? "" : it + "-" }

    log.info("Model: " + model)

    def body = template "starter-pom.xml", model
    new ResponseEntity<byte[]>(body, ["Content-Type":"application/octet-stream"] as HttpHeaders, HttpStatus.OK)
  }

}

class PomRequest { 
  def style = []

  String name = "demo"
  String description = "Demo project for Spring Bootstrap"
  String groupId = "org.test"
  String artifactId
  String version = "0.0.1.SNAPSHOT"
  String packageName
  String getName() {
    artifactId == null ? name : artifactId
  }
  String getPackageName() {
    packageName == null ? name : packageName
  }
}