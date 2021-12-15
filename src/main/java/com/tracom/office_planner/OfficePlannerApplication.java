package com.tracom.office_planner;

import com.tracom.office_planner.MeetingsLog.PlannerLogger;
import org.apache.logging.log4j.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.transaction.Transactional;

@Transactional
@SpringBootApplication
public class OfficePlannerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OfficePlannerApplication.class, args);
        PlannerLogger.appStart();
    }
}
