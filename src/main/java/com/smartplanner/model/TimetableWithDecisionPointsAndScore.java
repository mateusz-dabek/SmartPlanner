package com.smartplanner.model;

import java.util.ArrayList;

/**
 * Holder of information about timetable. It stores decision points and amount of minutes spent on optimized activity.
 */
public class TimetableWithDecisionPointsAndScore {
    private int minutesSpentAtOptimizedActivity;
    private ArrayList<TimetableEntry> optimalTimetable;
    private ArrayList<ArrayList<Boolean>> optimalDecisionPoints;

    public TimetableWithDecisionPointsAndScore(int minutesSpentAtOptimizedActivity, ArrayList<TimetableEntry> optimalTimetable,
                                               ArrayList<ArrayList<Boolean>> optimalDecisionPoints) {
        this.minutesSpentAtOptimizedActivity = minutesSpentAtOptimizedActivity;
        this.optimalTimetable = optimalTimetable;
        this.optimalDecisionPoints = optimalDecisionPoints;
    }

    public int getMinutesSpentAtOptimizedActivity() {
        return minutesSpentAtOptimizedActivity;
    }

    public ArrayList<TimetableEntry> getOptimalTimetable() {
        return optimalTimetable;
    }

    /**
     * Calculates optimal timetable that will maximize amount of time spent on optimized activity.
     *
     * @return an array with decision points. First dimension is responsible for the day in cycle.
     * Second dimension is responsible for decision point. e.g. <br>
     * optimalDecisionPoints.get(0).get(0) answers question: <br>
     *      Should I go to work in day 0 before first activity? <br>
     * optimalDecisionPoints.get(0).get(1) answers question: <br>
     *      Should I go to work in day 0 after first activity? <br>
     *  ... <br>
     * optimalDecisionPoints.get(0).get(n) [n = last index in array] answers question: <br>
     *      Should I go to work in day 0 after last activity?
     */
    public ArrayList<ArrayList<Boolean>> getOptimalDecisionPoints() {
        return optimalDecisionPoints;
    }
}
