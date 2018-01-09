package no.difi.statistikk.fetch;

import no.difi.statistics.ingest.client.IngestClient;
import no.difi.statistics.ingest.client.model.TimeSeriesDefinition;
import no.difi.statistics.ingest.client.model.TimeSeriesPoint;
import no.difi.statistikk.domain.IdportenLoginField;
import no.difi.statistikk.domain.IdportenLoginFieldBuilder;
import no.difi.statistikk.domain.IdportenLoginValue;
import no.difi.statistikk.mapper.IdportenLoginMapper;
import no.difi.statistikk.service.IdportenLoginFetch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import static java.lang.Math.toIntExact;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.internal.verification.VerificationModeFactory.times;

class DataTransferTest {

    private final ZonedDateTime timeRef = ZonedDateTime.of(LocalDate.of(2017, 1, 4), LocalTime.of(9, 05), ZoneId.of("Europe/Paris"));
    private final IdportenLoginField[] fields = {createIdportenLoginField("Direktoratet for forvaltning og ikt", "autotest-systest-sptest1", "cucumber-samltest", "cucumber-samltest", "0", "0", "43", "0", "0", "0", "4", "0", "0", "47")};

    @Mock
    private IdportenLoginFetch fetchMock;

    @Mock
    private IdportenLoginMapper mapperMock;

    @Mock
    private IngestClient ingestClientMock;

    @Mock
    private List<TimeSeriesPoint> tspMock;

    @BeforeEach
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldFetchAndPushR1WhenGotDataOnNextHour() {

        ZonedDateTime from = timeRef;

        //peak
        when(fetchMock.perform(timeRef.plusHours(1))).thenReturn(fields);
        when(fetchMock.perform(from)).thenReturn(fields);
        when(mapperMock.mapMeasurements(Mockito.anyList(), eq(from))).thenReturn(tspMock);

        DataTransfer dataTransfer = new DataTransfer(fetchMock, mapperMock, ingestClientMock);
        dataTransfer.transfer(from, from);
        verify(fetchMock, times(1)).perform(from);
        verify(ingestClientMock, times(1)).ingest(any(TimeSeriesDefinition.class), any());
    }

    @Test
    public void shouldNotFetchR1WhenDataOnNextHourIsEmpty() {
        final IdportenLoginField[] emptyFields = {createEmptyIdportenLoginField()};

        ZonedDateTime from = timeRef.minusHours(1);

        //peak
        when(fetchMock.perform(from)).thenReturn(fields);
        when(fetchMock.perform(timeRef)).thenReturn(emptyFields);

        DataTransfer dataTransfer = new DataTransfer(fetchMock, mapperMock, ingestClientMock);
        dataTransfer.transfer(from, from);
        verify(fetchMock, never()).perform(from);
        verifyZeroInteractions(mapperMock);
        verifyZeroInteractions(ingestClientMock);
    }

    @Test
    public void shouldTransferAllDataFromTo() {
        final IdportenLoginField[] emptyFields = {createEmptyIdportenLoginField()};

        ZonedDateTime from = ZonedDateTime.of(LocalDate.of(2014, 1, 4), LocalTime.of(9, 05), ZoneId.of("Europe/Paris"));
        ZonedDateTime to = from.plusYears(2);

        int numberOfHoursToGet = toIntExact(from.until(to, HOURS));

        when(fetchMock.perform(any(ZonedDateTime.class))).thenReturn(fields);
        when(fetchMock.perform(to.plusHours(1))).thenReturn(emptyFields);
        when(mapperMock.mapMeasurements(Mockito.anyList(), eq(from))).thenReturn(tspMock);

        DataTransfer dataTransfer = new DataTransfer(fetchMock, mapperMock, ingestClientMock);
        dataTransfer.transfer(from, to);
        verify(fetchMock, times(1)).perform(from);
        verify(ingestClientMock, times( numberOfHoursToGet)).ingest(any(TimeSeriesDefinition.class), any());
    }

    @Test
    public void shouldHandleThatR1ReportIsEmptyAndToFieldIsEmpty() {
        final IdportenLoginField[] emptyFields = {createEmptyIdportenLoginField()};

        ZonedDateTime from = ZonedDateTime.of(LocalDate.of(2014, 1, 4), LocalTime.of(9, 00), ZoneId.of("Europe/Paris"));
        ZonedDateTime hourWithEmptyR1Report = ZonedDateTime.of(LocalDate.of(2014, 1, 4), LocalTime.of(12, 00), ZoneId.of("Europe/Paris"));
        ZonedDateTime to = from.plusDays(1);

        when(fetchMock.perform(any(ZonedDateTime.class))).thenReturn(fields);
        when(fetchMock.perform(hourWithEmptyR1Report)).thenReturn(emptyFields);
        when(fetchMock.perform(to)).thenReturn(emptyFields);
        when(mapperMock.mapMeasurements(Mockito.anyList(), eq(from))).thenReturn(tspMock);

        DataTransfer dataTransfer = new DataTransfer(fetchMock, mapperMock, ingestClientMock);
        dataTransfer.transfer(from, to);

        verify(ingestClientMock, times( 23)).ingest(any(TimeSeriesDefinition.class), any());
    }

    @Test
    public void shouldHandleThatAllReportsAreEmpty() {
        final IdportenLoginField[] emptyFields = {createEmptyIdportenLoginField()};

        ZonedDateTime from = timeRef;
        ZonedDateTime to = timeRef.plusYears(2);

        when(fetchMock.perform(any(ZonedDateTime.class))).thenReturn(emptyFields);
        when(mapperMock.mapMeasurements(Mockito.anyList(), eq(from))).thenReturn(tspMock);
        DataTransfer dataTransfer = new DataTransfer(fetchMock, mapperMock, ingestClientMock);
        dataTransfer.transfer(from, to);

        verifyZeroInteractions(mapperMock);
        verifyZeroInteractions(ingestClientMock);
    }

    private IdportenLoginField createEmptyIdportenLoginField() {
        IdportenLoginFieldBuilder idportenLoginFieldBuilder = new IdportenLoginFieldBuilder(Collections.emptyList());
        return idportenLoginFieldBuilder.build();
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
}