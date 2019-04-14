package com.smartplanner.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "plan")
public class Plan {

    @Id
    @Column(name = "plan_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "plan_name", length = 100)
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "plan", cascade = CascadeType.ALL)
    private OptimizedActivity optimizedActivity;

    @Column(name = "days_in_cycle")
    private int daysInCycle;

    @Column(name = "max_commutes_per_day")
    private int maxCommutesPerDay;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
    private List<Lesson> lessons;

    @OneToMany
    private List<TimeDistanceFirstDimension> timeDistanceFirstDimension;
}
