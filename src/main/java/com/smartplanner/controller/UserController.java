package com.smartplanner.controller;

import com.smartplanner.exception.ResourceNotFoundException;
import com.smartplanner.model.dto.PlanOutputDto;
import com.smartplanner.model.dto.UserDto;
import com.smartplanner.model.entity.Plan;
import com.smartplanner.model.entity.User;
import com.smartplanner.service.PlanService;
import com.smartplanner.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    private final UserService userService;
    private final PlanService planService;
    private ModelMapper modelMapper;

    @Autowired
    public UserController(UserService userService, PlanService planService) {
        this(userService, planService, new ModelMapper());
    }

    public UserController(
            UserService userService,
            PlanService planService,
            ModelMapper modelMapper
    ) {
        this.userService = userService;
        this.planService = planService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/signup")
    public UserDto createUser(@RequestBody UserDto userDto) {
        UserDto newUserDto = modelMapper.map(userService.saveUser(userDto), UserDto.class);

        return newUserDto;
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public UserDto getUserById(@PathVariable(value = "id") int id) throws ResourceNotFoundException {
        if (!userService.findUserById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }

        UserDto userDto = modelMapper.map(userService.getUserById(id), UserDto.class);

        return userDto;
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDto> getAllUsers() {
        List<User> users = userService.findAll();
        List<UserDto> usersDto = new ArrayList<>();

        users.forEach(x -> {
                    UserDto userDto = modelMapper.map(x, UserDto.class);
                    usersDto.add(userDto);
                }
        );

        return usersDto;
    }

    @GetMapping("/plans")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<PlanOutputDto> getAllUserPlans() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();

        List<Plan> plans = userService.findByUsername(username).getPlans();
        List<PlanOutputDto> planOutputsDto = new ArrayList<>();

        plans.forEach(x -> {
                    PlanOutputDto planOutputDto = modelMapper.map(x, PlanOutputDto.class);
                    planOutputsDto.add(planOutputDto);
                }
        );

        return planOutputsDto;
    }
}
