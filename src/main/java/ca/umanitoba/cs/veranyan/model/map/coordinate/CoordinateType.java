package ca.umanitoba.cs.veranyan.model.map.coordinate;

public enum CoordinateType {
    EMPTY,
    ROUTE,
    OBSTACLE,
    VISITED,
    CURRENT,
    BORDER // used internally, only for the Map grid borders
}
