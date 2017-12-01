package no.difi.statistikk.service;

import no.difi.statistics.ingest.client.IngestClient;
import no.difi.statistics.ingest.client.model.TimeSeriesDefinition;
import no.difi.statistics.ingest.client.model.TimeSeriesPoint;

import java.time.ZonedDateTime;

import static no.difi.statistics.ingest.client.model.MeasurementDistance.hours;

public class LastDatapoint {
    private final IngestClient ingestClient;
    private final static ZonedDateTime baseTime = ZonedDateTime.now().minusHours(10);

    public LastDatapoint(IngestClient ingestClient) {
        this.ingestClient = ingestClient;
    }

    public ZonedDateTime get(String seriesName) {
        return ingestClient.last(TimeSeriesDefinition.builder().name(seriesName).distance(hours))
                .map(TimeSeriesPoint::getTimestamp).orElse(baseTime);
    }
}
