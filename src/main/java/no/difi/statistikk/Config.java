package no.difi.statistikk;

import no.difi.statistics.ingest.client.IngestClient;
import no.difi.statistikk.fetch.DataTransfer;
import no.difi.statistikk.mapper.IdportenLoginMapper;
import no.difi.statistikk.schedule.Scheduler;
import no.difi.statistikk.service.IdportenLoginFetch;
import no.difi.statistikk.service.LastDatapoint;
import no.difi.statistikk.service.ServiceProviderFetch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;


@EnableAutoConfiguration
@EnableScheduling
@Configuration
public class Config {

    private static final int readTimeout = 15000;
    private static final int connTimeout = 60000;
    private static String ingestPassword;
    private URL idportenAdminUrl;
    private URL statisticsIngestUrl;

    private final String reportOwner = "991825827";

    @Autowired
    public Config(Environment environment) throws IOException {
        idportenAdminUrl = environment.getRequiredProperty("url.base.admin", URL.class);
        statisticsIngestUrl = environment.getRequiredProperty("url.base.ingest.statistikk", URL.class);
        ingestPassword = new String(Files.readAllBytes(Paths.get(environment.getRequiredProperty("file.base.difi-statistikk"))));
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder().build();
    }

    @Bean
    public ServiceProviderFetch spFetch() {
        return new ServiceProviderFetch(idportenAdminUrl, restTemplate());
    }

    @Bean
    public IdportenLoginFetch idportenLoginFetch() {
        return new IdportenLoginFetch(idportenAdminUrl, restTemplate());
    }

    @Bean
    public Scheduler scheduler() {
        return new Scheduler(dataTransfer(), lastDatapoint());
    }

    @Bean
    public DataTransfer dataTransfer() {
        return new DataTransfer(idportenLoginFetch(), idportenLoginMapper(), ingestClient());
    }

    @Bean
    public LastDatapoint lastDatapoint() {
        return new LastDatapoint(ingestClient());
    }

    @Bean
    public IngestClient ingestClient() {
        return new IngestClient(statisticsIngestUrl, readTimeout, connTimeout, reportOwner, reportOwner, ingestPassword);
    }

    @Bean
    public IdportenLoginMapper idportenLoginMapper() {
        return new IdportenLoginMapper(spFetch());
    }

}
