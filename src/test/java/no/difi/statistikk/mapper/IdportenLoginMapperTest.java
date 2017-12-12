package no.difi.statistikk.mapper;

import no.difi.statistics.ingest.client.model.Measurement;
import no.difi.statistics.ingest.client.model.TimeSeriesPoint;
import no.difi.statistikk.domain.IdportenLoginField;
import no.difi.statistikk.domain.IdportenLoginFieldBuilder;
import no.difi.statistikk.domain.IdportenLoginValue;
import no.difi.statistikk.domain.ServiceProvider;
import no.difi.statistikk.service.ServiceProviderFetch;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class IdportenLoginMapperTest {

    private final ZonedDateTime timeRef = ZonedDateTime.of(LocalDate.of(2017, 1, 4), LocalTime.of(9, 05), ZoneId.of("Europe/Paris"));
    private List<ServiceProvider> serviceProviders;

    @Mock
    private ServiceProviderFetch serviceProviderFetchMock;

    @BeforeEach
    public void setUp() {
        initMocks(this);
        serviceProviders = createServiceProvidersList();
        when(serviceProviderFetchMock.perform()).thenReturn(serviceProviders);
    }

    @Test
    public void whenTEnotFoundItShouldMapToTLifFoundInSplist() {

        Map<String, String> categoriesExpected = createCategoriesMap("GotTL", "8765", "GotTL", "8765" );

        List<Measurement> measurementsExpected = getBasicExpectedMeasurements();
        List<TimeSeriesPoint> tspExpected = new ArrayList<>();
        tspExpected.add(TimeSeriesPoint.builder()
                .timestamp(timeRef)
                .categories(categoriesExpected)
                .measurements(measurementsExpected)
                .build());

        List<IdportenLoginField> idportenLoginFields = new ArrayList<>();
        idportenLoginFields.add(createIdportenLoginField("GotTL", "TL-entityID", "", "", "12", "13", "14", "15", "16", "17", "0", "18", "19", "124"));

        IdportenLoginMapper idportenLoginMapper = new IdportenLoginMapper(serviceProviderFetchMock);
        List<TimeSeriesPoint> tsp = idportenLoginMapper.mapMeasurements(idportenLoginFields, timeRef);

        Assertions.assertTrue(tsp.equals(tspExpected));

    }

    @Test
    public void whenTEentityIdEmptyMapTLorgnumToTEentityId() {

        Map<String, String> categoriesExpected = createCategoriesMap("GotTL", "5678", "GotTL", "5678" );

        List<Measurement> measurementsExpected = getBasicExpectedMeasurements();
        List<TimeSeriesPoint> tspExpected = new ArrayList<>();
        tspExpected.add(TimeSeriesPoint.builder()
                .timestamp(timeRef)
                .categories(categoriesExpected)
                .measurements(measurementsExpected)
                .build());

        List<IdportenLoginField> idportenLoginFields = new ArrayList<>();
        idportenLoginFields.add(createIdportenLoginField("GotTL", "test_te_id", "", "", "12", "13", "14", "15", "16", "17", "0", "18", "19", "124"));

        IdportenLoginMapper idportenLoginMapper = new IdportenLoginMapper(serviceProviderFetchMock);
        List<TimeSeriesPoint> tsp = idportenLoginMapper.mapMeasurements(idportenLoginFields, timeRef);

        Assertions.assertTrue(tsp.equals(tspExpected));
    }

    @Test
    public void shouldMapIdportenFieldValuesToTimeSeriesPoints() {
        Map<String, String> categoriesExpected = createCategoriesMap("Direktoratet for forvaltning og ikt", "1234", "cucumber-samltest", "4321" );

        List<Measurement> measurementsExpected = getBasicExpectedMeasurements();
        List<TimeSeriesPoint> tspExpected = new ArrayList<>();
        tspExpected.add(TimeSeriesPoint.builder()
                .timestamp(timeRef)
                .categories(categoriesExpected)
                .measurements(measurementsExpected)
                .build());

        List<IdportenLoginField> idportenLoginFields = new ArrayList<>();
        idportenLoginFields.add(createIdportenLoginField("Direktoratet for forvaltning og ikt", "autotest-systest-sptest1", "cucumber-samltest", "cucumber-samltest_id", "12", "13", "14", "15", "16", "17", "0", "18", "19", "124"));

        IdportenLoginMapper idportenLoginMapper = new IdportenLoginMapper(serviceProviderFetchMock);
        List<TimeSeriesPoint> tsp = idportenLoginMapper.mapMeasurements(idportenLoginFields, timeRef);

        Assertions.assertTrue(tsp.equals(tspExpected));
    }

    @Test
    public void whenOrgnumNotFoundSetOrgnumCatEmpty(){
        Map<String, String> categoriesExpected = createCategoriesMap("GotTL", "", "GotTL", "" );

        List<Measurement> measurementsExpected = getBasicExpectedMeasurements();
        List<TimeSeriesPoint> tspExpected = new ArrayList<>();
        tspExpected.add(TimeSeriesPoint.builder()
                .timestamp(timeRef)
                .categories(categoriesExpected)
                .measurements(measurementsExpected)
                .build());

        List<IdportenLoginField> idportenLoginFields = new ArrayList<>();
        idportenLoginFields.add(createIdportenLoginField("GotTL", "TE-entityId-not-in-splist", "", "TL-entityId-not-in-splist", "12", "13", "14", "15", "16", "17", "0", "18", "19", "124"));

        IdportenLoginMapper idportenLoginMapper = new IdportenLoginMapper(serviceProviderFetchMock);
        List<TimeSeriesPoint> tsp = idportenLoginMapper.mapMeasurements(idportenLoginFields, timeRef);

        Assertions.assertTrue(tsp.equals(tspExpected));
    }

    @Test
    public void handleThatTEandTEentitiIDareNull(){
        Map<String, String> categoriesExpected = createCategoriesMap("GotTL", "", "GotTL", "" );

        List<Measurement> measurementsExpected = getBasicExpectedMeasurements();
        List<TimeSeriesPoint> tspExpected = new ArrayList<>();
        tspExpected.add(TimeSeriesPoint.builder()
                .timestamp(timeRef)
                .categories(categoriesExpected)
                .measurements(measurementsExpected)
                .build());

        List<IdportenLoginField> idportenLoginFields = new ArrayList<>();
        idportenLoginFields.add(createIdportenLoginField("GotTL", "TE-entityId-not-in-splist", null, null, "12", "13", "14", "15", "16", "17", "0", "18", "19", "124"));

        IdportenLoginMapper idportenLoginMapper = new IdportenLoginMapper(serviceProviderFetchMock);
        List<TimeSeriesPoint> tsp = idportenLoginMapper.mapMeasurements(idportenLoginFields, timeRef);

        Assertions.assertTrue(tsp.equals(tspExpected));
    }

    @Test
    public void handleThatTEareNull(){
        Map<String, String> categoriesExpected = createCategoriesMap("GotTL", "", "GotTL", "5678" );

        List<Measurement> measurementsExpected = getBasicExpectedMeasurements();
        List<TimeSeriesPoint> tspExpected = new ArrayList<>();
        tspExpected.add(TimeSeriesPoint.builder()
                .timestamp(timeRef)
                .categories(categoriesExpected)
                .measurements(measurementsExpected)
                .build());

        List<IdportenLoginField> idportenLoginFields = new ArrayList<>();
        idportenLoginFields.add(createIdportenLoginField("GotTL", "TE-entityId-not-in-splist", null, "test_te_id", "12", "13", "14", "15", "16", "17", "0", "18", "19", "124"));

        IdportenLoginMapper idportenLoginMapper = new IdportenLoginMapper(serviceProviderFetchMock);
        List<TimeSeriesPoint> tsp = idportenLoginMapper.mapMeasurements(idportenLoginFields, timeRef);

        Assertions.assertTrue(tsp.equals(tspExpected));
    }

    @Test
    public void handleThatTEentityIdareNull(){
        Map<String, String> categoriesExpected = createCategoriesMap("GotTL", "", "TE", "");

        List<Measurement> measurementsExpected = getBasicExpectedMeasurements();
        List<TimeSeriesPoint> tspExpected = new ArrayList<>();
        tspExpected.add(TimeSeriesPoint.builder()
                .timestamp(timeRef)
                .categories(categoriesExpected)
                .measurements(measurementsExpected)
                .build());

        List<IdportenLoginField> idportenLoginFields = new ArrayList<>();
        idportenLoginFields.add(createIdportenLoginField("GotTL", "TE-entityId-not-in-splist", "TE", null, "12", "13", "14", "15", "16", "17", "0", "18", "19", "124"));

        IdportenLoginMapper idportenLoginMapper = new IdportenLoginMapper(serviceProviderFetchMock);
        List<TimeSeriesPoint> tsp = idportenLoginMapper.mapMeasurements(idportenLoginFields, timeRef);

        Assertions.assertTrue(tsp.equals(tspExpected));
    }

    private Map<String, String> createCategoriesMap(String TL, String TLentityId, String TE, String TEentityId){
        Map<String, String> categoriesExpected = new HashMap<>();
        categoriesExpected.put("TL", TL);
        categoriesExpected.put("TL-orgnum", TLentityId);
        categoriesExpected.put("TE", TE);
        categoriesExpected.put("TE-orgnum", TEentityId);
        return categoriesExpected;
    }

    private List<Measurement> getBasicExpectedMeasurements() {
        List<Measurement> measurementsExpected = new ArrayList<>();
        measurementsExpected.add(new Measurement("MinID", 12L));
        measurementsExpected.add(new Measurement("MinID OTC", 13L));
        measurementsExpected.add(new Measurement("MinID PIN", 14L));
        measurementsExpected.add(new Measurement("BuyPass", 15L));
        measurementsExpected.add(new Measurement("Commfides", 16L));
        measurementsExpected.add(new Measurement("Federated", 17L));
        measurementsExpected.add(new Measurement("BankID", 0L));
        measurementsExpected.add(new Measurement("eIDAS", 18L));
        measurementsExpected.add(new Measurement("BankID mobil", 19L));
        measurementsExpected.add(new Measurement("Antall", 124L));
        return measurementsExpected;
    }

    private IdportenLoginField createIdportenLoginField(String... values) {

        List idportenLoginValue = new ArrayList<IdportenLoginValue>();

        for (String value : values) {
            IdportenLoginValue idplv = new IdportenLoginValue();
            idplv.setValue(value);
            idportenLoginValue.add(idplv);
        }
        IdportenLoginFieldBuilder idportenLoginFieldBuilder = new IdportenLoginFieldBuilder(idportenLoginValue);
        return idportenLoginFieldBuilder.build();
    }

    private List<ServiceProvider> createServiceProvidersList() {

        ServiceProvider serviceProvider1 = new ServiceProvider("autotest-systest-sptest1", "1234");
        ServiceProvider serviceProvider2 = new ServiceProvider("cucumber-samltest_id", "4321");
        ServiceProvider serviceProvider3 = new ServiceProvider("test_te_id", "5678");
        ServiceProvider serviceProvider4 = new ServiceProvider("TL-entityID", "8765");

        List<ServiceProvider> serviceProviders = new ArrayList<>();
        serviceProviders.add(serviceProvider1);
        serviceProviders.add(serviceProvider2);
        serviceProviders.add(serviceProvider3);
        serviceProviders.add(serviceProvider4);
        return serviceProviders;
    }
}
