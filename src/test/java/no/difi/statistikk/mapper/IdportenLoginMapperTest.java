package no.difi.statistikk.mapper;

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

import static no.difi.statistics.ingest.client.model.TimeSeriesPoint.timeSeriesPoint;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class IdportenLoginMapperTest {

    private final ZonedDateTime timeRef = ZonedDateTime.of(LocalDate.of(2020, 1, 4), LocalTime.of(9, 05), ZoneId.of("Europe/Paris"));
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

        Map<String, String> categoriesExpected = createCategoriesMap("GotTL", "8765", "GotTL", "8765", "TL-entityID", "TL-entityID" );

        Map<String, Long> measurementsExpected = getBasicExpectedMeasurements();
        List<TimeSeriesPoint> tspExpected = new ArrayList<>();
        tspExpected.add(point(timeRef, measurementsExpected, categoriesExpected));

        List<IdportenLoginField> idportenLoginFields = new ArrayList<>();
        idportenLoginFields.add(createIdportenLoginField("GotTL", "TL-entityID", "", "", "12", "13", "14", "15", "16", "17", "0", "18", "19", "0", "0", "124"));

        IdportenLoginMapper idportenLoginMapper = new IdportenLoginMapper(serviceProviderFetchMock);
        List<TimeSeriesPoint> tsp = idportenLoginMapper.mapMeasurements(idportenLoginFields, timeRef);

        assertTrue(tsp.equals(tspExpected));

    }

    @Test
    public void whenTEentityIdEmptyMapTLorgnumToTEentityId() {

        Map<String, String> categoriesExpected = createCategoriesMap("GotTL", "5678", "GotTL", "5678", "test_te_id", "test_te_id" );

        Map<String, Long> measurementsExpected = getBasicExpectedMeasurements();
        List<TimeSeriesPoint> tspExpected = new ArrayList<>();
        tspExpected.add(point(timeRef, measurementsExpected, categoriesExpected));

        List<IdportenLoginField> idportenLoginFields = new ArrayList<>();
        idportenLoginFields.add(createIdportenLoginField("GotTL", "test_te_id", "", "", "12", "13", "14", "15", "16", "17", "0", "18", "19", "0", "0", "124"));

        IdportenLoginMapper idportenLoginMapper = new IdportenLoginMapper(serviceProviderFetchMock);
        List<TimeSeriesPoint> tsp = idportenLoginMapper.mapMeasurements(idportenLoginFields, timeRef);

        assertTrue(tsp.equals(tspExpected));
    }

    @Test
    public void shouldMapIdportenFieldValuesToTimeSeriesPoints() {
        Map<String, String> categoriesExpected = createCategoriesMap("Direktoratet for forvaltning og ikt", "1234", "cucumber-samltest", "4321", "autotest-systest-sptest1", "cucumber-samltest_id" );

        Map<String, Long> measurementsExpected = getBasicExpectedMeasurements();
        List<TimeSeriesPoint> tspExpected = new ArrayList<>();
        tspExpected.add(point(timeRef, measurementsExpected, categoriesExpected));

        List<IdportenLoginField> idportenLoginFields = new ArrayList<>();
        idportenLoginFields.add(createIdportenLoginField("Direktoratet for forvaltning og ikt", "autotest-systest-sptest1", "cucumber-samltest", "cucumber-samltest_id", "12", "13", "14", "15", "16", "17", "0", "18", "19", "0", "0", "124"));

        IdportenLoginMapper idportenLoginMapper = new IdportenLoginMapper(serviceProviderFetchMock);
        List<TimeSeriesPoint> tsp = idportenLoginMapper.mapMeasurements(idportenLoginFields, timeRef);

        assertTrue(tsp.equals(tspExpected));
    }

    @Test
    public void whenOrgnumNotFoundSetOrgnumCatEmpty(){
        Map<String, String> categoriesExpected = createCategoriesMap("GotTL", "", "GotTL", "", "TE-entityId-not-in-splist", "TL-entityId-not-in-splist" );

        Map<String, Long> measurementsExpected = getBasicExpectedMeasurements();
        List<TimeSeriesPoint> tspExpected = new ArrayList<>();
        tspExpected.add(point(timeRef, measurementsExpected, categoriesExpected));

        List<IdportenLoginField> idportenLoginFields = new ArrayList<>();
        idportenLoginFields.add(createIdportenLoginField("GotTL", "TE-entityId-not-in-splist", "", "TL-entityId-not-in-splist", "12", "13", "14", "15", "16", "17", "0", "18", "19", "0", "0", "124"));

        IdportenLoginMapper idportenLoginMapper = new IdportenLoginMapper(serviceProviderFetchMock);
        List<TimeSeriesPoint> tsp = idportenLoginMapper.mapMeasurements(idportenLoginFields, timeRef);

        assertTrue(tsp.equals(tspExpected));
    }

    @Test
    public void handleThatTEandTEentitiIDareNull(){
        Map<String, String> categoriesExpected = createCategoriesMap("GotTL", "", "GotTL", "", "TE-entityId-not-in-splist", "TE-entityId-not-in-splist" );

        Map<String, Long> measurementsExpected = getBasicExpectedMeasurements();
        List<TimeSeriesPoint> tspExpected = new ArrayList<>();
        tspExpected.add(point(timeRef, measurementsExpected, categoriesExpected));

        List<IdportenLoginField> idportenLoginFields = new ArrayList<>();
        idportenLoginFields.add(createIdportenLoginField("GotTL", "TE-entityId-not-in-splist", "", "", "12", "13", "14", "15", "16", "17", "0", "18", "19", "0", "0", "124"));

        IdportenLoginMapper idportenLoginMapper = new IdportenLoginMapper(serviceProviderFetchMock);
        List<TimeSeriesPoint> tsp = idportenLoginMapper.mapMeasurements(idportenLoginFields, timeRef);

        assertTrue(tsp.equals(tspExpected));
    }

    @Test
    public void shouldRemoveSumRowInIdportenFieldsIfTwoRows(){

        List<IdportenLoginField> idportenLoginFields = new ArrayList<>();
        idportenLoginFields.add(createIdportenLoginField("GotTL", "TE-entityId-not-in-splist", "TE", "", "12", "13", "14", "15", "16", "17", "0", "18", "19", "0", "0", "124"));
        idportenLoginFields.add(createIdportenLoginField("Sum", "0", "0", "0", "12", "13", "14", "15", "16", "17", "0", "18", "19", "0", "0", "124"));
        IdportenLoginMapper idportenLoginMapper = new IdportenLoginMapper(serviceProviderFetchMock);

        List<TimeSeriesPoint> timeSeriesPoints;
        timeSeriesPoints = idportenLoginMapper.mapMeasurements(idportenLoginFields, timeRef);

        Assertions.assertEquals(1, timeSeriesPoints.size());
    }

    @Test
    public void shouldRemoveSumRowInIdportenFieldsIfLargerThanTwoRows(){

        List<IdportenLoginField> idportenLoginFields = new ArrayList<>();
        idportenLoginFields.add(createIdportenLoginField("GotTL", "TE-entityId-not-in-splist", "TE", "", "12", "13", "14", "15", "16", "17", "0", "18", "19", "0", "0","124"));
        idportenLoginFields.add(createIdportenLoginField("GotTL2", "TE-entityId-not-in-splist2", "TE2", "", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0","0"));
        idportenLoginFields.add(createIdportenLoginField("GotTL3", "TE-entityId-not-in-splist3", "TE3", "", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0","0"));
        idportenLoginFields.add(createIdportenLoginField("GotTL4", "TE-entityId-not-in-splist4", "TE4", "", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0","0"));
        idportenLoginFields.add(createIdportenLoginField("Sum", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"));
        IdportenLoginMapper idportenLoginMapper = new IdportenLoginMapper(serviceProviderFetchMock);

        List<TimeSeriesPoint> timeSeriesPoints;
        timeSeriesPoints = idportenLoginMapper.mapMeasurements(idportenLoginFields, timeRef);

        Assertions.assertEquals(4, timeSeriesPoints.size());
    }

    @Test
    public void shouldNotRemoveSumRowInIdportenFieldsIfLessThanTwoRows(){

        List<IdportenLoginField> idportenLoginFields = new ArrayList<>();
        idportenLoginFields.add(createIdportenLoginField("Sum", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0","0", "0", "0"));
        IdportenLoginMapper idportenLoginMapper = new IdportenLoginMapper(serviceProviderFetchMock);

        List<TimeSeriesPoint> timeSeriesPoints;
        timeSeriesPoints = idportenLoginMapper.mapMeasurements(idportenLoginFields, timeRef);

        Assertions.assertEquals(1, timeSeriesPoints.size());
    }

    @Test
    public void TEentityIdShouldFallBackToTLentityIdIfEmpty(){
        Map<String, String> categoriesExpected = createCategoriesMap("GotTL", "", "GotTL", "", "GotTLentityId", "GotTLentityId" );

        Map<String, Long> measurementsExpected = getBasicExpectedMeasurements();
        List<TimeSeriesPoint> tspExpected = new ArrayList<>();
        tspExpected.add(point(timeRef, measurementsExpected, categoriesExpected));

        List<IdportenLoginField> idportenLoginFields = new ArrayList<>();
        idportenLoginFields.add(createIdportenLoginField("GotTL", "GotTLentityId", "", "", "12", "13", "14", "15", "16", "17", "0", "18", "19", "0", "0","124"));

        IdportenLoginMapper idportenLoginMapper = new IdportenLoginMapper(serviceProviderFetchMock);
        List<TimeSeriesPoint> tsp = idportenLoginMapper.mapMeasurements(idportenLoginFields, timeRef);

        assertTrue(tsp.equals(tspExpected));
    }

    @Test
    public void shouldFailIfNewEidInDataButNotInCode(){
        Map<String, String> categoriesExpected = createCategoriesMap("GotTL", "", "GotTL", "", "GotTLentityId", "GotTLentityId" );

        Map<String, Long> measurementsExpected = getBasicExpectedMeasurements();
        List<TimeSeriesPoint> tspExpected = new ArrayList<>();
        tspExpected.add(point(timeRef, measurementsExpected, categoriesExpected));

        List<IdportenLoginField> idportenLoginFields = new ArrayList<>();
        idportenLoginFields.add(createIdportenLoginField("GotTL", "GotTLentityId", "", "", "12", "13", "14", "15", "16", "17", "0", "18", "19", "0", "0", "0", "124"));

        IdportenLoginMapper idportenLoginMapper = new IdportenLoginMapper(serviceProviderFetchMock);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> idportenLoginMapper.mapMeasurements(idportenLoginFields, timeRef),
                "Expected doThing() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Antall eID-er"));
    }


    private Map<String, String> createCategoriesMap(String TL, String TLorgnum, String TE, String TEorgnum, String TLEntityId, String TEEntityId){
        Map<String, String> categoriesExpected = new HashMap<>();
        categoriesExpected.put("TL", TL);
        categoriesExpected.put("TL-orgnum", TLorgnum);
        categoriesExpected.put("TE", TE);
        categoriesExpected.put("TE-orgnum", TEorgnum);
        categoriesExpected.put("TL-entityId", TLEntityId);
        categoriesExpected.put("TE-entityId", TEEntityId);
        return categoriesExpected;
    }

    private Map<String, Long> getBasicExpectedMeasurements() {
        Map<String, Long> measurementsExpected = new HashMap<>();
        measurementsExpected.put("MinID", 12L);
        measurementsExpected.put("MinID OTC", 13L);
        measurementsExpected.put("MinID PIN", 14L);
        measurementsExpected.put("BuyPass", 15L);
        measurementsExpected.put("Commfides", 16L);
        measurementsExpected.put("Federated", 17L);
        measurementsExpected.put("BankID", 0L);
        measurementsExpected.put("eIDAS", 18L);
        measurementsExpected.put("BankID mobil", 19L);
        measurementsExpected.put("MinID passport", 0L);
        measurementsExpected.put("Buypass passport", 0L);
        measurementsExpected.put("Antall", 124L);
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

    private TimeSeriesPoint point(ZonedDateTime timestamp, Map<String, Long> measurements, Map<String, String> categories) {
        TimeSeriesPoint.MeasurementOrCategoryOrBuildEntry point = timeSeriesPoint()
                .timestamp(timestamp)
                .measurements(measurements);
        categories.forEach(point::category);
        return point.build();
    }

}
