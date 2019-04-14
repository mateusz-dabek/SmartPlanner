package com.smartplanner.model;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generator for decision points (that tells us if we should go to work)
 *
 * GoToOptimizedActivityDecider can't be reused, what means that you have to create new object each time you want to get all of combinations.
 * After getNext() returns false, the object is useless.
 */
public class GoToOptimizedActivityDecider {
    private ArrayList<TimetableEntry> singleDayTimetable;
    private ArrayList<AtomicInteger> currentCombination;
    private boolean hasFinished;

    /**
     * Creates decider based on single day timetable
     *
     * @param singleDayTimetable a timetable schedule for particular day
     */
    public GoToOptimizedActivityDecider(ArrayList<TimetableEntry> singleDayTimetable) {
        this.singleDayTimetable = singleDayTimetable;
        this.hasFinished = false;

        int numberOfDecisionPoints = singleDayTimetable.size() + 1;
        this.currentCombination = new ArrayList<AtomicInteger>(numberOfDecisionPoints);
        for (int i = 0; i < numberOfDecisionPoints; ++i)
            currentCombination.add(new AtomicInteger(0));
    }

    /**
     * Creates decider based on complete timetable. Since the decider needs a timetable for single day, one have to provide
     * number of the day in cycle as a second argument
     *
     * @param completeTimetable a complete timetable
     * @param getDecisionsForCycleNumberDay specifies a particular day for computations, based on this argument getNext() method will
     *                                      return decisions for the day passed in this argument
     */
    public GoToOptimizedActivityDecider(ArrayList<TimetableEntry> completeTimetable, int getDecisionsForCycleNumberDay) {
        ArrayList<TimetableEntry> singleDayTimetable = new ArrayList<TimetableEntry>();

        for (TimetableEntry entry : completeTimetable)
            if (entry.getTerm().getCycleDayNumber() == getDecisionsForCycleNumberDay)
                singleDayTimetable.add(entry);

        this.singleDayTimetable = singleDayTimetable;
        this.hasFinished = false;

        int numberOfDecisionPoints = singleDayTimetable.size() + 1;
        this.currentCombination = new ArrayList<AtomicInteger>(numberOfDecisionPoints);
        for (int i = 0; i < numberOfDecisionPoints; ++i)
            currentCombination.add(new AtomicInteger(0));
    }

    /**
     * Returns next combination of decisions
     *
     * @return ArrayList of decision points generated using brute force method. returnedTable.get(n) equal to true
     *          means that after n-th lesson we should go to optimized activity
     */
    public ArrayList<Boolean> getNext() {
        ArrayList<Boolean> returnedCombination = castToBooleanArrayList(currentCombination);

        shiftIndexesToNextSet();
        return returnedCombination;
    }

    private ArrayList<Boolean> castToBooleanArrayList(ArrayList<AtomicInteger> currentCombination) {
        ArrayList<Boolean> casted = new ArrayList<Boolean>(currentCombination.size());

        for (int i = 0; i < currentCombination.size(); ++i)
            casted.add(currentCombination.get(i).get() == 1);

        return casted;
    }

    /**
     * Checks if there is next combination available.
     *
     * @return true if there is another combination from brute force method, false else
     */
    public boolean isNext() {
        return !hasFinished;
    }

    private void shiftIndexesToNextSet() {
        currentCombination.get(0).incrementAndGet();

        for (int i = 0; i + 1 < currentCombination.size(); ++i) {
            if (currentCombination.get(i).get() > 1) {
                currentCombination.get(i).getAndSet(0);
                currentCombination.get(i + 1).getAndIncrement();
            }
        }

        int lastIndex = currentCombination.size() - 1;
        if (currentCombination.get(lastIndex).get() > 1)
            hasFinished = true;
    }
}
