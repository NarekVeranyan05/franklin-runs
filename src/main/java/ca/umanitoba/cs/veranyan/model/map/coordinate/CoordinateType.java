package ca.umanitoba.cs.veranyan.model.map.coordinate;

/**
 * The possible types of a {@link Coordinate} on the {@link ca.umanitoba.cs.veranyan.model.map.Map}
 */
public enum CoordinateType {
    EMPTY,
    ROUTE,
    OBSTACLE,
    VISITED,
    BORDER // used internally, only for the Map grid borders
}
