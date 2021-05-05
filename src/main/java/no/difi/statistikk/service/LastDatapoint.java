package no.difi.statistikk.service;

import no.difi.statistics.ingest.client.IngestClient;
import no.difi.statistics.ingest.client.model.TimeSeriesPoint;
import no.difi.statistikk.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

import static no.difi.statistics.ingest.client.model.MeasurementDistance.hours;
import static no.difi.statistics.ingest.client.model.TimeSeriesDefinition.timeSeriesDefinition;

@Service
public class LastDatapoint {
    private final IngestClient ingestClient;
    private Properties properties;

    //private final static ZonedDateTime availableDataFrom = ZonedDateTime.of(2011, 8, 14, 0, 0, 0, 0, ZoneId.of("UTC"));

    @Autowired
    public LastDatapoint(IngestClient ingestClient, Properties properties) {
        this.ingestClient = ingestClient;
        this.properties = properties;
    }

    public ZonedDateTime get(String seriesName) {
        return ingestClient.last(timeSeriesDefinition().name(seriesName).distance(hours))
                .map(TimeSeriesPoint::getTimestamp).orElse(properties.getBaseLine());
    }
}
