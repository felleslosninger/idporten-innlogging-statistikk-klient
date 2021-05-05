package no.difi.statistikk.fetch;

import no.difi.statistics.ingest.client.IngestClient;
import no.difi.statistics.ingest.client.IngestService;
import no.difi.statistics.ingest.client.model.TimeSeriesDefinition;
import no.difi.statistics.ingest.client.model.TimeSeriesPoint;
import no.difi.statistikk.domain.IdportenLoginField;
import no.difi.statistikk.mapper.IdportenLoginMapper;
import no.difi.statistikk.maskinporten.MaskinportenIntegration;
import no.difi.statistikk.service.IdportenLoginFetch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.util.Arrays.asList;
import static no.difi.statistics.ingest.client.model.MeasurementDistance.hours;
import static no.difi.statistics.ingest.client.model.TimeSeriesDefinition.timeSeriesDefinition;

@Service
public class DataTransfer {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final IdportenLoginFetch fetch;
    private final IdportenLoginMapper idportenLoginMapper;
    private final IngestClient ingestClient;

    private MaskinportenIntegration maskinportenIntegration;

    private static final TimeSeriesDefinition timeSeriesDefinition = timeSeriesDefinition()
            .name("idporten-innlogging")
            .distance(hours);

    @Autowired
    public DataTransfer(IdportenLoginFetch fetch, IdportenLoginMapper idportenLoginMapper, IngestClient ingestClient, MaskinportenIntegration maskinportenIntegration) {
        this.fetch = fetch;
        this.idportenLoginMapper = idportenLoginMapper;
        this.ingestClient = ingestClient;
        this.maskinportenIntegration = maskinportenIntegration;
    }

    public void transfer(ZonedDateTime from, ZonedDateTime to) {

        while (missingDataForNextHour(to) && from.isBefore(to)) {
            to = to.minusHours(1);
        }

        if (missingDataForNextHour(to)) {
            return;
        }

        logger.info("Transfering data for {}", DateTimeFormatter.ofPattern("yyyy-MM-dd - hh:00").format(from) + " to " + DateTimeFormatter.ofPattern("yyyy-MM-dd - hh:00").format(to));
        String accessToken = fetchAccessTokenFromMaskinporten();

        while (from.isBefore(to) || from.isEqual(to)) {

            final List<IdportenLoginField> r1Report = asList(fetch.perform(from));
            if (r1Report.isEmpty()) throw new FetchError("R1 report is empty");
            if (r1Report.get(0).getValues().size() == 0) {
                logger.warn("Report from R1 contains no data for {}, skip transfer to statistics", from);
            } else {
                List<TimeSeriesPoint> timeSeriesPoints = idportenLoginMapper.mapMeasurements(r1Report, from);
                try {
                    ingestClient.ingest(timeSeriesDefinition, timeSeriesPoints, accessToken);
                } catch (IngestService.Unauthorized e) {
                    logger.info("Unautorized access-token: " + e.getMessage()); // normal flow, do not want to log stacktrace as error.
                    accessToken = maskinportenIntegration.acquireNewAccessToken();
                    ingestClient.ingest(timeSeriesDefinition, timeSeriesPoints, accessToken);
                    logger.info("New access-token fetched from Maskinporten and used successfully against inndata-api.");
                }

            }
            from = from.plusHours(1);
        }
    }

    private String fetchAccessTokenFromMaskinporten() {
        String accessToken = maskinportenIntegration.acquireAccessToken();
        if (maskinportenIntegration.getExpiresIn() != null) {
            final long now = Clock.systemUTC().millis();
            final long expiresTimeInMs = now + (maskinportenIntegration.getExpiresIn() * 1000);
            if (expiresTimeInMs <= now) {
                accessToken = maskinportenIntegration.acquireNewAccessToken();
            }
        }
        return accessToken;
    }

    private boolean missingDataForNextHour(ZonedDateTime timestamp) {
        return asList(fetch.perform(timestamp.plusHours(1))).get(0).getValues().size() == 0;
    }
}