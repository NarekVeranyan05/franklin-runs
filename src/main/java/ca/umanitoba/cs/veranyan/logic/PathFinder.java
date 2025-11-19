package ca.umanitoba.cs.veranyan.logic;

import ca.umanitoba.cs.veranyan.logic.exceptions.EndCoordOutOfBoundsException;
import ca.umanitoba.cs.veranyan.logic.exceptions.StartCoordOutOfBoundsException;
import ca.umanitoba.cs.veranyan.model.assets.Stack;
import ca.umanitoba.cs.veranyan.model.assets.LinkedListStack;
import ca.umanitoba.cs.veranyan.model.Profile;
import ca.umanitoba.cs.veranyan.model.map.Map;
import ca.umanitoba.cs.veranyan.model.map.Route;
import ca.umanitoba.cs.veranyan.model.map.coordinate.Coordinate;
import ca.umanitoba.cs.veranyan.model.map.coordinate.CoordinateType;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * {@link PathFinder} manages the business logic of finding a path on the {@link Map} using the given routes
 */
public class PathFinder {
    private static final Coordinate PLACEHOLDER = new Coordinate(CoordinateType.BORDER, -1, -1);

    private final Profile currentProfile;
    private final Map map;

    public PathFinder(Profile currentProfile, Map map){
        this.map = map;
        this.currentProfile = currentProfile;

        checkPathFinder();
    }

    public Map getMap() {
        checkPathFinder();

        return map;
    }

    /**
     * Finds a {@link Route} between two points
     * @param start the starting point of the {@link Route} to be found
     * @param end the ending point of the {@link Route} to be found
     * @param includeFriends indicates whether friends' routes are included or not.
     * If {@code false}, only the current {@link Profile} routes are considered
     * @return {@code true} if route was found, {@code false} otherwise
     * @throws StartCoordOutOfBoundsException if {@code start} is out of {@link Map} bounds
     * @throws EndCoordOutOfBoundsException if th {@code end} is out of {@link Map} bounds
     */
    public boolean findPath(Coordinate start, Coordinate end, boolean includeFriends) throws StartCoordOutOfBoundsException, EndCoordOutOfBoundsException {
        // pre-conditions and invariants
        Preconditions.checkNotNull(start, "start cannot be null");
        Preconditions.checkNotNull(end, "end cannot be null");

        if(start.x() < 1 || start.x() > map.getLength() || start.y() < 1 || start.y() > map.getWidth())
            throw new StartCoordOutOfBoundsException();
        if(end.x() < 1 || end.x() > map.getLength() || end.y() < 1 || end.y() > map.getWidth())
            throw new EndCoordOutOfBoundsException();

        checkPathFinder();

        // route to be built
        var currentCoordinate = startUp(start, includeFriends);
        List<Coordinate> routeCoordinates = new ArrayList<>();
        routeCoordinates.add(currentCoordinate); // contains at least the currentCoordinate

        // algorithm control variables
        LinkedListStack<Coordinate> candidates = new LinkedListStack<>(PLACEHOLDER); // the stack of candidates checked while finding the route
        boolean canGo = true;

        while (canGo && !currentCoordinate.equals(end)){
            // pre-conditions
            Preconditions.checkNotNull(candidates, "candidates cannot be null");
            Preconditions.checkNotNull(currentCoordinate, "currentCoordinate cannot be null");
            Preconditions.checkState(!routeCoordinates.isEmpty(), "routeCoordinates cannot be empty");

            map.setCoordinateType(CoordinateType.VISITED, currentCoordinate.x(), currentCoordinate.y());

            int numAvails = checkNeighbours(currentCoordinate, candidates);

            canGo = !candidates.isEmpty();
            if (canGo) {
                currentCoordinate = getCurrentCoordinate(candidates, numAvails, routeCoordinates);
            }
            else{
                // post-condition: algorithm should have finished with failure
                Preconditions.checkState(!currentCoordinate.equals(end), "if backtracking cannot proceed further, currentCoordinate must not equal to end");
            }
        }

        // post-processing
        boolean isSuccess = isSuccess(end, currentCoordinate);
        if (isSuccess) {
            map.clearRoutes();
            for(var coordinate : routeCoordinates) {
                Preconditions.checkState(coordinate.type() == CoordinateType.ROUTE, "coordinate must be of type ROUTE");

                map.setCoordinateType(CoordinateType.ROUTE, coordinate.x(), coordinate.y());
            }
        }

        // invariants
        checkPathFinder();

        return isSuccess;
    }

    /**
     * Marks the new coordinate on the top of the {@link Stack} of candidates
     * @param candidates the candidates for backtracking
     * @param numAvails the number of available neighbours in the last check for new candidates.
     * <br> if equal to {@code 0}, the sub-path was invalid, and the {@link PathFinder} will backtrack to the
     * last coordinate in {@code routeCoordinates} neighbouring the new current coordinate
     * @param routeCoordinates the coordinates of the {@link Route} to be built by backtracking
     * @return the new current coordinate for backtracking
     */
    private Coordinate getCurrentCoordinate(LinkedListStack<Coordinate> candidates, int numAvails, List<Coordinate> routeCoordinates) {
        Preconditions.checkNotNull(candidates, "candidates cannot be null");
        Preconditions.checkState(!candidates.isEmpty(), "candidates cannot be empty");
        Preconditions.checkState(numAvails >= 0 && numAvails <= 4, "numAvails must be in range [0, 4]");

        Preconditions.checkNotNull(routeCoordinates, "routeCoordinates cannot be null");
        Preconditions.checkState(!routeCoordinates.isEmpty(), "routeCoordinates cannot be null");
        Preconditions.checkState(!routeCoordinates.contains(null), "routeCoordinates entry cannot be null");

        checkPathFinder();

        Coordinate currentCoordinate = candidates.pop();
        if(numAvails == 0) {
            backtrack(routeCoordinates, currentCoordinate);
        }
        routeCoordinates.add(currentCoordinate);

        checkPathFinder();
        return currentCoordinate;
    }

    /**
     * Backtracks to the point of where the backtracking continues from
     * @param routeCoordinates the coordinates of the {@link Route} to be built by backtracking. Invalid sub-path has been added to {@code routeCoordinates}
     * @param currentCoordinate the current position to which the algorithm backtracked
     */
    private void backtrack(List<Coordinate> routeCoordinates, Coordinate currentCoordinate) {
        Preconditions.checkNotNull(routeCoordinates, "routeCoordinates cannot be null");
        Preconditions.checkState(!routeCoordinates.contains(null), "routeCoordinates entry cannot be null");
        Preconditions.checkNotNull(currentCoordinate, "currentCoordinate cannot be null");
        checkPathFinder();

        while(!routeCoordinates.get(routeCoordinates.size() - 1).isNeighbourOf(currentCoordinate)) {
            Preconditions.checkState(!routeCoordinates.isEmpty(), "routeCoordinates must contain at least the starting coordinate");

            routeCoordinates.remove(routeCoordinates.size() - 1);
        }

        checkPathFinder();
    }

    /**
     * Checks if the neighbours of the given coordinate are available; if so, pushes onto the {@link Stack} of candidates
     * @param currentCoordinate the coordinate to check availability of neighbours
     * @param candidates the {@link Stack} of candidates in backtracking
     * @return the number of available neighbours
     */
    private int checkNeighbours(Coordinate currentCoordinate, LinkedListStack<Coordinate> candidates) {
        Preconditions.checkNotNull(currentCoordinate, "currentCoordinate cannot be null");
        Preconditions.checkNotNull(candidates, "candidates cannot be null");
        checkPathFinder();

        int numAvails = 0;

        for (var neighbour : currentCoordinate.getNeighbours()) {
            if (isVisitable(candidates, neighbour)) {
                numAvails++;
                candidates.push(neighbour);
            }
        }

        checkPathFinder();

        return numAvails;
    }

    /**
     * Starts up the backtracking process
     * @param start the starting coordinate for backtracking
     * @param includeFriends indicates if only the current {@link Profile} routes should be included, or friends' routes as well
     * @return the starting coordinate of backtracking, with appropriate {@link CoordinateType} on the {@link Map} grid
     */
    private Coordinate startUp(Coordinate start, boolean includeFriends) {
        Preconditions.checkNotNull(start, "start cannot be null");
        checkPathFinder();

        setUpRoutes(includeFriends);

        var startWithType = new Coordinate(
            map.getCoordinateType(start.x(), start.y()),
            start.x(),
            start.y()
        );

        checkPathFinder();

        return startWithType;
    }

    /**
     * Determines whether backtracking finished with finding a route between the starting and ending coordinates
     * @param end the ending {@link Coordinate} for backtracking
     * @param finalCoordinate the final {@link Coordinate} that backtracking finished with
     * @return {@code true} if {@code end} and {@code finalCoordinate} match, {@code false} otherwise
     */
    private boolean isSuccess(Coordinate end, Coordinate finalCoordinate) {
        Preconditions.checkNotNull(end, "end cannot be null");
        Preconditions.checkNotNull(finalCoordinate, "finalCoordinate cannot be null");
        checkPathFinder();

        return (finalCoordinate.type() == CoordinateType.ROUTE && finalCoordinate.equals(end));
    }

    /**
     * Sets up all the routes on the {@link Map} to find a {@link Route} from
     * @param includeFriends indicates whether to include friends' routes or only the current {@link Profile} routes
     */
    private void setUpRoutes(boolean includeFriends) {
        checkPathFinder();

        var routesToConsider = currentProfile.getRoutes();
        if(includeFriends){
            for(var friend : currentProfile.getFriends())
                routesToConsider.addAll(friend.getRoutes());
        }

        map.clearRoutes();
        map.addProcessedRoutes(routesToConsider);

        checkPathFinder();
    }

    /**
     * Determines whether a {@link Coordinate} is visitable to be searched from for a route
     *
     * @param candidates the candidates that have already been recorded
     * @param coord      the {@link Coordinate} to gauge possibility to search from
     * @return {@code true} if {@link Coordinate can be used as starting point for search.
     * <p>
     * That is, it is not a wall, it is not visited, and it is not in {@code candidates} yet.
     * <p>
     * And {@code false} otherwise
     */
    private boolean isVisitable(Stack<Coordinate> candidates, Coordinate coord){
        Preconditions.checkNotNull(candidates, "candidates cannot be null");
        checkPathFinder();

        boolean isAvailable =
            (map.getCoordinateType(coord.x(), coord.y()) == CoordinateType.ROUTE);

        if(isAvailable){
            Stack<Coordinate> checkedCoordinates = new LinkedListStack<>(PLACEHOLDER);

            // check if the coordinate was previously added to coordinates
            while(!candidates.isEmpty() && isAvailable){
                var temp = candidates.pop();

                isAvailable = !temp.equals(coord);

                checkedCoordinates.push(temp);
            }

            // filling in the stack back up
            while(!checkedCoordinates.isEmpty()){
                candidates.push(
                    checkedCoordinates.pop()
                );
            }
        }

        checkPathFinder();

        return isAvailable;
    }

    /**
     * Invariants for {@link PathFinder}
     */
    private void checkPathFinder(){
        Preconditions.checkNotNull(map, "map cannot be null");
        Preconditions.checkNotNull(currentProfile, "currentProfile cannot be null");
    }
}
