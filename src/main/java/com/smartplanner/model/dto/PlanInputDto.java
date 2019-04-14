package com.smartplanner.model.dto;

import com.smartplanner.model.LessonWithPossibleTerms;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlanInputDto extends PlanDto {

    private List<LessonWithPossibleTerms> lessons;
}
