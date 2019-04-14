package com.smartplanner.model.dto;

import com.smartplanner.model.entity.Lesson;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlanOutputDto extends PlanDto {

    private List<Lesson> lessons;
}
