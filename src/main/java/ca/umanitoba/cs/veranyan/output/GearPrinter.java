package ca.umanitoba.cs.veranyan.output;

import ca.umanitoba.cs.veranyan.model.gear.Gear;
import com.google.common.base.Preconditions;

/**
 * The printer class for a {@link Gear}
 */
public class GearPrinter {
    private final Gear gear;

    /**
     * Constructor for GearPrinter.
     * @param gear the Gear to be printed. Must not be {@code null}.
     */
    public GearPrinter(Gear gear){
        this.gear = gear;

        checkGearPrinter();
    }

    /**
     * Prints out a Gear. This method prints to standard output (`System.out`).
     */
    public void print(){
        checkGearPrinter();

        System.out.print("Gear " + gear.getName() + " of getType "  + gear.getType() +
                " | average speed = " + gear.getAvgSpeed() + " meters per second.");

        checkGearPrinter();
    }

    /**
     * Ensures GearPrinter invariants are not violated.
     */
    private void checkGearPrinter(){
        Preconditions.checkNotNull(gear,"gear cannot be null");
    }
}
