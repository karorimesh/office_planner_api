package com.tracom.office_planner;

import org.junit.jupiter.api.Test;

public class LoggingTest {
    @Test
    public void loggingTest() throws Exception{
        PlannerLogger logger  = new PlannerLogger();
        logger.loggingTask();
    }
}
