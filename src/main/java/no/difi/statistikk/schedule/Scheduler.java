package no.difi.statistikk.schedule;

import no.difi.statistikk.fetch.DataTransfer;
import no.difi.statistikk.service.LastDatapoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Scheduler {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final DataTransfer dataTransfer;
    private final LastDatapoint lastDatapoint;
    private final String seriesName = "idporten-innlogging";

    public Scheduler(DataTransfer dataTransfer, LastDatapoint lastDatapoint) {
        this.dataTransfer = dataTransfer;
        this.lastDatapoint = lastDatapoint;
    }

    @Scheduled(fixedDelay = 60000L)
    public void fetchIdportenInloggingReportData() {

        ZonedDateTime from = lastDatapoint.get(seriesName).plusHours(1);
        ZonedDateTime to = ZonedDateTime.now(ZoneId.of("UTC"));

        logger.info("Transfering data from {} to {}", from, to);

        try {
            dataTransfer.transfer(from, to);
        } catch (Exception e) {
            logger.error("Failed to transfer data", e);
        }
        logger.info("Data transfer completed");
    }
}
