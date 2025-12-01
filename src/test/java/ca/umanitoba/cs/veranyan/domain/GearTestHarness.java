package ca.umanitoba.cs.veranyan.domain;

import ca.umanitoba.cs.veranyan.model.exceptions.BlankNameException;
import ca.umanitoba.cs.veranyan.model.exceptions.InvalidGearTypeException;
import ca.umanitoba.cs.veranyan.model.exceptions.NonPositiveSpeedException;
import ca.umanitoba.cs.veranyan.model.gear.Gear;
import ca.umanitoba.cs.veranyan.model.gear.GearType;
import ca.umanitoba.cs.veranyan.output.Colourise;
import ca.umanitoba.cs.veranyan.tests.TestResults;

import static ca.umanitoba.cs.veranyan.tests.TestHarness.bubblePrint;

public class GearTestHarness {
    private int successes = 0;
    private int failures = 0;

    public TestResults runTests() {
        bubblePrint("Gear Test Harness");

        testCreateGear();
        testInvalidName();
        testInvalidNegativeAvgSpeed();
        testInvalidZeroAvgSpeed();
        testInvalidGearType();

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
    public void testCreateGear() {
        var builder = new Gear.GearBuilder();

        try {
            var gear = builder.type(GearType.fromString("ELECTRIC_BIKE")).name("my_best_bike").avgSpeed(120).build();

            if (gear.getType() != GearType.ELECTRIC_BIKE) {
                fail("Type was not set as expected, got " + gear.getType() + " expected ELECTRIC_BIKE");
            } else if (!gear.getName().equals("my_best_bike")) {
                fail("Name was not set as expected, got " + gear.getName() + " expected my_best_bike");
            } else if (gear.getAvgSpeed() != 120) {
                fail("Average speed is not what was expected, got " + gear.getAvgSpeed() + " expected 120");
            } else {
                pass("All properties in the happy path were set as expected.");
            }
        } catch (Exception e) {
            fail("Exception thrown during happy path inputs.");
            e.printStackTrace();
        }
    }

    public void testInvalidGearType() {
        var builder = new Gear.GearBuilder();

        try {
            builder.type(GearType.fromString("Invalid_name"));

            fail("Should not have succeeded in setting a type for gear.");
        } catch (InvalidGearTypeException e) {
            pass("Successfully rejected non-existent gear type.");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
            e.printStackTrace();
        }
    }

    public void testInvalidName() {
        var builder = new Gear.GearBuilder();

        try {
            builder.name("");

            fail("Should not have succeeded in setting empty string as name.");
        } catch (BlankNameException e) {
            pass("Successfully rejected empty string.");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
            e.printStackTrace();
        }
    }

    public void testInvalidNegativeAvgSpeed() {
        var builder = new Gear.GearBuilder();

        try {
            builder.avgSpeed(-1);

            fail("Should not have succeeded in setting negative number for average speed.");
        } catch (NonPositiveSpeedException e) {
            pass("Successfully rejected negative number for average speed.");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
            e.printStackTrace();
        }
    }

    public void testInvalidZeroAvgSpeed() {
        var builder = new Gear.GearBuilder();

        try {
            builder.avgSpeed(0);

            fail("Should not have succeeded in setting 0 for average speed.");
        } catch (NonPositiveSpeedException e) {
            pass("Successfully rejected 0 for average speed.");
        } catch (Exception e) {
            fail("Some other exception was thrown.");
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
