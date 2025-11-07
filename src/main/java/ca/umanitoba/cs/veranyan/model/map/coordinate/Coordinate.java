package ca.umanitoba.cs.veranyan.model.map.coordinate;

import ca.umanitoba.cs.veranyan.model.map.Map;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
/**
 * A Coordinate instance contains the x- and y-values of a point
 * (x, y) on the {@link Map} grid.
 */
public record Coordinate(CoordinateType type, int x, int y) {
    /**
     * Generates a List of coordinates that form a rectangle
     * @param type the coordinate getType to fill the rectangle with
     * @param topLeftX the x-coordinate of the top-left vertex of the rectangle
     * @param topLeftY the y-coordinate of the top-left vertex of the rectangle
     * @param bottomRightX the x-coordinate of the bottom-right vertex of the rectangle
     * @param bottomRightY the y-coordinate of the bottom-right vertex of the rectangle
     * @return the List of coordinates that form the rectangle. Must be sorted
     */
    public static List<Coordinate> generateRectangle(
            CoordinateType type, int topLeftX, int topLeftY, int bottomRightX, int bottomRightY){

        var coordinates = new ArrayList<Coordinate>();

        for(int x = topLeftX; x <= bottomRightX; x++){
            for(int y = topLeftY; y <= bottomRightY; y++){
                coordinates.add(
                        new Coordinate(type, x, y)
                );
            }
        }

        Preconditions.checkNotNull(coordinates, "coordinates cannot be null.");
        return coordinates;
    }

    /**
     * Compact constructor for Coordinate.
     *
     * @param x the non-negative x-component of the point (x, y) represented by the instance.
     * @param y the non-negative y-component of the point (x, y) represented by the instance.
     */
    public Coordinate {
        Preconditions.checkNotNull(type, "getType cannot be null");
    }

    public Coordinate getLeft(){
        return new Coordinate(type, x, y - 1);
    }

    public Coordinate getRight(){
        return new Coordinate(type, x, y + 1);
    }

    public Coordinate getAbove(){
        return new Coordinate(type, x - 1, y);
    }

    public Coordinate getBelow(){
        return new Coordinate(type, x + 1, y);
    }

    /**
     * @return all the four neighbour-coordinates of this coordinate in an array of length 4.
     * <p>
     * The order that the neighbours are stored in and their indices are:
     * <p>
     * 0. left neighbour
     * 1. right neighbour
     * 2. upper neighbour
     * 3. lower neighbour
     */
    public Coordinate[] getNeighbours(){
        return new Coordinate[] {
                getLeft(),
                getRight(),
                getAbove(),
                getBelow()
        };
    }

    /**
     * Determines if this coordinate is equal to an object
     * @param o the object to compare this coordinate with
     * @return {@code false} if {@code o} is null or not an instance of Coordinate
     * <pr>
     * {@true} if {@code o} is an instance of Coordinate and has identical
     * x- and y-coordinates to this coordinate
     */
    @Override
    public final boolean equals(Object o) {
        boolean result;

        if(o == null)
            result = false;
        else if(o instanceof Coordinate other){
            result = (this.x == other.x) && (this.y == other.y);
        }
        else result = false;

        return result;
    }
}
