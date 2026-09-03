package ca.umanitoba.cs.veranyan.tests;

import ca.umanitoba.cs.veranyan.domain.*;
import ca.umanitoba.cs.veranyan.logic.MapManagerTestHarness;
import ca.umanitoba.cs.veranyan.logic.PathFinderTestHarness;
import ca.umanitoba.cs.veranyan.logic.ProfileRegistryTestHarness;
import ca.umanitoba.cs.veranyan.logic.RouteManagerTestHarness;
import ca.umanitoba.cs.veranyan.output.Colourise;
import com.github.lalyos.jfiglet.FigletFont;

import java.io.IOException;

public class TestHarness {
    private static int successes;
    private static int failures;

    public static void main(String[] args) {
        bubblePrint("Test Harness");

        TestResults results = new ProfileTestHarness().runTests();
        successes += results.successes();
        failures += results.failures();

        results = new ActivityTestHarness().runTests();
        successes += results.successes();
        failures += results.failures();

        results = new GearTestHarness().runTests();
        successes += results.successes();
        failures += results.failures();

        results = new ObstacleTestHarness().runTests();
        successes += results.successes();
        failures += results.failures();

        results = new RouteTestHarness().runTests();
        successes += results.successes();
        failures += results.failures();

        results = new MapTestHarness().runTests();
        successes += results.successes();
        failures += results.failures();

        results = new ProfileRegistryTestHarness().runTests();
        successes += results.successes();
        failures += results.failures();

        results = new MapManagerTestHarness().runTests();
        successes += results.successes();
        failures += results.failures();

        results = new RouteManagerTestHarness().runTests();
        successes += results.successes();
        failures += results.failures();

        results = new PathFinderTestHarness().runTests();
        successes += results.successes();
        failures += results.failures();

        results = new LinkedListStackTestHarness().runTests();
        successes += results.successes();
        failures += results.failures();



        System.out.printf("Total tests: %d\n", successes + failures);
        System.out.printf("\tSuccesses: %d\n", successes);
        System.out.printf("\tFailures: %d\n", failures);

        if (failures > 0) {
            Colourise.red("There were test failures.\n");
        } else {
            Colourise.green("All tests passed!");
        }
    }

    public void pass(String message) {
        successes++;
        Colourise.green("PASS: " + message + "\n");
    }

    public void fail(String message) {
        failures++;

        Colourise.red("FAIL: " + message + "\n");
    }

    public static void bubblePrint(String message) {
        try {
            System.out.println(FigletFont.convertOneLine(message));
        } catch (IOException ignored) { }
    }
}
