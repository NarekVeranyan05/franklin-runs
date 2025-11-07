package ca.umanitoba.cs.veranyan.logic.assets;

import ca.umanitoba.cs.veranyan.model.exceptions.InvalidGearTypeException;
import com.google.common.base.Preconditions;

public enum RouteFilterType {
    ALL,
    OWN, // current profile's routes
    FRIENDS, // current profile's firends' routes
    OWN_AND_FRIENDS;

    public static RouteFilterType fromString(String value) throws InvalidGearTypeException {
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
