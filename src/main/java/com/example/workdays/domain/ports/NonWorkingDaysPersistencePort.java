package com.example.workdays.domain.ports;

import com.example.workdays.domain.entities.NonWorkingDay;

import java.util.Collection;

public interface NonWorkingDaysPersistencePort {
    void save(Collection<NonWorkingDay> days);

    Collection<NonWorkingDay> load();
}
