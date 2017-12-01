package no.difi.statistikk.fetch;

import no.difi.statistikk.domain.IdportenLoginField;
import no.difi.statistikk.service.IdportenLoginFetch;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.difi.statistikk.domain.IdportenLoginReport.*;

public class DataTransfer {

    private final IdportenLoginFetch fetch;

    public DataTransfer(IdportenLoginFetch fetch) {
        this.fetch = fetch;
    }


    public void transfer(ZonedDateTime from) {

        if (gotDataForNextHour(from)) {

            final List<IdportenLoginField> r1Report = asList(fetch.perform(R1.getId(), from));
            if (r1Report.isEmpty()) throw new FetchError("R1 report is empty");
            List<IdportenLoginField> fields = new ArrayList<>();
            fields.addAll(r1Report);

            // PBLEID-15223 Legg inn data frå R1 til statistikkløsning
        }
    }

    private boolean gotDataForNextHour(ZonedDateTime from) {
        return (asList(fetch.perform(R1.getId(), from.plusHours(1))).get(0).getValues().size() != 0);
    }
}