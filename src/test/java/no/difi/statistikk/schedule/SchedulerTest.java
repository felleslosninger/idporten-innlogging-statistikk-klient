package no.difi.statistikk.schedule;

import no.difi.statistikk.fetch.DataTransfer;
import no.difi.statistikk.service.LastDatapoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.net.MalformedURLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class SchedulerTest {

    @Mock
    DataTransfer dataTransferMock;

    @Mock
    LastDatapoint lastDatapointMock;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        initMocks(this);
    }

    final String seriesName = "idporten-innlogging";

    @Test
    public void shouldFetchFromOneHourAfterLastDataPoint(){

        ZonedDateTime lastPoint = ZonedDateTime.of(2015, 5, 1, 0, 0, 0, 0, ZoneId.of("UTC"));
        ZonedDateTime shouldTransferDataFrom = ZonedDateTime.of(2015, 5, 1, 1, 0, 0, 0, ZoneId.of("UTC"));

        Scheduler scheduler = new Scheduler(dataTransferMock, lastDatapointMock);
        doNothing().when(dataTransferMock).transfer(any(ZonedDateTime.class));
        when(lastDatapointMock.get(seriesName)).thenReturn(lastPoint);

        scheduler.fetchIdportenInloggingReportData();

        verify(dataTransferMock).transfer(shouldTransferDataFrom);
    }
}