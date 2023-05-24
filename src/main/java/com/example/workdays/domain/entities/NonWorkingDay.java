package com.example.workdays.domain.entities;


import com.cronutils.model.CronType;
import com.cronutils.validation.Cron;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.apache.bval.constraints.NotEmpty;


@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Value
public class NonWorkingDay {

    @EqualsAndHashCode.Include()
    @Cron(type = CronType.QUARTZ)
    String expression;


    @NotEmpty(message = "Name cannot be blank")
    String name;

}
