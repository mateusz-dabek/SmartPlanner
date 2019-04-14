package com.smartplanner.service.implementation;

import com.smartplanner.exception.InvalidDataProvidedException;
import com.smartplanner.model.SmartPlanner;
import com.smartplanner.model.TimeDistanceManager;
import com.smartplanner.model.TimetableEntry;
import com.smartplanner.model.TimetableWithDecisionPointsAndScore;
import com.smartplanner.model.dto.PlanInputDto;
import com.smartplanner.model.dto.PlanOutputDto;
import com.smartplanner.model.entity.Lesson;
import com.smartplanner.model.entity.OptimizedActivity;
import com.smartplanner.model.entity.Plan;
import com.smartplanner.model.entity.Term;
import com.smartplanner.repository.PlanRepository;
import com.smartplanner.repository.UserRepository;
import com.smartplanner.service.PlanService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private ModelMapper modelMapper;

    @Autowired
    public PlanServiceImpl(PlanRepository planRepository, UserRepository userRepository) {
        this(planRepository, userRepository, new ModelMapper());
    }

    public PlanServiceImpl(
            PlanRepository planRepository,
            UserRepository userRepository,
            ModelMapper modelMapper
    ) {
        this.planRepository = planRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public boolean findPlanById(int id) {
        return planRepository.existsById(id);
    }

    public Plan getPlanById(int id) {
        return planRepository.getOne(id);
    }

    @Override
    public Plan savePlan(Plan plan) {

        OptimizedActivity optimizedActivity = plan.getOptimizedActivity();
        optimizedActivity.setPlan(plan);
        plan.setOptimizedActivity(optimizedActivity);

        List<Lesson> lessons = plan.getLessons();
        lessons.forEach(x -> {
                    x.setPlan(plan);

                    Term term = x.getTerm();
                    term.setLesson(x);
                    x.setTerm(term);
                }
        );
        plan.setLessons(lessons);

        return planRepository.save(plan);
    }

    @Override
    public PlanOutputDto generateOptimalPlan(PlanInputDto planInputDto, String username) {
        Plan plan = modelMapper.map(planInputDto, Plan.class);
        plan.setLessons(new ArrayList<>());
        plan.setLessons(this.pickOptimalTerm(planInputDto));

        plan.setUser(userRepository.findByUsername(username));
        plan = this.savePlan(plan);

        PlanOutputDto planOutputDto = modelMapper.map(plan, PlanOutputDto.class);

        return planOutputDto;
    }

    @Override
    public List<Lesson> pickOptimalTerm(PlanInputDto planInputDto) {
        SmartPlanner smartPlanner = new SmartPlanner(
                planInputDto.getLessons(),
                planInputDto.getDaysInCycle(),
                new TimeDistanceManager(planInputDto.getTimeDistanceInMinutes()),
                planInputDto.getMaxCommutesPerDay(),
                planInputDto.getOptimizedActivity()
        );

        TimetableWithDecisionPointsAndScore timetable = smartPlanner.getOptimalPlan();

        List<TimetableEntry> timetableEntries = timetable.getOptimalTimetable();

        if (timetableEntries == null) {
            throw new InvalidDataProvidedException(
                    "Activities are either unreachable or they overlap with each other");
        }

        List<Lesson> lessons = new ArrayList<>();
        timetableEntries.forEach(x -> {
                    Lesson lesson = new Lesson();
                    lesson.setName(x.getName());
                    lesson.setRepeatingPeriod(x.getLesson().getRepeatingPeriod());
                    lesson.setTerm(x.getTerm());
                    lessons.add(lesson);
                }
        );

        return lessons;
    }
}
