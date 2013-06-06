@Grab("org.springframework.bootstrap:spring-bootstrap-actuator:0.5.0.BUILD-SNAPSHOT")
@Grab("org.codehaus.groovy:groovy-ant:2.1.3")
@Grab("org.codehaus.groovy.modules.http-builder:http-builder:0.5.2")
import groovyx.net.http.*

@Controller
@Log
class MainController {

  @Value('${info.home:http://localhost:8080/}')
  private String home

  @Value('${TMPDIR:.}')
  private String tmpdir

  private gettingStartedRepos = []

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
  ResponseEntity<byte[]> pom(PomRequest request) {

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

  @RequestMapping("/gs")
  @ResponseBody
  String gettingStartedList(@RequestHeader("Authorization") auth) { 
    if (gettingStartedRepos.empty) {
      RESTClient github = new RESTClient("https://api.github.com")
      if (auth) { 
        github.headers['Authorization'] = auth
      }
      github.headers['User-Agent'] = 'Mozilla/4.0'
      def names = github.get( path : "orgs/springframework-meta/repos").data.collect { it.name }
      names = names.findAll {  it.startsWith "gs-"}
      gettingStartedRepos = names.collect { [repo:it, name:it.split("-").findAll{it!="gs"}.collect{it.capitalize()}.join(" ")]}
    }
    template "gs.html", [repos:gettingStartedRepos]
  }

  @RequestMapping("/gs/{repo}")
  @ResponseBody
  ResponseEntity<byte[]> gettingStartedProject(java.security.Principal principal, @RequestHeader("Authorization") auth, @PathVariable String repo) {
    RESTClient github = new RESTClient("https://api.github.com")
    if (auth) { 
      github.headers['Authorization'] = auth
    }
    github.headers['User-Agent'] = 'Mozilla/4.0'
    def body = github.get( path : "repos/springframework-meta/${repo}/zipball/master").data.bytes
    log.info("Downloaded: " + body.length + " bytes of ${repo} for ${principal.name}")
    new ResponseEntity<byte[]>(body, ["Content-Type":"application/zip"] as HttpHeaders, HttpStatus.OK)
  }

}

@Grab("org.springframework.security:spring-security-javaconfig:1.0.0.CI-SNAPSHOT")
import org.springframework.security.config.annotation.web.*
import org.springframework.security.authentication.*
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.bootstrap.actuate.properties.SecurityProperties
@Configuration
@Log
class SecurityConfiguration {

  @Bean(name = "org.springframework.bootstrap.actuate.properties.SecurityProperties")
  SecurityProperties securityProperties() {
    SecurityProperties security = new SecurityProperties()
    security.getBasic().setPath("/gs/**")
    security.getBasic().setRealm("Github Credentials")
    security
  }

  @Bean
  AuthenticationManager authenticationManager() { 
    new AuthenticationManager() {
      Authentication authenticate(Authentication authentication) {
        log.info("Authenticating: " + authentication.name)
        new UsernamePasswordAuthenticationToken(authentication.name, "<N/A>", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"))
      }
    }
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