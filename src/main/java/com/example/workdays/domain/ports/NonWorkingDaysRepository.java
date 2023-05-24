package com.example.workdays.domain.ports;

import com.example.workdays.domain.entities.NonWorkingDay;

import java.util.Collection;

public interface NonWorkingDaysRepository {
    Collection<NonWorkingDay> getAll();

    void add(NonWorkingDay day);

    void remove(NonWorkingDay day);

    void addAll(Collection<NonWorkingDay> load);
}
