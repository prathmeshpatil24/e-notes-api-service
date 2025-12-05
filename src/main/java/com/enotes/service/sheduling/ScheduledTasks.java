package com.enotes.service.sheduling;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class ScheduledTasks {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    //re-run after every 5 sec
//    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        System.out.printf("The time is now **%s** ", dateFormat.format(new Date()));
        System.out.println(" ");
    }
}
