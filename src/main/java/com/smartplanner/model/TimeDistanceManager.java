package com.smartplanner.model;

import com.smartplanner.model.entity.Lesson;
import com.smartplanner.model.entity.OptimizedActivity;

import java.util.List;

/**
 * Wrapper for commute matrix, which is a two dimensional array where commuteMatrix.get(id1).get(id2) yields amount
 * of time that is needed to travel from activity with id1 to activity with id2
 */
public class TimeDistanceManager {
    // timeDistance[from ID][to ID]
    private List<List<Integer>> timeDistanceInMinutes;

    /**
     * Creates TimeDistanceManager based on commute matrix which is two dimensional array,
     * where commuteMatrix.get(id1).get(id2) yields amount of time that is needed to travel from activity
     * with id1 to activity with id2
     *
     * @param timeDistanceInMinutes commute matrix where commuteMatrix.get(id1).get(id2) yields amount
     *                              of time that is needed to travel from activity with id1 to activity with id2
     */
    public TimeDistanceManager(List<List<Integer>> timeDistanceInMinutes) {
        this.timeDistanceInMinutes = timeDistanceInMinutes;
    }

    /**
     * Returns time needed to travel between points passed as arguments
     *
     * @param from lesson for which you want to get information about travel time (traveling time from the lesson)
     * @param to lesson for which you want to get information about travel time (traveling time to the lesson)
     * @return amount of time that is needed to travel from lesson "from" to lesson "to"
     */
    public int getTimeDistanceInMinutes(Lesson from, Lesson to) {
        return timeDistanceInMinutes.get(from.getId()).get(to.getId());
    }

    /**
     * Returns time needed to travel between points passed as arguments
     *
     * @param from optimized activity for which you want to get information about travel time (traveling time from the activity)
     * @param to lesson for which you want to get information about travel time (traveling time to the lesson)
     * @return amount of time that is needed to travel from optimized activity "from" to lesson "to"
     */
    public int getTimeDistanceInMinutes(OptimizedActivity from, Lesson to) {
        return timeDistanceInMinutes.get(from.getId()).get(to.getId());
    }

    /**
     * Returns time needed to travel between points passed as arguments
     *
     * @param from lesson for which you want to get information about travel time (traveling time from the lesson)
     * @param to optimized activity for which you want to get information about travel time (traveling time to the activity)
     * @return amount of time that is needed to travel from lesson "from" to optimized activity "to"
     */
    public int getTimeDistanceInMinutes(Lesson from, OptimizedActivity to) {
        return timeDistanceInMinutes.get(from.getId()).get(to.getId());
    }
}
