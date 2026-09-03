package ca.umanitoba.cs.veranyan.persistence.json;

import ca.umanitoba.cs.veranyan.model.exceptions.CoordinateOutOfBoundsException;

import ca.umanitoba.cs.veranyan.model.exceptions.TopLeftBottomRightCoordMismatchException;
import ca.umanitoba.cs.veranyan.model.map.Map;
import ca.umanitoba.cs.veranyan.model.map.Obstacle;
import ca.umanitoba.cs.veranyan.persistence.ObstaclePersistence;
import com.google.common.base.Preconditions;

import javax.json.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * {@link ObstaclePersistenceJson} is the Json implementation for {@link ObstaclePersistence}
 */
public class ObstaclePersistenceJson implements ObstaclePersistence {
    private final Path obstacleStorage;

    public ObstaclePersistenceJson(Path obstacleStorage) {
        this.obstacleStorage = obstacleStorage;

        checkObstaclePersistenceJson();
    }

    @Override
    public void save(Obstacle obstacle){
        Preconditions.checkNotNull(obstacle, "obstacle cannot be null");
        checkObstaclePersistenceJson();

        // loading all profiles, adding new profile, and saving
        Set<Obstacle> allObstacles = loadObstacles();
        allObstacles.add(obstacle);
        saveObstacles(allObstacles);

        checkObstaclePersistenceJson();
    }

    public void saveObstacles(Set<Obstacle> obstacles) {
        Preconditions.checkNotNull(obstacles, "map cannot be null");
        checkObstaclePersistenceJson();

        try {
            JsonWriter writer = Json.createWriter(Files.newOutputStream(obstacleStorage));
            JsonArrayBuilder obstaclesJson = Json.createArrayBuilder();

            for(var obstacle : obstacles) {
                Preconditions.checkNotNull(obstacle, "obstacles entry must not be null");
                JsonArrayBuilder coordinates = Json.createArrayBuilder();

                for (var coord : obstacle.getCoordinates()) {
                    coordinates.add(
                            Json.createObjectBuilder().add("x", coord.x()).add("y", coord.y())
                    );
                }

                obstaclesJson.add(coordinates);
            }

            writer.writeArray(obstaclesJson.build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        checkObstaclePersistenceJson();
    }

    @Override
    public Set<Obstacle> loadObstacles() {
        checkObstaclePersistenceJson();

        Set<Obstacle> obstacles = new HashSet<>();

        if(Files.exists(obstacleStorage) && new File(String.valueOf(obstacleStorage)).length() != 0){
            try {
                JsonReader reader = Json.createReader(Files.newInputStream(obstacleStorage));
                JsonArray obstaclesJson = reader.readArray();

                for (var obstacleJson : obstaclesJson) {
                    obstacles.add(
                        obstacleFromJson(obstacleJson)
                    );
                }
            } catch (IOException | CoordinateOutOfBoundsException | TopLeftBottomRightCoordMismatchException e){
                e.printStackTrace();
            }
        }

        checkObstaclePersistenceJson();

        return obstacles;
    }

    /**
     * Converts the json representation of an {@link Obstacle} into an {@link Obstacle} object
     * @param obstacleJson the json representation of the {@link Obstacle}
     * @return the converted {@link Obstacle} object
     * @throws CoordinateOutOfBoundsException if the {@link Obstacle} coordinates are out of {@link Map} bounds.
     * @throws TopLeftBottomRightCoordMismatchException if the top-left coordinate is to the right and below the bottom-right coordinate
     */
    private Obstacle obstacleFromJson(JsonValue obstacleJson) throws CoordinateOutOfBoundsException, TopLeftBottomRightCoordMismatchException {
        Preconditions.checkNotNull(obstacleJson, "obstacleJson cannot be null");
        checkObstaclePersistenceJson();

        var builder = new Obstacle.ObstacleBuilder();
        var coordinates = obstacleJson.asJsonArray();

        builder.topLeftX(
                coordinates.getJsonObject(0).getInt("x")
        ).topLeftY(
                coordinates.getJsonObject(0).getInt("y")
        ).bottomRightX(
                coordinates.getJsonObject(coordinates.size() - 1).getInt("x")
        ).bottomRightY(
                coordinates.getJsonObject(coordinates.size() - 1).getInt("y")
        );

        checkObstaclePersistenceJson();

        return builder.build();
    }

    private void checkObstaclePersistenceJson(){
        Preconditions.checkNotNull(obstacleStorage, "mapStorage cannot be null");
    }

}
