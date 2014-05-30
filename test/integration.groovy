package test

@SpringApplicationConfiguration(classes=TestConfiguration)
@WebAppConfiguration
@IntegrationTest('server.port:0')
@DirtiesContext
class IntegrationTests {

  @Value('${local.server.port}')
  int port

  private String home() {
    HttpHeaders headers = new HttpHeaders()
    headers.setAccept([MediaType.TEXT_HTML])
    new TestRestTemplate().exchange('http://localhost:' + port, HttpMethod.GET, new HttpEntity<Void>(headers), String).body
  }
  
  @Test
  void homeIsForm() {
    String body = home()
    assertTrue('Wrong body:\n' + body, body.contains('action="/starter.zip"'))
  }
  
  @Test
  void homeIsJson() {
    String body = new TestRestTemplate().getForObject('http://localhost:' + port, String)
    assertTrue('Wrong body:\n' + body, body.contains('{"styles"'))
  }
  
  @Test
  void webIsAdded() {
    String body = new TestRestTemplate().getForObject('http://localhost:' + port + '/pom.xml?packaging=war', String)
    assertTrue('Wrong body:\n' + body, body.contains('spring-boot-starter-web'))
  }
  
  @Test
  void infoHasExternalProperties() {
    String body = new TestRestTemplate().getForObject('http://localhost:' + port + '/info', String)
    assertTrue('Wrong body:\n' + body, body.contains('"project"'))
  }
  
  @Test
  void homeHasWebStyle() {
    String body = home()
    assertTrue('Wrong body:\n' + body, body.contains('name="style" value="web"'))
  }

  @Test
  void downloadStarter() {
    byte[] body = new TestRestTemplate().getForObject('http://localhost:' + port + 'starter.zip', byte[])
    assertNotNull(body)
    assertTrue(body.length>100)
  }
  
}

// CLI compiled classes are not @ComponentScannable so we have to create
// an explicit configuration for the test
@Configuration
@Import([app.MainController, app.Projects, app.TemporaryFileCleaner])
class TestConfiguration { 
}