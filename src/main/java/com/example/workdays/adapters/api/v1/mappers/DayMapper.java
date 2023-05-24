package com.example.workdays.adapters.api.v1.mappers;

import com.example.workdays.adapters.api.v1.entities.Day;
import com.example.workdays.domain.entities.NonWorkingDay;
import org.mapstruct.Mapper;

import java.util.Collection;

@Mapper
public interface DayMapper {
    Day toDto(NonWorkingDay day);

    NonWorkingDay fromDto(Day dto);

    Collection<Day> toDto(Collection<NonWorkingDay> days);

}
