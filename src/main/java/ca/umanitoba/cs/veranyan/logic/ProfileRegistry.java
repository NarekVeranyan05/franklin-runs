package ca.umanitoba.cs.veranyan.logic;

import ca.umanitoba.cs.veranyan.model.assets.Pair;
import ca.umanitoba.cs.veranyan.logic.exceptions.*;
import ca.umanitoba.cs.veranyan.model.exceptions.*;
import ca.umanitoba.cs.veranyan.model.Activity;
import ca.umanitoba.cs.veranyan.model.Profile;
import ca.umanitoba.cs.veranyan.model.gear.Gear;
import ca.umanitoba.cs.veranyan.persistence.ProfilePersistence;
import com.google.common.base.Preconditions;

import java.util.*;
import java.util.stream.Collectors;

/**
 * {@link ProfileRegistry} manages the business logic of keeping multiple profiles within the same system.
 */
public class ProfileRegistry {
    private final ProfilePersistence profilePersistence;

    private final SortedSet<Profile> profiles;
    private Profile currentProfile;
    private Status loginStatus;

    public ProfileRegistry(ProfilePersistence profilePersistence){
        this.profilePersistence = profilePersistence;

        profiles = new TreeSet<>(
            (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName())
        );
        loginStatus = Status.OFFLINE; // no profile logged in yet

        checkProfileRegistry();
    }

    /**
     * @return all the profiles in the system. Must not be {@code null}.
     */
    public SortedSet<Profile> getProfiles() throws EmptyProfilesException {
        checkProfileRegistry();

        if(profiles.isEmpty())
            throw new EmptyProfilesException();

        return profiles;
    }

    /**
     * @return the current {@link Profile} logged into the system. May be {@code null}.
     */
    public Profile getCurrentProfile() {
        Preconditions.checkState(checkStatus(Status.ONLINE), "profile status must be online");
        checkProfileRegistry();

        return currentProfile;
    }

    /**
     * @return all the friends of the current {@link Profile}
     */
    public Set<Profile> getFriends() throws EmptyProfilesException {
        Preconditions.checkState(checkStatus(Status.ONLINE), "profile status must be online");
        checkProfileRegistry();

        var friends = currentProfile.getFriends();

        if(friends.isEmpty())
            throw new EmptyProfilesException();

        checkProfileRegistry();

        return friends;
    }

    /**
     * @return all the profiles not followed by the current {@link Profile}
     */
    public Set<Profile> getProfilesNotInCircle() throws EmptyProfilesException {
        Preconditions.checkState(checkStatus(Status.ONLINE), "profile status must be online");
        checkProfileRegistry();

        var profilesNotInCircle  = profiles.stream().filter(
                profile -> (!currentProfile.getFriends().contains(profile) && profile != currentProfile)
        ).collect(Collectors.toUnmodifiableSet());

        if(profilesNotInCircle.isEmpty())
            throw new EmptyProfilesException();

        checkProfileRegistry();

        return profilesNotInCircle;
    }

    /**
     * @param name the name of the {@link Profile} to access
     * @return the {@link Profile} with matching name. May not be {@code null}
     * @throws NoNameMatchException if no {@link Profile} has a matching name
     */
    public Profile getProfile(String name) throws NoNameMatchException {
        Preconditions.checkNotNull(name, "getName cannot be null");
        checkProfileRegistry();

        Profile selectedProfile = null;
        var iterator = profiles.iterator();

        while(iterator.hasNext() && selectedProfile == null){
            var next = iterator.next();
            if(next.getName().equals(name))
                selectedProfile = next;
        }

        if(selectedProfile == null)
            throw new NoNameMatchException();

        checkProfileRegistry();

        return selectedProfile;
    }

    /**
     * Adds a new {@link Profile} to the {@link ProfileRegistry}
     *
     * @param profile the new {@link Profile} to be added. Must not be {@code null}
     */
    public void addProfile(Profile profile) throws DuplicateProfileException {
        Preconditions.checkNotNull(profile, "profile cannot be null");
        checkProfileRegistry();

        if(profiles.contains(profile)) throw new DuplicateProfileException();

        profiles.add(profile);

        profilePersistence.save(profile);

        checkProfileRegistry();
    }

    /**
     * @param type the filter setting for activities
     * @return all the activities filtered by type
     */
    public List<Pair<String, Activity>> getActivities(ActivityFilterType type) {
        Preconditions.checkNotNull(type, "type cannot be null");
        Preconditions.checkState(checkStatus(Status.ONLINE), "profile status must be online");
        checkProfileRegistry();

        List<Pair<String, Activity>> filteredActivities = new ArrayList<>();

        switch (type){
            case OWN -> {
                for(var activity : currentProfile.getActivities()) {
                    filteredActivities.add(new Pair<>(currentProfile.getName(), activity));
                }
            }
            case FRIENDS ->{
                for(var profile : currentProfile.getFriends()) {
                    for (var activity : profile.getActivities())
                        filteredActivities.add(new Pair<>(profile.getName(), activity));
                }
            }
            case OWN_AND_FRIENDS -> {
                for(var activity : currentProfile.getActivities()) {
                    filteredActivities.add(new Pair<>(currentProfile.getName(), activity));
                }

                for(var profile : currentProfile.getFriends()) {
                    for (var activity : profile.getActivities())
                        filteredActivities.add(new Pair<>(profile.getName(), activity));
                }
            }
            case ALL -> {
                for(var profile : profiles){
                    for(var activity : profile.getActivities())
                        filteredActivities.add(new Pair<>(profile.getName(), activity));
                }
            }
        }

        filteredActivities.sort(Comparator.comparing(Pair::getSecond));

        checkProfileRegistry();

        return filteredActivities;
    }

    /**
     * Adds the new {@link Gear} to the current {@link Profile} in the system
     * @param gear the {@link Gear} to add
     * @throws DuplicateGearException if a {@link Gear} with the same name was previously added to the current {@link Profile}
     */
    public void addGear(Gear gear) throws DuplicateGearException {
        Preconditions.checkNotNull(gear, "gear cannot be null");
        Preconditions.checkState(checkStatus(Status.ONLINE), "profile status must be online");
        checkProfileRegistry();

        currentProfile.addGear(gear);
        profilePersistence.save(currentProfile);

        checkProfileRegistry();
    }

    /**
     * Adds an {@link Activity} to the current {@link Profile}
     * @param activity the {@link Activity} to add
     * @throws DuplicateActivityException if the current {@link Profile} already has an {@link Activity} with the same start date.
     */
    public void addActivity(Activity activity) throws DuplicateActivityException {
        Preconditions.checkState(checkStatus(Status.ONLINE), "profile status must be online");
        Preconditions.checkNotNull(activity, "activity cannot be null");
        checkProfileRegistry();

        currentProfile.addActivity(activity);

        profilePersistence.save(currentProfile);

        checkProfileRegistry();
    }

    /**
     * Logs into the system with selected {@link Profile}.
     * @param profile the {@link Profile} to log in with.
     */
    public void loadProfile(Profile profile){
        Preconditions.checkNotNull(profile, "profile cannot be null");
        Preconditions.checkState(profiles.contains(profile), "profile has to be added to be loaded");
        checkProfileRegistry();

        boolean isAdded = false;
        Iterator<Profile> iterator = profiles.iterator();
        while(iterator.hasNext() && !isAdded)
            isAdded = (iterator.next() == profile);

        currentProfile = profile;
        loginStatus = Status.ONLINE; // profile is now online (logged into the system)

        checkProfileRegistry();
    }

    /**
     * Logs out of a particular {@link Profile}.
     */
    public void unloadProfile(){
        checkProfileRegistry();

        currentProfile = null;
        loginStatus = Status.OFFLINE;

        checkProfileRegistry();
    }

    /**
     * Follows another {@link Profile}
     *
     * @param other the other {@link Profile} to follow
     * @throws CannotFollowSelfException if attempted to follow {@code this}
     * @throws CannotFollowAgainException if {@code other} is not in the list of other profiles that can be followed
     */
    public void follow(Profile other) throws CannotFollowAgainException, CannotFollowSelfException {
        Preconditions.checkState(checkStatus(Status.ONLINE), "profile status must be online");
        Preconditions.checkNotNull(other, "other cannot be null.");
        checkProfileRegistry();

        currentProfile.follow(other);
        profilePersistence.save(currentProfile);
    }

    /**
     * Unfollows another {@link Profile}
     *
     * @param other the other {@link Profile} to unfollow
     * @throws CannotUnfollowSelfException if attempted to unfollow {@code this}
     * @throws CannotUnfollowNonFriendException if {@code other} is not in the list of friends
     */
    public void unfollow(Profile other) throws CannotUnfollowSelfException, CannotUnfollowNonFriendException {
        Preconditions.checkState(checkStatus(Status.ONLINE), "profile status must be online");
        Preconditions.checkNotNull(other, "other cannot be null.");
        checkProfileRegistry();

        currentProfile.unfollow(other);
        profilePersistence.save(currentProfile);
    }

    /**
     * Checks the login status of the system
     * @param status the login status to check
     * @return {@code true} if the login status matched, {@code false} otherwise
     */
    public boolean checkStatus(Status status){
        checkProfileRegistry();

        return (loginStatus == status);
    }

    /**
     * Class invariants for {@link ProfileRegistry}
     */
    private void checkProfileRegistry(){
        Preconditions.checkNotNull(profiles, "profiles cannot be null");
        Preconditions.checkNotNull(loginStatus, "loginStatus cannot be null");

        if(loginStatus == Status.ONLINE)
            Preconditions.checkNotNull(currentProfile, "currentProfile cannot be null when logged in");
    }

    /**
     * The status of the registry's current {@link Profile}
     */
    public enum Status {
        ONLINE, // if user logged in
        OFFLINE // if user hasn't logged in yet
    }

    /**
     * Filtering types for the activities of various profiles in the registry
     */
    public enum ActivityFilterType {
        ALL,
        OWN, // current profile's routes
        FRIENDS, // current profile's friends' routes
        OWN_AND_FRIENDS;

        /**
         * Converts a string to {@link ActivityFilterType}
         * @param value the string to convert to {@link ActivityFilterType}
         * @return the corresponding {@link ActivityFilterType}
         * @throws InvalidGearTypeException if the string does not correspond to any of the available filter types
         */
        public static ActivityFilterType fromString(String value) throws InvalidGearTypeException {
            Preconditions.checkNotNull(value, "Value passed to enum should not be null.");

            return switch(value) {
                case "ALL" -> ALL;
                case "OWN" -> OWN;
                case "FRIENDS" -> FRIENDS;
                case "OWN_AND_FRIENDS" -> OWN_AND_FRIENDS;
                default -> throw new InvalidGearTypeException();
            };
        }
    }
}
