package com.example.workdays.domain.usecases.calculate;

import com.cronutils.model.Cron;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.example.workdays.domain.ports.NonWorkingDaysRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static com.cronutils.model.CronType.QUARTZ;
import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME;


@RequiredArgsConstructor
public class WorkdaysCalculatorService implements CalculateWorkDaysUseCase {

    public static final int INCLUSIVE_ADJUSTMENT = 1;
    private final NonWorkingDaysRepository nonWorkingDaysRepository;

    @Override
    public int workdaysBetweenInclusive(LocalDate from, LocalDate till) {
        ZoneId zoneId = ZoneId.of("UTC");
        ZonedDateTime zonedFrom = ZonedDateTime.of(from, LocalTime.MIN, zoneId);
        ZonedDateTime zonedTill = ZonedDateTime.of(till, LocalTime.MAX, zoneId);
        return workdaysBetweenInclusive(zonedFrom, zonedTill);
    }

    private int workdaysBetweenInclusive(ZonedDateTime from, ZonedDateTime till) {
        if (till.isBefore(from)) {
            throw new IllegalArgumentException(
                    String.format("Till date %s should not be before from %s",
                            till.format(ISO_ZONED_DATE_TIME),
                            from.format(ISO_ZONED_DATE_TIME)));
        }

        final CronParser quartzCronParser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(QUARTZ));

        int nonWorkingDaysCount = nonWorkingDaysRepository.getAll().stream()
                .flatMap(nonWorkingDay -> {
                    String expression = nonWorkingDay.getExpression();
                    Cron parsedCron = quartzCronParser.parse(expression);
                    return ExecutionTime.forCron(parsedCron).getExecutionDates(from, till).stream();
                })
                .distinct()
                .mapToInt(date -> 1)
                .sum();

        int allDaysCountInclusive = (int) from.until(till, ChronoUnit.DAYS) + INCLUSIVE_ADJUSTMENT;

        return allDaysCountInclusive - nonWorkingDaysCount;
    }

}
