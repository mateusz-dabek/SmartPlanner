package com.smartplanner.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "time_distance_first_dimension")
public class TimeDistanceFirstDimension {

    @Id
    @Column(name = "time_distance_first_dimension_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ElementCollection(targetClass = Integer.class)
    private List<Integer> timeDistanceSecondDimension;
}
