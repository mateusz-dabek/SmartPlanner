package com.smartplanner.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "term")
public class Term {

    @Id
    @Column(name = "term_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private int id;

    @OneToOne
    @JoinColumn(name = "lesson_id")
    @MapsId
    @JsonIgnore
    private Lesson lesson;

    @Column(name = "duration_in_minutes")
    private int durationInMinutes;

    @Column(name = "cycle_day_number")
    private int cycleDayNumber;

    @Column(name = "start_time")
    private LocalTime startTime;

    public Term() {
    }

    public Term(int durationInMinutes, int numberOfWeekDay, LocalTime startTime) {
        this.durationInMinutes = durationInMinutes;
        this.cycleDayNumber = numberOfWeekDay;
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        LocalTime endTime = LocalTime.of(startTime.getHour(), startTime.getMinute());
        return endTime.plusMinutes(durationInMinutes);
    }

    public Term(int id,
                Lesson lesson,
                int durationInMinutes,
                int cycleDayNumber,
                LocalTime startTime
    ) {
        this.id = id;
        this.lesson = lesson;
        this.durationInMinutes = durationInMinutes;
        this.cycleDayNumber = cycleDayNumber;
        this.startTime = startTime;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof Term))
            return false;
        if (other == this)
            return true;

        try {
            Term otherTimetableEntry = (Term) other;

            return otherTimetableEntry.durationInMinutes == this.durationInMinutes
                    && otherTimetableEntry.cycleDayNumber == this.cycleDayNumber
                    && otherTimetableEntry.startTime.equals(this.startTime);
        } catch (ClassCastException e) {
            return false;
        }
    }
}
