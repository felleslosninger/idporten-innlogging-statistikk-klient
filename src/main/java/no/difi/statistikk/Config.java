package no.difi.statistikk;

import no.difi.statistics.ingest.client.IngestClient;
import no.difi.statistikk.config.KeySecretProvider;
import no.difi.statistikk.config.MaskinportenProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


@Configuration
public class Config {


    private final Properties properties;
    private final MaskinportenProperties mpProperties;

    @Autowired
    public Config(Properties properties, MaskinportenProperties maskinportenProperties) {
        this.properties = properties;
        this.mpProperties = maskinportenProperties;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder().build();
    }


    @Bean
    public KeySecretProvider keySecretProvider() {
        final MaskinportenProperties.KeyProperties keyProperties = mpProperties.getClientKeys();
        return KeySecretProvider.from(keyProperties);
    }


    @Bean
    public IngestClient ingestClient() {
        return new IngestClient(properties.getStatisticsIngestUrl(), properties.getReadTimeout(), properties.getConnTimeout(), properties.getReportOwner());
    }


}
