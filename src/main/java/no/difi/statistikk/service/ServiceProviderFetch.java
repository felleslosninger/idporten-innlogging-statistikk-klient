package no.difi.statistikk.service;

import no.difi.statistikk.Properties;
import no.difi.statistikk.domain.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static java.lang.System.currentTimeMillis;

@Service
public class ServiceProviderFetch {
    private final UriTemplate uriTemplate;
    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Properties properties;



    @Autowired
    public ServiceProviderFetch(Properties properties, RestTemplate restTemplate) {
        this.properties = properties;
        this.uriTemplate = new UriTemplate(properties.getIdportenAdminUrl() +"/idporten-admin/sp/idporten/rest/splist/");
        this.restTemplate = restTemplate;
    }

    public List<ServiceProvider> perform() {
        URI uri = uriTemplate.expand();
        logger.debug("Fetching serviceproviders from " + uri + "...");
        long t0 = currentTimeMillis();
        ServiceProvider[] result =  restTemplate.getForObject(uri, ServiceProvider[].class);
        logger.debug((result != null ? result.length : 0) + " serviceproviders fetched in " + ((currentTimeMillis() - t0) ) + " ms");
        return Arrays.asList(result != null ? result : new ServiceProvider[0]);
    }
}
