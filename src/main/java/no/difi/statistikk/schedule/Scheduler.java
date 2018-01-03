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
    public void fetchIdportenInloggingReportData() {

        ZonedDateTime from = lastDatapoint.get(seriesName).plusHours(1);

        logger.info("Transfering data for {}", from);
        try {
            dataTransfer.transfer(from);
        } catch (Exception e) {
            logger.error("Failed to transfer data", e);
        }
        logger.info("Data transfer completed");
    }
}
