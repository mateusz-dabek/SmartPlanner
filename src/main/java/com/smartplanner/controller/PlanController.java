package com.smartplanner.controller;

import com.smartplanner.exception.InvalidDataProvidedException;
import com.smartplanner.exception.ResourceNotFoundException;
import com.smartplanner.model.dto.PlanInputDto;
import com.smartplanner.model.dto.PlanOutputDto;
import com.smartplanner.model.entity.Plan;
import com.smartplanner.service.PlanService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/plans")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PlanController {

    private final PlanService planService;
    private ModelMapper modelMapper;

    @Autowired
    public PlanController(PlanService planService) {
        this(planService, new ModelMapper());
    }

    public PlanController(PlanService planService, ModelMapper modelMapper) {
        this.planService = planService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public PlanOutputDto getPlanById(@PathVariable(value = "id") int id) throws ResourceNotFoundException {
        if (!planService.findPlanById(id)) {
            throw new ResourceNotFoundException("Plan", "id", id);
        }

        SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Plan plan = planService.getPlanById(id);
        PlanOutputDto planOutputDto = modelMapper.map(plan, PlanOutputDto.class);

        return planOutputDto;
    }

    @PostMapping()
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public PlanOutputDto createPlan(@RequestBody PlanInputDto planInputDto) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();

        try {
            return planService.generateOptimalPlan(planInputDto, username);
        } catch (InvalidDataProvidedException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
