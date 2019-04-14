package com.smartplanner.model;

import com.smartplanner.model.entity.Term;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Validator of timetables. It checks if lessons doesn't overlaps each other.
 */
public class TimetableValidator {
    TimeDistanceManager distanceManager;

    /**
     * Creates validator for timetables
     *
     * @param distanceManager object that contains commute matrix, which is data about travel time between each lesson and work
     */
    public TimetableValidator(TimeDistanceManager distanceManager) {
        this.distanceManager = distanceManager;
    }

    /**
     * Checks if the timetable provided as an argument is valid (i.e. none of entries overlaps each other)
     *
     * @param timetable timetable to be validated
     * @return true if timetable is valid, false else
     */
    public boolean isValid(ArrayList<TimetableEntry> timetable) {

        ArrayList<TimetableEntry> timetableSorted = new ArrayList<TimetableEntry>(timetable);

        Comparator<TimetableEntry> timeComparator = new Comparator<TimetableEntry>() {
            @Override
            public int compare(TimetableEntry lhs, TimetableEntry rhs) {
                if (lhs.getTerm().getCycleDayNumber() < rhs.getTerm().getCycleDayNumber())
                    return -1;
                else if (lhs.getTerm().getCycleDayNumber() > rhs.getTerm().getCycleDayNumber())
                    return 1;
                else
                    return lhs.getTerm().getStartTime().compareTo(rhs.getTerm().getStartTime());
            }
        };

        timetableSorted.sort(timeComparator);

        return noneOfActivitesOverlaps(timetable) && allActivitiesAreTransportationReachable(timetableSorted);
    }

    private boolean allActivitiesAreTransportationReachable(ArrayList<TimetableEntry> sortedTimetable) {
        for (int i = 0; i + 1 < sortedTimetable.size(); ++i)
            if (isTransportationReachable(sortedTimetable.get(i), sortedTimetable.get(i + 1)) == false)
                return false;

        return true;
    }

    private boolean isTransportationReachable(TimetableEntry first, TimetableEntry second) {
        if (first.getTerm().getCycleDayNumber() != second.getTerm().getCycleDayNumber())
            return true;

        LocalTime possibleArrivalToSecondActivityTime = first.getTerm().getEndTime()
                .plusMinutes(distanceManager.getTimeDistanceInMinutes(first.getLesson(), second.getLesson()));

        return possibleArrivalToSecondActivityTime.isAfter(second.getTerm().getStartTime()) == false;
    }

    private boolean noneOfActivitesOverlaps(ArrayList<TimetableEntry> timetable) {
        for (int i = 0; i < timetable.size(); ++i) {
            for (int j = 0; j < timetable.size(); ++j) {
                if (i == j)
                    continue;
                if (doesOverlap(timetable.get(i), timetable.get(j)))
                    return false;
            }
        }
        return true;
    }

    private boolean doesOverlap(TimetableEntry lhs, TimetableEntry rhs) {
        Term lhsTerm = lhs.getTerm();
        Term rhsTerm = rhs.getTerm();

        if(lhsTerm.getCycleDayNumber() != rhsTerm.getCycleDayNumber())
            return false;

        Term earlier;
        Term later;
        if (lhsTerm.getStartTime().isAfter(rhsTerm.getStartTime())) {
            later = lhsTerm;
            earlier = rhsTerm;
        } else {
            later = rhsTerm;
            earlier = lhsTerm;
        }

        LocalTime earlierEndingTime = earlier.getStartTime().plusMinutes(earlier.getDurationInMinutes());
        return earlierEndingTime.isAfter(later.getStartTime());
    }
}
