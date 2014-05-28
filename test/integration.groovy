package test

@Grab("spring-boot-starter-test")

@SpringApplicationConfiguration(classes=app.MainController)
@WebAppConfiguration
@IntegrationTest('server.port:0')
@DirtiesContext
class IntegrationTests {

  @Value('${local.server.port}')
  int port
  
  @Test
  void testHome() {
    String body = new TestRestTemplate().getForObject('http://localhost:' + port, String)
    assertTrue('Wrong body:\n' + body, body.contains('action="/starter.zip"'))
  }
  
}