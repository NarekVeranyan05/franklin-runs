package ca.umanitoba.cs.veranyan.persistence;

import ca.umanitoba.cs.veranyan.model.Profile;

import java.util.Set;

/**
 * {@link ProfilePersistence} interface for a persistence layer class that
 * manages the profiles in the system.
 */
public interface ProfilePersistence {
    /**
     * Saves the given profile in storage
     * @param profile the profile to save
     */
    void save(Profile profile);

    /**
     * Loads all the profiles back from storage to the system
     * @return the set of loaded profiles
     */
    Set<Profile> loadProfiles();

}
