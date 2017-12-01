package no.difi.statistikk.domain;

public enum IdportenLoginReport {

    R1("r1", "Autentiseringer fordelt på tjenesteeier");

    private final String id;
    private final String name;

    IdportenLoginReport(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName(){
        return name;
    }

}
