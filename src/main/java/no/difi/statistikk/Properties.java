package no.difi.statistikk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@EnableAutoConfiguration
@Configuration
public class Properties {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final int readTimeout = 15000;
    private static final int connTimeout = 60000;
    private static String ingestPassword;
    private URL idportenAdminUrl;
    private URL statisticsIngestUrl;

    private ZonedDateTime baseLine;

    private final String reportOwner = "991825827";

    public Properties(Environment environment) {
        idportenAdminUrl = environment.getRequiredProperty("url.base.admin", URL.class);
        statisticsIngestUrl = environment.getRequiredProperty("url.base.ingest.statistikk", URL.class);

        baseLine = getBaseLine(environment.getProperty("statistics.years.back", Integer.class));
        final String fileName = environment.getRequiredProperty("file.base.difi-statistikk");
        try {
            final Path pathToPasswordFile = Paths.get(fileName);
            ingestPassword = new String(Files.readAllBytes(pathToPasswordFile));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load file defined in environment property 'file.base.difi-statistikk': " + fileName, e);
        }
    }

    protected ZonedDateTime getBaseLine(Integer yearsFromConfig) {
        int yearsOfStatistics = 3;
        logger.info("Found setting for statistics.years.back in enviroment. Fetching data: " + yearsFromConfig + " years back. Default years if null: " + yearsOfStatistics);
        yearsOfStatistics = yearsFromConfig != null ? yearsFromConfig : yearsOfStatistics;
        return ZonedDateTime.now().minusYears(yearsOfStatistics).truncatedTo(ChronoUnit.HOURS);
    }


    public ZonedDateTime getBaseLine() {
        return baseLine;
    }

    public static int getReadTimeout() {
        return readTimeout;
    }

    public static int getConnTimeout() {
        return connTimeout;
    }

    public static String getIngestPassword() {
        return ingestPassword;
    }

    public URL getIdportenAdminUrl() {
        return idportenAdminUrl;
    }

    public URL getStatisticsIngestUrl() {
        return statisticsIngestUrl;
    }

    public String getReportOwner() {
        return reportOwner;
    }
}
