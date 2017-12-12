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

        ZonedDateTime from = timeRef.minusHours(2);

        //peak
        when(fetchMock.perform(timeRef.minusHours(1))).thenReturn(fields);
        when(fetchMock.perform(from)).thenReturn(fields);
        when(mapperMock.mapMeasurements(Mockito.anyList(), eq(from))).thenReturn(tspMock);

        DataTransfer dataTransfer = new DataTransfer(fetchMock, mapperMock, ingestClientMock);
        dataTransfer.transfer(from);
        verify(fetchMock, times(1)).perform(from);
        verify(ingestClientMock, times(1)).ingest(any(TimeSeriesDefinition.class), any());
    }

    @Test
    public void shouldNotFetchR1WhenDataOnNextHourIsEmpty() {
        final IdportenLoginField[] emptyFields = {createEmptyIdportenLoginField()};

        ZonedDateTime from = timeRef.minusHours(2);

        //peak
        when(fetchMock.perform(timeRef.minusHours(1))).thenReturn(emptyFields);

        when(fetchMock.perform(from)).thenReturn(fields);

        DataTransfer dataTransfer = new DataTransfer(fetchMock, mapperMock, ingestClientMock);
        dataTransfer.transfer(from);
        verify(fetchMock, never()).perform(from);
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