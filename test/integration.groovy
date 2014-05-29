package test

@SpringApplicationConfiguration(classes=TestConfiguration)
@WebAppConfiguration
@IntegrationTest('server.port:0')
@DirtiesContext
class IntegrationTests {

  @Value('${local.server.port}')
  int port
  
  @Test
  void homeIsForm() {
    String body = new TestRestTemplate().getForObject('http://localhost:' + port, String)
    assertTrue('Wrong body:\n' + body, body.contains('action="/starter.zip"'))
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
    String body = new TestRestTemplate().getForObject('http://localhost:' + port, String)
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