package ca.umanitoba.cs.veranyan.model.map;

import ca.umanitoba.cs.veranyan.model.exceptions.CoordinateOutOfBoundsException;
import ca.umanitoba.cs.veranyan.model.map.coordinate.Coordinate;
import ca.umanitoba.cs.veranyan.model.map.coordinate.CoordinateType;
import com.google.common.base.Preconditions;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * An obstacle is an object which a {@link Route}
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

        checkObstacle();
    }

    /**
     * Returns the coordinates that form the obstacle
     *
     * @return a list of coordinates that form the obstacle. Must not be {@code null}.
     */
    @Override
    public List<Coordinate> getCoordinates() {
        checkObstacle();

        return Collections.unmodifiableList(coordinates);
    }

    /**
     * Determines whether a point is withing the obstacle.
     *
     * @param x the x-coordinate of the point.
     * @param y the y-coordinate of the point.
     * @return true if (x, y) is within obstacle boundaries; false otherwise.
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
     * @return the number of coordinates that form the obstacle
     */
    @Override
    public int getMeasure() {
        checkObstacle();

        return coordinates.size();
    }

    private void checkObstacle() {
        Preconditions.checkNotNull(coordinates, "coordinates cannot be null.");
        Preconditions.checkState(!coordinates.isEmpty(), "coordinates cannot be empty");

        for (var coord : coordinates) {
            Preconditions.checkNotNull(coord, "coordinates entry cannot be null.");
            Preconditions.checkState(coord.type().equals(CoordinateType.OBSTACLE), "coordinates entry getType must be OBSTACLE");
        }
    }

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

        public ObstacleBuilder topLeftX(int x) throws CoordinateOutOfBoundsException{
            if(x < 1)
                throw new CoordinateOutOfBoundsException();
            if(bottomRightX != -1 && x > bottomRightX)
                throw new CoordinateOutOfBoundsException();
            topLeftX = x;

            return this;
        }

        public ObstacleBuilder topLeftY(int y) throws CoordinateOutOfBoundsException{
            if(y < 1)
                throw new CoordinateOutOfBoundsException();
            if(bottomRightY != -1 && y > bottomRightY)
                throw new CoordinateOutOfBoundsException();

            topLeftY = y;

            return this;
        }

        public ObstacleBuilder bottomRightX(int x) throws CoordinateOutOfBoundsException{
            if(x < 1)
                throw new CoordinateOutOfBoundsException();
            if(topLeftX != -1 && x < topLeftX)
                throw new CoordinateOutOfBoundsException();

            bottomRightX = x;

            return this;
        }

        public ObstacleBuilder bottomRightY(int y) throws CoordinateOutOfBoundsException{
            if(y < 1)
                throw new CoordinateOutOfBoundsException();
            if(topLeftY != -1 && y < topLeftY)
                throw new CoordinateOutOfBoundsException();

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