package test

@SpringApplicationConfiguration(classes=app.MainController)
@WebAppConfiguration
@IntegrationTest('server.port:0')
@DirtiesContext
class IntegrationTests {

  @Value('${local.server.port}')
  int port
  
  @Test
  void homeIsZipForm() {
    String body = new TestRestTemplate().getForObject('http://localhost:' + port, String)
    assertTrue('Wrong body:\n' + body, body.contains('action="/starter.zip"'))
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

}