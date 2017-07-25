package io.spring.initializr;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.Session;
import io.spring.initializr.vcs.config.VcsServiceAutoConfiguration;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.Executor;

/**
 */
@SpringBootApplication
@ComponentScan({"io.spring.initializr.controller"})
@EnableScheduling
@Import(VcsServiceAutoConfiguration.class)
public class InitializrApp {

    static {
        SshSessionFactory.setInstance(new JschConfigSessionFactory() {
            protected void configure(OpenSshConfig.Host hc, Session session) {
                session.setConfig("StrictHostKeyChecking", "no");
            }
        });
    }

    public static void main(String[] args) {
        SpringApplication.run(InitializrApp.class, args);
    }

    @Value("${github.username}")
    String githubUsername;

    @Value("${github.password}")
    String githubPassword;

    @Bean
    CredentialsProvider githubCredentialsProvider() {
        return new UsernamePasswordCredentialsProvider(githubPassword,"");
    }

    @Bean
    RestTemplate githubRestTemplate(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder
                .setConnectTimeout(10000)
                .setReadTimeout(10000)
                .basicAuthorization(githubUsername, githubPassword)
                .build();

        MappingJackson2HttpMessageConverter c = new MappingJackson2HttpMessageConverter();
        c.setObjectMapper(new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false));
        restTemplate.setMessageConverters(Collections.singletonList(c));

        return restTemplate;
    }

    @Configuration
    @EnableAsync
    static class AsyncConfiguration extends AsyncConfigurerSupport {
        @Override
        public Executor getAsyncExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(1);
            executor.setMaxPoolSize(5);
            executor.setThreadNamePrefix("initializr-");
            executor.initialize();
            return executor;
        }
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.US);
        return slr;
    }

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages/messages");
        messageSource.setCacheSeconds(3600); //refresh cache once per hour
        return messageSource;
    }
}
