package ca.umanitoba.cs.veranyan.output;

import ca.umanitoba.cs.veranyan.model.map.Obstacle;
import com.google.common.base.Preconditions;

/**
 * The printer class for an {@link Obstacle}
 */
public class ObstaclePrinter {
    private final Obstacle obstacle;

    /**
     * Constructor for ObstaclePrinter
     * @param obstacle the Obstacle to be printed. Must not be {@code null}.
     */
    public ObstaclePrinter(Obstacle obstacle){
        this.obstacle = obstacle;

        checkObstaclePrinter();
    }

    /**
     * Prints an Obstacle and its properties. This method prints to standard output (`System.out`).
     */
    public void print(){
        checkObstaclePrinter();

        var topLeftCoord = obstacle.getCoordinates().get(0);
        var bottomRightCoord = obstacle.getCoordinates().get(obstacle.getCoordinates().size() - 1);

        int width = bottomRightCoord.x() - topLeftCoord.x() + 1;
        int length = bottomRightCoord.y() - topLeftCoord.y() + 1;
        System.out.printf("Obstacle has width %d and length %d.\n", width, length);
        System.out.printf("Coordinates of upper-left and lower-right vertices: (%d, %d), (%d, %d).",
                topLeftCoord.x(), topLeftCoord.y(), bottomRightCoord.x(), bottomRightCoord.y());

        checkObstaclePrinter();
    }

    /**
     * Ensures Obstacle invariants are not violated.
     */
    private void checkObstaclePrinter() {
        Preconditions.checkNotNull(obstacle, "obstacle cannot be null");
    }

}
