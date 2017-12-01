package no.difi.statistikk.fetch;

public class FetchError extends RuntimeException {

    public FetchError(String message ) {
        super(message);
    }
}
