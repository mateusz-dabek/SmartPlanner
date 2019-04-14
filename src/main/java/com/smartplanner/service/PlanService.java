package com.smartplanner.service;

import com.smartplanner.model.dto.PlanInputDto;
import com.smartplanner.model.dto.PlanOutputDto;
import com.smartplanner.model.entity.Lesson;
import com.smartplanner.model.entity.Plan;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PlanService {

    boolean findPlanById(int id);

    Plan getPlanById(int id);

    Plan savePlan(Plan plan);

    List<Lesson> pickOptimalTerm(PlanInputDto planInputDto);

    PlanOutputDto generateOptimalPlan(PlanInputDto planInputDto, String username);
}
