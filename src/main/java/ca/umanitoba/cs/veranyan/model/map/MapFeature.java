package ca.umanitoba.cs.veranyan.model.map;

import ca.umanitoba.cs.veranyan.model.map.coordinate.Coordinate;

import java.util.List;

/**
 * A {@link MapFeature} represents a feature on {@link Map}
 */
public interface MapFeature {
    List<Coordinate> getCoordinates();
    int getMeasure();
    boolean contains(int x, int y);
}
