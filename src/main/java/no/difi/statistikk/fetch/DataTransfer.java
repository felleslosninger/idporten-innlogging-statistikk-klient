package no.difi.statistikk.fetch;

import no.difi.statistics.ingest.client.IngestClient;
import no.difi.statistics.ingest.client.model.TimeSeriesDefinition;
import no.difi.statistics.ingest.client.model.TimeSeriesPoint;
import no.difi.statistikk.domain.IdportenLoginField;
import no.difi.statistikk.mapper.IdportenLoginMapper;

import no.difi.statistikk.service.IdportenLoginFetch;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.difi.statistics.ingest.client.model.MeasurementDistance.hours;

public class DataTransfer {

    private final IdportenLoginFetch fetch;
    private final IdportenLoginMapper idportenLoginMapper;
    private final IngestClient ingestClient;

    private static final TimeSeriesDefinition timeSeriesDefinition = TimeSeriesDefinition.builder()
            .name("idporten-innlogging")
            .distance(hours);

    public DataTransfer(IdportenLoginFetch fetch, IdportenLoginMapper idportenLoginMapper, IngestClient ingestClient) {
        this.fetch = fetch;
        this.idportenLoginMapper = idportenLoginMapper;
        this.ingestClient = ingestClient;
    }

    public void transfer(ZonedDateTime from) {

        if (gotDataForNextHour(from)) {
            final List<IdportenLoginField> r1Report = asList(fetch.perform(from));
            if (r1Report.isEmpty()) throw new FetchError("R1 report is empty");
            List<IdportenLoginField> fields = new ArrayList<>();
            fields.addAll(r1Report);
            List<TimeSeriesPoint> timeSeriesPoints = idportenLoginMapper.mapMeasurements(fields, from);
            ingestClient.ingest(timeSeriesDefinition, timeSeriesPoints);
        }
    }

    private boolean gotDataForNextHour(ZonedDateTime from) {
        return (asList(fetch.perform(from.plusHours(1))).get(0).getValues().size() != 0);
    }
}