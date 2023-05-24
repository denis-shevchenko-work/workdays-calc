package com.example.workdays.domain.usecases.manage;

import com.example.workdays.domain.entities.NonWorkingDay;
import com.example.workdays.domain.ports.NonWorkingDaysRepository;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor
public class NonWorkingDaysManagingService implements ManageNonWorkingDaysUseCase {

    private final NonWorkingDaysRepository nonWorkingDaysRepository;

    @Override
    public Collection<NonWorkingDay> getAllNonWorkingDays() {
        return nonWorkingDaysRepository.getAll();
    }

    @Override
    public void addNonWorkingDay(NonWorkingDay day) {
        nonWorkingDaysRepository.add(day);
    }

    @Override
    public void removeNonWorkingDay(NonWorkingDay day) {
        nonWorkingDaysRepository.remove(day);
    }

}
