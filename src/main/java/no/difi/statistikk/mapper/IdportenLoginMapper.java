package no.difi.statistikk.mapper;

import no.difi.statistics.ingest.client.model.Measurement;
import no.difi.statistics.ingest.client.model.TimeSeriesPoint;
import no.difi.statistikk.domain.*;
import no.difi.statistikk.service.ServiceProviderFetch;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IdportenLoginMapper {

    private final ServiceProviderFetch serviceProviderFetch;
    private List<String> measurementsIds = new ArrayList<>();

    public IdportenLoginMapper(ServiceProviderFetch serviceProviderFetch) {
        this.serviceProviderFetch = serviceProviderFetch;
        measurementsIds.add("MinID");
        measurementsIds.add("MinID OTC");
        measurementsIds.add("MinID PIN");
        measurementsIds.add("BuyPass");
        measurementsIds.add("Commfides");
        measurementsIds.add("Federated");
        measurementsIds.add("BankID");
        measurementsIds.add("eIDAS");
        measurementsIds.add("BankID mobil");
        measurementsIds.add("Antall");
    }

    public List<TimeSeriesPoint> mapMeasurements(List<IdportenLoginField> fields, ZonedDateTime dateTime) {
        List<ServiceProvider> serviceProviders = serviceProviderFetch.perform();
        return fields.stream()
                .map(field -> TimeSeriesPoint.builder()
                        .timestamp(dateTime)
                        .categories(mapCategoriesForRow(field, serviceProviders))
                        .measurements(mapMeasurementsForRow(field))
                        .build())
                .collect(Collectors.toList());
    }

    private Map<String, String> mapCategoriesForRow(IdportenLoginField field, List<ServiceProvider> serviceProviders) {
        List<IdportenLoginValue> idpv = field.getValues();

        CategoryMapper cv = new CategoryMapper(idpv, serviceProviders);
        return cv.mapCategories();
    }

    private List<Measurement> mapMeasurementsForRow(IdportenLoginField field) {
        List<Measurement> measurements = new ArrayList<>();
        List<IdportenLoginValue> idpv = field.getValues();

        for (int valueIndex = 4; valueIndex < idpv.size(); valueIndex++) {
            String id = measurementsIds.get(valueIndex - 4);
            Long value = idpv.get(valueIndex).getValueAsLong();
            Measurement measurement = new Measurement(id, value);
            measurements.add(measurement);
        }
        return measurements;
    }

}
