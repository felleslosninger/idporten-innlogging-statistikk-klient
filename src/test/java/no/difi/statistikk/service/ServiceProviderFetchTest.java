package no.difi.statistikk.service;

import no.difi.statistikk.Properties;
import no.difi.statistikk.domain.ServiceProvider;
import no.difi.statistikk.testutils.MockitoExtension;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ServiceProviderFetchTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Properties properties;

    @InjectMocks
    private ServiceProviderFetch serviceProviderFetch;

    @Before
    public void setup() throws MalformedURLException {
        when(properties.getIdportenAdminUrl()).thenReturn(new URL("http://localhost/"));
    }

    @Test
    public void shouldReturnEmptyListWhenResponseIsEmpty() {
        ServiceProvider[] spList = {};
        when(restTemplate.getForObject(any(URI.class), eq(ServiceProvider[].class))).thenReturn(spList);
        List actualServiceproviders = serviceProviderFetch.perform();
        assertEquals(actualServiceproviders, Arrays.asList(spList));
    }

    @Test
    public void shouldReturnTwoServiceProvidersWhenResponseContainsTwo() {
        ServiceProvider sp1 = new ServiceProvider("entity1", "1");
        ServiceProvider sp2 = new ServiceProvider("entity2", "2");
        ServiceProvider[] spList = {sp1, sp2};

        when(restTemplate.getForObject(any(URI.class), eq(ServiceProvider[].class))).thenReturn(spList);
        List actualServiceproviders = serviceProviderFetch.perform();
        assertEquals(actualServiceproviders, Arrays.asList(spList));
    }

    @Test
    public void shouldHandleThatResultIsNull() {
        ServiceProvider[] spList = {};
        when(restTemplate.getForObject(any(URI.class), eq(ServiceProvider[].class))).thenReturn(null);
        List actualServiceproviders = serviceProviderFetch.perform();
        assertEquals(actualServiceproviders, Arrays.asList(spList));
    }
}