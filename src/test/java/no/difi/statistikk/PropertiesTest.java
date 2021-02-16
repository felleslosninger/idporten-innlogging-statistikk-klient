package no.difi.statistikk;


import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PropertiesTest {
    private Properties props;

    @Mock
    Environment environment;
    final static String pwdFileName = "test-pwd.txt";

    @BeforeAll
    public static void createPasswordFile() throws IOException {
        createTestFile(pwdFileName);
    }

    @BeforeEach
    public void setup() throws IOException {
        initMocks(this);

        when(environment.getRequiredProperty("url.base.admin")).thenReturn("http://test.admin.org");
        when(environment.getRequiredProperty("url.base.ingest.statistikk")).thenReturn("http://test.stat.org");
        when(environment.getRequiredProperty("file.base.difi-statistikk")).thenReturn(pwdFileName);

    }

    private static void createTestFile(final String fileName) throws IOException {
        FileWriter fileWriter = new FileWriter(fileName);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.print("Here, take my password! :)");
        printWriter.close();
    }

    @Test
    @DisplayName("It should return ZoneDateTime set to default 3 years back when enviroment variable statistics.years.back not set")
    public void whenEnviromentNotSetBaseLineShouldReturnDefault3YearsBack() {
        props = new Properties(environment);

        final int defaultYearsBack = 3;
        ZonedDateTime now = ZonedDateTime.now().minusYears(defaultYearsBack).truncatedTo(ChronoUnit.HOURS);
        final ZonedDateTime baseLineTime = props.getBaseLine();
        assertNotNull(baseLineTime);
        assertEquals(now.getYear(), baseLineTime.getYear());
        assertEquals(now.getMonthValue(), baseLineTime.getMonthValue());
        assertEquals(now.getDayOfMonth(), baseLineTime.getDayOfMonth());
        assertEquals(now.getHour(), baseLineTime.getHour());
    }
    @Test
    @DisplayName("It should return ZoneDateTime set to 2 years back when enviroment variable statistics.years.back set to 2")
    public void whenEnvironmentYearsBack2ThenBaseLineShouldReturn2YearsBack() {
        int yearsBack = 2;
        when(environment.getProperty("statistics.years.back", Integer.class)).thenReturn(yearsBack);
        props = new Properties(environment);
        ZonedDateTime now = ZonedDateTime.now().minusYears(yearsBack).truncatedTo(ChronoUnit.HOURS);
        final ZonedDateTime baseLineTime = props.getBaseLine();
        assertNotNull(baseLineTime);
        assertEquals(now.getYear(), baseLineTime.getYear());
        assertEquals(now.getMonthValue(), baseLineTime.getMonthValue());
        assertEquals(now.getDayOfMonth(), baseLineTime.getDayOfMonth());
        assertEquals(now.getHour(), baseLineTime.getHour());
    }

    @Test
    @DisplayName("It should return ZoneDateTime set to now when enviroment variable statistics.years.back set to 0")
    public void whenEnvironmentYearsBack0ThenBaseLineShouldReturnToday() {
        int yearsBack = 0;
        when(environment.getProperty("statistics.years.back", Integer.class)).thenReturn(yearsBack);
        props = new Properties(environment);
        ZonedDateTime now = ZonedDateTime.now().minusYears(yearsBack).truncatedTo(ChronoUnit.HOURS);
        final ZonedDateTime baseLineTime = props.getBaseLine();
        assertNotNull(baseLineTime);
        assertEquals(now.getYear(), baseLineTime.getYear());
        assertEquals(now.getMonthValue(), baseLineTime.getMonthValue());
        assertEquals(now.getDayOfMonth(), baseLineTime.getDayOfMonth());
        assertEquals(now.getHour(), baseLineTime.getHour());
    }

    @AfterAll
    public static void deleteFile() {
        File myObj = new File(pwdFileName);
        if (!myObj.delete()) {
            System.out.println("Failed to delete file: " + pwdFileName);
        }
    }
}
