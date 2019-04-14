package com.smartplanner.model;

import com.smartplanner.model.entity.OptimizedActivity;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Calculator for time spent on optimized activity.
 */
public class OptimalityCalculator {
    private TimeDistanceManager timeDistanceManager;
    private int maxCommutesPerDay;
    private int minTimeSpentOnOptimizedAtOnceInMinutes;
    private int numberOfDaysInCycle;
    private OptimizedActivity optimizedActivity;

    /**
     * Creates OptimalityCalculator.
     *
     * @param timeDistanceManager object that contains commute matrix, which is data about travel time between each lesson and work
     * @param maxCommutesPerDay maximal amount of commutes to work per day (specified by a user)
     * @param minTimeSpentOnOptimizedAtOnceInMinutes minimal amount of time in session that user wants to spent on
     *                                               optimized activity if he decides to start it
     * @param numberOfDaysInCycle number of days after which the plan will repeat
     * @param optimizedActivity an object that contains data about optimized activity
     */
    public OptimalityCalculator(TimeDistanceManager timeDistanceManager,
                                int maxCommutesPerDay,
                                int minTimeSpentOnOptimizedAtOnceInMinutes,
                                int numberOfDaysInCycle,
                                OptimizedActivity optimizedActivity) {
        this.optimizedActivity = optimizedActivity;
        this.timeDistanceManager = timeDistanceManager;
        this.maxCommutesPerDay = maxCommutesPerDay;
        this.minTimeSpentOnOptimizedAtOnceInMinutes = minTimeSpentOnOptimizedAtOnceInMinutes;
        this.numberOfDaysInCycle = numberOfDaysInCycle;
    }

    /**
     * Calculates amount of time (in minutes) spent on optimized activity for provided timetable.
     *
     * @param timetable complete timetable that first should pass the validation done by TimetableValidator
     * @return amount of minutes spent on optimized activity based on provided timetable
     */
    public TimetableWithDecisionPointsAndScore calculate(ArrayList<TimetableEntry> timetable) {
        ArrayList<ArrayList<Boolean>> optimalDecisionPoints = new ArrayList<ArrayList<Boolean>>(numberOfDaysInCycle);
        int timeSpentInWorkInCycle = 0;

        for (int cycleDayNumber = 0; cycleDayNumber < numberOfDaysInCycle; ++cycleDayNumber) {

            if (optimizedActivity.isOpenedInDay(cycleDayNumber) == false) {
                optimalDecisionPoints.add(new ArrayList<Boolean>(Arrays.asList(false)));
                continue;
            }

            GoToOptimizedActivityDecider decider = new GoToOptimizedActivityDecider(timetable, cycleDayNumber);
            int maxForCurrentDay = 0;
            ArrayList<Boolean> bestDecisionPoints = null;

            while (decider.isNext()) {
                ArrayList<Boolean> currDecisionPoints = decider.getNext();
                int currentVal = calculateRegardingProvidedDecisionPoints(timetable, currDecisionPoints, cycleDayNumber);

                if (currentVal > maxForCurrentDay) {
                    bestDecisionPoints = currDecisionPoints;
                    maxForCurrentDay = currentVal;
                }
            }

            if (maxForCurrentDay > optimizedActivity.getMaxTimeInMinutes())
                maxForCurrentDay = optimizedActivity.getMaxTimeInMinutes();

            timeSpentInWorkInCycle += maxForCurrentDay;
            optimalDecisionPoints.add(bestDecisionPoints);
        }

        return new TimetableWithDecisionPointsAndScore(timeSpentInWorkInCycle, timetable, optimalDecisionPoints);
    }

    private int getNumberOfCommutesToWork(ArrayList<Boolean> decisionPoints) {
        int numberOfOnes = 0;
        for (Boolean decision : decisionPoints)
            if (decision == true)
                ++numberOfOnes;

        return numberOfOnes;
    }

    private int calculateRegardingProvidedDecisionPoints(ArrayList<TimetableEntry> timetable,
                                                         ArrayList<Boolean> currDecisionPoints,
                                                         int cycleDayNumber) {
        if (getNumberOfCommutesToWork(currDecisionPoints) > maxCommutesPerDay)
            return 0;

        int timeSpentInWork = 0;
        ArrayList<TimetableEntry> specifiedDayTimetable = extractEntriesForSpecifiedDay(timetable, cycleDayNumber);
        if (specifiedDayTimetable.size() == 0) //there are no lessons that day
            return calculateMinutesBetweenTwoTimePoints(optimizedActivity.getStartsAt(), optimizedActivity.getEndsAt());

        if (currDecisionPoints.get(0) == true) {
            int timeBetweenOptimizedActivityOpenAndFirstActivityStart = calculateMinutesBetweenTwoTimePoints(
                    optimizedActivity.getStartsAt(), specifiedDayTimetable.get(0).getTerm().getStartTime())
                    - timeDistanceManager.getTimeDistanceInMinutes(specifiedDayTimetable.get(0).getLesson(), optimizedActivity);
            int minutesSpentInWorkForCurrDecisionPoint = timeBetweenOptimizedActivityOpenAndFirstActivityStart;

            if (minutesSpentInWorkForCurrDecisionPoint >= minTimeSpentOnOptimizedAtOnceInMinutes)
                timeSpentInWork += minutesSpentInWorkForCurrDecisionPoint;
        }

        for (int i = 1; i < currDecisionPoints.size() - 1; ++i) {
            if (currDecisionPoints.get(i) == true) {
                int timeBetweenTwoActivities = calculateMinutesBetweenTwoTimePoints(specifiedDayTimetable.get(i).getTerm()
                        .getStartTime(), specifiedDayTimetable.get(i - 1).getTerm().getEndTime());
                int transportTimeFromFirstActToOptimized = timeDistanceManager.getTimeDistanceInMinutes(specifiedDayTimetable
                        .get(i - 1).getLesson(), optimizedActivity);
                int transportTimeFromOptimizedToSecondAct = timeDistanceManager.getTimeDistanceInMinutes(optimizedActivity,
                        specifiedDayTimetable.get(i).getLesson());
                int minutesSpentInWorkForCurrDecisionPoint = timeBetweenTwoActivities
                        - transportTimeFromFirstActToOptimized - transportTimeFromOptimizedToSecondAct;

                if (minutesSpentInWorkForCurrDecisionPoint >= minTimeSpentOnOptimizedAtOnceInMinutes)
                    timeSpentInWork += minutesSpentInWorkForCurrDecisionPoint;
            }
        }

        if (currDecisionPoints.get(currDecisionPoints.size() - 1) == true) {
            int timeBetweenLastActivityEndingAndOptimizedActivityClose = calculateMinutesBetweenTwoTimePoints(specifiedDayTimetable
                    .get(specifiedDayTimetable.size() - 1).getTerm().getEndTime(), optimizedActivity.getEndsAt());
            int travelTimeFromLastActivityToOptimizedActivity = timeDistanceManager.getTimeDistanceInMinutes(specifiedDayTimetable
                    .get(specifiedDayTimetable.size() - 1).getLesson(), optimizedActivity);
            int minutesSpentInWorkForCurrDecisionPoint = timeBetweenLastActivityEndingAndOptimizedActivityClose
                    - travelTimeFromLastActivityToOptimizedActivity;

            if (minutesSpentInWorkForCurrDecisionPoint >= minTimeSpentOnOptimizedAtOnceInMinutes)
                timeSpentInWork += minutesSpentInWorkForCurrDecisionPoint;
        }

        return timeSpentInWork;
    }

    private ArrayList<TimetableEntry> extractEntriesForSpecifiedDay(ArrayList<TimetableEntry> timetable, int cycleDayNumber) {
        ArrayList<TimetableEntry> entriesForSpecifiedDay = new ArrayList<TimetableEntry>();
        for (TimetableEntry entry : timetable)
            if (entry.getTerm().getCycleDayNumber() == cycleDayNumber)
                entriesForSpecifiedDay.add(entry);

        return entriesForSpecifiedDay;
    }

    private int calculateMinutesBetweenTwoTimePoints(LocalTime lhs, LocalTime rhs) {
        Duration duration = Duration.between(lhs, rhs);
        return (int) Math.abs(duration.toMinutes());
    }
}
