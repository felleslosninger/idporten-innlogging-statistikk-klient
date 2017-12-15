package no.difi.statistikk.Domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.difi.statistikk.domain.ServiceProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ServiceProviderTest {

    @Test()
    public void serializeTest() throws IOException {
        ServiceProvider serviceProviderExpected = new ServiceProvider("entity.no", "1234567");

        String json = "{\"EntityId\":\"entity.no\",\"Organisasjonsnummer\":\"1234567\"}";
        ObjectMapper mapper = new ObjectMapper();
        ServiceProvider serviceProviderActual = mapper.reader().forType(ServiceProvider.class).readValue(json);

        Assertions.assertEquals(serviceProviderExpected.getEntityId(), serviceProviderActual.getEntityId());
        Assertions.assertEquals(serviceProviderExpected.getOrganisasjonsnummer(), serviceProviderActual.getOrganisasjonsnummer());
    }
}