package no.difi.statistikk.domain;

import java.util.List;

public class IdportenLoginFieldBuilder {

    private List<IdportenLoginValue> values;

    public IdportenLoginFieldBuilder(List<IdportenLoginValue> values) {
        this.values = values;
    }

    public IdportenLoginField build() {
        return new IdportenLoginField(this);
    }

    public List<IdportenLoginValue> getValues() {
        return values;
    }

}
