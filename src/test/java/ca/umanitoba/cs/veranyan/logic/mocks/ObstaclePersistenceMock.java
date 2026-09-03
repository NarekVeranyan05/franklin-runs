package ca.umanitoba.cs.veranyan.logic.mocks;

import ca.umanitoba.cs.veranyan.model.map.Obstacle;
import ca.umanitoba.cs.veranyan.persistence.ObstaclePersistence;

import java.util.Set;

public class ObstaclePersistenceMock implements ObstaclePersistence {
    @Override
    public void save(Obstacle obstacle) { }

    @Override
    public Set<Obstacle> loadObstacles() {
        return Set.of();
    }
}
