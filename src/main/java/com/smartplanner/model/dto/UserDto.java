package com.smartplanner.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartplanner.model.entity.Plan;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDto {

    private int id;

    private String username;

    private String email;

    @JsonProperty(value = "password", access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private List<Plan> plans;
}
