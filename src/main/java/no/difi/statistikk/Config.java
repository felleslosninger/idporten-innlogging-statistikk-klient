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


@EnableAutoConfiguration
@EnableScheduling
@Configuration
public class Config {


    @Autowired
    private Environment environment;

    @Bean
    public Properties properties() {
        return new Properties(environment);
    }


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder().build();
    }

    @Bean
    public ServiceProviderFetch spFetch() {
        return new ServiceProviderFetch(properties().getIdportenAdminUrl(), restTemplate());
    }

    @Bean
    public IdportenLoginFetch idportenLoginFetch() {
        return new IdportenLoginFetch(properties().getIdportenAdminUrl(), restTemplate());
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
        return new LastDatapoint(ingestClient(), properties());
    }

    @Bean
    public IngestClient ingestClient() {
        return new IngestClient(properties().getStatisticsIngestUrl(), properties().getReadTimeout() , properties().getConnTimeout(), properties().getReportOwner(), properties().getReportOwner(), properties().getIngestPassword());
    }

    @Bean
    public IdportenLoginMapper idportenLoginMapper() {
        return new IdportenLoginMapper(spFetch());
    }

}
