package ca.umanitoba.cs.veranyan.domain;

import ca.umanitoba.cs.veranyan.model.map.Map;
import ca.umanitoba.cs.veranyan.model.map.Obstacle;
import ca.umanitoba.cs.veranyan.model.map.Route;
import ca.umanitoba.cs.veranyan.model.map.coordinate.Coordinate;
import ca.umanitoba.cs.veranyan.model.map.coordinate.CoordinateType;
import ca.umanitoba.cs.veranyan.output.Colourise;
import ca.umanitoba.cs.veranyan.tests.TestResults;

import static ca.umanitoba.cs.veranyan.tests.TestHarness.bubblePrint;

public class MapTestHarness {
    private int successes = 0;
    private int failures = 0;

    public TestResults runTests() {
        bubblePrint("Map Test Harness");

        testSetCoordinateType();

        testClearRoutes();

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

    private void testSetCoordinateType() {
        Map.destroyInstance();

        try {
            Map.getInstance().setCoordinateType(CoordinateType.ROUTE, 10, 10);

            if (Map.getInstance().getCoordinateType(10, 10) != CoordinateType.ROUTE) {
                fail("Did not set coordinate type of the coordinate (10, 10) properly, expected ROUTE, got " + Map.getInstance().getCoordinateType(10, 10));
            } else {
                pass("Succeeded in setting coordinate type to ROUTE");
            }
        } catch (Exception e) {
            fail("Unexpected exception was thrown");
            e.printStackTrace();
        }
    }

    private void testClearRoutes() {
        Map.destroyInstance();
        Route route = null;

        try {
            route = new Route.RouteBuilder()
                    .withCoordinate(new Coordinate(CoordinateType.ROUTE, 15, 15))
                    .withCoordinate(new Coordinate(CoordinateType.ROUTE, 20, 20))
                    .withCoordinate(new Coordinate(CoordinateType.ROUTE, 1, 2)).build();
        } catch (Exception e) {
            fail("Unexpected exception was thrown");
            e.printStackTrace();
        }

        Obstacle obstacle = null;

        try {
            obstacle = new Obstacle.ObstacleBuilder().topLeftX(10).topLeftY(10).bottomRightX(11).bottomRightY(11).build();
        } catch (Exception e) {
            fail("Unexpected exception was thrown");
            e.printStackTrace();
        }

        try {
            Map.getInstance().addObstacle(obstacle);
            Map.getInstance().addRoute(route);
            Map.getInstance().clearRoutes();

            boolean haveFoundFail = false;
            for (int i = 1; i <= Map.getInstance().getLength() && !haveFoundFail; i++) {
                for (int j = 1; j <= Map.getInstance().getWidth() && !haveFoundFail; j++) {
                    if (Map.getInstance().getCoordinateType(i, j) != CoordinateType.OBSTACLE && Map.getInstance().getCoordinateType(i, j) != CoordinateType.EMPTY) {
                        fail("Should have emptied coordinate (" + i + ", " + j + "), expected EMPTY, got " + Map.getInstance().getCoordinateType(i, j));
                        haveFoundFail = true;
                    }
                }
            }

            haveFoundFail = false;
            for (int i = 10; i <= 11 && !haveFoundFail; i++) {
                for (int j = 10; j <= 11 && !haveFoundFail; j++) {
                    if (Map.getInstance().getCoordinateType(i, j) != CoordinateType.OBSTACLE) {
                        fail("Should not have emptied the obstacle");
                        haveFoundFail = true;
                    }
                }
            }

        } catch (Exception e) {
            fail("Unexpected exception was thrown");
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
