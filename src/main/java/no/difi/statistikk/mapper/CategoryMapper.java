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

        String tjenesteleverandør = idpv.get(0).getValue();
        String tjenesteleverandørId = idpv.get(1).getValue();
        String tjenesteeier = idpv.get(2).getValue();
        String tjenesteeierId = idpv.get(3).getValue();

        categoriesMap.put("TL", tjenesteleverandør);
        categoriesMap.put("TL-orgnum", getOrgnumForServiceProvider(tjenesteleverandørId));
        categoriesMap.put("TL-entityId", tjenesteleverandørId);
        categoriesMap.put("TE", withFallback(tjenesteeier, tjenesteleverandør));
        categoriesMap.put("TE-orgnum", getOrgnumForServiceProvider(withFallback(tjenesteeierId, tjenesteleverandørId)));
        categoriesMap.put("TE-entityId", withFallback(tjenesteeierId, tjenesteleverandørId));
        return categoriesMap;
    }

    private String withFallback(String value, String fallback) {
        return !value.isEmpty() ? value : fallback;
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
