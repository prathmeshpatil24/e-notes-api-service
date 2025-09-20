package com.enotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ENotesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ENotesApplication.class, args);
        System.out.println("Server is running on http://localhost:8081");
        System.out.println("Press Ctrl+C to stop the server");
	}

}
