package ca.umanitoba.cs.veranyan.logic;

import ca.umanitoba.cs.veranyan.logic.exceptions.RouteObstacleOverlapException;
import ca.umanitoba.cs.veranyan.model.exceptions.CoordinateOutOfBoundsException;
import ca.umanitoba.cs.veranyan.model.map.Map;
import ca.umanitoba.cs.veranyan.model.map.Obstacle;
import ca.umanitoba.cs.veranyan.model.map.Route;
import ca.umanitoba.cs.veranyan.model.map.coordinate.Coordinate;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link MapManager} manages the business logic of manipulating map contents
 */
public class MapManager {
    private final Map map;
    private final List<ProcessedRoute> routesAdded;

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
     * Clears up the {@link Map} and resets its list of routes to only the provided {@link Route}.
     * @param route the route to reset the {@link Route} to show.
     */
    public void setUpRoute(ProcessedRoute route) {
        Preconditions.checkNotNull(route, "route cannot be null");
        checkMapManager();

        map.clearRoutes();
        map.addProcessedRoute(route);

        checkMapManager();
    }

    /**
     * Adds a new {@link Route} to the {@link Map}
     * @param route the {@link Route} to add
     * @return the {@link ProcessedRoute} that wraps the successfully added {@link Route}
     * @throws RouteObstacleOverlapException if there is an {@link Obstacle} that overlaps with the given {@link Route}
     * @throws CoordinateOutOfBoundsException if the {@link Route} goes out of the {@link Map} boundaries
     */
    public ProcessedRoute addRoute(Route route) throws RouteObstacleOverlapException, CoordinateOutOfBoundsException {
        Preconditions.checkNotNull(route, "route cannot be null");
        checkMapManager();

        map.addRoute(route);
        var processedRoute = new ProcessedRoute(route);
        routesAdded.add(processedRoute);

        checkMapManager();

        return processedRoute;
    }

    /**
     * Adds a new {@link Obstacle} to the {@link Map}
     * @param obstacle the {@link Obstacle} to add
     * @throws RouteObstacleOverlapException if there is a {@link Route} that overlaps with the given {@link Obstacle}
     * @throws CoordinateOutOfBoundsException if the {@link Obstacle} goes out of the {@link Map} boundaries
     */
    public void addObstacle(Obstacle obstacle) throws RouteObstacleOverlapException, CoordinateOutOfBoundsException {
        Preconditions.checkNotNull(obstacle, "obstacle cannot be null");
        checkMapManager();

        // need to consider all routes
        map.clearRoutes();
        map.addProcessedRoutes(routesAdded);

        map.addObstacle(obstacle);

        checkMapManager();
    }

    /**
     * Invariants for {@link MapManager}
     */
    private void checkMapManager(){
        Preconditions.checkNotNull(map, "map cannot be null");
    }

    /**
     * A {@link ProcessedRoute} is a wrapper for a {@link Route} that was successfully added
     * to the {@link Map}, passing all validation
     */
    public static class ProcessedRoute implements Cloneable {
        Route processedRoute;

        private ProcessedRoute(Route route){
            Preconditions.checkNotNull(route, "route cannot be null");
            for(var coord : route.getCoordinates()){
                Preconditions.checkState(coord.x() >= 1 &&
                        coord.x() <= Map.getInstance().getLength() &&
                        coord.y() >= 1 &&
                        coord.y() <= Map.getInstance().getWidth(), "validated route cannot be out of map bounds");

            }
            this.processedRoute = route;

            checkProcessedRoute();
        }

        /**
         * @return the {@link Route} that's wrapped around
         */
        public Route getRoute(){
            checkProcessedRoute();

            return this.processedRoute;
        }

        /**
         * @return the list of coordinates of the {@link Route} that's wrapped around
         */
        public List<Coordinate> getCoordinates() {
            checkProcessedRoute();

            return processedRoute.getCoordinates();
        }

        /**
         * @return the measure of the {@link Route} that's wrapped around
         */
        public int getMeasure(){
            checkProcessedRoute();

            return processedRoute.getMeasure();
        }

        /**
         * moves in a particular direction for the route
         * @param direction the direction to move. Must be any of and only of [UP = {@code 1}, RIGHT = {@code 2}, DOWN = {@code 3}, LEFT = {@code 4}].
         * @param numSteps the number of steps of the move. Must be non-negative.
         */
        public void move(int direction, int numSteps) {
            checkProcessedRoute();

            processedRoute.move(direction, numSteps);
        }

        public ProcessedRoute clone() {
            checkProcessedRoute();

            try{
                ProcessedRoute clone = (ProcessedRoute) super.clone();
                clone.processedRoute = processedRoute.clone();

                checkProcessedRoute();

                return clone;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException();
            }
        }

        /**
         * Class Invariants for {@link ProcessedRoute}
         */
        private void checkProcessedRoute(){
            Preconditions.checkNotNull(processedRoute, "processedRoute cannot be null");
        }
    }
}
