package ca.umanitoba.cs.veranyan.tests;

/**
 * Communicates the number of successes and failures between a test suite and
 * a test runner.
 * @param successes The total number of successes from the tests.
 * @param failures The total number of failures from the tests.
 */
public record TestResults(int successes, int failures) {
}
