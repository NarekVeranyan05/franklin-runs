package ca.umanitoba.cs.veranyan.model.map;

import ca.umanitoba.cs.veranyan.model.exceptions.CoordinateOutOfBoundsException;
import ca.umanitoba.cs.veranyan.model.Profile;
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
public class Route implements MapFeature, Cloneable {
    private static final int UP = 1;
    private static final int RIGHT = 2;
    private static final int DOWN = 3;
    private static final int LEFT = 4;

    private List<Coordinate> coordinates;

    /**
     * A constructor for {@link Route}. A {@link Route} cannot overlap with any {@link Obstacle} on the {@link Map}.
     *
     * @param x the non-negative x-coordinate of the starting point (x, y) of the {@link Route}.
     * @param y the non-negative y-coordinate of the starting point (x, y) of the {@link Route}.
     */
    private Route(int x, int y) {
        coordinates = new ArrayList<>();
        coordinates.add(new Coordinate(CoordinateType.ROUTE, x, y)); // adding starting point (x, y)

        checkRoute();
    }

    /**
     * @return unmodifiable list of coordinates in the {@link Route}
     */
    @Override
    public List<Coordinate> getCoordinates() {
        checkRoute();

        return Collections.unmodifiableList(coordinates);
    }

    /**
     * @return the number of steps that the {@link Profile}
     * passed in the {@link Route}.
     * @implNote a step is one coordinate on the {@link Map} grid.
     */
    @Override
    public int getMeasure() {
        checkRoute();

        return coordinates.size();
    }

    /**
     * Adds a {@link Coordinate} to the {@link Route}
     * @param coordinate the {@link Coordinate} to add
     */
    public void addCoordinate(Coordinate coordinate){
        Preconditions.checkNotNull(coordinate, "coordinate cannot be null");
        checkRoute();

        coordinates.add(new Coordinate(CoordinateType.ROUTE, coordinate.x(), coordinate.y()));

        checkRoute();
    }

    /**
     * Makes a move in a particular direction on the {@link Map}, adding indicated number of coordinates to the {@link Route}.
     *
     * @param direction the direction to move. Must be any of and only of [UP = {@code 1}, RIGHT = {@code 2}, DOWN = {@code 3}, LEFT = {@code 4}].
     * @param steps     the number of steps of the move. Must be non-negative.
     * @implNote a step is one coordinate on the {@link Map} grid.
     */
    public void move(int direction, int steps) {
        Preconditions.checkState(direction >= UP && direction <= LEFT,
                "direction cannot be out of range [" + UP + ", " + LEFT + "]");
        Preconditions.checkState(steps >= 0, "steps cannot be negative");

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
     * Determines whether a point is within the {@link Route}.
     *
     * @param x the x-coordinate of the point.
     * @param y the y-coordinate of the point.
     * @return {@code true} if (x, y) is in the {@link Route}; {@code false} otherwise.
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
        checkRoute();

        try {
            Route clone = (Route) super.clone();
            clone.coordinates = new ArrayList<>(this.coordinates);

            checkRoute();

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException();
        }
    }

    /**
     * Ensures {@link Route} invariants are not violated.
     */
    private void checkRoute() {
        Preconditions.checkNotNull(coordinates, "coordinates cannot be null.");
        Preconditions.checkState(!coordinates.isEmpty(), "coordinates must have at least one entry.");

        for (var coord : coordinates) {
            Preconditions.checkNotNull(coord, "coordinates entry cannot be null.");
            Preconditions.checkState(coord.type().equals(CoordinateType.ROUTE), "coordinates entry type must be ROUTE");
        }
    }

    /**
     * Builder class for {@link Route}
     */
    public static class RouteBuilder{
        private final List<Coordinate> validCoordinates = new ArrayList<>();
        private final List<Coordinate> coordinates = new ArrayList<>();

        /**
         * Adds an additional {@link Coordinate} to the {@link Route}
         * @param coordinate the coordinate to add
         * @return the builder instance
         */
        public RouteBuilder withCoordinate(Coordinate coordinate) throws CoordinateOutOfBoundsException{
            Preconditions.checkState(validCoordinates.isEmpty(), "cannot add regular coordinates and violate the expected behaviour that all coordinates are valid");
            Preconditions.checkNotNull(coordinate, "coordinate cannot be null");
            Preconditions.checkState(coordinate.type() == CoordinateType.ROUTE, "finalCoordinate must be of CoordinateType ROUTE");

            if(coordinate.x() < 1 || coordinate.y() < 1)
                throw new CoordinateOutOfBoundsException();

            coordinates.add(coordinate);

            return this;
        }

        public Route build(){
            Preconditions.checkState(!coordinates.isEmpty() || !validCoordinates.isEmpty(), "either coordinates or validCoordinates must be non-empty");

            var coords = validCoordinates.isEmpty() ? coordinates : validCoordinates;

            var route = new Route(coords.get(0).x(), coords.get(0).y());

            coords.remove(0);
            for (var coord : coords) {
                route.addCoordinate(coord);
            }

            return route;
        }
    }
}
