package ca.umanitoba.cs.veranyan.logic;

import ca.umanitoba.cs.veranyan.logic.exceptions.RouteObstacleOverlapException;
import ca.umanitoba.cs.veranyan.model.exceptions.CoordinateOutOfBoundsException;
import ca.umanitoba.cs.veranyan.model.map.Map;
import ca.umanitoba.cs.veranyan.model.map.Obstacle;
import ca.umanitoba.cs.veranyan.model.map.coordinate.CoordinateType;
import com.google.common.base.Preconditions;

/**
 * {@link RouteManager} manages the business logic of moving through the {@link Map} for a particular {@link ca.umanitoba.cs.veranyan.model.map.Route}
 */
public class RouteManager {
    private static final int UP = 1;
    private static final int RIGHT = 2;
    private static final int DOWN = 3;
    private static final int LEFT = 4;

    private final Map map;

    public RouteManager(Map map){
        this.map = map;

        checkRouteManager();
    }

    /**
     * Moves through the {@link Map} for the given {@link MapManager.ProcessedRoute}
     * @param route the {@link MapManager.ProcessedRoute} to modify when moving
     * @param direction the direction to move [UP = {@code 1}, RIGHT = {@code 2}, DOWN = {@code 3}, LEFT = {@code 4}].
     * @param numSteps the number of steps to move in the given direction
     * @throws RouteObstacleOverlapException if the {@link Obstacle} overlaps with the {@link MapManager.ProcessedRoute} if moving in the indicated way
     * @throws CoordinateOutOfBoundsException if the {@link MapManager.ProcessedRoute} is out of {@link Map} boundaries if moving in the indicated way
     */
    public void doMove(MapManager.ProcessedRoute route, int direction, int numSteps) throws RouteObstacleOverlapException, CoordinateOutOfBoundsException {
        Preconditions.checkNotNull(route, "moveIn cannot be null");
        Preconditions.checkState(direction >= UP && direction <= LEFT, "direction should be in the range of [1, 4]");
        Preconditions.checkState(numSteps >= 0, "numSteps cannot be negative");
        checkRouteManager();

        var currCoord = route.getCoordinates().get(route.getCoordinates().size()-1);

        switch (direction){
            case UP -> {
                if(currCoord.x() - numSteps < 1)
                    throw new CoordinateOutOfBoundsException();
                for(int i = 1; i <= numSteps; i++){
                    currCoord = currCoord.getAbove();

                    if(map.getCoordinateType(currCoord.x(), currCoord.y()) == CoordinateType.OBSTACLE)
                        throw new RouteObstacleOverlapException();
                }

                route.move(direction, numSteps);
            }
            case RIGHT -> {
                if(currCoord.y() + numSteps > map.getWidth())
                    throw new CoordinateOutOfBoundsException();
                for(int i = 1; i <= numSteps; i++){
                    currCoord = currCoord.getRight();

                    if(map.getCoordinateType(currCoord.x(), currCoord.y()) == CoordinateType.OBSTACLE)
                        throw new RouteObstacleOverlapException();
                }

                route.move(direction, numSteps);
            }
            case DOWN -> {
                if(currCoord.x() + numSteps > map.getLength())
                    throw new CoordinateOutOfBoundsException();
                for(int i = 1; i <= numSteps; i++){
                    currCoord = currCoord.getBelow();

                    if(map.getCoordinateType(currCoord.x(), currCoord.y()) == CoordinateType.OBSTACLE)
                        throw new RouteObstacleOverlapException();
                }

                route.move(direction, numSteps);
            }
            case LEFT -> {
                if(currCoord.y() - numSteps < 1)
                    throw new CoordinateOutOfBoundsException();
                for(int i = 1; i <= numSteps; i++){
                    currCoord = currCoord.getLeft();

                    if(map.getCoordinateType(currCoord.x(), currCoord.y()) == CoordinateType.OBSTACLE)
                        throw new RouteObstacleOverlapException();
                }

                route.move(direction, numSteps);
            }
        }

        checkRouteManager();
    }

    /**
     * Invariants for {@link RouteManager}
     */
    private void checkRouteManager(){
        Preconditions.checkNotNull(map, "map cannot be null");
    }
}
