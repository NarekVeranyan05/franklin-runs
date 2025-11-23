package ca.umanitoba.cs.veranyan.persistence;

import ca.umanitoba.cs.veranyan.model.map.Route;

import java.util.Collection;
import java.util.List;

/**
 * {@link RoutePersistence} interface for a persistence layer class that
 * manages the routes in the system.
 */
public interface RoutePersistence {
    /**
     * Saves the given {@link Route} in storage
     * @param route the {@link Route} to save
     */
    void save(Route route);

    void saveRoutes(Collection<Route> routes);

    /**
     * Loads all the routes back from storage to the system
     * @return the list of loaded routes
     */
    List<Route> loadRoutes();
}
