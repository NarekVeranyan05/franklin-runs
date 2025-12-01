package ca.umanitoba.cs.veranyan.logic;

import ca.umanitoba.cs.veranyan.logic.exceptions.RouteObstacleOverlapException;
import ca.umanitoba.cs.veranyan.model.Activity;
import ca.umanitoba.cs.veranyan.model.exceptions.CoordinateOutOfBoundsException;
import ca.umanitoba.cs.veranyan.model.map.Map;
import ca.umanitoba.cs.veranyan.model.map.Obstacle;
import ca.umanitoba.cs.veranyan.model.map.Route;
import ca.umanitoba.cs.veranyan.persistence.ObstaclePersistence;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link MapManager} manages the business logic of manipulating map contents
 */
public class MapManager {
    private final Map map;
    private final ObstaclePersistence obstaclePersistence;
    private final List<ProcessedRoute> processedRoutes;

    public MapManager(ObstaclePersistence obstaclePersistence, Map map){
        this.obstaclePersistence = obstaclePersistence;
        this.map = map;
        this.processedRoutes = new ArrayList<>();

        checkMapManager();
    }

    public Map getMap() {
        checkMapManager();

        return map;
    }

    /**
     * Clears up the {@link Map} and resets its list of routes to only the {@link Route} of the provided {@link Activity}.
     * @param activity the activity to reset the {@link Map} to show.
     */
    public void setUpActivity(Activity activity) {
        Preconditions.checkNotNull(activity, "route cannot be null");
        checkMapManager();

        map.clearRoutes();
        map.addActivity(activity);

        checkMapManager();
    }

    /**
     * Adds a new {@link Route} to the {@link Map}
     * @param route the {@link Route} to add
     * @throws RouteObstacleOverlapException if there is an {@link Obstacle} that overlaps with the given {@link Route}
     * @throws CoordinateOutOfBoundsException if the {@link Route} goes out of the {@link Map} boundaries
     */
    public void addRoute(Route route) throws RouteObstacleOverlapException, CoordinateOutOfBoundsException {
        Preconditions.checkNotNull(route, "route cannot be null");
        checkMapManager();

        map.addRoute(route);
        processedRoutes.add(new ProcessedRoute(route));

        checkMapManager();
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

        // need to consider all the previous routes
        map.clearRoutes();
        map.addProcessedRoutes(processedRoutes);

        map.addObstacle(obstacle);
        obstaclePersistence.save(obstacle);

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
    public static class ProcessedRoute {
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
         * Class Invariants for {@link ProcessedRoute}
         */
        private void checkProcessedRoute(){
            Preconditions.checkNotNull(processedRoute, "processedRoute cannot be null");
        }
    }
}
