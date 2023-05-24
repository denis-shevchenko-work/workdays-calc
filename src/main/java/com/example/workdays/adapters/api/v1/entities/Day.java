package com.example.workdays.adapters.api.v1.entities;

import lombok.Value;
import org.apache.bval.constraints.NotEmpty;


@Value
public class Day {
    @NotEmpty
    String expression;

    @NotEmpty
    String name;

}
