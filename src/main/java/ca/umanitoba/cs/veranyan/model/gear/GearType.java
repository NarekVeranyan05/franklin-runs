package ca.umanitoba.cs.veranyan.model.gear;

import ca.umanitoba.cs.veranyan.model.exceptions.InvalidGearTypeException;
import com.google.common.base.Preconditions;

/**
 * Types of a {@link Gear} instance to specify {@link Gear} characteristics.
 */
public enum GearType {
    ROAD_BIKE,
    MOUNTAIN_BIKE,
    COMMUTER_BIKE,
    ELECTRIC_BIKE,
    TANDEM_BIKE;

    /**
     * Converts a string to a gear type
     * @param value the string to convert to gear type
     * @return the corresponding gear type
     * @throws InvalidGearTypeException if the string does not correspond to any of the available gear types
     */
    public static GearType fromString(String value) throws InvalidGearTypeException {
        Preconditions.checkNotNull(value, "Value passed to enum should not be null.");

        return switch(value) {
            case "ROAD_BIKE" -> ROAD_BIKE;
            case "MOUNTAIN_BIKE" -> MOUNTAIN_BIKE;
            case "COMMUTER_BIKE" -> COMMUTER_BIKE;
            case "ELECTRIC_BIKE" -> ELECTRIC_BIKE;
            case "TANDEM_BIKE" -> TANDEM_BIKE;
            default -> throw new InvalidGearTypeException();
        };
    }
}
