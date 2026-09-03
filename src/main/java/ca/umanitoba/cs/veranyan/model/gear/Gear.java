package ca.umanitoba.cs.veranyan.model.gear;

import ca.umanitoba.cs.veranyan.model.exceptions.BlankNameException;
import ca.umanitoba.cs.veranyan.model.exceptions.NonPositiveSpeedException;
import com.google.common.base.Preconditions;

/**
 * A {@link Gear} is a particular bike that's used during a cycling {@link ca.umanitoba.cs.veranyan.model.Activity}.
 */
public class Gear {
    private final GearType type;
    private final String name;
    private final double avgSpeed;

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

    /**
     * Class invariants for {@link Gear}
     */
    private void checkGear(){
        Preconditions.checkNotNull(type, "getType of Gear cannot be null.");
        Preconditions.checkNotNull(name, "getName cannot be null.");
        Preconditions.checkState(!name.isBlank(), "getName cannot be blank.");
        Preconditions.checkState(avgSpeed > 0, "getAvgSpeed cannot be negative or 0.");
    }

    /**
     * Builder class for {@link Gear}
     */
    public static class GearBuilder{
        private GearType type;
        private String name;
        private int avgSpeed;

        /**
         * Sets the type of the {@link Gear} to build
         * @param type the type of the {@link Gear}. Must not be {@code null}.
         * @return the builder instance
         */
        public GearBuilder type(GearType type){
            Preconditions.checkNotNull(type, "type cannot be null");

            this.type = type;

            return this;
        }

        /**
         * Sets the name of the {@link Gear} to build
         * @param name the name of the {@link Gear}. Must not be {@code null}. Must have at least one character.
         * @return the builder instance
         */
        public GearBuilder name(String name) throws BlankNameException{
            Preconditions.checkNotNull(name, "name cannot be null");

            if(name.isBlank())
                throw new BlankNameException();

            this.name = name;

            return this;
        }

        /**
         * Sets the type of the {@link Gear} to build
         * @param avgSpeed the type of the {@link Gear}. Must be positive.
         * @return the builder instance
         */
        public GearBuilder avgSpeed(int avgSpeed) throws NonPositiveSpeedException{
            if(avgSpeed <= 0)
                throw new NonPositiveSpeedException();

            this.avgSpeed = avgSpeed;

            return this;
        }

        public Gear build(){
            Preconditions.checkNotNull(type, "type cannot be null.");
            Preconditions.checkNotNull(name, "name cannot be null.");
            Preconditions.checkState(!name.isBlank(), "name cannot be blank.");
            Preconditions.checkState(avgSpeed > 0, "avgSpeed cannot be negative or 0.");

            return new Gear(type, name, avgSpeed);
        }
    }
}
