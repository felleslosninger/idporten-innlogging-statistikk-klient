package no.difi.statistikk.fetch;

import no.difi.statistikk.domain.IdportenLoginField;
import no.difi.statistikk.domain.IdportenLoginFieldBuilder;
import no.difi.statistikk.domain.IdportenLoginValue;
import no.difi.statistikk.service.IdportenLoginFetch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static no.difi.statistikk.domain.IdportenLoginReport.R1;

class DataTransferTest {

    private final ZonedDateTime timeRef = ZonedDateTime.of(LocalDate.of(2017, 1, 4), LocalTime.of(9, 05), ZoneId.of("Europe/Paris"));
    private final IdportenLoginField[] fields = {createIdportenLoginField("Direktoratet for forvaltning og ikt", "autotest-systest-sptest1", "cucumber-samltest", "cucumber-samltest", "0", "0", "43", "0", "0", "0", "4", "0", "0", "47")};

    @Mock
    private IdportenLoginFetch fetchMock;

    @BeforeEach
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldFetchR1WhenGotDataOnNextHour() {

        ZonedDateTime from = timeRef.minusHours(2);

        //peak
        when(fetchMock.perform(R1.getId(), timeRef.minusHours(1))).thenReturn(fields);

        when(fetchMock.perform(R1.getId(), from)).thenReturn(fields);

        DataTransfer dataTransfer = new DataTransfer(fetchMock);
        dataTransfer.transfer(from);
        verify(fetchMock, times(1)).perform(R1.getId(), from);
    }

    @Test
    public void shouldNotFetchR1WhenDataOnNextHourIsEmpty() {
        final IdportenLoginField[] emptyFields = {createEmptyIdportenLoginField()};

        ZonedDateTime from = timeRef.minusHours(2);

        //peak
        when(fetchMock.perform(R1.getId(), timeRef.minusHours(1))).thenReturn(emptyFields);

        when(fetchMock.perform(R1.getId(), from)).thenReturn(fields);

        DataTransfer dataTransfer = new DataTransfer(fetchMock);
        dataTransfer.transfer(from);
        verify(fetchMock, never()).perform(R1.getId(), from);
    }

    private IdportenLoginField createEmptyIdportenLoginField() {
        IdportenLoginFieldBuilder idportenLoginFieldBuilder = new IdportenLoginFieldBuilder(Collections.emptyList());
        return idportenLoginFieldBuilder.build();
    }

    private IdportenLoginField createIdportenLoginField(String... values) {

        List idportenLoginValue = new ArrayList<IdportenLoginValue>();

        for (String value : values) {
            IdportenLoginValue idplv = new IdportenLoginValue();
            idplv.setValue(value);
            idportenLoginValue.add(idplv);
        }
        IdportenLoginFieldBuilder idportenLoginFieldBuilder = new IdportenLoginFieldBuilder(idportenLoginValue);
        return idportenLoginFieldBuilder.build();
    }
}