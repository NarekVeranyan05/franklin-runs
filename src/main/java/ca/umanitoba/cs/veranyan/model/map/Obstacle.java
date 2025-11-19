package ca.umanitoba.cs.veranyan.model.map;

import ca.umanitoba.cs.veranyan.model.exceptions.CoordinateOutOfBoundsException;
import ca.umanitoba.cs.veranyan.model.exceptions.InvalidGearTypeException;
import ca.umanitoba.cs.veranyan.model.exceptions.TopLeftBottomRightCoordMismatchException;
import ca.umanitoba.cs.veranyan.model.map.coordinate.Coordinate;
import ca.umanitoba.cs.veranyan.model.map.coordinate.CoordinateType;
import com.google.common.base.Preconditions;

import java.util.Collections;
import java.util.List;

/**
 * An {@link Obstacle} is an object which a {@link Route}
 * cannot pass through. Obstacles are on the {@link Map}.
 * An obstacle must be within {@link Map} boundaries.
 */
public final class Obstacle implements MapFeature {
    private final List<Coordinate> coordinates;

    private Obstacle(int topLeftX, int topLeftY, int bottomRightX, int bottomRightY) {
        this.coordinates = Coordinate.generateRectangle(
                CoordinateType.OBSTACLE,
                topLeftX,
                topLeftY,
                bottomRightX,
                bottomRightY
        );

        Collections.sort(coordinates);

        checkObstacle();
    }

    /**
     * Returns the coordinates that form the {@link Obstacle}
     *
     * @return a list of coordinates that form the {@link Obstacle}. Must not be {@code null}.
     */
    @Override
    public List<Coordinate> getCoordinates() {
        checkObstacle();

        return Collections.unmodifiableList(coordinates);
    }

    /**
     * Determines whether a point is withing the {@link Obstacle}.
     *
     * @param x the x-coordinate of the point.
     * @param y the y-coordinate of the point.
     * @return true if (x, y) is within {@link Obstacle} boundaries; false otherwise.
     */
    @Override
    public boolean contains(int x, int y) {
        checkObstacle();

        boolean contains = false;

        // going over all coordinates in the obstacle.
        for (int i = 0; i < coordinates.size() && !contains; i++)
            contains = (x == coordinates.get(i).x()) && (y == coordinates.get(i).y());

        checkObstacle();

        return contains;
    }

    /**
     * @return the number of coordinates that form the {@link Obstacle}
     */
    @Override
    public int getMeasure() {
        checkObstacle();

        return coordinates.size();
    }

    /**
     * {@link Obstacle} class invariants
     */
    private void checkObstacle() {
        Preconditions.checkNotNull(coordinates, "coordinates cannot be null.");
        Preconditions.checkState(!coordinates.isEmpty(), "coordinates cannot be empty");

        for (var coord : coordinates) {
            Preconditions.checkNotNull(coord, "coordinates entry cannot be null.");
            Preconditions.checkState(coord.type().equals(CoordinateType.OBSTACLE), "coordinates entry getType must be OBSTACLE");
        }
    }

    /**
     * Builder class for {@link Obstacle}
     */
    public static class ObstacleBuilder{
        private int topLeftX;
        private int topLeftY;
        private int bottomRightX;
        private int bottomRightY;

        public ObstacleBuilder(){
            this.topLeftX = -1;
            this.topLeftY = -1;
            this.bottomRightX = -1;
            this.bottomRightY = -1;
        }

        /**
         * Sets the top-left x-coordinate of the {@link Obstacle}
         * @param x the x-coordinate
         * @return the builder instance
         * @throws CoordinateOutOfBoundsException if the coordinate is out of {@link Map} bounds
         * @throws TopLeftBottomRightCoordMismatchException if the top-left coordinate is to the right and below the bottom-right coordinate
         */
        public ObstacleBuilder topLeftX(int x) throws CoordinateOutOfBoundsException, TopLeftBottomRightCoordMismatchException {
            if(x < 1)
                throw new CoordinateOutOfBoundsException();
            if(bottomRightX != -1 && x > bottomRightX)
                throw new TopLeftBottomRightCoordMismatchException();
            topLeftX = x;

            return this;
        }

        /**
         * Sets the top-left y-coordinate of the {@link Obstacle}
         * @param y the y-coordinate
         * @return the builder instance
         * @throws CoordinateOutOfBoundsException if the coordinate is out of {@link Map} bounds
         * @throws TopLeftBottomRightCoordMismatchException if the top-left coordinate is to the right and below the bottom-right coordinate
         */
        public ObstacleBuilder topLeftY(int y) throws CoordinateOutOfBoundsException, TopLeftBottomRightCoordMismatchException {
            if(y < 1)
                throw new CoordinateOutOfBoundsException();
            if(bottomRightY != -1 && y > bottomRightY)
                throw new TopLeftBottomRightCoordMismatchException();

            topLeftY = y;

            return this;
        }

        /**
         * Sets the bottom-right x-coordinate of the {@link Obstacle}
         * @param x the x-coordinate
         * @return the builder instance
         * @throws CoordinateOutOfBoundsException if the coordinate is out of {@link Map} bounds
         * @throws TopLeftBottomRightCoordMismatchException if the top-left coordinate is to the right and below the bottom-right coordinate
         */
        public ObstacleBuilder bottomRightX(int x) throws CoordinateOutOfBoundsException, TopLeftBottomRightCoordMismatchException {
            if(x < 1)
                throw new CoordinateOutOfBoundsException();
            if(topLeftX != -1 && x < topLeftX)
                throw new TopLeftBottomRightCoordMismatchException();

            bottomRightX = x;

            return this;
        }

        /**
         * Sets the bottom-right y-coordinate of the {@link Obstacle}
         * @param y the y-coordinate
         * @return the builder instance
         * @throws CoordinateOutOfBoundsException if the coordinate is out of {@link Map} bounds
         * @throws TopLeftBottomRightCoordMismatchException if the top-left coordinate is to the right and below the bottom-right coordinate
         */
        public ObstacleBuilder bottomRightY(int y) throws CoordinateOutOfBoundsException, TopLeftBottomRightCoordMismatchException {
            if(y < 1)
                throw new CoordinateOutOfBoundsException();
            if(topLeftY != -1 && y < topLeftY)
                throw new TopLeftBottomRightCoordMismatchException();

            bottomRightY = y;

            return this;
        }

        public Obstacle build(){
            Preconditions.checkState(topLeftX != -1, "topLeftX cannot be negative");
            Preconditions.checkState(topLeftY != -1, "topLeftY cannot be negative");
            Preconditions.checkState(bottomRightX != -1, "bottomRightX cannot be negative");
            Preconditions.checkState(bottomRightY != -1, "bottomRightY cannot be negative");

            return new Obstacle(
                    topLeftX,
                    topLeftY,
                    bottomRightX,
                    bottomRightY
            );
        }
    }
}