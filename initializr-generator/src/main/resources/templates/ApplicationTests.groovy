package {{packageName}}

{{testImports}}
{{#newTestInfrastructure}}
{{^jupiterAvailable}}
@RunWith(SpringRunner)
{{/jupiterAvailable}}
@SpringBootTest
{{/newTestInfrastructure}}
{{^newTestInfrastructure}}
@RunWith(SpringJUnit4ClassRunner)
@SpringApplicationConfiguration(classes = {{applicationName}})
{{/newTestInfrastructure}}
{{testAnnotations}}class {{applicationName}}Tests {

	@Test
	void contextLoads() {
	}

}
