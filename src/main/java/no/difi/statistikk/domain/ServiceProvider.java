package no.difi.statistikk.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceProvider {

    @JsonProperty("EntityId")
    private String entityId;

    @JsonProperty("Organisasjonsnummer")
    private String organisasjonsnummer;

    public ServiceProvider() {}

    public ServiceProvider(String entityId, String organisasjonsnummer){
        this.entityId = entityId;
        this.organisasjonsnummer = organisasjonsnummer;

    }

    public String getEntityId() {
        return entityId;
    }

    public String getOrganisasjonsnummer() {
        return organisasjonsnummer;
    }
}
