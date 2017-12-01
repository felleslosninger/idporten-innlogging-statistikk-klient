package no.difi.statistikk.service;

import no.difi.statistikk.domain.IdportenLoginField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;
import java.net.URI;
import java.net.URL;
import java.time.ZonedDateTime;


public class IdportenLoginFetch {
    private final UriTemplate uriTemplate;
    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public IdportenLoginFetch(URL baseUrl, RestTemplate restTemplate) {
        this.uriTemplate = new UriTemplate(
                baseUrl + "/idporten-admin/statistics/statistics/json/" +
                "{retportType}/" +
                "{fromYear}/{fromMonth}/{fromDay}/{fromHour}/"
        );
        this.restTemplate = restTemplate;
    }

    public IdportenLoginField[] perform(String reportType, ZonedDateTime from) {
        URI uri = uriTemplate.expand(reportType, from.getYear(), from.getMonthValue(), from.getDayOfMonth(), from.getHour());
        logger.info("Fetching from " + uri + "...");
        IdportenLoginField[] result = restTemplate.getForObject(uri, IdportenLoginField[].class);
        return result;
    }
}