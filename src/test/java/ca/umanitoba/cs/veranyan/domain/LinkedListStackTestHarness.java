package ca.umanitoba.cs.veranyan.domain;

import ca.umanitoba.cs.comp2450.stack.Stack;
import ca.umanitoba.cs.veranyan.model.assets.LinkedListStack;
import ca.umanitoba.cs.veranyan.output.Colourise;
import ca.umanitoba.cs.veranyan.tests.TestResults;

import static ca.umanitoba.cs.veranyan.tests.TestHarness.bubblePrint;

public class LinkedListStackTestHarness {
    private int successes = 0;
    private int failures = 0;

    public static void main(String[] args) {
        new LinkedListStackTestHarness().runTests();
    }

    public TestResults runTests() {
        bubblePrint("Gear Test Harness");

        testPushOnEmpty();
        testPushOnFilled();
        testEmptyStack();
        testPopOnEmpty();
        testPopAndPush();

        testPeekOnEmpty();

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

    private void testPushOnEmpty(){
        try{
            Stack<String> stack = new LinkedListStack<>("PLACEHOLDER");
            stack.push("first");

            if(stack.size() != 1){
                fail("Size was not changed appropriately, expected 1, got " + stack.size());
            }
            else if(stack.isEmpty()){
                fail("Expected stack to be full, got false");
            }
            else if(!stack.peek().equals("first")){
                fail("Expected the top of the stack to be \"first\", got " + stack.peek());
            }

            var top = stack.pop();
            if(!top.equals("first")){
                fail("Expected the top of the stack to be \"first\", got " + top);
            }
            else{
                pass("Successfully pushed \"first\" to the top of the stack");
            }

        } catch (Exception e){
            fail("Unexpected exception thrown");
            e.printStackTrace();
        }
    }

    private void testPushOnFilled(){
        try {
            Stack<String> stack = new LinkedListStack<>("PLACEHOLDER");

            stack.push("a");
            stack.push("b");
            stack.push("c");

            if (stack.size() != 3) {
                fail("Size was not changed appropriately, expected 3, got " + stack.size());
            } else if (stack.isEmpty()) {
                fail("Expected stack to be full, got false");
            } else if (!stack.peek().equals("c")) {
                fail("Expected the top of the stack to be \"c\", got " + stack.peek());
            }

            var top = stack.pop();
            if (!top.equals("c")) {
                fail("Expected the top of the stack to be \"c\", got " + top);
            } else if(!stack.peek().equals("b")){
                fail("Expected the top of the stack to be \"b\" after pop, got " + stack.peek());
            }
            else pass("Successfully pushed \"c\" to the top of the stack");
        } catch (Exception e) {
            fail("Unexpected exception thrown");
            e.printStackTrace();
        }
    }

    private void testEmptyStack(){
        try {
            Stack<String> stack = new LinkedListStack<>("PLACEHOLDER");

            if (!stack.isEmpty()) {
                fail("Expected the stack to be empty, got false");
            }
            else if (stack.size() != 0){
                fail("Expected the stack size to be 0, got " + stack.size());
            }
            else pass("Stack methods show it is empty, as expected");
        } catch (Exception e) {
            fail("Unexpected exception thrown");
            e.printStackTrace();
        }
    }

    private void testPopOnEmpty(){
        try {
            Stack<String> stack = new LinkedListStack<>("PLACEHOLDER");

            try{
                stack.pop();

                fail("Should not be able to pop an empty stack");
            } catch (Stack.EmptyStackException e) {
                pass("Successfully rejected popping an empty stack");
            }

        } catch (Exception e) {
            fail("Unexpected exception thrown");
            e.printStackTrace();
        }
    }

    private void testPopAndPush(){
        try {
            Stack<String> stack = new LinkedListStack<>("PLACEHOLDER");
            stack.push("a");
            stack.push("b");
            stack.push("c");
            stack.push("d");

            stack.pop();
            stack.push("k");

            if (stack.size() != 4) {
                fail("Size was not changed appropriately, expected 4, got " + stack.size());
            } else if (stack.isEmpty()) {
                fail("Expected stack to be full, got false");
            } else if (!stack.peek().equals("k")) {
                fail("Expected the top of the stack to be \"k\", got " + stack.peek());
            }

            var top = stack.pop();
            if (!top.equals("k")) {
                fail("Expected the top of the stack to be \"k\", got " + top);
            } else if(!stack.peek().equals("c")){
                fail("Expected the top of the stack to be \"c\" after pop, got " + stack.peek());
            }
            else pass("Successfully pushed \"k\" to the top of the stack after pop.");
        } catch (Exception e) {
            fail("Unexpected exception thrown");
            e.printStackTrace();
        }
    }

    private void testPeekOnEmpty(){
        try {
            Stack<String> stack = new LinkedListStack<>("PLACEHOLDER");

            try{
                stack.peek();

                fail("Should not be able to peek an empty stack");
            } catch (Stack.EmptyStackException e) {
                pass("Successfully rejected peeking an empty stack");
            }

        } catch (Exception e) {
            fail("Unexpected exception thrown");
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

