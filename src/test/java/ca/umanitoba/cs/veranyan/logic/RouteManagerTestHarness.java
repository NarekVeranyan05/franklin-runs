package ca.umanitoba.cs.veranyan.logic;

import ca.umanitoba.cs.veranyan.logic.exceptions.RouteObstacleOverlapException;
import ca.umanitoba.cs.veranyan.model.exceptions.CoordinateOutOfBoundsException;
import ca.umanitoba.cs.veranyan.model.map.Map;
import ca.umanitoba.cs.veranyan.model.map.Obstacle;
import ca.umanitoba.cs.veranyan.model.map.Route;
import ca.umanitoba.cs.veranyan.model.map.coordinate.Coordinate;
import ca.umanitoba.cs.veranyan.model.map.coordinate.CoordinateType;
import ca.umanitoba.cs.veranyan.output.Colourise;
import ca.umanitoba.cs.veranyan.tests.TestResults;

import static ca.umanitoba.cs.veranyan.tests.TestHarness.bubblePrint;

public class RouteManagerTestHarness {
    private int successes = 0;
    private int failures = 0;

    public TestResults runTests() {
        bubblePrint("RouteManager Test Harness");

        testMove();
        testInvalidMoveUpOutOfBounds();
        testInvalidMoveUpObstacleOverlap();

        testInvalidMoveRightOutOfBounds();
        testInvalidMoveRightObstacleOverlap();

        testInvalidMoveDownOutOfBounds();
        testInvalidMoveDownObstacleOverlap();

        testInvalidMoveLeftOutOfBounds();
        testInvalidMoveLeftObstacleOverlap();

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

    public void testMove(){
        try {
            Map.destroyInstance();

            var route = new Route.RouteBuilder().withCoordinate(new Coordinate(CoordinateType.ROUTE, 10, 10)).build();
            var routeManager = new RouteManager(Map.getInstance());

            routeManager.doMove(route,1, 2);
            routeManager.doMove(route,2, 3);
            routeManager.doMove(route,3, 4);
            routeManager.doMove(route,4, 2);

            if(!route.contains(8, 10)){
                fail("The move upward was not done as expected, while expected to do 2 steps");
            } else if(!route.contains(8, 13)){
                fail("The move to the right was not done as expected, while expected to do 3 steps");
            } else if(!route.contains(12, 13)){
                fail("The move downward was not done as expected, while expected to do 4 steps");
            } else if(!route.contains(12, 11)){
                fail("The move to the left was not done as expected, while expected to do 2 steps");
            } else{
                pass("All the moves in the four directions were done as expected");
            }
        } catch (Exception e){
            fail("Unexpected exception was thrown.");
            throw new RuntimeException(e);
        }
    }

    public void testInvalidMoveUpOutOfBounds(){
        try {
            Map.destroyInstance();

            var route = new Route.RouteBuilder().withCoordinate(new Coordinate(CoordinateType.ROUTE, 1, 1)).build();
            var routeManager = new RouteManager(Map.getInstance());

            try{
                routeManager.doMove(route, 1, 1);

                fail("Should not be able to move upward, moving upward is out of bounds");
            } catch (CoordinateOutOfBoundsException e) {
                if(!route.contains(0, 1))
                    pass("Successfully rejected move upward that goes out of bounds");
                else fail("Should not be able to move upward, moving upward is out of bounds");
            }
        } catch (Exception e) {
            fail("Unexpected exception was thrown.");
            throw new RuntimeException(e);
        }
    }

    public void testInvalidMoveUpObstacleOverlap(){
        try {
            Map.destroyInstance();

            var obstacle = new Obstacle.ObstacleBuilder().topLeftX(7).topLeftY(5).bottomRightX(8).bottomRightY(15).build();
            Map.getInstance().addObstacle(obstacle);

            var route = new Route.RouteBuilder().withCoordinate(new Coordinate(CoordinateType.ROUTE, 10, 10)).build();

            var routeManager = new RouteManager(Map.getInstance());

            try{
                routeManager.doMove(route, 1, 4);

                fail("Should not be able to move upward, moving upward overlaps with an obstacle");
            } catch (RouteObstacleOverlapException e) {
                if(!route.contains(8, 10))
                    pass("Successfully rejected move upward that overlaps with obstacle");
                else fail("Should not be able to move upward, moving upward overlaps with an obstacle");
            }
        } catch (Exception e) {
            fail("Unexpected exception was thrown.");
            throw new RuntimeException(e);
        }
    }

    public void testInvalidMoveRightOutOfBounds(){
        try {
            Map.destroyInstance();

            var route = new Route.RouteBuilder().withCoordinate(new Coordinate(CoordinateType.ROUTE, 1, 20)).build();
            var routeManager = new RouteManager(Map.getInstance());

            try{
                routeManager.doMove(route, 2, 1);

                fail("Should not be able to move right, moving right is out of bounds");
            } catch (CoordinateOutOfBoundsException e) {
                if(!route.contains(1, 21))
                    pass("Successfully rejected move right that goes out of bounds");
                else fail("Should not be able to move right, moving right is out of bounds");
            }
        } catch (Exception e) {
            fail("Unexpected exception was thrown.");
            throw new RuntimeException(e);
        }
    }

    public void testInvalidMoveRightObstacleOverlap(){
        try {
            Map.destroyInstance();

            var obstacle = new Obstacle.ObstacleBuilder().topLeftX(8).topLeftY(13).bottomRightX(12).bottomRightY(13).build();
            Map.getInstance().addObstacle(obstacle);

            var route = new Route.RouteBuilder().withCoordinate(new Coordinate(CoordinateType.ROUTE, 10, 10)).build();

            var routeManager = new RouteManager(Map.getInstance());

            try{
                routeManager.doMove(route, 2, 5);

                fail("Should not be able to move right, moving right overlaps with an obstacle");
            } catch (RouteObstacleOverlapException e) {
                if(!route.contains(10, 13))
                    pass("Successfully rejected move right that overlaps with obstacle");
                else fail("Should not be able to move right, moving right with an obstacle");
            }
        } catch (Exception e) {
            fail("Unexpected exception was thrown.");
            throw new RuntimeException(e);
        }
    }

    public void testInvalidMoveDownOutOfBounds(){
        try {
            Map.destroyInstance();

            var route = new Route.RouteBuilder().withCoordinate(new Coordinate(CoordinateType.ROUTE, 25, 3)).build();
            var routeManager = new RouteManager(Map.getInstance());

            try{
                routeManager.doMove(route, 3, 1);

                fail("Should not be able to move down, moving down is out of bounds");
            } catch (CoordinateOutOfBoundsException e) {
                if(!route.contains(26, 3))
                    pass("Successfully rejected move down that goes out of bounds");
                else fail("Should not be able to move down, moving down is out of bounds");
            }
        } catch (Exception e) {
            fail("Unexpected exception was thrown.");
            throw new RuntimeException(e);
        }
    }

    public void testInvalidMoveDownObstacleOverlap(){
        try {
            Map.destroyInstance();

            var obstacle = new Obstacle.ObstacleBuilder().topLeftX(15).topLeftY(6).bottomRightX(15).bottomRightY(18).build();
            Map.getInstance().addObstacle(obstacle);

            var route = new Route.RouteBuilder().withCoordinate(new Coordinate(CoordinateType.ROUTE, 10, 10)).build();

            var routeManager = new RouteManager(Map.getInstance());

            try{
                routeManager.doMove(route, 3, 8);

                fail("Should not be able to move down, moving down overlaps with an obstacle");
            } catch (RouteObstacleOverlapException e) {
                if(!route.contains(10, 15))
                    pass("Successfully rejected move down that overlaps with obstacle");
                else fail("Should not be able to move down, moving down with an obstacle");
            }
        } catch (Exception e) {
            fail("Unexpected exception was thrown.");
            throw new RuntimeException(e);
        }
    }

    public void testInvalidMoveLeftOutOfBounds(){
        try {
            Map.destroyInstance();

            var route = new Route.RouteBuilder().withCoordinate(new Coordinate(CoordinateType.ROUTE, 1, 1)).build();
            var routeManager = new RouteManager(Map.getInstance());

            try{
                routeManager.doMove(route, 4, 1);

                fail("Should not be able to move left, moving left is out of bounds");
            } catch (CoordinateOutOfBoundsException e) {
                if(!route.contains(1, 0))
                    pass("Successfully rejected move down that goes out of bounds");
                else fail("Should not be able to move left, moving left is out of bounds");
            }
        } catch (Exception e) {
            fail("Unexpected exception was thrown.");
            throw new RuntimeException(e);
        }
    }

    public void testInvalidMoveLeftObstacleOverlap(){
        try {
            Map.destroyInstance();

            var obstacle = new Obstacle.ObstacleBuilder().topLeftX(7).topLeftY(8).bottomRightX(15).bottomRightY(8).build();
            Map.getInstance().addObstacle(obstacle);

            var route = new Route.RouteBuilder().withCoordinate(new Coordinate(CoordinateType.ROUTE, 10, 10)).build();

            var routeManager = new RouteManager(Map.getInstance());

            try{
                routeManager.doMove(route, 4, 5);

                fail("Should not be able to move left, moving left overlaps with an obstacle");
            } catch (RouteObstacleOverlapException e) {
                if(!route.contains(10, 8))
                    pass("Successfully rejected move left that overlaps with obstacle");
                else fail("Should not be able to move left, moving left with an obstacle");
            }
        } catch (Exception e) {
            fail("Unexpected exception was thrown.");
            throw new RuntimeException(e);
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
