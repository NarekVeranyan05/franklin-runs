package ca.umanitoba.cs.veranyan.model.map;

import ca.umanitoba.cs.veranyan.model.map.coordinate.CoordinateType;

public interface Gridable {
    public int getWidth();

    public int getLength();

    public CoordinateType getCoordinateType(int x, int y);

    public void appendToGrid(MapFeature feature);

    public void refillGrid();
}
