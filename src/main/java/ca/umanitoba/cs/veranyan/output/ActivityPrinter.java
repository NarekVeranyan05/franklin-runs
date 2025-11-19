package ca.umanitoba.cs.veranyan.output;

import ca.umanitoba.cs.veranyan.model.Activity;
import com.google.common.base.Preconditions;

/**
 * The printer class for an {@link Activity}
 */
public class ActivityPrinter {
    private static final int METERS_PER_STEP = 10;

    private final Activity activity;

    /**
     * Constructor for the {@link Activity}.
     * @param activity the {@link Activity} to be printed. Must not be {@code null}.
     */
    public ActivityPrinter(Activity activity) {
        this.activity = activity;

        checkActivityPrinter();
    }

    /**
     * Prints out an {@link Activity}. This method prints to standard output (`System.out`).
     */
    public void print() {
        checkActivityPrinter();

        System.out.print("Activity start = " + activity.getStart() +
                " | end = " + activity.getEnd() + " | distance passed = " +
                (activity.getRoute().getMeasure() * METERS_PER_STEP) + " meters | average speed = " +
                activity.getAvgSpeed() + " meters per second.\n");

        // gear info
        if(activity.getGear() != null) {
            System.out.print("Gear used: ");
            new GearPrinter(activity.getGear()).print();
        }

        checkActivityPrinter();
    }

    /**
     * Ensures {@link ActivityPrinter} invariants are not violated.
     */
    private void checkActivityPrinter(){
        Preconditions.checkNotNull(activity, "activity cannot be null");
    }
}
