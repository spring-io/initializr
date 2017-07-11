package io.spring.initializr

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
class InitializrAppTest {

    //    This is an integration test
    @Test
    void contextLoads() {
    }

}