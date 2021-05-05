package no.difi.statistikk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.net.URL;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@Configuration
public class Properties {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final int readTimeout = 15000;
    private static final int connTimeout = 60000;
    private URL idportenAdminUrl;
    private URL statisticsIngestUrl;

    private ZonedDateTime baseLine;

    private final String reportOwner = "991825827";

    @Autowired
    private Environment environment;

    public Properties(Environment environment) {
        logger.error("starting now");
        this.environment = environment;
        idportenAdminUrl = environment.getRequiredProperty("url.base.admin", URL.class);
        statisticsIngestUrl = environment.getRequiredProperty("url.base.ingest.statistikk", URL.class);

        baseLine = getBaseLine(environment.getProperty("statistics.years.back", Integer.class), environment.getProperty("statistics.days.back", Integer.class));
//        final String fileName = environment.getRequiredProperty("file.base.difi-statistikk");
//        try {
//            final Path pathToPasswordFile = Paths.get(fileName);
//            ingestPassword = new String(Files.readAllBytes(pathToPasswordFile));
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to load file defined in environment property 'file.base.difi-statistikk': " + fileName, e);
//        }
    }

    protected ZonedDateTime getBaseLine(Integer yearsFromConfig, Integer daysFromConfig) {
        int yearsOfStatistics = 3;
        logger.info("Found setting for statistics.years.back in enviroment. Fetching data: " + yearsFromConfig + " years back. Default years if null: " + yearsOfStatistics);
        yearsOfStatistics = yearsFromConfig != null ? yearsFromConfig : yearsOfStatistics;
        int daysOfStatistics = daysFromConfig != null ? daysFromConfig : 0;
        return ZonedDateTime.now().minusYears(yearsOfStatistics).minusDays(daysOfStatistics).truncatedTo(ChronoUnit.HOURS);
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
