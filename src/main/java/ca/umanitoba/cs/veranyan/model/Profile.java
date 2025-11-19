package ca.umanitoba.cs.veranyan.model;

import ca.umanitoba.cs.veranyan.logic.MapManager;
import ca.umanitoba.cs.veranyan.model.exceptions.GearNotFoundException;
import ca.umanitoba.cs.veranyan.model.exceptions.*;
import ca.umanitoba.cs.veranyan.model.gear.Gear;
import ca.umanitoba.cs.veranyan.model.map.Map;
import com.google.common.base.Preconditions;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The {@link Profile} domain model object.
 * @implNote {@link Profile} names are unique.
 */
public class Profile implements Cloneable {
    private final String name;
    private final SortedSet<Gear> gears;
    private final SortedSet<Activity> activities;
    private final SortedSet<Profile> friends;

    /**
     * Constructor for {@link Profile}.
     * Requires to add a {@link Gear} to a {@link Profile}.
     * @param profileName the name of the {@link Profile}.
     */
    public Profile(String profileName){
        name = profileName;

        // Gears are ordered by name. Duplicates not allowed
        this.gears = new TreeSet<>((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));

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

        this.friends = new TreeSet<>(Comparator.comparing(Profile::getName));

        checkProfile();
    }

    /**
     * @return the {@link Profile} name
     */
    public String getName() {
        checkProfile();

        return name;
    }

    /**
     * @return the unmodifiable list of {@link Gear} objects. Must not be {@code null}
     */
    public SortedSet<Gear> getGears(){
        checkProfile();

        return Collections.unmodifiableSortedSet(gears);
    }

    /**
     * @param name the name of the {@link Gear}
     * @return the {@link Gear} with matching name or {@code null} if no such {@link Gear} found
     */
    public Gear getGear(String name) throws GearNotFoundException  {
        Preconditions.checkNotNull(name, "name cannot be null");
        checkProfile();

        Gear gearFound = null;
        Iterator<Gear> gearIterator = gears.iterator();

        // omitting all elements until reaching the element at index
        while(gearFound == null && gearIterator.hasNext()){
            var nextGear = gearIterator.next();
            if(nextGear.getName().equals(name))
                gearFound = nextGear;
        }

        if(gearFound == null)
            throw new GearNotFoundException();

        checkProfile();

        return gearFound;
    }

    /**
     * Adds new {@link Gear} to the {@link Profile}
     * @param gear the {@link Gear} to be added
     */
    public void addGear(Gear gear) throws DuplicateGearException {
        Preconditions.checkNotNull(gear, "gear cannot be null");
        checkProfile();

        boolean isAdded = gears.add(gear);

        if(!isAdded)
            throw new DuplicateGearException();

        checkProfile();
    }

    /**
     * Removes a {@link Gear} whose index matches from the {@link Profile}.
     * @param gear the {@link Gear} to remove
     */
    public void removeGear(Gear gear){
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
        Preconditions.checkNotNull(currentDay, "currentDay cannot be null");
        Preconditions.checkNotNull(range, "range cannot be null");
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
     * @return the unmodifiable list of activities of the {@link Profile}. Must not be {@code null}.
     */
    public SortedSet<Activity> getActivities() {
        checkProfile();

        return Collections.unmodifiableSortedSet(activities);
    }

    /**
     * Adds an {@link Activity} to the {@link Profile}
     * @param activity the {@link Activity} instance to add to {@link Map}. Must not be {@code null}.
     */
    public void addActivity(Activity activity) throws DuplicateActivityException {
        Preconditions.checkNotNull(activity, "activity cannot be null");
        checkProfile();

        boolean isAdded = activities.add(activity);

        if(!isAdded)
            throw new DuplicateActivityException();

        checkProfile();
    }

    /**
     * Removes an activity from the Map by index.
     * @param index the index of the activity to remove.
     */
    public void removeActivity(int index) {
        checkProfile();

        Iterator<Activity> iterator = activities.iterator();

        // omitting previous elements to reach element at appropriate index;
        for(int j = 0; j < index; j++)
            iterator.next();

        activities.remove(iterator.next());

        checkProfile();
    }

    /**
     * @return the set of all friend profiles for the {@link Profile}
     */
    public Set<Profile> getFriends() {
        return friends;
    }

    /**
     * Adds the other {@link Profile} to the list of friends of the current {@link Profile}.
     *
     * @param other the {@link Profile} to follow
     * @throws CannotFollowSelfException if attempted to follow {@code this}
     * @throws CannotFollowAgainException if {@code other} is not in the list of other profiles that can be followed
     */
    public void follow(Profile other) throws CannotFollowSelfException, CannotFollowAgainException {
        Preconditions.checkNotNull(other, "other cannot be null");
        checkProfile();

        if(other.getName().equals(name))
            throw new CannotFollowSelfException();
        if(friends.contains(other))
            throw new CannotFollowAgainException();

        friends.add(other);

        checkProfile();
    }

    /**
     * Removes the other {@link Profile} from the list of friends of the current {@link Profile}
     *
     * @param other the other {@link Profile} to unfollow
     * @throws CannotUnfollowSelfException if attempted to unfollow {@code this}
     * @throws CannotUnfollowNonFriendException if {@code other} is not in the list of friends
     */
    public void unfollow(Profile other) throws CannotUnfollowSelfException, CannotUnfollowNonFriendException{
        Preconditions.checkNotNull(other, "other cannot be null");
        checkProfile();

        if(other.getName().equals(name))
            throw new CannotUnfollowSelfException();
        if(!friends.contains(other))
            throw new CannotUnfollowNonFriendException();

        boolean unfollowed = false;
        Iterator<Profile> iterator = friends.iterator();

        while(iterator.hasNext() && !unfollowed){
            var curr = iterator.next();

            if(curr == other){
                friends.remove(other);
                unfollowed = true;
            }
        }

        checkProfile();
    }

    /**
     * @return the unmodifiable list of routes on the {@link Map}. Must not be {@code null}.
     */
    public List<MapManager.ProcessedRoute> getRoutes() {
        checkProfile();

        return activities.stream().map(Activity::getRoute).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Ensures {@link Profile} invariants are not violated.
     */
    private void checkProfile(){
        Preconditions.checkNotNull(name, "getName cannot be null.");
        Preconditions.checkState(!name.isBlank(), "getName cannot be blank.");
        Preconditions.checkNotNull(gears, "gears cannot be null.");
        Preconditions.checkNotNull(activities, "activities cannot be null.");
        Preconditions.checkNotNull(friends, "friends cannot be null.");

        // Gear cannot be null
        for (var gear : gears)
            Preconditions.checkNotNull(gear, "gears entry cannot be null.");

        // Activity cannot be null
        for (var activity : activities)
            Preconditions.checkNotNull(activity, "activities entries cannot be null.");

        // Friend cannot be null, Profile cannot follow themselves
        for (var friend : friends){
            Preconditions.checkNotNull(friend, "friend profile cannot be null");
            Preconditions.checkState(this != friend, "profile cannot follow themselves");
        }

    }

    @Override
    public Profile clone() {
        checkProfile();

        try {
            return (Profile) super.clone();

        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    /**
     * The builder class for {@link Profile}
     */
    public static class ProfileBuilder {
        String name;

        /**
         * Sets a name for the {@link Profile} to build
         * @param name the name of the {@link Profile}
         * @return the builder instance
         * @throws BlankNameException if {@code name} has no characters
         */
        public ProfileBuilder name(String name) throws BlankNameException {
            Preconditions.checkNotNull(name, "getName cannot be null");

            if (name.isBlank())
                throw new BlankNameException();

            this.name = name;

            return this;
        }

        /**
         * Builds the {@link Profile} without gears or activities
         * @return the new {@link Profile}
         */
        public Profile build(){
            Preconditions.checkNotNull(name, "name cannot be null");

            return new Profile(name);
        }
    }
}
