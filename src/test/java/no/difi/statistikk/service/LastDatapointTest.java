package no.difi.statistikk.service;

import no.difi.statistics.ingest.client.IngestClient;
import no.difi.statistics.ingest.client.model.TimeSeriesPoint;
import no.difi.statistikk.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.net.MalformedURLException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static java.time.ZonedDateTime.now;
import static no.difi.statistics.ingest.client.model.TimeSeriesPoint.timeSeriesPoint;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@DisplayName("When retrieving last datapoint for admin from statistics")
public class LastDatapointTest {
    private static final String seriesId = "idporten-innlogging";

    private LastDatapoint lastDatapoint;

    @Mock
    private IngestClient ingestClientMock;

    @Mock
    private Properties props;

    private final ZonedDateTime baseDate = now().minusYears(3).truncatedTo(ChronoUnit.HOURS);


    @BeforeEach
    public void setUp() throws MalformedURLException {
        initMocks(this);
        lastDatapoint = new LastDatapoint(ingestClientMock, props);
        when(props.getBaseLine()).thenReturn(baseDate);

    }

    @Test
    @DisplayName("It should return ZoneDateTime set to baseTime")
    public void shouldReturnNullWhenResponseCode200AndEmptyDataset() {
        when(ingestClientMock.last(anyObject())).thenReturn(Optional.empty());
        assertEquals(baseDate, lastDatapoint.get(seriesId).truncatedTo(ChronoUnit.HOURS));
    }

    @Test
    @DisplayName("It should return ZoneDateTime type when response containts value")
    public void shouldReturnZoneDateTimeWithValueFromResponseWhenResponseCode200AndValueExistsInDataset() {
        when(ingestClientMock.last(anyObject())).thenReturn(createResponseOk());
        assertTrue(lastDatapoint.get(seriesId).getClass().equals(ZonedDateTime.class));
    }

    @Test
    @DisplayName("It should return same ZoneDateTime as the timestamp in response when response contains value")
    public void shouldReturnZoneDateTimeValueFoundInResponseWhenResponseContainsValue() {
        final ZonedDateTime dateTime = ZonedDateTime.of(2016, 12, 24, 18, 0, 0, 0, ZonedDateTime.now().getZone());
        when(ingestClientMock.last(anyObject())).thenReturn(createResponseOk(dateTime));
        assertEquals(dateTime, lastDatapoint.get(seriesId));
    }

    private Optional<TimeSeriesPoint> createResponseOk() {
        return createResponseOk(now().minusDays(3));
    }


    private Optional<TimeSeriesPoint> createResponseOk(ZonedDateTime dateTime) {
        return Optional.of(timeSeriesPoint()
                .timestamp(dateTime)
                .measurement("d5_8", 31)
                .measurement("d5_11", 63)
                .measurement("d5_9", 17)
                .measurement("d5_6", 18)
                .measurement("d5_7", 26)
                .measurement("d5_10", 57)
                .measurement("d5_4", 16)
                .measurement("Pål sine høner", 83)
                .measurement("d5_5", 62)
                .measurement("d5_2", 2)
                .measurement("d5_3", 2)
                .build());
    }

}