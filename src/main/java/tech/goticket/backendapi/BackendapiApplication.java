package tech.goticket.backendapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BackendapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendapiApplication.class, args);
	}

}
