# Task

Please implement a maven library using the Java programming language. The library should have the following:
1. A method to calculate the number of workdays between two given dates (inclusive)
2. A method to set/add holidays and other non-working days
3. Ability to read/write holidays from/to a local JSON file (keep in mind that later we may want to
   store the holidays somewhere else, for example a database)
   
The completed assignment should be sent back in a zip file (please include pom and java files only).

## Example
Given:
1. July 1st, 2022 is a holiday
2. July 2nd, 2022 is a weekend
3. July 3rd, 2022 is a weekend

The number of workdays between June 27, 2022 and July 4th, 2022 is 5.

Evaluation points:
1. Object oriented design
2. Code maintainability
3. Unit tests


## Solution
There are several types of Non-working days. 
Some of them have a periodic nature, like weekends, and some of them are fixed, like holidays or personal day-offs.
So Non-working days can be represented as cron expressions.

For example, weekends can be represented as `0 0 0 ? * SAT,SUN *` and holidays as `1 0 0 2 2 ? 2023`.

Each Non-working day has unique expression and described by its name.
To calculate the number of workdays between two given dates (inclusive) we need to know the number of non-working days between these dates.

So Number of working days is calculated as follows:
* Working days = Total days - Non-working days;

It is assumed that number of working days is requested for local date-time zone.
The number of non-working days is calculated as follows:
* for each non-working day in the set of non-working days we check if there are matches between two given dates (inclusive) and if it is we increment the number of non-working days.

## Implementation
The solution is implemented as a maven library using the Java programming language.
One of the way to implement it is to use [cronutils](http://cron-parser.com/ ).
It has a lot of features and can be used to parse cron expressions and calculate the number of matches in range.


From architectural point of view it is possible to define following use cases:
1. [CalculateWorkDaysUseCase](src%2Fmain%2Fjava%2Fcom%2Fexample%2Fworkdays%2Fdomain%2Fusecases%2Fcalculate%2FCalculateWorkDaysUseCase.java) to calculate the number of workdays 
2. [ManageNonWorkingDaysUseCase](src%2Fmain%2Fjava%2Fcom%2Fexample%2Fworkdays%2Fdomain%2Fusecases%2Fmanage%2FManageNonWorkingDaysUseCase.java) to set/add non-working days
3. [ExportNonWorkingDaysUseCase](src%2Fmain%2Fjava%2Fcom%2Fexample%2Fworkdays%2Fdomain%2Fusecases%2Fpersistence%2FExportNonWorkingDaysUseCase.java) to persist non-working days
4. [ImportNonWorkingDaysUseCase](src%2Fmain%2Fjava%2Fcom%2Fexample%2Fworkdays%2Fdomain%2Fusecases%2Fpersistence%2FImportNonWorkingDaysUseCase.java) to load non-working days

All the use cases are defined in the [usecases](src%2Fmain%2Fjava%2Fcom%2Fexample%2Fworkdays%2Fdomain%2Fusecases) package.

Following API primary driving adapters defined in [workdaysApi.v1](src%2Fmain%2Fjava%2Fcom%2Fexample%2Fworkdays%2Fadapters%2Fapi%2Fv1) package:
* [WorkdaysCalculator](src%2Fmain%2Fjava%2Fcom%2Fexample%2Fworkdays%2Fadapters%2Fapi%2Fv1%2FWorkdaysCalculator.java) - to calculate the number of workdays
  * int workdaysBetweenInclusive(LocalDate from, LocalDate till)
  * int workdaysBetweenInclusive(String from, String till)
* [NonWorkingDaysManager](src%2Fmain%2Fjava%2Fcom%2Fexample%2Fworkdays%2Fadapters%2Fapi%2Fv1%2FNonWorkingDaysManager.java) - to set/add non-working days
  * Collection<Day> getAll()
  * add(Day day)
  * remove(Day day)
* [NonWorkingDaysPersister](src%2Fmain%2Fjava%2Fcom%2Fexample%2Fworkdays%2Fadapters%2Fapi%2Fv1%2FNonWorkingDaysPersister.java) - to import/export non-working days
  * loadData()
  * saveData()

Adaptors are coming with corresponding entities and mappers to prevent coupling domain with the outside world

Following secondary driven gateway adapters defined in [gateways](src%2Fmain%2Fjava%2Fcom%2Fexample%2Fworkdays%2Fadapters%2Fgateways) package:
* [NonWorkingDayInMemoryRepository](src%2Fmain%2Fjava%2Fcom%2Fexample%2Fworkdays%2Fadapters%2Fgateways%2FNonWorkingDayInMemoryRepository.java) - to store non-working days in memory
* [NonWorkingDaysFilePersistenceService](src%2Fmain%2Fjava%2Fcom%2Fexample%2Fworkdays%2Fadapters%2Fgateways%2FNonWorkingDaysFilePersistenceService.java) - to persist non-working days in local files

Library is configured with [Spring](https://spring.io/) and [Spring Boot](https://spring.io/projects/spring-boot) .
Alternatively there is a manual non-spring configuration. 

## Usage
To use the library you need to add it as a dependency to your project.
For example, if you are using maven you need to add following dependency to your pom.xml:
```xml
<dependency>
<groupId>com.example</groupId>
<artifactId>workdays-calc</artifactId>
<version>0.0.1-SNAPSHOT</version>
</dependency>
```

Persistence is configured to use local files.
Path to repository is defined in [application.properties](src%2Fmain%2Fresources%2Fapplication.properties)
```properties
workday.gateway.persistence.file.pathToRepository=~/.workdays/repository.json
```

Spring configuration is done automatically by Spring Boot.
Alternatively you can define Bean manually.
```java
    @Bean
    public WorkdaysApi workdaysApi() {
        return WorkdaysApi.builder()
            .addDefaultHolidays()
            .buildPreconfigured();
    }
```
or customize it
```java
    @Bean
    public WorkdaysApi workdaysApi(
            WorkdaysCalculator workdaysCalculator,
            NonWorkingDaysPersister nonWorkingDaysPersister,
            NonWorkingDaysManager nonWorkingDaysManager) {
        return WorkdaysApi.builder()
                .nonWorkingDaysManager(nonWorkingDaysManager)
                .nonWorkingDaysPersister(nonWorkingDaysPersister)
                .workdaysCalculator(workdaysCalculator)
                .build();
    }
```
It is also possible to use library without Spring
```java
    WorkdaysApi workdaysApi = WorkdaysApi.builder().addDefaultHolidays() .buildPreconfigured();
```
```java
    WorkdaysApi workdaysApi = WorkdaysApi.builder().addDefaultHolidays().preconfiguredPathToRepositoryFile("~/.workdays/repository.json").buildPreconfigured();
```
Anyway after that you can use it as follows:
```java
    int workdays = workdaysApi.workdaysCalculator().workdaysBetweenInclusive("2022-06-27", "2022-07-04");
```
To add non-working day:
```java
    workdaysApi.nonWorkingDaysManager().add(new Day.("1 0 0 2 2 ? 2023", "New Year"));
```
To remove non-working day:
```java
    workdaysApi.nonWorkingDaysManager().remove(new Day.("1 0 0 2 2 ? 2023", "New Year"));
```
To persist non-working days:
```java
    workdaysApi.nonWorkingDaysPersister().saveData();
```

## Possible improvements

Please note that Day DTO's expresion starts with 1 second of a new day.  
It is possible to fulfill it with more convenient builder accepting  
dayOfMonth or dayOfWeek and month and year fields or cron expression without time part.
