package com.example.workdays.adapters.api.v1;

import com.example.workdays.configuration.NoSpringConfiguration;

public class Api {

    private final WorkdaysCalculator workdaysCalculator;
    private final NonWorkingDaysPersister nonWorkingDaysPersister;
    private final NonWorkingDaysManager nonWorkingDaysManager;

    public Api(WorkdaysCalculator workdaysCalculator, NonWorkingDaysPersister nonWorkingDaysPersister, NonWorkingDaysManager nonWorkingDaysManager) {
        this.workdaysCalculator = workdaysCalculator;
        this.nonWorkingDaysPersister = nonWorkingDaysPersister;
        this.nonWorkingDaysManager = nonWorkingDaysManager;
    }

    public static Api buildDefault() {
        return NoSpringConfiguration.buildDefault("~/holidays.json");
    }

    public WorkdaysCalculator getWorkdaysCalculator() {
        return workdaysCalculator;
    }

    public NonWorkingDaysPersister getNonWorkingDaysPersister() {
        return nonWorkingDaysPersister;
    }

    public NonWorkingDaysManager getNonWorkingDaysManager() {
        return nonWorkingDaysManager;
    }

}
