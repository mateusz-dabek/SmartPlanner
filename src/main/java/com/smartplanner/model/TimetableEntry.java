package com.smartplanner.model;

import com.smartplanner.model.entity.Lesson;
import com.smartplanner.model.entity.Term;

/**
 * Binder of lesson and picked term. Represents a particular entry in timetable.
 */
public class TimetableEntry {
    private Lesson lesson;
    private Term pickedTerm;

    /**
     * Creates an object that binds lesson with particular term
     *
     * @param lesson the lesson for which one wants to specify a term
     * @param pickedTerm particular term
     */
    public TimetableEntry(Lesson lesson, Term pickedTerm) {
        this.lesson = lesson;
        this.pickedTerm = pickedTerm;
    }

    /**
     * Returns term stored in object
     *
     * @return term picked for the lesson
     */
    public Term getTerm() {
        return pickedTerm;
    }

    /**
     * Returns name of the lesson stored in object
     *
     * @return name of the lesson
     */
    public String getName() {
        return lesson.getName();
    }

    /**
     * Returns lesson stored in object
     *
     * @return Lesson object for which the term is bounded
     */
    public Lesson getLesson() {
        return lesson;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof TimetableEntry))
            return false;
        if (other == this)
            return true;

        TimetableEntry otherTimetableEntry = (TimetableEntry) other;
        return otherTimetableEntry.pickedTerm.equals(this.pickedTerm);
    }
}
