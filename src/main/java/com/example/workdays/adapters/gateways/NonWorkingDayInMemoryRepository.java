package com.example.workdays.adapters.gateways;

import com.example.workdays.domain.entities.NonWorkingDay;
import com.example.workdays.domain.ports.NonWorkingDaysRepository;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;


public class NonWorkingDayInMemoryRepository implements NonWorkingDaysRepository {

    private final Set<NonWorkingDay> nonWorkingDays;

    public NonWorkingDayInMemoryRepository(Collection<NonWorkingDay> nonWorkingDays) {
        Comparator<NonWorkingDay> comparing = Comparator.comparing(NonWorkingDay::getExpression);
        this.nonWorkingDays = new ConcurrentSkipListSet<>(comparing);

        Optional.ofNullable(nonWorkingDays)
                .ifPresent(this.nonWorkingDays::addAll);
    }

    @Override
    public Collection<NonWorkingDay> getAll() {
        return Collections.unmodifiableSet(nonWorkingDays);
    }

    @Override
    public void add(NonWorkingDay day) {
        nonWorkingDays.add(day);
    }

    @Override
    public void addAll(Collection<NonWorkingDay> nonWorkingDays) {
        this.nonWorkingDays.addAll(nonWorkingDays);
    }

    @Override
    public void remove(NonWorkingDay day) {
        nonWorkingDays.remove(day);
    }

}
