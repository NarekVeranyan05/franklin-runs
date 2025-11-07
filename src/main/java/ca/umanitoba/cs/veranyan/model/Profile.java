package ca.umanitoba.cs.veranyan.model;

import ca.umanitoba.cs.veranyan.model.exceptions.BlankNameException;
import ca.umanitoba.cs.veranyan.model.exceptions.DuplicateActivityException;
import ca.umanitoba.cs.veranyan.model.exceptions.DuplicateGearException;
import ca.umanitoba.cs.veranyan.model.gear.Gear;
import ca.umanitoba.cs.veranyan.model.map.Map;
import com.google.common.base.Preconditions;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

public class Profile implements Cloneable {
    private String name;
    private final SortedSet<Gear> gears;
    private final SortedSet<Activity> activities;
    private final SortedSet<Profile> friends;

    /**
     * Constructor for Profile.
     * Requires to add a Gear to a Profile.
     * @param profileName the getName of the Profile.
     */
    private Profile(String profileName){
        name = profileName;

        // Gears are ordered by getName. Duplicates not allowed
        this.gears = new TreeSet<>(new Comparator<Gear>() {
            @Override
            public int compare(Gear o1, Gear o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });

        // activities should not have duplicates.
        // activities are put in ascending order in the Set (ordered by start time).
        this.activities = new TreeSet<>(new Comparator<>() {
            @Override
            public int compare(Activity o1, Activity o2) {
                return o2.getStart().compareTo(o1.getStart());
            }

            @Override
            public boolean equals(Object obj) {
                return super.equals(obj);
            }
        });

        this.friends = new TreeSet<>(new Comparator<Profile>() {
            @Override
            public int compare(Profile o1, Profile o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        checkProfile();
    }

    /**
     * @return the Profile getName
     */
    public String getName() {
        checkProfile();

        return name;
    }

    /**
     * Changes the getName of the Profile
     * @param name the new getName of the Profile
     */
    public void setName(String name) {
        checkProfile();

        this.name = name;

        checkProfile();
    }

    public boolean hasGear(){
        return !gears.isEmpty();
    }

    /**
     * @return the unmodifiable list of Gears. Must not be {@code null}
     */
    public SortedSet<Gear> getGears(){
        checkProfile();

        return Collections.unmodifiableSortedSet(gears);
    }

    /**
     * @param index the index of the Gear.
     * @return the Gear at the given index. Must not be {@code null}.
     */
    public Gear getGear(int index){
        checkProfile();

        Iterator<Gear> gearIterator = gears.iterator();

        // omitting all elements until reaching the element at index
        for(int i = 0; i < index; i++)
            gearIterator.next();

        checkProfile();

        return gearIterator.next();
    }

    /**
     * Adds new Gear to the Profile.
     * @param gear the Gear to be added
     */
    public boolean addGear(Gear gear) throws DuplicateGearException {
        checkProfile();

        boolean isAdded = gears.add(gear);

        if(!isAdded)
            throw new DuplicateGearException();

        checkProfile();
        return isAdded;
    }

    /**
     * Removes a Gear whose index matches from the Profile.
     * Profile must have at least one Gear.
     * @param gear the gear to remove
     */
    public void removeGear(Gear gear){
        Preconditions.checkNotNull(gear, "gear cannot be null");
        checkProfile();

        gears.remove(gear);

        checkProfile();
    }

    /**
     * Calculates the total number of steps passed in all Activities in a particular time range.
     * @param currentDay the day to consider.
     * @param range the time range to consider. Must be any of and only of
     *              [{@code ChronoUnit.WEEKS}, {@code ChronoUnit.MONTHS}].
     * @return the total number of steps in range. Must be non-negative.
     */
    public int getTotalNumSteps(LocalDate currentDay, ChronoUnit range) {
        checkProfile();

        LocalDate start = currentDay; // default initialisation
        LocalDate end = currentDay; // default initialisation
        int totalNumSteps = 0;

        end = switch (range) {
            case WEEKS -> {
                start = currentDay.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                yield currentDay.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
            }
            case MONTHS -> {
                start = currentDay.with(TemporalAdjusters.firstDayOfMonth());
                yield currentDay.with(TemporalAdjusters.lastDayOfMonth());
            }
            default -> end;
        };

        for(var activity : activities) {
            if (activity.getEnd() != null) {
                // if activity occurred this week
                if (activity.getStart().isAfter(start.atStartOfDay()) &&
                        activity.getEnd().isBefore(end.atStartOfDay())) {

                    totalNumSteps += activity.getRoute().getMeasure();
                }
            }
        }

        checkProfile();

        return totalNumSteps;
    }

    /**
     * @return the unmodifiable list of activities on the Map. Must not be {@code null}.
     */
    public SortedSet<Activity> getActivities() {
        checkProfile();

        return Collections.unmodifiableSortedSet(activities);
    }

    /**
     * Adds an activity to the Map instance.
     * @param activity the activity instance to add to Map. Must not be {@code null}.
     */
    public void addActivity(Activity activity) throws DuplicateActivityException {
        checkProfile();;

        boolean isAdded = activities.add(activity);

        if(!isAdded)
            throw new DuplicateActivityException();

        checkProfile();;
    }

    /**
     * Removes an activity from the Map by index.
     * @param index the index of the activity to remove.
     */
    public void removeActivity(int index){
        checkProfile();

        Iterator<Activity> iterator = activities.iterator();

        // omitting previous elements to reach element at appropriate index;
        for(int j = 0; j < index; j++)
            iterator.next();

        activities.remove(iterator.next());

        checkProfile();;
    }

    public Set<Profile> getFriends() {
        return friends;
    }

    public boolean follow(Profile other){
        Preconditions.checkNotNull(other, "other cannot be null");
        Preconditions.checkState(other != this, "profile cannot follow themselves");

        return friends.add(other);
    }

    public boolean unfollow(Profile other) {
        Preconditions.checkNotNull(other, "other cannot be null");
        Preconditions.checkNotNull(other, "profile cannot unfollow themselves");

        boolean unfollowed = false;
        Iterator<Profile> iterator = friends.iterator();

        while(iterator.hasNext() && !unfollowed){
            var curr = iterator.next();
            if(curr == other){
                friends.remove(other);
                unfollowed = true; // FIXME assign to what's returned by above statement
            }
        }

        return unfollowed;
    }

    /**
     * @return the unmodifiable list of activities on the Map. Must not be {@code null}.
     */
    public ArrayList<Map.ProcessedRoute> getRoutes() {
        checkProfile();

        return activities.stream().map(Activity::getRoute).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Ensures Profile invariants are not violated.
     */
    private void checkProfile(){
        /*
            private String getName;
    private final SortedSet<Gear> gears;
    private final SortedSet<Activity> activities;
    private final Set<Profile> friends;
         */

        Preconditions.checkNotNull(name, "getName cannot be null.");
        Preconditions.checkState(!name.isBlank(), "getName cannot be blank.");
        Preconditions.checkNotNull(gears, "gears cannot be null.");
//        Preconditions.checkState(!gears.isEmpty(), "gears should have at least one entry.");
        Preconditions.checkNotNull(activities, "activities cannot be null.");
        Preconditions.checkNotNull(friends, "friends cannot be null.");

        // Gear cannot be null
        for (var gear : gears)
            Preconditions.checkNotNull(gear, "gears entry cannot be null.");

        // Friend cannot be null, Profile cannot follow themselves
        for (var friend : friends){
            Preconditions.checkNotNull(friend, "friend profile cannot be null");
            Preconditions.checkState(this != friend, "profile cannot follow themselves");
        }

        // Activity cannot be null
        for (var activity : activities)
            Preconditions.checkNotNull(activity, "activities entries cannot be null.");
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Friends of ").append(this.name).append("\n");
        for (var friend : this.friends){
            stringBuilder.append(friend.name).append("\n");
        }

        return stringBuilder.toString();
    }

    @Override
    public Profile clone() {
        try {
            return (Profile) super.clone();

        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public static class ProfileBuilder{
        String name;

        public ProfileBuilder name(String name) throws BlankNameException{
            Preconditions.checkNotNull(name, "getName cannot be null");

            if(name.isBlank())
                throw new BlankNameException();

            this.name = name;

            return this;
        }

        public Profile build(){
            checkProfileBuilder();

            return new Profile(name);
        }

        private void checkProfileBuilder(){
            Preconditions.checkNotNull(name, "getName cannot be null");
            Preconditions.checkState(!name.isBlank(), "getName cannot be blank");
        }
    }
}
