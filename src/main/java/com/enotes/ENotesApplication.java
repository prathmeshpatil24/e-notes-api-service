package com.enotes;

import com.enotes.service.sheduling.ScheduledTasks;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

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
