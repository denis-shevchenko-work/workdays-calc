package com.example.workdays.domain.usecases.calculate;


import java.time.LocalDate;

public interface CalculateWorkDaysUseCase {
    int workdaysBetweenInclusive(LocalDate from, LocalDate till);
}
