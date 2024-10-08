package br.com.academicquiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class AcademicquizApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();

		System.setProperty("spring.datasource.url", dotenv.get("DB_URL"));
		System.setProperty("spring.datasource.username", dotenv.get("DB_USERNAME"));
		System.setProperty("spring.datasource.password", dotenv.get("DB_PASSWORD"));

		SpringApplication.run(AcademicquizApplication.class, args);
	}

}
