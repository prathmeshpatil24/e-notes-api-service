package com.enotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
public class ENotesApplication {

	public static void main(String[] args) {
        SpringApplication.run(ENotesApplication.class, args);
        System.out.println("Server is running on http://localhost:8085");
        System.out.println("Press Ctrl+C to stop the server");

//        ScheduledTasks scheduledTasks = context.getBean(ScheduledTasks.class);
//
//        scheduledTasks.reportCurrentTime();
    }

}
