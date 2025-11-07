package ca.umanitoba.cs.veranyan.logic;

import ca.umanitoba.cs.veranyan.model.exceptions.CoordinateOutOfBoundsException;
import ca.umanitoba.cs.veranyan.model.exceptions.RouteObstacleOverlapException;
import ca.umanitoba.cs.veranyan.model.map.Map;
import ca.umanitoba.cs.veranyan.model.map.Obstacle;
import ca.umanitoba.cs.veranyan.model.map.Route;
import ca.umanitoba.cs.veranyan.model.map.coordinate.CoordinateType;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

public class MapManager {
    private static final int UP = 1;
    private static final int RIGHT = 2;
    private static final int DOWN = 3;
    private static final int LEFT = 4;

    private final Map map;
    private final List<Map.ProcessedRoute> routesAdded;


    public MapManager(Map map){
        this.map = map;
        this.routesAdded = new ArrayList<>();

        checkMapManager();
    }

    public Map getMap() {
        checkMapManager();

        return map;
    }

    /**
     * Clears up the map and resets its list of routes to only the provided route.
     * @param route the route to reset the map to show.
     */
    public void setUpRoute(Map.ProcessedRoute route) {
        Preconditions.checkNotNull(route, "route cannot be null");
        checkMapManager();

        map.clearRoutes();
        map.addProcessedRoute(route);

        checkMapManager();
    }

    public void doMove(Map.ProcessedRoute route, int direction, int numSteps) throws RouteObstacleOverlapException, CoordinateOutOfBoundsException {
        Preconditions.checkNotNull(route, "moveIn cannot be null");
        checkMapManager();

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

        checkMapManager();
    }

    public Map.ProcessedRoute addRoute(Route route) throws RouteObstacleOverlapException, CoordinateOutOfBoundsException {
        Preconditions.checkNotNull(route, "route cannot be null");
        checkMapManager();

        var processedRoute = map.addRoute(route);
        routesAdded.add(processedRoute);
        checkMapManager();

        return processedRoute;
    }

    public void addObstacle(Obstacle obstacle) throws RouteObstacleOverlapException, CoordinateOutOfBoundsException {
        Preconditions.checkNotNull(obstacle, "obstacle cannot be null");
        checkMapManager();

        // need to consider all routes
        map.clearRoutes();
        map.addProcessedRoutes(routesAdded);


        map.addObstacle(obstacle);

        checkMapManager();
    }

    private void checkMapManager(){
        Preconditions.checkNotNull(map, "map cannot be null");
    }
}
