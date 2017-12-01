package no.difi.statistikk.schedule;

import no.difi.statistikk.fetch.DataTransfer;
import no.difi.statistikk.service.LastDatapoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static java.time.temporal.ChronoUnit.YEARS;

public class Scheduler {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final String cron_one_minute_interval = "0 */1 * * * *";

    private final DataTransfer dataTransfer;
    private final LastDatapoint lastDatapoint;
    private final String seriesName = "idporten-innlogging";

    public Scheduler(DataTransfer dataTransfer, LastDatapoint lastDatapoint) {
        this.dataTransfer = dataTransfer;
        this.lastDatapoint = lastDatapoint;
    }

    @Scheduled(cron = cron_one_minute_interval)
    public void fetchKontaktregisterReportData() {

        ZonedDateTime from = lastDatapoint.get(seriesName);
        ZonedDateTime to = ZonedDateTime.now(ZoneId.of("UTC"));
        if (isMoreThanOneYearDifference(from, to)) {
            to = oneYearAfter(from);
        }
        logger.info("Transfering data from {} to {}", from, to);
        try {
            dataTransfer.transfer(from);
        } catch (Exception e) {
            logger.error("Failed to transfer data", e);
        }
        logger.info("Data transfer completed");
    }

    private ZonedDateTime oneYearAfter(ZonedDateTime reference) {
        return reference.plus(1, YEARS);
    }

    private boolean isMoreThanOneYearDifference(ZonedDateTime from, ZonedDateTime to) {
        return from.plus(1, YEARS).isBefore(to);
    }
}
