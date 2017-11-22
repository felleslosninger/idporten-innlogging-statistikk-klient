package no.difi.statistikk;

import no.difi.statistikk.service.ServiceProviderFetch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URL;

@Configuration
public class Config {

    private URL kontaktregisterUrl;

    @Autowired
    public Config(Environment environment) throws IOException {
        kontaktregisterUrl = environment.getRequiredProperty("url.base.admin", URL.class);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder().build();
    }

    @Bean
    public ServiceProviderFetch spFetch() {
        return new ServiceProviderFetch(kontaktregisterUrl, restTemplate());
    }
}
