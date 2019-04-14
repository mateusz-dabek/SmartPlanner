package com.smartplanner.model;

import com.smartplanner.controller.LoggingController;
import com.smartplanner.exception.InvalidDataProvidedException;
import com.smartplanner.model.entity.OptimizedActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Finder of optimal timetable that allows to spent maximum possible time doing
 * optimized activity.
 */
public class SmartPlanner {
    private List<LessonWithPossibleTerms> lessons;
    private int daysInCycle;
    private TimeDistanceManager distanceManager;
    private int maxCommutesPerDay;
    private int minTimeSpentAtOptimizedAtOnceInMinutes;
    private OptimizedActivity optimizedActivity;
    private ExecutorService executor;
    private Logger logger;

    /**
     * Creates SmartPlanner that finds the most optimal plan based on passed arguments
     *
     * @param lessons           list of all lessons that contains among others possible terms for each lesson.
     * @param daysInCycle       amount of days, after which the whole plan will repeat
     * @param distanceManager   object that contains commute matrix, which is data about travel time between each lesson and work
     * @param maxCommutesPerDay maximal amount of commutes to work per day (specified by a user)
     * @param optimizedActivity object containing data about optimized activity (for example work)
     */
    public SmartPlanner(List<LessonWithPossibleTerms> lessons, int daysInCycle, TimeDistanceManager distanceManager,
                        int maxCommutesPerDay, OptimizedActivity optimizedActivity) {
        this.lessons = lessons;
        this.daysInCycle = daysInCycle;
        this.distanceManager = distanceManager;
        this.maxCommutesPerDay = maxCommutesPerDay;
        this.minTimeSpentAtOptimizedAtOnceInMinutes = optimizedActivity.getMinTimeInMinutes(); //TODO:
        this.optimizedActivity = optimizedActivity;
        this.executor = Executors.newSingleThreadExecutor();
        this.logger = LoggerFactory.getLogger(LoggingController.class);
    }

    /**
     * Returns the optimal plan that is calculated based on arguments passed in constructor
     *
     * @return if provided data is valid it returns specified terms for each lesson, amount of time spent and decision points(that tells us if we should go to work
     * after each lesson. If data for computations is not valid returns object with amount of minutes spent on optimized activity
     * equal to 0 and every other field set to null
     */
    public TimetableWithDecisionPointsAndScore getOptimalPlan() {
        LessonPicker lessonPicker = new LessonPicker((ArrayList<LessonWithPossibleTerms>) lessons, daysInCycle);

        ArrayList<ArrayList<TimetableEntry>> potentialTimetables = new ArrayList<ArrayList<TimetableEntry>>();

        while (lessonPicker.isNext())
            potentialTimetables.add(lessonPicker.getNext());

        TimetableValidator validator = new TimetableValidator(distanceManager);

        int halfIndex = potentialTimetables.size() / 2;

        List<ArrayList<TimetableEntry>> firstHalfOfPotentialTimetables = potentialTimetables.subList(0, halfIndex);
        List<ArrayList<TimetableEntry>> secondHalfOfPotentialTimetables = potentialTimetables.subList(halfIndex, potentialTimetables.size());
        ArrayList<ArrayList<TimetableEntry>> firstHalfOfValidTimetables = new ArrayList<ArrayList<TimetableEntry>>();
        ArrayList<ArrayList<TimetableEntry>> secondHalfOfValidTimetables = new ArrayList<ArrayList<TimetableEntry>>();

        class CallTask implements Callable<Boolean> {

            /**
             * Perform computations for the first half of the potential timetables
             *
             * @return true if the computations have been successfully performed,
             * false otherwise
             *
             * @throws Exception whenever computation is interrupted
             */
            @Override
            public Boolean call() throws Exception {
                try {
                    for (ArrayList<TimetableEntry> timetable : firstHalfOfPotentialTimetables) {
                        if (validator.isValid(timetable)) {
                            firstHalfOfValidTimetables.add(timetable);
                        }
                    }

                    return true;
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    return false;
                }
            }
        }

        Future<Boolean> isDataComputed = executor.submit(() -> {
            /**
             * Perform computations for the second half of the potential timetables
             *
             * return true if the computations have been successfully performed,
             * false otherwise
             */
            try {
                for (ArrayList<TimetableEntry> timetable : secondHalfOfPotentialTimetables) {
                    if (validator.isValid(timetable)) {
                        secondHalfOfValidTimetables.add(timetable);
                    }
                }

                return true;
            } catch (Exception e) {
                logger.error(e.getMessage());
                return false;
            }
        });

        try {
            List<Callable<Boolean>> listOfCallables = new ArrayList<>();
            listOfCallables.add(new CallTask());
            executor.invokeAll(listOfCallables);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }

        try {
            while (!isDataComputed.isDone()) {
                logger.info("Computing data...");
                Thread.sleep(300);
            }

            if (!isDataComputed.get()) {
                throw new InvalidDataProvidedException(
                        "Cannot perform computations. An error occurred");
            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        } catch (ExecutionException e) {
            logger.error(e.getMessage());
        }

        try {
            executor.shutdown();
            if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            throw new InvalidDataProvidedException(
                    "An error occurred during computations");
        }

        ArrayList<ArrayList<TimetableEntry>> validTimetables = new ArrayList<ArrayList<TimetableEntry>>(firstHalfOfValidTimetables);
        validTimetables.addAll(secondHalfOfValidTimetables);

        TimetableWithDecisionPointsAndScore bestTimetable = new TimetableWithDecisionPointsAndScore(0, null, null);
        if (validTimetables.size() != 0) {
            OptimalityCalculator optimalityCalculator = new OptimalityCalculator(distanceManager, maxCommutesPerDay,
                    minTimeSpentAtOptimizedAtOnceInMinutes, daysInCycle, optimizedActivity);

            for (ArrayList<TimetableEntry> validTimetable : validTimetables) {
                TimetableWithDecisionPointsAndScore currentTimetable = optimalityCalculator.calculate(validTimetable);
                if (currentTimetable.getMinutesSpentAtOptimizedActivity() > bestTimetable.getMinutesSpentAtOptimizedActivity())
                    bestTimetable = currentTimetable;
            }

        }

        return bestTimetable;
    }
}
