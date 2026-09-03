package ca.umanitoba.cs.veranyan.domain;

import ca.umanitoba.cs.veranyan.model.exceptions.CoordinateOutOfBoundsException;
import ca.umanitoba.cs.veranyan.model.exceptions.TopLeftBottomRightCoordMismatchException;
import ca.umanitoba.cs.veranyan.model.map.Obstacle;
import ca.umanitoba.cs.veranyan.output.Colourise;
import ca.umanitoba.cs.veranyan.tests.TestResults;

import static ca.umanitoba.cs.veranyan.tests.TestHarness.bubblePrint;

public class ObstacleTestHarness {
    private int successes = 0;
    private int failures = 0;

    public TestResults runTests() {
        bubblePrint("Obstacle Test Harness");

        testCreateObstacle();

        testInvalidNegativeTopLeftX();
        testInvalidZeroTopLeftX();

        testInvalidNegativeTopLeftY();
        testInvalidZeroTopLeftY();

        testInvalidNegativeBottomRightX();
        testInvalidZeroBottomRightX();

        testInvalidNegativeBottomRightY();
        testInvalidZeroBottomRightY();

        testInvalidTopLeftXBiggerThanBottomRightX();
        testInvalidTopLeftXBiggerThanBottomRightXReversed();

        testInvalidTopLeftYBiggerThanBottomRightY();
        testInvalidTopLeftYBiggerThanBottomRightYReversed();

        testContains();

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

    // this is my happy path:
    public void testCreateObstacle() {
        var builder = new Obstacle.ObstacleBuilder();

        try {
            var obstacle = builder.topLeftX(10).topLeftY(10).bottomRightX(13).bottomRightY(13).build();
            var coordinates = obstacle.getCoordinates();
            int numCoordinates = obstacle.getMeasure();

            if (numCoordinates != 16) {
                fail("Coordinates were not set as expected, got " + numCoordinates + " coordinates, expected " + 16);
            } else if (coordinates.get(0).x() != 10) {
                fail("Top-left x-coordinate is not what was expected, got " + coordinates.get(0).x() + " expected 10");
            } else if (obstacle.getCoordinates().get(0).y() != 10) {
                fail("Top-left y-coordinate is not what was expected, got " + coordinates.get(0).y() + " expected 10");
            } else if (coordinates.get(numCoordinates - 1).x() != 13) {
                fail("Bottom-right x-coordinate is not what was expected, got " + coordinates.get(numCoordinates - 1).x() + " expected 13");
            } else if (coordinates.get(numCoordinates - 1).y() != 13) {
                fail("Bottom-right y-coordinate is not what was expected, got " + coordinates.get(numCoordinates - 1).y() + " expected 13");
            } else {
                pass("All properties in the happy path were set as expected.");
            }
        } catch (Exception e) {
            fail("Exception thrown during happy path inputs.");
            e.printStackTrace();
        }
    }

    public void testInvalidNegativeTopLeftX() {
        var builder = new Obstacle.ObstacleBuilder();

        try {
            builder.topLeftX(-1);

            fail("Should not have succeeded in setting negative number for top-left x-coordinate.");
        } catch (CoordinateOutOfBoundsException e) {
            pass("Successfully rejected -1 for top-left x-coordinate.");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
            throw new RuntimeException(e);
        }
    }

    public void testInvalidZeroTopLeftX() {
        var builder = new Obstacle.ObstacleBuilder();

        try {
            builder.topLeftX(0);

            fail("Should not have succeeded in setting 0 for top-left x-coordinate.");
        } catch (CoordinateOutOfBoundsException e) {
            pass("Successfully rejected 0 for top-left x-coordinate.");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
            throw new RuntimeException(e);
        }
    }

    public void testInvalidNegativeTopLeftY() {
        var builder = new Obstacle.ObstacleBuilder();

        try {
            builder.topLeftY(-1);

            fail("Should not have succeeded in setting negative number for top-left y-coordinate.");
        } catch (CoordinateOutOfBoundsException e) {
            pass("Successfully rejected -1 for top-left y-coordinate.");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
            throw new RuntimeException(e);
        }
    }

    public void testInvalidZeroTopLeftY() {
        var builder = new Obstacle.ObstacleBuilder();

        try {
            builder.topLeftY(0);

            fail("Should not have succeeded in setting 0 for top-left y-coordinate.");
        } catch (CoordinateOutOfBoundsException e) {
            pass("Successfully rejected 0 for top-left y-coordinate.");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
            throw new RuntimeException(e);
        }
    }

    public void testInvalidNegativeBottomRightX() {
        var builder = new Obstacle.ObstacleBuilder();

        try {
            builder.bottomRightX(-1);

            fail("Should not have succeeded in setting negative number for bottom-right x-coordinate.");
        } catch (CoordinateOutOfBoundsException e) {
            pass("Successfully rejected -1 for bottom-right x-coordinate.");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
            throw new RuntimeException(e);
        }
    }

    public void testInvalidZeroBottomRightX() {
        var builder = new Obstacle.ObstacleBuilder();

        try {
            builder.bottomRightX(0);

            fail("Should not have succeeded in setting 0 for bottom-right x-coordinate.");
        } catch (CoordinateOutOfBoundsException e) {
            pass("Successfully rejected 0 for bottom-right x-coordinate.");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
            throw new RuntimeException(e);
        }
    }

    public void testInvalidNegativeBottomRightY() {
        var builder = new Obstacle.ObstacleBuilder();

        try {
            builder.bottomRightY(-1);

            fail("Should not have succeeded in setting negative number for bottom-right y-coordinate.");
        } catch (CoordinateOutOfBoundsException e) {
            pass("Successfully rejected -1 for bottom-right y-coordinate.");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
            throw new RuntimeException(e);
        }
    }

    public void testInvalidZeroBottomRightY() {
        var builder = new Obstacle.ObstacleBuilder();

        try {
            builder.bottomRightY(0);

            fail("Should not have succeeded in setting 0 for bottom-right y-coordinate.");
        } catch (CoordinateOutOfBoundsException e) {
            pass("Successfully rejected 0 for bottom-right y-coordinate.");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
            throw new RuntimeException(e);
        }
    }

    public void testInvalidTopLeftXBiggerThanBottomRightX() {
        var builder = new Obstacle.ObstacleBuilder();

        try {
            builder.topLeftX(10).bottomRightX(3);

            fail("Should not have succeeded in setting 3 for bottom-right x-coordinate.");
        } catch (TopLeftBottomRightCoordMismatchException e) {
            pass("Successfully rejected 3 for bottom-right x-coordinate.");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
            throw new RuntimeException(e);
        }
    }

    public void testInvalidTopLeftXBiggerThanBottomRightXReversed() {
        var builder = new Obstacle.ObstacleBuilder();

        try {
            builder.bottomRightX(3).topLeftX(10);

            fail("Should not have succeeded in setting 10 for top-left x-coordinate.");
        } catch (TopLeftBottomRightCoordMismatchException e) {
            pass("Successfully rejected 10 for top-left x-coordinate.");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
            throw new RuntimeException(e);
        }
    }

    public void testInvalidTopLeftYBiggerThanBottomRightY() {
        var builder = new Obstacle.ObstacleBuilder();

        try {
            builder.topLeftY(10).bottomRightY(3);

            fail("Should not have succeeded in setting 3 for bottom-right y-coordinate.");
        } catch (TopLeftBottomRightCoordMismatchException e) {
            pass("Successfully rejected 3 for bottom-right y-coordinate.");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
            throw new RuntimeException(e);
        }
    }

    public void testInvalidTopLeftYBiggerThanBottomRightYReversed() {
        var builder = new Obstacle.ObstacleBuilder();

        try {
            builder.bottomRightY(3).topLeftY(10);

            fail("Should not have succeeded in setting 10 for top-left y-coordinate.");
        } catch (TopLeftBottomRightCoordMismatchException e) {
            pass("Successfully rejected 10 for top-left y-coordinate.");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
            throw new RuntimeException(e);
        }
    }

    public void testContains() {
        var builder = new Obstacle.ObstacleBuilder();

        try {
            var obstacle = builder.topLeftX(10).topLeftY(10).bottomRightX(13).bottomRightY(13).build();
            if (!obstacle.contains(10, 10)) {
                fail("Expected obstacle to find that top-left vertex (10, 10) is in obstacle");
            } else if (!obstacle.contains(13, 13)) {
                fail("Expected obstacle to find that bottom-right vertex (13, 13) is in obstacle");
            } else if (obstacle.contains(9, 9)) {
                fail("Expected obstacle to evaluate that (9, 9) is not in obstacle");
            } else if (obstacle.contains(14, 14)) {
                fail("Expected obstacle to evaluate that (14, 14) is not in obstacle");
            } else {
                pass("All results for contains were as expected.");
            }
        } catch (Exception e) {
            fail("Some other exception was thrown.");
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
