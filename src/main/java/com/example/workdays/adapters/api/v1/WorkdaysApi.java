package com.example.workdays.adapters.api.v1;

import com.example.workdays.adapters.api.v1.entities.Day;
import com.example.workdays.configuration.NoSpringConfiguration;
import lombok.NoArgsConstructor;

public class WorkdaysApi {

    private final WorkdaysCalculator workdaysCalculator;
    private final NonWorkingDaysPersister nonWorkingDaysPersister;
    private final NonWorkingDaysManager nonWorkingDaysManager;

    private WorkdaysApi(WorkdaysCalculator workdaysCalculator, NonWorkingDaysPersister nonWorkingDaysPersister, NonWorkingDaysManager nonWorkingDaysManager) {
        this.workdaysCalculator = workdaysCalculator;
        this.nonWorkingDaysPersister = nonWorkingDaysPersister;
        this.nonWorkingDaysManager = nonWorkingDaysManager;
    }

    public static WorkdaysApiBuilder builder() {
        return new WorkdaysApiBuilder();
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
    public static class WorkdaysApiBuilder {
        private WorkdaysCalculator workdaysCalculator;
        private NonWorkingDaysPersister nonWorkingDaysPersister;
        private NonWorkingDaysManager nonWorkingDaysManager;
        private boolean addDefaultHolidays = false;
        private String pathToRepositoryFile;

        public WorkdaysApiBuilder preconfiguredPathToRepositoryFile(String pathToRepositoryFile) {
            this.pathToRepositoryFile = pathToRepositoryFile;
            return this;
        }

        public WorkdaysApi buildPreconfigured() {
            WorkdaysApi workdaysApi;
            if(pathToRepositoryFile != null) {
                workdaysApi = NoSpringConfiguration.buildDefault(pathToRepositoryFile);
            }  else {
                workdaysApi = NoSpringConfiguration.buildDefault();
            }
            if (addDefaultHolidays) {
                addDefaultNonWorkingDays(workdaysApi.getNonWorkingDaysManager());
            }
            return workdaysApi;
        }

        public WorkdaysApiBuilder workdaysCalculator(WorkdaysCalculator workdaysCalculator) {
            this.workdaysCalculator = workdaysCalculator;
            return this;
        }

        public WorkdaysApiBuilder nonWorkingDaysPersister(NonWorkingDaysPersister nonWorkingDaysPersister) {
            this.nonWorkingDaysPersister = nonWorkingDaysPersister;
            return this;
        }

        public WorkdaysApiBuilder nonWorkingDaysManager(NonWorkingDaysManager nonWorkingDaysManager) {
            this.nonWorkingDaysManager = nonWorkingDaysManager;
            return this;
        }

        public WorkdaysApiBuilder addDefaultHolidays() {
            this.addDefaultHolidays = true;
            return this;
        }

        public WorkdaysApi build() {
            WorkdaysApi workdaysApi;
            if (workdaysCalculator == null || nonWorkingDaysPersister == null || nonWorkingDaysManager == null) {
                throw new IllegalStateException("Missing one of the following: workdaysCalculator, nonWorkingDaysPersister, nonWorkingDaysManager");
            }
            else {
                workdaysApi = new WorkdaysApi(workdaysCalculator, nonWorkingDaysPersister, nonWorkingDaysManager);
            }

            if (addDefaultHolidays) {
                WorkdaysApiBuilder.addDefaultNonWorkingDays(nonWorkingDaysManager);
            }
            return workdaysApi;
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
