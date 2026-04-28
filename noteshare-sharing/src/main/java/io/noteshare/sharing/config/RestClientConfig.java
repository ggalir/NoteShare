package io.noteshare.sharing.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient notesRestClient(@Value("${notes.service.url}") String notesServiceUrl) {
        return RestClient.builder().baseUrl(notesServiceUrl).build();
    }
}
