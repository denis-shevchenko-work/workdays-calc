package com.example.workdays.adapters.api.v1;

import com.example.workdays.adapters.api.v1.entities.Day;
import com.example.workdays.configuration.NoSpringConfiguration;
import lombok.NoArgsConstructor;

public class Api {

    private final WorkdaysCalculator workdaysCalculator;
    private final NonWorkingDaysPersister nonWorkingDaysPersister;
    private final NonWorkingDaysManager nonWorkingDaysManager;

    private Api(WorkdaysCalculator workdaysCalculator, NonWorkingDaysPersister nonWorkingDaysPersister, NonWorkingDaysManager nonWorkingDaysManager) {
        this.workdaysCalculator = workdaysCalculator;
        this.nonWorkingDaysPersister = nonWorkingDaysPersister;
        this.nonWorkingDaysManager = nonWorkingDaysManager;
    }

    public static ApiBuilder builder() {
        return new ApiBuilder();
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


    @NoArgsConstructor
    public static class ApiBuilder {
        private WorkdaysCalculator workdaysCalculator;
        private NonWorkingDaysPersister nonWorkingDaysPersister;
        private NonWorkingDaysManager nonWorkingDaysManager;
        private boolean addDefaultHolidays = false;
        private String pathToRepositoryFile;

        public ApiBuilder preconfiguredPathToRepositoryFile(String pathToRepositoryFile) {
            this.pathToRepositoryFile = pathToRepositoryFile;
            return this;
        }

        public Api buildPreconfigured() {
            Api api;
            if(pathToRepositoryFile != null) {
                api = NoSpringConfiguration.buildDefault(pathToRepositoryFile);
            }  else {
                api = NoSpringConfiguration.buildDefault();
            }
            if (addDefaultHolidays) {
                addDefaultNonWorkingDays(api.getNonWorkingDaysManager());
            }
            return api;
        }

        public ApiBuilder workdaysCalculator(WorkdaysCalculator workdaysCalculator) {
            this.workdaysCalculator = workdaysCalculator;
            return this;
        }

        public ApiBuilder nonWorkingDaysPersister(NonWorkingDaysPersister nonWorkingDaysPersister) {
            this.nonWorkingDaysPersister = nonWorkingDaysPersister;
            return this;
        }

        public ApiBuilder nonWorkingDaysManager(NonWorkingDaysManager nonWorkingDaysManager) {
            this.nonWorkingDaysManager = nonWorkingDaysManager;
            return this;
        }

        public ApiBuilder addDefaultHolidays() {
            this.addDefaultHolidays = true;
            return this;
        }

        public Api build() {
            Api api;
            if (workdaysCalculator == null || nonWorkingDaysPersister == null || nonWorkingDaysManager == null) {
                throw new IllegalStateException("Missing one of the following: workdaysCalculator, nonWorkingDaysPersister, nonWorkingDaysManager");
            }
            else {
                api = new Api(workdaysCalculator, nonWorkingDaysPersister, nonWorkingDaysManager);
            }

            if (addDefaultHolidays) {
                ApiBuilder.addDefaultNonWorkingDays(nonWorkingDaysManager);
            }
            return api;
        }

        public static void addDefaultNonWorkingDays(NonWorkingDaysManager nonWorkingDaysManager) {
            //default configuration
            nonWorkingDaysManager.add(new Day("1 0 0 ? * SAT,SUN *", "Weekend"));
            nonWorkingDaysManager.add(new Day("1 0 0 31 12 ? *", "New Year's eve"));
            nonWorkingDaysManager.add(new Day("1 0 0 1 1 ? *", "New Year"));
            nonWorkingDaysManager.add(new Day("1 0 0 25 12 ? *", "Christmas"));
            nonWorkingDaysManager.add(new Day("1 0 0 04 07 ? *", "Independence Day"));
        }

        public String toString() {
            return "Api.ApiBuilder(workdaysCalculator=" + this.workdaysCalculator + ", nonWorkingDaysPersister=" + this.nonWorkingDaysPersister + ", nonWorkingDaysManager=" + this.nonWorkingDaysManager + ")";
        }
    }
}
