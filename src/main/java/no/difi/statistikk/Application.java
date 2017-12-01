package no.difi.statistikk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

public class Application {

    public static void main(final String[] args) throws Exception {
        SpringApplication.run(Config.class, args);
    }
}
