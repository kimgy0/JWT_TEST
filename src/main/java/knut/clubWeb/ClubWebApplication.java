package knut.clubWeb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"knut"})
public class ClubWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClubWebApplication.class, args);
	}

}
