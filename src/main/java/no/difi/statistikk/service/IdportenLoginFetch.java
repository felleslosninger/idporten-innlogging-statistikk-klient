package no.difi.statistikk.service;

import no.difi.statistikk.Properties;
import no.difi.statistikk.domain.IdportenLoginField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.time.ZonedDateTime;


@Service
public class IdportenLoginFetch {
    private final UriTemplate uriTemplate;
    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String reportType = ("r1");

    @Autowired
    public IdportenLoginFetch(Properties properties, RestTemplate restTemplate) {
        this.uriTemplate = new UriTemplate(
                properties.getIdportenAdminUrl() + "/idporten-admin/statistics/statistics/json/" +
                        "{reportType}/" +
                        "{fromYear}/{fromMonth}/{fromDay}/{fromHour}/"
        );
        this.restTemplate = restTemplate;
    }

    public IdportenLoginField[] perform(ZonedDateTime from) {
        URI uri = uriTemplate.expand(reportType, from.getYear(), from.getMonthValue(), from.getDayOfMonth(), from.getHour());
        logger.info("Fetching from " + uri + "...");
        IdportenLoginField[] result = restTemplate.getForObject(uri, IdportenLoginField[].class);
        return result;
    }
}