package ca.umanitoba.cs.veranyan.logic;

import ca.umanitoba.cs.veranyan.model.Profile;
import ca.umanitoba.cs.veranyan.model.exceptions.DuplicateGearException;
import ca.umanitoba.cs.veranyan.model.exceptions.InvalidGearNameException;
import ca.umanitoba.cs.veranyan.model.gear.Gear;
import com.google.common.base.Preconditions;
import java.util.SortedSet;

public class GearManager {
    private final Profile profile;

    public GearManager(Profile profile){
        this.profile = profile;

        checkGearManager();
    }

    /**
     * @return the gears of the current profile
     */
    public SortedSet<Gear> getGears(){
        checkGearManager();

        return profile.getGears();
    }

    public Gear getGear(String name) throws InvalidGearNameException{
        Preconditions.checkNotNull(name, "name cannot be null");

        // gear with matching name not found
        if (profile.getGears().stream().filter(gear -> gear.getName().equals(name)).toArray().length != 1)
            throw new InvalidGearNameException();

        return (Gear) profile.getGears().stream().
                filter(gear -> gear.getName().equals(name)).
                toArray()[0]; // there will be only a single, unique gear
    }

    /**
     * Adds a new gear to the current profile
     * @param gear the gear to add
     * @return true if gear was added, false if gear with matching getName already exists.
     */
    public void addGear(Gear gear) throws DuplicateGearException {
        Preconditions.checkNotNull(gear, "Gear cannot be null");
        checkGearManager();

        profile.addGear(gear);

        checkGearManager();
    }

    /**
     * Removes a gear from the current profile
     * @param gear the gear to be removed
     */
    public void removeGear(Gear gear){
        Preconditions.checkNotNull(gear, "gear cannot be null");
        checkGearManager();

        profile.removeGear(gear);

        checkGearManager();
    }


    private void checkGearManager(){
        Preconditions.checkNotNull(profile, "profile cannot be null");
    }
}
