package com.example.workdays.domain.usecases.manage;

import com.example.workdays.domain.entities.NonWorkingDay;

import java.util.Collection;

public interface ManageNonWorkingDaysUseCase {
    Collection<NonWorkingDay> getAllNonWorkingDays();

    void addNonWorkingDay(NonWorkingDay day);

    void removeNonWorkingDay(NonWorkingDay day);
}
