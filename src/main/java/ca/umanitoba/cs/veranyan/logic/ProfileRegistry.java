package ca.umanitoba.cs.veranyan.logic;

import ca.umanitoba.cs.veranyan.logic.assets.RouteFilterType;
import ca.umanitoba.cs.veranyan.model.Activity;
import ca.umanitoba.cs.veranyan.model.Profile;
import ca.umanitoba.cs.veranyan.model.exceptions.DuplicateActivityException;
import ca.umanitoba.cs.veranyan.model.exceptions.DuplicateProfileException;
import ca.umanitoba.cs.veranyan.model.exceptions.NoNameMatchException;
import com.google.common.base.Preconditions;

import java.util.*;
import java.util.stream.Collectors;

public class ProfileRegistry {
    private final SortedSet<Profile> profiles;
    private Profile currentProfile;
    private Status loginStatus;

    /**
     * Constructor for {@link ProfileRegistry}
     */
    public ProfileRegistry(){
        profiles = new TreeSet<>(
                new Comparator<Profile>() {
                    @Override
                    public int compare(Profile o1, Profile o2) {
                        return o1.getName().compareToIgnoreCase(o2.getName());
                    }
                }
        );
        loginStatus = Status.OFFLINE;

        checkProfileRegistry();
    }

    /**
     * @return all the profiles in the system. Must not be {@code null}.
     */
    public SortedSet<Profile> getProfiles() {
        checkProfileRegistry();
        return profiles;
    }

    /**
     * @return all the alternative profiles in the system other than the current profile.
     */
    public Set<Profile> getOtherProfiles(){
        checkProfileRegistry();

        return profiles.stream().filter(profile -> profile != currentProfile).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * @return the current profile logged into the system. May be {@code null}.
     */
    public Profile getCurrentProfile() {
        Preconditions.checkState(checkStatus(Status.ONLINE), "profile status must be online");
        checkProfileRegistry();

        return currentProfile;
    }

    public Set<Profile> getFriends(){
        Preconditions.checkState(checkStatus(Status.ONLINE), "profile status must be online");

        return currentProfile.getFriends();
    }

    public Set<Profile> getProfilesNotInCircle(){
        Preconditions.checkState(checkStatus(Status.ONLINE), "profile status must be online");

        return profiles.stream().filter(
                profile -> (!currentProfile.getFriends().contains(profile) && profile != currentProfile)
        ).collect(Collectors.toUnmodifiableSet());
    }

    public Profile getProfile(String name) throws NoNameMatchException{
        Preconditions.checkNotNull(name, "getName cannot be null");

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
     * Adds a new profile to the registry
     *
     * @param profile the new profile to be added. Must not be {@code null}
     */
    public void addProfile(Profile profile) throws DuplicateProfileException {
        Preconditions.checkNotNull(profile, "profile cannot be null");
        checkProfileRegistry();

        if(profiles.contains(profile)) throw new DuplicateProfileException();

        int currSize = profiles.size();
        boolean isAdded = profiles.add(profile);

        if(isAdded)
            Preconditions.checkState(profiles.size() == currSize + 1, "must add new profile");
        checkProfileRegistry();

    }

    /**
     * Replaces the current profile with replacement containing desired modifications
     *
     * @param replacement the replacement of the current profile. Must not be {@code null}.
     */
    public void replaceCurrentProfileName(Profile replacement) throws DuplicateProfileException{
        Preconditions.checkState(checkStatus(Status.ONLINE), "profile status must be online");
        Preconditions.checkNotNull(replacement, "replacement cannot be null");
        checkProfileRegistry();

        boolean isDuplicate = false;

        Iterator<Profile> iterator = profiles.iterator();
        while(iterator.hasNext() && !isDuplicate) {
            var next = iterator.next();
            isDuplicate = (next != currentProfile) && (next.getName().equalsIgnoreCase(replacement.getName()));
        }

        if(isDuplicate)
            throw new DuplicateProfileException();

        currentProfile.setName(replacement.getName());

        checkProfileRegistry();

    }

    /**
     * @param type the filter setting for activities
     * @return all the activities filtered by type
     */
    public SortedSet<Activity> getActivities(RouteFilterType type) {
        checkProfileRegistry();
        Preconditions.checkState(checkStatus(Status.ONLINE), "profile status must be online");
        Preconditions.checkNotNull(type, "type cannot be null");

        SortedSet<Activity> filteredActivities = new TreeSet<>(
                (o1, o2) -> {
                    int result;
                    if (o1.getStart().isEqual(o2.getStart()))
                        result = -1;
                    else result = o1.getStart().compareTo(o2.getStart());

                    return result;
                }
        );

        switch (type){
            case OWN -> {
                filteredActivities.addAll(currentProfile.getActivities());
            }
            case FRIENDS ->{
                for(var profile : currentProfile.getFriends())
                    filteredActivities.addAll(profile.getActivities());
            }
            case OWN_AND_FRIENDS -> {
                filteredActivities.addAll(currentProfile.getActivities());

                for(var profile : currentProfile.getFriends())
                    filteredActivities.addAll(profile.getActivities());
            }
        }

        return filteredActivities;
    }

    public void addActivity(Activity activity) throws DuplicateActivityException {
        Preconditions.checkState(checkStatus(Status.ONLINE), "profile status must be online");
        Preconditions.checkNotNull(activity, "activity cannot be null");

        checkProfileRegistry();

        currentProfile.addActivity(activity);

        checkProfileRegistry();
    }

    /**
     * Logs into the system with selected {@link Profile}.
     * @param profile the {@link Profile} to log in with.
     */
    public void loadProfile(Profile profile){
        Preconditions.checkNotNull(profile, "profile cannot be null.");
        checkProfileRegistry();

        boolean isAdded = false;
        Iterator<Profile> iterator = profiles.iterator();
        while(iterator.hasNext() && !isAdded)
            isAdded = (iterator.next() == profile);

        Preconditions.checkState(isAdded, "Profile has to be added to be loaded.");

        currentProfile = profile;
        loginStatus = Status.ONLINE; // profile is now online (logged into the system)

        checkProfileRegistry();
    }

    public void unloadProfile(){
        currentProfile = null;
        loginStatus = Status.OFFLINE;
    }

    /**
     * Follows another profile
     *
     * @param other the other profile to follow
     */
    public void follow(Profile other){
        Preconditions.checkNotNull(other, "other cannot be null.");
        checkProfileRegistry();

        currentProfile.follow(other);
    }

    /**
     * Unfollows another profile
     *
     * @param other the other profile to unfollow
     */
    public void unfollow(Profile other){
        Preconditions.checkNotNull(other, "other cannot be null.");
        checkProfileRegistry();

        currentProfile.unfollow(other);
    }


    public boolean isEmpty(){
        checkProfileRegistry();

        return profiles.isEmpty();
    }

    public boolean checkStatus(Status status){
        return (loginStatus == status);
    }

    private void checkProfileRegistry(){
        Preconditions.checkNotNull(profiles, "profiles cannot be null");
        Preconditions.checkNotNull(loginStatus, "loginStatus cannot be null");

        if(loginStatus == Status.ONLINE)
            Preconditions.checkNotNull(currentProfile, "currentProfile cannot be null when logged in");
    }

    public enum Status {
        ONLINE, // if user logged in
        OFFLINE // if user hasn't logged in yet
    }
}
