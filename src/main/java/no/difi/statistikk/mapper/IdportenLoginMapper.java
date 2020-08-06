package no.difi.statistikk.mapper;

import no.difi.statistics.ingest.client.model.TimeSeriesPoint;
import no.difi.statistikk.domain.IdportenLoginField;
import no.difi.statistikk.domain.IdportenLoginValue;
import no.difi.statistikk.domain.ServiceProvider;
import no.difi.statistikk.service.ServiceProviderFetch;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static no.difi.statistics.ingest.client.model.TimeSeriesPoint.timeSeriesPoint;

public class IdportenLoginMapper {

    private final ServiceProviderFetch serviceProviderFetch;
    protected List<String> measurementsIds = new ArrayList<>();

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
        measurementsIds.add("MinID passport");
        measurementsIds.add("Buypass passport");
        measurementsIds.add("Antall");
    }

    public List<TimeSeriesPoint> mapMeasurements(List<IdportenLoginField> fields, ZonedDateTime dateTime) {
        List<IdportenLoginField> r1fields = new ArrayList<>(fields);
        List<ServiceProvider> serviceProviders = serviceProviderFetch.perform();
        fields = removeSumFromReport(r1fields);
        return fields.stream()
                .map(field -> mapPointForRow(dateTime, field, serviceProviders))
                .collect(Collectors.toList());
    }

    private TimeSeriesPoint mapPointForRow(ZonedDateTime timestamp, IdportenLoginField field, List<ServiceProvider> serviceProviders) {
        TimeSeriesPoint.MeasurementOrCategoryOrBuildEntry point = timeSeriesPoint()
                .timestamp(timestamp)
                .measurements(mapMeasurementsForRow(field));
        mapCategoriesForRow(field, serviceProviders).forEach(point::category);
        return point.build();
    }

    private Map<String, String> mapCategoriesForRow(IdportenLoginField field, List<ServiceProvider> serviceProviders) {
        List<IdportenLoginValue> idpv = field.getValues();

        CategoryMapper cv = new CategoryMapper(idpv, serviceProviders);
        return cv.mapCategories();
    }

    private Map<String, Long> mapMeasurementsForRow(IdportenLoginField field) {
        Map<String, Long> measurements = new HashMap<>();
        List<IdportenLoginValue> idpv = field.getValues();


        if(measurementsIds.size() != idpv.size() - 4){
            throw new RuntimeException("Antall eID-er i statistikken i idporten-admin/event har endra seg! Ny eID i ID-porten? Registrerte eId-ar i idporten-innlogging-statistikk-klient: " + measurementsIds);
        }
        for (int valueIndex = 4; valueIndex < idpv.size(); valueIndex++) {


            String id = measurementsIds.get(valueIndex - 4);
            Long value = idpv.get(valueIndex).getValueAsLong();
            measurements.put(id, value);
        }
        return measurements;
    }

    private List<IdportenLoginField> removeSumFromReport(List<IdportenLoginField> list) {
        if (list.size() > 1) {
            list.remove(list.size() - 1);
        }
        return list;
    }
}
