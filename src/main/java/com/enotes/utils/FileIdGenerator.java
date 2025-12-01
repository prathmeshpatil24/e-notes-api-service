package com.enotes.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class FileIdGenerator {

    //for generating id
    private int counter = 0;

    // Stores the date when the last ID was generated
    private String currentDate;

    // Formatter to convert today's date into 'yyyyMMdd' format (e.g., 20250804)
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    //constructor
    public FileIdGenerator() {
        this.currentDate = getFormattedDate();// set currentDate to today
    }

    //get the current date as a string in 'yyyyMMdd' format
    private String getFormattedDate() {
        return LocalDate.now().format(DATE_FORMATTER);
    }

    //ID Generator
    public synchronized String generateId(String originalFilename) {
        String today = getFormattedDate();// get today's date

        // If the date has changed since the last ID was generated,
        // reset the counter to 0 and update currentDate
        if (!today.equals(currentDate)) {
            currentDate = today;
            counter = 0;
        }

        // Format the counter as a 3-digit number (e.g., 000, 001, 002)
        String paddedCounter = String.format("%03d", counter);

        //for incrementing
        counter++;

        // op- Doc-20250804-001
        return "Doc-" + currentDate + "-" + paddedCounter;
    }
}
