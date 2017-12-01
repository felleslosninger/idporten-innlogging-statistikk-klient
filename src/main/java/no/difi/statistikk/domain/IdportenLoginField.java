package no.difi.statistikk.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IdportenLoginField {
    @JsonProperty("fields")
    private List<IdportenLoginValue> values;

    public IdportenLoginField(IdportenLoginFieldBuilder idportenLoginFieldBuilder) {
        this.values = idportenLoginFieldBuilder.getValues();
    }

    public List<IdportenLoginValue> getValues() {
        if (values == null) {
            Collections.emptyList();
        }
        return values;
    }
}
