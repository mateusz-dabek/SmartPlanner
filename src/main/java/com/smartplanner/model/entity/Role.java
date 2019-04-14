package com.smartplanner.model.entity;

import com.smartplanner.model.security.RoleName;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    @Enumerated(EnumType.STRING)
    private RoleName name;
}
