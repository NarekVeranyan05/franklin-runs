package ca.umanitoba.cs.veranyan.logic;

import ca.umanitoba.cs.veranyan.logic.exceptions.RouteObstacleOverlapException;
import ca.umanitoba.cs.veranyan.logic.mocks.ObstaclePersistenceMock;
import ca.umanitoba.cs.veranyan.model.Activity;
import ca.umanitoba.cs.veranyan.model.exceptions.CoordinateOutOfBoundsException;
import ca.umanitoba.cs.veranyan.model.gear.Gear;
import ca.umanitoba.cs.veranyan.model.gear.GearType;
import ca.umanitoba.cs.veranyan.model.map.Map;
import ca.umanitoba.cs.veranyan.model.map.Obstacle;
import ca.umanitoba.cs.veranyan.model.map.Route;
import ca.umanitoba.cs.veranyan.model.map.coordinate.Coordinate;
import ca.umanitoba.cs.veranyan.model.map.coordinate.CoordinateType;
import ca.umanitoba.cs.veranyan.output.Colourise;
import ca.umanitoba.cs.veranyan.tests.TestResults;

import static ca.umanitoba.cs.veranyan.tests.TestHarness.bubblePrint;

public class MapManagerTestHarness {
    private int successes = 0;
    private int failures = 0;

    public TestResults runTests() {
        bubblePrint("MapManager Test Harness");

        testAddObstacle();
        testInvalidXOutOfBoundsAddObstacle();
        testInvalidYOutOfBoundsAddObstacle();
        testInvalidRouteOverlapAddObstacle();

        testSetUpActivity();
        testAddRoute();
        testInvalidXOutOfBoundsAddRoute();
        testInvalidYOutOfBoundsAddRoute();
        testInvalidObstacleOverlapAddRoute();

        bubblePrint("Test results");
        System.out.printf("Total tests: %d\n", successes + failures);
        System.out.printf("\tSuccesses: %d\n", successes);
        System.out.printf("\tFailures: %d\n", failures);

        if (failures > 0) {
            Colourise.red("There were test failures.\n");
        } else {
            Colourise.green("All tests passed!\n");
        }

        return new TestResults(successes, failures);
    }

    private void testAddObstacle() {
        try {
            Map.destroyInstance();

            var obstacle = new Obstacle.ObstacleBuilder().topLeftX(10).topLeftY(10).bottomRightX(13).bottomRightY(13).build();

            var mapManager = new MapManager(new ObstaclePersistenceMock(), Map.getInstance());
            mapManager.addObstacle(obstacle);

            if (mapManager.getMap().getObstacles().isEmpty()) {
                fail("Did not add obstacle to the map, the opposite was expected");
            } else if (mapManager.getMap().getObstacles().size() > 1) {
                fail("Added more obstacles than given, expected 1, got " + mapManager.getMap().getObstacles().size());
            } else if (!mapManager.getMap().getObstacles().contains(obstacle)) {
                fail("Did not add obstacle to the map, the opposite was expected");
            } else {
                pass("Successfully added obstacle to the map");
            }
        } catch (CoordinateOutOfBoundsException e) {
            fail("Incorrectly identified out of bounds for obstacle");
        } catch (RouteObstacleOverlapException e) {
            fail("Incorrectly identified overlap between obstacle and a route");
        } catch (Exception e) {
            fail("Unexpected exception was thrown");
            e.printStackTrace();
        }
    }

    private void testInvalidXOutOfBoundsAddObstacle() {
        try {
            Map.destroyInstance();

            var obstacle = new Obstacle.ObstacleBuilder().topLeftX(10).topLeftY(10).bottomRightX(26).bottomRightY(10).build();

            var mapManager = new MapManager(new ObstaclePersistenceMock(), Map.getInstance());
            mapManager.addObstacle(obstacle);

            fail("Should not have succeeded in adding obstacle with coordinate x=26 to map.");
        } catch (CoordinateOutOfBoundsException e) {
            pass("Successfully rejected obstacle with coordinate x=26 for the map.");
        } catch (Exception e) {
            fail("Some other exception was thrown");
            e.printStackTrace();
        }
    }

    private void testInvalidYOutOfBoundsAddObstacle() {
        try {
            Map.destroyInstance();

            var obstacle = new Obstacle.ObstacleBuilder().topLeftX(2).topLeftY(2).bottomRightX(10).bottomRightY(21).build();

            var mapManager = new MapManager(new ObstaclePersistenceMock(), Map.getInstance());
            mapManager.addObstacle(obstacle);

            fail("Should not have succeeded in adding obstacle with coordinate y=21 to map.");
        } catch (CoordinateOutOfBoundsException e) {
            pass("Successfully rejected obstacle with coordinate y=21 for the map.");
        } catch (Exception e) {
            fail("Some other exception was thrown");
            e.printStackTrace();
        }
    }

    private void testInvalidRouteOverlapAddObstacle() {
        try {
            Map.destroyInstance();

            var coordinate = new Coordinate(CoordinateType.ROUTE, 12, 12);
            var route = new Route.RouteBuilder().withCoordinate(coordinate).build();

            var obstacle = new Obstacle.ObstacleBuilder().topLeftX(10).topLeftY(10).bottomRightX(13).bottomRightY(13).build();

            var mapManager = new MapManager(new ObstaclePersistenceMock(), Map.getInstance());

            mapManager.addRoute(route);
            mapManager.addObstacle(obstacle);

            fail("Should not have succeeded in adding obstacle with overlap at (12, 12) to map.");
        } catch (RouteObstacleOverlapException e) {
            pass("Successfully rejected obstacle with overlap at (12, 12) for the map.");
        } catch (Exception e) {
            fail("Some other exception was thrown");
            e.printStackTrace();
        }
    }

    private void testSetUpActivity(){
        try {
            Map.destroyInstance();

            var coordinate = new Coordinate(CoordinateType.ROUTE, 12, 12);
            var route = new Route.RouteBuilder().withCoordinate(coordinate).build();
            var activityBuilder = new Activity.ActivityBuilder()
                    .durationInMinutes(10)
                    .startMinute(10)
                    .startHour(10)
                    .startMonth(12)
                    .startDayOfMonth(10)
                    .gear(new Gear.GearBuilder().name("GG").type(GearType.ROAD_BIKE).avgSpeed(100).build());

            var mapManager = new MapManager(new ObstaclePersistenceMock(), Map.getInstance());

            mapManager.addRoute(route);
            mapManager.setUpActivity(activityBuilder.route(route).build());

            boolean failed = false;
            for(int i = 0; i < route.getCoordinates().size() && !failed; i++){
                var routeCoord = route.getCoordinates().get(i);

                if(mapManager.getMap().getCoordinateType(routeCoord.x(),routeCoord.y()) != CoordinateType.ROUTE) {
                    fail("All the route coordinates should have been displayed on the map grid," +
                            "got that (" + routeCoord.x() + ", " + routeCoord.y() + ") was not updated.");

                    failed = true;
                }
            }

            if(!failed){
                pass("Successfully updated the map with the route of the new activity");
            }

        } catch (Exception e) {
            fail("Some other exception was thrown");
            e.printStackTrace();
        }
    }

    private void testAddRoute() {
        try {
            Map.destroyInstance();

            var coordinate1 = new Coordinate(CoordinateType.ROUTE, 10, 10);
            var coordinate2 = new Coordinate(CoordinateType.ROUTE, 11, 9);
            var coordinate3 = new Coordinate(CoordinateType.ROUTE, 12, 10);
            var coordinate4 = new Coordinate(CoordinateType.ROUTE, 13, 14);
            var route = new Route.RouteBuilder()
                    .withCoordinate(coordinate1)
                    .withCoordinate(coordinate2)
                    .withCoordinate(coordinate3)
                    .withCoordinate(coordinate4).build();

            var mapManager = new MapManager(new ObstaclePersistenceMock(), Map.getInstance());

            mapManager.addRoute(route);

            if (mapManager.getMap().getCoordinateType(10, 10) != CoordinateType.ROUTE) {
                fail("Did not add route to the map, the opposite was expected");
            } else {
                pass("Successfully added obstacle to the map");
            }
        } catch (CoordinateOutOfBoundsException e) {
            fail("Incorrectly identified out of bounds for route");
        } catch (RouteObstacleOverlapException e) {
            fail("Incorrectly identified overlap between route and an obstacle");
        } catch (Exception e) {
            fail("Unexpected exception was thrown");
            e.printStackTrace();
        }
    }

    private void testInvalidXOutOfBoundsAddRoute() {
        try {
            Map.destroyInstance();

            var route = new Route.RouteBuilder().
                    withCoordinate(new Coordinate(CoordinateType.ROUTE, 12, 12)).
                    withCoordinate(new Coordinate(CoordinateType.ROUTE, 26, 20)).build();

            var mapManager = new MapManager(new ObstaclePersistenceMock(), Map.getInstance());
            mapManager.addRoute(route);

            fail("Should not have succeeded in adding route with coordinate x=26 to map.");
        } catch (CoordinateOutOfBoundsException e) {
            pass("Successfully rejected route with coordinate x=21 for the map.");
        } catch (Exception e) {
            fail("Unexpected exception was thrown");
            e.printStackTrace();
        }
    }

    private void testInvalidYOutOfBoundsAddRoute() {
        try {
            Map.destroyInstance();

            var route = new Route.RouteBuilder().
                    withCoordinate(new Coordinate(CoordinateType.ROUTE, 12, 12)).
                    withCoordinate(new Coordinate(CoordinateType.ROUTE, 19, 21)).build();

            var mapManager = new MapManager(new ObstaclePersistenceMock(), Map.getInstance());
            mapManager.addRoute(route);

            fail("Should not have succeeded in adding route with coordinate y=21 to map.");
        } catch (CoordinateOutOfBoundsException e) {
            pass("Successfully rejected route with coordinate y=21 for the map.");
        } catch (Exception e) {
            fail("Unexpected exception was thrown");
            e.printStackTrace();
        }
    }

    private void testInvalidObstacleOverlapAddRoute() {
        try {
            Map.destroyInstance();

            var obstacle = new Obstacle.ObstacleBuilder().topLeftX(10).topLeftY(10).bottomRightX(13).bottomRightY(13).build();

            var coordinate = new Coordinate(CoordinateType.ROUTE, 12, 12);
            var route = new Route.RouteBuilder().withCoordinate(coordinate).build();

            var mapManager = new MapManager(new ObstaclePersistenceMock(), Map.getInstance());

            mapManager.addObstacle(obstacle);
            mapManager.addRoute(route);

            fail("Should not have succeeded in adding route with overlap at (12, 12) to map.");
        } catch (RouteObstacleOverlapException e) {
            pass("Successfully rejected route with overlap at (12, 12) for the map.");
        } catch (Exception e) {
            fail("Some other exception was thrown");
            e.printStackTrace();
        }
    }

    private void pass(String message) {
        successes++;
        Colourise.green("PASS: " + message + "\n");
    }

    private void fail(String message) {
        failures++;
        Colourise.red("FAIL: " + message + "\n");
    }
}
