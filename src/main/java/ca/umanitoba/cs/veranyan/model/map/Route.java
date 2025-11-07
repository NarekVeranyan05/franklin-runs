package ca.umanitoba.cs.veranyan.model.map;

import ca.umanitoba.cs.veranyan.model.Profile;
import ca.umanitoba.cs.veranyan.model.exceptions.CoordinateOutOfBoundsException;
import ca.umanitoba.cs.veranyan.model.map.coordinate.Coordinate;
import ca.umanitoba.cs.veranyan.model.map.coordinate.CoordinateType;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A {@link Route} is the path of an {@link ca.umanitoba.cs.veranyan.model.Activity} through the {@link Map} grid.
 * Contains information about the path taken and its distance.
 * A Route cannot overlap with any {@link Obstacle} on the Map.
 */
public class Route implements MapFeature, Cloneable{
    private static final int UP = 1;
    private static final int RIGHT = 2;
    private static final int DOWN = 3;
    private static final int LEFT = 4;

    List<Coordinate> coordinates;

    /**
     * A constructor for Route. A route cannot overlap with any {@link Obstacle} on the Map.
     *
     * @param x the non-negative x-coordinate of the starting point (x, y) of the Route.
     * @param y the non-negative y-coordinate of the starting point (x, y) of the Route.
     */
    private Route(int x, int y) {
        coordinates = new ArrayList<>();
        coordinates.add(new Coordinate(CoordinateType.ROUTE, x, y)); // adding starting point (x, y)

        checkRoute();
    }

    /**
     * @return unmodifiable list of coordinates in the route
     */
    @Override
    public List<Coordinate> getCoordinates() {
        checkRoute();

        return Collections.unmodifiableList(coordinates);
    }

    /**
     * @return the number of steps that the {@link Profile}
     * passed in the route.
     * @implNote a step is one coordinate on the {@link Map} grid.
     */
    @Override
    public int getMeasure() {
        checkRoute();

        return coordinates.size();
    }

    public void addCoordinate(Coordinate coordinate){
        coordinates.add(new Coordinate(CoordinateType.ROUTE, coordinate.x(), coordinate.y()));
    }

    /**
     * Makes a move in a particular direction on the Map, adding indicated number of coordinates to the Activity route.
     *
     * @param direction the direction to move. Must be any of and only of [UP = 1, RIGHT = 2, DOWN = 3, LEFT = 4].
     * @param steps     the number of steps of the move. Must be non-negative.
     * @implNote a step is one coordinate on the {@link Map} grid.
     */
    public void move(int direction, int steps) {
        checkRoute();

        var currCoordinate = coordinates.get(coordinates.size() - 1); // the current (x, y) coordinate

        switch (direction) {
            case UP -> {
                for (int i = 1; i <= steps; i++) {
                    currCoordinate = currCoordinate.getAbove();
                    coordinates.add(currCoordinate);
                }
            }
            case RIGHT -> {
                for (int i = 1; i <= steps; i++) {
                    currCoordinate = currCoordinate.getRight();
                    coordinates.add(currCoordinate);
                }
            }
            case DOWN -> {
                for (int i = 1; i <= steps; i++) {
                    currCoordinate = currCoordinate.getBelow();
                    coordinates.add(currCoordinate);
                }
            }
            case LEFT -> {
                for (int i = 1; i <= steps; i++) {
                    currCoordinate = currCoordinate.getLeft();
                    coordinates.add(currCoordinate);
                }
            }
        }

        checkRoute();
    }

    /**
     * Determines whether a point is within the Route.
     *
     * @param x the x-coordinate of the point.
     * @param y the y-coordinate of the point.
     * @return true if (x, y) is in the Route; false otherwise.
     */
    @Override
    public boolean contains(int x, int y) {
        checkRoute();

        boolean contains = false;

        // going over all coordinates in the Route.
        for (int i = 0; i < coordinates.size() && !contains; i++)
            contains = (x == coordinates.get(i).x()) && (y == coordinates.get(i).y());

        checkRoute();

        return contains;
    }

    public Route clone() {
        try {
            Route clone = (Route) super.clone();
            clone.coordinates = new ArrayList<>(this.coordinates);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException();
        }
    }

    /**
     * Ensures Route invariants are not violated.
     */
    private void checkRoute() {
        Preconditions.checkNotNull(coordinates, "coordinates cannot be null.");
        Preconditions.checkState(!coordinates.isEmpty(), "coordinates must have at least one entry.");
    }

    public static class RouteBuilder{
        private int x = -1;
        private int y = -1;

        public RouteBuilder x(int x) throws CoordinateOutOfBoundsException{
            if(x < 1)
                throw new CoordinateOutOfBoundsException();

            this.x = x;

            return this;
        }

        public RouteBuilder y(int y) throws CoordinateOutOfBoundsException{
            if(y < 1)
                throw new CoordinateOutOfBoundsException();

            this.y = y;

            return this;
        }

        public Route build(){
            Preconditions.checkState(x >= 1, "x cannot be negative");
            Preconditions.checkState(y >= 1, "y cannot be negative");

            return new Route(x, y);
        }
    }
}
