package com.example.workdays.adapters.api.v1;

import com.example.workdays.domain.usecases.calculate.CalculateWorkDaysUseCase;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
public class WorkdaysCalculator {
    private final CalculateWorkDaysUseCase calculateWorkDaysUseCase;

    public int workdaysBetweenInclusive(LocalDate from, LocalDate till) {
        return calculateWorkDaysUseCase.workdaysBetweenInclusive(from, till);
    }

    public int workdaysBetweenInclusive(String from, String till) {
        LocalDate fromDate = LocalDate.parse(from);
        LocalDate tillDate = LocalDate.parse(till);
        return workdaysBetweenInclusive(fromDate, tillDate);
    }

}
