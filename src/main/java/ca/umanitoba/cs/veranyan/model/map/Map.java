package ca.umanitoba.cs.veranyan.model.map;

import ca.umanitoba.cs.veranyan.logic.MapManager;
import ca.umanitoba.cs.veranyan.logic.exceptions.RouteObstacleOverlapException;
import ca.umanitoba.cs.veranyan.model.exceptions.CoordinateOutOfBoundsException;
import ca.umanitoba.cs.veranyan.model.Activity;
import ca.umanitoba.cs.veranyan.model.map.coordinate.CoordinateType;
import com.google.common.base.Preconditions;

import java.util.*;
import java.util.stream.Stream;

/**
 * The {@link Map} is the class that contains all the {@link Obstacle}
 * instances and {@link Route} instances in an {@link Activity}.
 */
public class Map{
    private static final int BORDER_INDENT = 1;
    private static final int MAP_WIDTH = 20; // the number of y-coordinates (horizontal axis)
    private static final int MAP_LENGTH = 25; // the number of x-coordinates (vertical axis)

    private static Map singleton;

    private final CoordinateType[][] grid;
    private final List<Obstacle> obstacles;
    private final Set<Route> routes;

    public static Map getInstance() {
        // create new singleton instance if none exists
        if (singleton == null)
            singleton = new Map();

        singleton.checkMap();

        return singleton;
    }

    /**
     * Destroys the singleton {@link Map} instance.
     */
    public static void destroyInstance(){
        singleton = null;
    }

    private Map() {
        this.obstacles = new ArrayList<>();
        this.routes = new HashSet<>();

        this.grid = new CoordinateType[MAP_LENGTH + (2 * BORDER_INDENT)][MAP_WIDTH + (2 * BORDER_INDENT)];
        fillGridBorders();
        refillGrid(); // initial filling with all EMPTY

        checkMap();
    }

    /**
     * @return the width (which is the number of y-coordinates) of the grid;
     */
    public int getWidth() {
        checkMap();

        return MAP_WIDTH;
    }

    /**
     * @return the length (which is the number of x-coordinates) of the grid
     */
    public int getLength() {
        checkMap();

        return MAP_LENGTH;
    }

    /**
     * @param x the non-negative x-component of the point (x, y)
     * @param y the non-negative y-component of the point (x, y)
     * @return the coordinate type of the point (x, y) on the {@link Map} <br>{@code EMPTY} if empty, <br>{@code ROUTE} if occupied by a {@link Route},
     * <br>{@code OBSTACLE} if occupied by an {@link Obstacle}.
     * <br>Must not be {@code null}.
     */
    public CoordinateType getCoordinateType(int x, int y){
        checkMap();

        return grid[x][y];
    }

    /**
     * Sets the coordinate type of particular coordinate on the grid
     * @param type the coordinate type to set
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public void setCoordinateType(CoordinateType type, int x, int y){
        Preconditions.checkNotNull(type, "type cannot be null");
        checkMap();

        grid[x][y] = type;

        checkMap();
    }

    /**
     * @return an unmodifiable list of obstacles on the {@link Map}
     */
    public List<Obstacle> getObstacles() {
        return Collections.unmodifiableList(obstacles);
    }

    /**
     * Adds an {@link Obstacle} to the {@link Map}
     * @param obstacle the {@link Obstacle} to add
     * @throws RouteObstacleOverlapException if {@code obstacle} overlaps with any of the existing routes
     * @throws CoordinateOutOfBoundsException if {@code obstacle} is out of {@link Map} boundaries
     */
    public void addObstacle(Obstacle obstacle) throws RouteObstacleOverlapException, CoordinateOutOfBoundsException{
        Preconditions.checkNotNull(obstacle, "obstacle cannot be null");
        checkMap();

        for(var coord : obstacle.getCoordinates()) {
            if(coord.x() > MAP_LENGTH || coord.y() > MAP_WIDTH)
                throw new CoordinateOutOfBoundsException();
            if(getCoordinateType(coord.x(), coord.y()) == CoordinateType.ROUTE)
                throw new RouteObstacleOverlapException();
        }

        obstacles.add(obstacle);
        addToGrid(obstacle);

        checkMap();
    }

    /**
     * Adds a processed (persisted) obstacle to the map
     * @param processedObstacle the processed routes to add
     */
    public void addProcessedObstacle(Obstacle processedObstacle) {
        Preconditions.checkNotNull(processedObstacle, "processedObstacle cannot be null");
        checkMap();

        obstacles.add(processedObstacle);
        addToGrid(processedObstacle);

        checkMap();
    }

    /**
     * Adds a {@link Route} to the {@link Map}
     * @param route the {@link Route} to add
     * @throws RouteObstacleOverlapException if {@code route} overlaps with any of the existing obstacles
     * @throws CoordinateOutOfBoundsException if {@code route} is out of {@link Map} boundaries
     */
    public void addRoute(Route route) throws RouteObstacleOverlapException, CoordinateOutOfBoundsException {
        Preconditions.checkNotNull(route, "route cannot be null");
        checkMap();

        for(var coord : route.getCoordinates()) {
            if(coord.x() < 1 || coord.x() > MAP_LENGTH || coord.y() < 1 || coord.y() > MAP_WIDTH)
                throw new CoordinateOutOfBoundsException();
            if(getCoordinateType(coord.x(), coord.y()) == CoordinateType.OBSTACLE)
                throw new RouteObstacleOverlapException();
        }

        routes.add(route);
        addToGrid(route);

        checkMap();
    }

    /**
     * Adds the {@link Route} of an {@link Activity} to the {@link Map}
     * @param activity the {@link Activity} whose {@link Route} will be added to the map
     * @implNote the {@link Route} of the {@code activity} has been validated
     */
    public void addActivity(Activity activity){
        Preconditions.checkNotNull(activity, "activity cannot be null");
        checkMap();

        routes.add(activity.getRoute());
        addToGrid(activity.getRoute());

        checkMap();
    }

    /**
     * Adds the routes of all the given activities to the map
     * @param activities the activities whose routes will be added to the map
     */
    public void addActivities(List<Activity> activities){
        Preconditions.checkNotNull(activities, "activities cannot be null");
        Preconditions.checkState(!activities.contains(null), "activities entry cannot be null");
        checkMap();

        for(var activity : activities) {
            routes.add(activity.getRoute());
            addToGrid(activity.getRoute());
        }

        checkMap();
    }

    /**
     * Adds all the processed routes to the map
     * @param processedRoutes the processed routes to add
     */
    public void addProcessedRoutes(List<MapManager.ProcessedRoute> processedRoutes){
        Preconditions.checkNotNull(processedRoutes, "processedRoutes cannot be null");
        Preconditions.checkState(!processedRoutes.contains(null), "processedRoutes entry cannot be null");
        checkMap();

        for(var processedRoute : processedRoutes) {
            routes.add(processedRoute.getRoute());
            addToGrid(processedRoute.getRoute());
        }

        checkMap();
    }

    /**
     * Removes all the routes from the {@link Map}
     * Refills the grid leaving no routes
     * @implNote obstacles are not cleared
     */
    public void clearRoutes(){
        checkMap();

        routes.clear();
        refillGrid();

        checkMap();
    }

    /**
     * Fills corresponding grid entries to represent the feature
     * @param feature the feature to add to the map grid
     */
    private void addToGrid(MapFeature feature){
        Preconditions.checkNotNull(feature, "feature cannot be null");
        checkMap();

        for(var coord : feature.getCoordinates())
            grid[coord.x()][coord.y()] = coord.type();

        checkMap();
    }

    /**
     * Refills the grid using the current collection of features in the map.
     */
    public void refillGrid(){
        checkMap();

        for(int x = 1; x <= MAP_LENGTH; x++){
            for(int y = 1; y <= MAP_WIDTH; y++){
                this.grid[x][y] = CoordinateType.EMPTY;
            }
        }

        Stream.concat(obstacles.stream(), routes.stream()).forEach(feature -> {
            for(var coord: feature.getCoordinates())
                grid[coord.x()][coord.y()] = coord.type();
        });

        checkMap();
    }

    /**
     * fills the grid borders with a corresponding symbol
     */
    private void fillGridBorders(){
        checkMap();

        for(int x = 0; x < MAP_LENGTH + 2*BORDER_INDENT; x++) {
            grid[x][0] = CoordinateType.BORDER;
            grid[x][MAP_WIDTH + BORDER_INDENT] = CoordinateType.BORDER;
        }

        for(int y = 1; y < MAP_WIDTH + 2*BORDER_INDENT; y++){
            grid[0][y] = CoordinateType.BORDER;
            grid[MAP_LENGTH + BORDER_INDENT][y] = CoordinateType.BORDER;
        }

        checkMap();
    }

    /**
     * Ensures Map invariants are not violated.
     */
    private void checkMap(){
        Preconditions.checkNotNull(grid, "grid cannot be null");

        for (var coordinateType : grid)
            Preconditions.checkNotNull(coordinateType, "grid entry cannot be null");

        Preconditions.checkNotNull(obstacles, "obstacles cannot be null.");
        Preconditions.checkNotNull(routes, "routes cannot be null.");

        /*
        Decided to check obstacle and route preconditions separately to be able to easily
        identify in which List the error occurred.
         */

        // checks obstacle not null and is within bounds
        // coordinate being non-negative is an Obstacle invariant
        for(var obstacle : obstacles){
            Preconditions.checkNotNull(obstacle, "obstacles entries cannot be null.");

            for(var coord : obstacle.getCoordinates()){
                Preconditions.checkState(coord.x() <= MAP_LENGTH,
                        "obstacles entry cannot be out of bounds.");
                Preconditions.checkState(coord.y() <= MAP_WIDTH,
                        "obstacles entry cannot be out of bounds.");
            }
        }

        // checks route not null, is within bounds, and does not overlap with any obstacle
        // coordinate being non-negative is a Route invariant
        for(var route : routes){
            Preconditions.checkNotNull(route, "routes entries cannot be null.");

            for(var coord : route.getCoordinates()){
                Preconditions.checkState(coord.x() <= MAP_LENGTH,
                        "routes entry cannot be out of bounds.");
                Preconditions.checkState(coord.y() <= MAP_WIDTH,
                        "routes entry cannot be out of bounds.");

                // route shall not overlap with obstacle
                for(var obstacle: obstacles){
                    Preconditions.checkState(
                            !obstacle.contains(coord.x(), coord.y()),
                            "routes and obstacles entries cannot overlap."
                    );
                }
            }
        }
    }
}
