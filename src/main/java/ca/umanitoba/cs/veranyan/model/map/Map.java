package ca.umanitoba.cs.veranyan.model.map;

import ca.umanitoba.cs.veranyan.model.Activity;
import ca.umanitoba.cs.veranyan.model.Profile;
import ca.umanitoba.cs.veranyan.model.exceptions.CoordinateOutOfBoundsException;
import ca.umanitoba.cs.veranyan.model.exceptions.RouteObstacleOverlapException;
import ca.umanitoba.cs.veranyan.model.map.coordinate.Coordinate;
import ca.umanitoba.cs.veranyan.model.map.coordinate.CoordinateType;
import com.google.common.base.Preconditions;

import java.util.*;
import java.util.stream.Stream;

/**
 * The Map is the class that contains all the {@link Obstacle}
 * instances and {@link Route} instances in an {@link Activity}.
 */
public class Map implements Gridable{
    private static final int BORDER_INDENT = 1;
    private static final int MAP_WIDTH = 20; // the number of y-coordinates (horizontal axis)
    private static final int MAP_LENGTH = 25; // the number of x-coordinates (vertical axis)

    private static Map singleton;

    private final CoordinateType[][] grid;

    private final List<Obstacle> obstacles;
    private final Set<Route> routes;

    /**
     * The width and length will be used to instantiate a new Map
     * singleton if the previous singleton has been destroyed.
     *
     * @return the new or already-existing Map singleton instance.
     */
    public static Map getInstance() {
        // create new singleton instance if none exists
        if (singleton == null)
            singleton = new Map();

        singleton.checkMap();

        return singleton;
    }

    /**
     * Destroys the singleton Map instance.
     */
    public static void destroyInstance(){
        singleton = null;
    }

    /**
     * Constructor for a new Map singleton.
     */
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
    @Override
    public int getWidth() {
        return MAP_WIDTH;
    }

    /**
     * @return the length (which is the number of x-coordinates) of the grid
     */
    @Override
    public int getLength() {
        return MAP_LENGTH;
    }

    /**
     * @param x the non-negative x-component of the point (x, y)
     * @param y the non-negative y-component of the point (x, y)
     * @return the coordinate getType of the point (x, y) on the map <br>EMPTY if empty, <br>ROUTE if occupied by route,
     * <br>OBSTACLE if occupied by obstacle.
     * <br>Must not be {@code null}.
     */
    public CoordinateType getCoordinateType(int x, int y){
        checkMap();

        return grid[x][y];
    }

    public void setCoordinateType(CoordinateType type, int x, int y){
        checkMap();

        grid[x][y] = type;

        checkMap();
    }

    public Coordinate getCoordinate(int x, int y) throws CoordinateOutOfBoundsException{
        checkMap();

        if(x < 1 || x > MAP_LENGTH)
            throw new CoordinateOutOfBoundsException();
        if(y < 1 || y > MAP_WIDTH)
            throw new CoordinateOutOfBoundsException();

        return new Coordinate(grid[x][y], x, y);
    }

    public ProcessedRoute addRoute(Route route) throws RouteObstacleOverlapException, CoordinateOutOfBoundsException {
        for(var coord : route.getCoordinates()) {
            if(getCoordinateType(coord.x(), coord.y()) == CoordinateType.OBSTACLE)
                throw new RouteObstacleOverlapException();
            if(coord.x() > MAP_LENGTH || coord.y() > MAP_WIDTH)
                throw new CoordinateOutOfBoundsException();
        }

        routes.add(route);
        appendToGrid(route);

        return new ProcessedRoute(route);
    }

    public void addObstacle(Obstacle obstacle) throws RouteObstacleOverlapException, CoordinateOutOfBoundsException{
        for(var coord : obstacle.getCoordinates()) {
            if(getCoordinateType(coord.x(), coord.y()) == CoordinateType.ROUTE)
                throw new RouteObstacleOverlapException();
            if(coord.x() > MAP_LENGTH || coord.y() > MAP_WIDTH)
                throw new CoordinateOutOfBoundsException();
        }

        obstacles.add(obstacle);
        appendToGrid(obstacle);
    }

    public void addProcessedRoute(ProcessedRoute processedRoute){
        checkMap();

        routes.add(processedRoute.getRoute());
        appendToGrid(processedRoute.getRoute());

        checkMap();
    }

    public void addProcessedRoutes(List<ProcessedRoute> processedRoutes){
        checkMap();

        // feature getType cannot be EMPTY (class invariant checks for it)
        for(var processedRoute : processedRoutes) {
            routes.add(processedRoute.getRoute());
            addProcessedRoute(processedRoute);
            appendToGrid(processedRoute.getRoute());
        }

        checkMap();
    }

    /**
     * Removes a feature from the map
     * @param processedRoute the feature to remove
     */
    public void removeRoute(ProcessedRoute processedRoute){
        checkMap();

        routes.remove(processedRoute.getRoute());
        refillGrid();

        checkMap();
    }

//    /**
//     * @return the unmodifiable list of Obstacles on the Map. Must not be {@code null}.
//     */
//    public List<MapFeature> getObstacles() {
//        checkMap();
//
//        return Collections.unmodifiableList(obstacles);
//    }
//
//    /**
//     * @param index the index of an obstacle
//     * @return the obstacle on the map at given index. Must not be {@code null}.
//     */
//    public MapFeature getObstacle(int index) {
//        checkMap();
//
//        var iterator = obstacles.iterator();
//        for(int i = 0; i < index; i++)
//            iterator.next();
//
//        return iterator.next();
//    }

    /**
     * Removes all the routes from the map
     * Refills the grid leaving no ROUTEs
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
    @Override
    public void appendToGrid(MapFeature feature){
        checkMap();

        for(var coord : feature.getCoordinates())
            grid[coord.x()][coord.y()] = coord.type();

        checkMap();
    }

    private void fillGridBorders(){
        for(int x = 0; x < MAP_LENGTH + 2*BORDER_INDENT; x++) {
            grid[x][0] = CoordinateType.BORDER;
            grid[x][MAP_WIDTH + BORDER_INDENT] = CoordinateType.BORDER;
        }

        for(int y = 1; y < MAP_WIDTH + 2*BORDER_INDENT; y++){
            grid[0][y] = CoordinateType.BORDER;
            grid[MAP_LENGTH + BORDER_INDENT][y] = CoordinateType.BORDER;
        }
    }

    /**
     * Refills the grid using the current collection of features in the map.
     */
    @Override
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

    //FIXME remove
    /**
     * Prints out the maze grid to the console.
     *     EMPTY,
     *     ROUTE,
     *     OBSTACLE,
     *     VISITED,
     *     CURRENT,
     *     BORDER
     */
    public void printout(){
        for(int i = 1; i <= getLength(); i++){
            for(int j = 1; j <= getWidth(); j++) {
                if (grid[i][j] == CoordinateType.EMPTY)
                    System.out.print(". ");
                else if (grid[i][j] == CoordinateType.VISITED)
                    System.out.print("V ");
                else if (grid[i][j] == CoordinateType.OBSTACLE)
                    System.out.print("O ");
                else if (grid[i][j] == CoordinateType.CURRENT)
                    System.out.print("U ");
                else if (grid[i][j] == CoordinateType.ROUTE)
                    System.out.print("R ");

            }

            System.out.println();
        }
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
        // coordinate being non-negative is an Coordinate invariant
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
        // coordinate being non-negative is a Coordinate invariant
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

    public static class ProcessedRoute implements Cloneable{
        Route processedRoute;

        public ProcessedRoute(Route route){
            this.processedRoute = route;

            checkProcessedRoute();
        }

        private Route getRoute(){
            checkProcessedRoute();

            return this.processedRoute;
        }

        public List<Coordinate> getCoordinates() {
            checkProcessedRoute();

            return processedRoute.getCoordinates();
        }

        public int getMeasure(){
            checkProcessedRoute();

            return processedRoute.getMeasure();
        }

        private void checkProcessedRoute(){
            Preconditions.checkNotNull(processedRoute, "processedRoute cannot be null");
        }

        public void move(int direction, int numSteps) {
            processedRoute.move(direction, numSteps);
        }

        public ProcessedRoute clone() {
            try{
                ProcessedRoute clone = (ProcessedRoute) super.clone();
                clone.processedRoute = processedRoute.clone();
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException();
            }
        }
    }
}
