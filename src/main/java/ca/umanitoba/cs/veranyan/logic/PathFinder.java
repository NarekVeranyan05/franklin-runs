package ca.umanitoba.cs.veranyan.logic;

import ca.umanitoba.cs.veranyan.assets.LinkedListStack;
import ca.umanitoba.cs.veranyan.model.Profile;
import ca.umanitoba.cs.veranyan.model.exceptions.CoordinateOutOfBoundsException;
import ca.umanitoba.cs.veranyan.model.map.Map;
import ca.umanitoba.cs.veranyan.model.map.Route;
import ca.umanitoba.cs.veranyan.model.map.coordinate.Coordinate;
import ca.umanitoba.cs.veranyan.model.map.coordinate.CoordinateType;
import com.google.common.base.Preconditions;

import java.util.*;

public class PathFinder {
    private static final Coordinate placeholder = new Coordinate(CoordinateType.BORDER, -1, -1);

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

    public Optional<Map.ProcessedRoute> findPath(Coordinate start, Coordinate end, boolean includeFriends) throws CoordinateOutOfBoundsException{
        checkPathFinder();

        Preconditions.checkNotNull(start, "start cannot be null");
        Preconditions.checkNotNull(end, "end cannot be null");

        // setup
        setUpRoutes(includeFriends);

        LinkedListStack<Coordinate> path = new LinkedListStack<>(placeholder);

        var currentCoordinate = new Coordinate(CoordinateType.ROUTE, start.x(), start.y());
        map.setCoordinateType(CoordinateType.CURRENT, currentCoordinate.x(), currentCoordinate.y());

        var route = buildRouteToFind(currentCoordinate);
        Optional<Map.ProcessedRoute> result;

        boolean canBeFound;
        do {
            map.setCoordinateType(CoordinateType.VISITED, currentCoordinate.x(), currentCoordinate.y());

            for (var neighbour : currentCoordinate.getNeighbours()) {
                if (isAvailable(path, neighbour)) {
                    path.push(neighbour);
                }
            }

            canBeFound = path.isEmpty();
            if (!canBeFound)
                currentCoordinate = path.pop();

            map.setCoordinateType(CoordinateType.CURRENT, currentCoordinate.x(), currentCoordinate.y());
            route.addCoordinate(currentCoordinate);

        } while (!canBeFound && !currentCoordinate.equals(end));

        if (currentCoordinate.equals(end)) {
            result = Optional.of(new Map.ProcessedRoute(route));
            map.clearRoutes();
            map.addProcessedRoute(result.get());
        }
        else result = Optional.empty();

        checkPathFinder();

        return result;
    }

    private Route buildRouteToFind(Coordinate currentCoordinate) throws CoordinateOutOfBoundsException {
        checkPathFinder();

        var builder = new Route.RouteBuilder();
        builder.x(currentCoordinate.x());
        builder.y(currentCoordinate.y());

        checkPathFinder();

        return builder.build();

    }

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
     * Determines whether a coordinate is available to be searched from for a path
     * @param coordinate the coordinate to gauge possibility to search from
     * @return {@code true} if coordinate can be used as starting point for search.
     * <p>
     * That is, it is not a wall, it is not visited, and it is not in the {@code path} yet.
     * <p>
     * And {@code false} otherwise
     */
    private boolean isAvailable(LinkedListStack<Coordinate> path, Coordinate coordinate){
        checkPathFinder();

        boolean isAvailable =
                (map.getCoordinateType(coordinate.x(), coordinate.y()) == CoordinateType.ROUTE);

        if(isAvailable){
            var checkedCoordinates = new LinkedListStack<>(placeholder);

            // check if the coordinate was previously added to the path
            while(!path.isEmpty() && isAvailable){
                var temp = path.pop();

                isAvailable = !temp.equals(coordinate);

                checkedCoordinates.push(temp);
            }

            // filling in the stack back up
            while(!checkedCoordinates.isEmpty()){
                path.push(
                        checkedCoordinates.pop()
                );
            }
        }

        checkPathFinder();

        return isAvailable;
    }

    private void checkPathFinder(){
        Preconditions.checkNotNull(map, "map cannot be null");
        Preconditions.checkNotNull(currentProfile, "currentProfile cannot be null");
    }
}
