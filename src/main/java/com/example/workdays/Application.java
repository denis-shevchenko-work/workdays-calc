package com.example.workdays;

import com.example.workdays.adapters.api.v1.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    Api api;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("loading data");
        api.getNonWorkingDaysPersister().loadData();
        System.out.println("loaded");
        int days = api.getWorkdaysCalculator().workdaysBetweenInclusive("2021-01-01", "2021-01-31");
        System.out.println("workdays: " + days);
    }
}