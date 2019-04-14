package com.smartplanner.model.dto;

import com.smartplanner.model.entity.OptimizedActivity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlanDto {

    private int id;

    private String name;

    private int daysInCycle;

    private int maxCommutesPerDay;

    private OptimizedActivity optimizedActivity;

    private List<List<Integer>> timeDistanceInMinutes;
}
