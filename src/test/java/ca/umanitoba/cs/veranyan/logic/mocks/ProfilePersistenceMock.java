package ca.umanitoba.cs.veranyan.logic.mocks;

import ca.umanitoba.cs.veranyan.model.Profile;
import ca.umanitoba.cs.veranyan.persistence.ProfilePersistence;

import java.util.Set;

public class ProfilePersistenceMock implements ProfilePersistence {
    @Override
    public void save(Profile profile) { }

    @Override
    public Set<Profile> loadProfiles() {
        return Set.of();
    }
}
