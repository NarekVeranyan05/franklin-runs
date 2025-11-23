package ca.umanitoba.cs.veranyan.tests;

import ca.umanitoba.cs.veranyan.domain.ActivityTestHarness;
import ca.umanitoba.cs.veranyan.domain.ProfileTestHarness;
import ca.umanitoba.cs.veranyan.output.Colourise;
import com.github.lalyos.jfiglet.FigletFont;

import java.io.IOException;

public class TestHarness {
    private static int successes;
    private static int failures;

    public static void main(String[] args) {
        bubblePrint();

        TestResults results = new ProfileTestHarness().runTests();
        successes += results.successes();
        failures += results.failures();

        results = new ActivityTestHarness().runTests();
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

    private static void bubblePrint() {
        try {
            System.out.println(FigletFont.convertOneLine("Test Harness"));
        } catch (IOException ignored) { }
    }
}
