package no.difi.statistikk.mapper;

import no.difi.statistikk.domain.IdportenLoginValue;
import no.difi.statistikk.domain.ServiceProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryMapper {

    private final List<ServiceProvider> serviceProviders;
    private final List<IdportenLoginValue> idpv;

    public CategoryMapper(List<IdportenLoginValue> idpv, List<ServiceProvider> serviceProviders) {
        this.idpv = idpv;
        this.serviceProviders = serviceProviders;
    }

    public Map<String, String> mapCategories() {
        Map<String, String> categoriesMap = new HashMap<>();

        for (int valueCol = 0; valueCol < 4; valueCol++) {
            String mappedValue;
            String mappedCategory;

            switch (valueCol) {
                case 0: {
                    mappedValue = idpv.get(valueCol).getValue();
                    mappedCategory = "TL";
                    categoriesMap.put(mappedCategory, mappedValue);
                }
                break;
                case 1: {
                    mappedValue = getOrgnumForServiceProvider(idpv.get(valueCol).getValue());
                    mappedCategory = "TL-orgnum";
                    categoriesMap.put(mappedCategory, mappedValue);
                }
                break;
                case 2: {
                    mappedValue = idpv.get(valueCol).getValue();
                    if (null == mappedValue || mappedValue.isEmpty()){
                        mappedValue = idpv.get(0).getValue();
                    }
                    mappedCategory = "TE";
                    categoriesMap.put(mappedCategory, mappedValue);
                }
                break;
                case 3: {
                    mappedValue = getOrgnumForServiceProvider(idpv.get(valueCol).getValue());
                    if (null == mappedValue || mappedValue.isEmpty()){
                        mappedValue = getOrgnumForServiceProvider(idpv.get(1).getValue());
                    }
                    mappedCategory = "TE-orgnum";
                    categoriesMap.put(mappedCategory, mappedValue);
                }
            }
        }
        return categoriesMap;
    }

    private String getOrgnumForServiceProvider(String EntityId) {
        for (ServiceProvider serviceProvider : serviceProviders) {
            if (serviceProvider.getEntityId().equals(EntityId)) {
                return serviceProvider.getOrganisasjonsnummer();
            }
        }
        return "";
    }

}
