package ca.umanitoba.cs.veranyan.model.gear;

import ca.umanitoba.cs.veranyan.model.exceptions.BlankNameException;
import ca.umanitoba.cs.veranyan.model.exceptions.NonPositiveSpeedException;
import com.google.common.base.Preconditions;

import java.util.Objects;

/**
 * A Gear. A gear is a particular bike that's used during a cycling activity.
 */
public class Gear {
    private final GearType type;
    private final String name;
    private final double avgSpeed;

    /**
     * Compact constructor for Gear
     *
     * @param type     the GearType of the gear. Must not be {@code null}.
     * @param name     the getName of the gear. Must not be {@code null} or blank.
     * @param avgSpeed the average speed of the gear. Must be positive.
     */
    private Gear(GearType type, String name, double avgSpeed) {
        this.type = type;
        this.name = name;
        this.avgSpeed = avgSpeed;

        checkGear();
    }

    public GearType getType() {
        checkGear();

        return type;
    }

    public String getName() {
        checkGear();

        return name;
    }

    public double getAvgSpeed() {
        checkGear();

        return avgSpeed;
    }

    private void checkGear(){
        Preconditions.checkNotNull(type, "getType of Gear cannot be null.");
        Preconditions.checkNotNull(name, "getName cannot be null.");
        Preconditions.checkState(!name.isBlank(), "getName cannot be blank.");
        Preconditions.checkState(avgSpeed > 0, "getAvgSpeed cannot be negative or 0.");
    }

    public static class GearBuilder{
        private GearType type;
        private String name;
        private int avgSpeed;

        public GearBuilder type(GearType type){
            Preconditions.checkNotNull(type, "type cannot be null");

            this.type = type;

            return this;
        }

        public GearBuilder name(String name) throws BlankNameException{
            Preconditions.checkNotNull(name, "name cannot be null");

            if(name.isBlank())
                throw new BlankNameException();

            this.name = name;

            return this;
        }

        public GearBuilder avgSpeed(int avgSpeed) throws NonPositiveSpeedException{
            if(avgSpeed <= 0)
                throw new NonPositiveSpeedException();

            this.avgSpeed = avgSpeed;

            return this;
        }

        public Gear build(){
            Preconditions.checkNotNull(type, "getType of Gear cannot be null.");
            Preconditions.checkNotNull(name, "getName cannot be null.");
            Preconditions.checkState(!name.isBlank(), "getName cannot be blank.");
            Preconditions.checkState(avgSpeed > 0, "getAvgSpeed cannot be negative or 0.");

            return new Gear(type, name, avgSpeed);
        }
    }
}
