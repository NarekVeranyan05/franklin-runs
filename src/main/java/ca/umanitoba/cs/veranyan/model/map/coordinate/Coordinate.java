package ca.umanitoba.cs.veranyan.model.map.coordinate;

import ca.umanitoba.cs.veranyan.model.map.Map;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
/**
 * A Coordinate instance contains the x- and y-values of a point
 * (x, y) on the {@link Map} grid.
 */
public record Coordinate(CoordinateType type, int x, int y) implements Comparable<Coordinate> {
    /**
     * Generates a List of coordinates that form a rectangle
     * @param type the coordinate getType to fill the rectangle with
     * @param topLeftX the x-coordinate of the top-left vertex of the rectangle
     * @param topLeftY the y-coordinate of the top-left vertex of the rectangle
     * @param bottomRightX the x-coordinate of the bottom-right vertex of the rectangle
     * @param bottomRightY the y-coordinate of the bottom-right vertex of the rectangle
     * @return the List of coordinates that form the rectangle. Must be sorted
     */
    public static List<Coordinate> generateRectangle(CoordinateType type, int topLeftX, int topLeftY, int bottomRightX, int bottomRightY){
        Preconditions.checkNotNull(type, "type cannot be null");

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
        checkCoordinate();

        return new Coordinate(type, x, y - 1);
    }

    public Coordinate getRight(){
        checkCoordinate();

        return new Coordinate(type, x, y + 1);
    }

    public Coordinate getAbove(){
        checkCoordinate();

        return new Coordinate(type, x - 1, y);
    }

    public Coordinate getBelow(){
        checkCoordinate();

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
        checkCoordinate();

        return new Coordinate[] {
                getLeft(),
                getRight(),
                getAbove(),
                getBelow()
        };
    }

    /**
     * Determines whether two coordinates differ in 1 unit (on either x- or y-axis, but not both)
     * @param coord the coordinate to determine neighbourhood with
     * @return {@code true} if the two coordinates are neighbours, {@code false} otherwise
     */
    public boolean isNeighbourOf(Coordinate coord){
        Preconditions.checkNotNull(coord, "coord cannot be null");
        checkCoordinate();

        boolean result = (Math.abs(this.x - coord.x) == 1);

        if(result){
            result = (this.y - coord.y == 0);
        }
        else if(this.x == coord.x){
            result = (Math.abs(this.y - coord.y) == 1);
        }

        checkCoordinate();

        return result;
    }

    /**
     * Determines if this coordinate is equal to an object
     * @param o the object to compare this coordinate with
     * @return {@code false} if {@code o} is null or not an instance of Coordinate
     * <pr>
     * {@code true} if {@code o} is an instance of Coordinate and has identical
     * x- and y-coordinates to this coordinate
     */
    @Override
    public boolean equals(Object o) {
        checkCoordinate();
        boolean result;

        if(o == null)
            result = false;
        else if(o instanceof Coordinate other){
            result = (this.x == other.x) && (this.y == other.y);
        }
        else result = false;

        checkCoordinate();

        return result;
    }

    @Override
    public int compareTo(Coordinate o) {
        checkCoordinate();

        int result;

        if(this.x < o.x || this.y < o.y)
            result = -1;
        else if(this.x == o.x && this.y == o.y)
            result =  0;
        else result = 1;

        checkCoordinate();

        return result;
    }

    private void checkCoordinate(){
        Preconditions.checkNotNull(type, "type cannot be null");
    }
}
