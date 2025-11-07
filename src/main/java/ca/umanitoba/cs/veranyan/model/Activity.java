package ca.umanitoba.cs.veranyan.model;

import ca.umanitoba.cs.veranyan.model.exceptions.InvalidGearNameException;
import ca.umanitoba.cs.veranyan.model.exceptions.InvalidTimeRangeException;
import ca.umanitoba.cs.veranyan.model.gear.Gear;
import ca.umanitoba.cs.veranyan.model.map.Map;
import ca.umanitoba.cs.veranyan.model.map.Route;
import com.google.common.base.Preconditions;

import java.time.*;

/**
 * An Activity is a class that contains all the information about
 * a particular cycling exercise. It contains the {@link Gear} used
 * for that particular exercise. Activities are stored in the {@link Profile} instance.
 */
public class Activity {
    private static final int METERS_PER_STEP = 10; // a step is one coordinate on the Map grid.

    private final Gear gear; // the gear to be used in Activity
    private final LocalDateTime start; // the start time of Activity
    private final LocalDateTime end; // Activity end is initialised with null until ended explicitly using endActivity()
    private final Map.ProcessedRoute route;

    /**
     * Constructor for Activity
     * @param gear the gear to add to the activity. Must not be {@code null}.
     * @param route the route to add to the activity. Must not be {@code null}.
     * @param start the start date of the activity. Must not be {@code null}.
     * @param end the end date of the activity. Must not be {@code null}. Must be after {@code start}
     */
    private Activity(Gear gear, Map.ProcessedRoute route, LocalDateTime start, LocalDateTime end) {
        this.gear = gear;

        this.start = start;
        this.end = end;

        /*
         * Decided to make Activity a class instead of record
         * Since its route field is modified from the external
         */
        this.route = route;

        checkActivity();
    }

    /**
     * @return the average speed (meters per second) throughout the Activity.
     */
    public double getAvgSpeed() {
        checkActivity();

        // average speed is computed dynamically as the route is being formed
        // decision made to calculate average speed upon return
        return ((double) route.getMeasure() * METERS_PER_STEP) / Duration.between(start, end).getSeconds();
    }

    /**
     * @return the start time for the Activity. Must not be {@code null}.
     */
    public LocalDateTime getStart() {
        checkActivity();

        return start;
    }

    /**
     * @return the end time for the activity. May not be {@code null}.
     */
    public LocalDateTime getEnd() {
        checkActivity();

        return end;
    }

    /**
     * @return the gear used in the activity. Must not be {@code null}.
     */
    public Gear getGear() {
        checkActivity();

        return gear;
    }

    /**
     * @return the route used in the activity. Must not be {@code null}.
     */
    public Map.ProcessedRoute getRoute(){
        checkActivity();

        return route;
    }

    /**
     * Ensures Activity invariants are not violated.
     */
    private void checkActivity(){
        Preconditions.checkNotNull(gear, "gear cannot be null.");
        Preconditions.checkNotNull(start, "start cannot be null.");
        Preconditions.checkNotNull(end, "end cannot be null.");
        Preconditions.checkNotNull(route, "route cannot be null.");

        Preconditions.checkState(
                start.isBefore(end), "start cannot be after end"
        );

        // Coordinate will ensure coordinates are non-negative
        // Map will ensure Route is within boundaries
    }

    public static class ActivityBuilder{
        private Gear gear; // the gear to be used in Activity
        private int startMonth;
        private int startDayOfMonth;
        private int startHour;
        private int startMinute;
        private int durationInMinutes;
        private Map.ProcessedRoute route;

        public ActivityBuilder(){
            this.startMonth = -1;
            this.startDayOfMonth = -1;
            this.startHour = -1;
            this.startMinute = -1;
            this.durationInMinutes = -1;
        }

        public ActivityBuilder gear(Gear gear){
            Preconditions.checkNotNull(gear, "gear cannot be null");

            this.gear = gear;

            return this;
        }

        public ActivityBuilder startMonth(int month) throws InvalidTimeRangeException {
            if(month < 1 || month > 12)
                throw new InvalidTimeRangeException();

            startMonth = month;

            return this;
        }

        public ActivityBuilder startDayOfMonth(int day) throws InvalidTimeRangeException {
            Preconditions.checkState(startMonth != -1, "startMonth() must be called before startDayOfMonth()");

            var yearMonth = YearMonth.of(Year.now().getValue(), startMonth);

            if(day < 1 || day > yearMonth.getMonth().length(yearMonth.isLeapYear()))
                throw new InvalidTimeRangeException();

            startDayOfMonth = day;

            return this;
        }

        public ActivityBuilder startHour(int hour) throws InvalidTimeRangeException{
            if(hour < 0 || hour > 23)
                throw new InvalidTimeRangeException();

            this.startHour = hour;

            return this;
        }

        public ActivityBuilder startMinute(int minute) throws InvalidTimeRangeException{
            if(minute < 0 || minute > 59)
                throw new InvalidTimeRangeException();

            this.startMinute = minute;

            return this;
        }

        public ActivityBuilder durationInMinutes(int duration) throws InvalidTimeRangeException{
            if(duration <= 0 || duration > 6000)
                throw new InvalidTimeRangeException();

            durationInMinutes = duration;

            return this;
        }

        public ActivityBuilder route(Map.ProcessedRoute route){
            Preconditions.checkNotNull(route, "route cannot be null");

            this.route = route;

            return this;
        }

        public Activity build(){
            Preconditions.checkNotNull(gear, "gear cannot be null");
            Preconditions.checkNotNull(route, "route cannot be null");
            Preconditions.checkState(startMonth != -1, "startMonth must be initialised");
            Preconditions.checkState(startDayOfMonth != -1, "startDayOfMonth must be initialised");
            Preconditions.checkState(startHour != -1, "startHour must be initialised");
            Preconditions.checkState(startMinute != -1, "startMinute must be initialised");
            Preconditions.checkState(durationInMinutes != -1, "durationInMinutes must be initialised");

            var yearMonth = YearMonth.of(Year.now().getValue(), startMonth);
            var start = LocalDateTime.of(
                    yearMonth.getYear(),
                    startMonth,
                    startDayOfMonth,
                    startHour,
                    startMinute
            );


            return new Activity(
                    gear,
                    route,
                    start,
                    start.plusMinutes(durationInMinutes)
            );
        }
    }
}
