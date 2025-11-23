package ca.umanitoba.cs.veranyan.persistence;

import ca.umanitoba.cs.veranyan.model.Profile;
import ca.umanitoba.cs.veranyan.model.map.Map;
import ca.umanitoba.cs.veranyan.model.map.Obstacle;

import java.util.Set;

/**
 * {@link ObstaclePersistence} interface for a persistence layer class that
 * manages the obstacles on the {@link Map}
 */
public interface ObstaclePersistence {
    /**
     * Saves the {@link Map} in storage
     * @param obstacle the singleton {@link Obstacle} to save
     */
    void save(Obstacle obstacle);

    /**
     * Loads all the obstacles back from storage to the system
     * @return the set of loaded obstacles
     */
    Set<Obstacle> loadObstacles();
}
