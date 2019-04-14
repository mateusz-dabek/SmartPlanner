package com.smartplanner.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "lesson")
public class Lesson {

    @Id
    @Column(name = "lesson_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(targetEntity = Plan.class)
    @JoinColumn(name = "plan_id")
    @JsonIgnore
    private Plan plan;

    @Column(name = "lesson_name", length = 100)
    private String name;

    @Column(name = "repeating_period")
    private int repeatingPeriod;

    @OneToOne(mappedBy = "lesson", cascade = CascadeType.ALL)
    private Term term;
}
