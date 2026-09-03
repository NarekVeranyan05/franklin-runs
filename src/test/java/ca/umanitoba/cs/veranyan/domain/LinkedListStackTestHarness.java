package ca.umanitoba.cs.veranyan.domain;

import ca.umanitoba.cs.veranyan.model.assets.Stack;
import ca.umanitoba.cs.veranyan.model.assets.LinkedListStack;
import ca.umanitoba.cs.veranyan.model.assets.Stack;
import ca.umanitoba.cs.veranyan.output.Colourise;
import ca.umanitoba.cs.veranyan.tests.TestResults;
import static ca.umanitoba.cs.veranyan.tests.TestHarness.bubblePrint;

public class LinkedListStackTestHarness {
    private int successes = 0;
    private int failures = 0;

    public TestResults runTests() {
        bubblePrint("LinkedListStack Test Harness");

        testPushOnEmpty();
        testPushOnFilled();
        testPushAfterPop();
        testPushAfterAllPop();
        testIsEmpty();
        testSizeOnEmpty();
        testPop();
        testPopOnFilled();
        testPopOnEmpty();
        testOverlyPop();
        testPeekOnEmpty();
        testPeekOnEmptied();

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
        try {
            Stack<String> stack = new LinkedListStack<>("PLACEHOLDER");;
            stack.push("first");

            if (stack.size() != 1) {
                fail("Size was not changed appropriately, expected 1, got " + stack.size());
            }
            if (stack.isEmpty()) {
                fail("Expected isEmpty to return false, got true");
            }
            
            try {
                if (!stack.peek().equals("first")) {
                    fail("Expected the top of the stack to be \"first\", got " + stack.peek());
                }
            } catch (Stack.EmptyStackException e) {
                fail("Should not have prevented peek on filled stack");
            }

            try{
                var top = stack.pop();
                if (!top.equals("first")) {
                    fail("Expected the top of the stack to be \"first\", got \"" + top + "\"");
                } else {
                    pass("Successfully pushed \"first\" to the top of the stack");
                }
            } catch (Stack.EmptyStackException e) {
                fail("Should not have prevented pop on filled stack");
            }
        } catch (Exception e){
            fail("Unexpected exception thrown");
            e.printStackTrace();
        }
    }

    private void testPushOnFilled(){
        try {
            int failsAtStart = failures;
            Stack<String> stack = new LinkedListStack<String>("PLACEHOLDER");;

            stack.push("a");
            stack.push("b");
            stack.push("c");
            stack.push("d");

            if (stack.size() != 4) {
                fail("Size was not changed appropriately, expected 4, got " + stack.size());
            }
            if (stack.isEmpty()) {
                fail("Expected isEmpty to return false, got true");
            }

            try {
                if (!stack.peek().equals("d")) {
                    fail("Expected the top of the stack to be \"d\", got " + stack.peek());
                }
            } catch (Stack.EmptyStackException e) {
                fail("Should not have prevented peek on filled stack");
            }

            try {
                var top = stack.pop();
                if (!top.equals("d")) {
                    fail("Expected the top of the stack to be \"d\", got \"" + top + "\"");
                }
                else{
                    try {
                        if (!stack.peek().equals("c")) {
                            fail("Expected the top of the stack to be \"c\" after pop, got " + stack.peek());
                        }
                        else if(failsAtStart - failures == 0)
                            pass("Successfully pushed all the entries to the stack");
                    } catch (Stack.EmptyStackException e){
                        fail("Should not have prevented peek on filled stack");
                    }
                }
            } catch (Stack.EmptyStackException e) {
                fail("Should not have prevented pop on filled stack");
            }
        } catch (Exception e) {
            fail("Unexpected exception thrown");
            e.printStackTrace();
        }
    }

    private void testPushAfterPop(){
        try {
            int failsAtStart = failures;
            Stack<String> stack = new LinkedListStack<String>("PLACEHOLDER");;

            stack.push("a");
            stack.push("b");
            stack.push("c");
            stack.push("d");

            try {
                stack.pop();
            } catch (Stack.EmptyStackException e) {
                fail("Should not have prevented pop on filled stack");
            }
            stack.push("k");

            if(stack.size() != 4){
                fail("Size was not changed appropriately, expected 4, got " + stack.size());
            }
            if(stack.isEmpty()){
                fail("Expected isEmpty to return false, got true");
            }

            try{
                if(!stack.peek().equals("k")){
                    fail("Expected the top of the stack to be \"k\", got " + stack.peek());
                }
            } catch (Stack.EmptyStackException e) {
                fail("Should not have prevented peek on filled stack");
            }

            try {
                var top = stack.pop();
                if (!top.equals("k")) {
                    fail("Expected the top of the stack to be \"k\", got \"" + top + "\"");
                }
                if (!stack.peek().equals("c")) {
                    fail("Expected the top of the stack to be \"c\" after pop, got " + stack.peek());
                } else if(failsAtStart - failures == 0){
                    pass("Successfully pushed \"k\" to the top of the stack after pop");
                }
            } catch (Stack.EmptyStackException e){
                fail("Should not have prevented pop on filled stack");
            }

        } catch (Exception e) {
            fail("Unexpected exception thrown");
            e.printStackTrace();
        }
    }

    private void testPushAfterAllPop(){
        try {
            int failsAtStart = failures;

            Stack<String> stack = new LinkedListStack<String>("PLACEHOLDER");;
            stack.push("a");
            stack.push("b");
            stack.push("c");
            stack.push("d");

            for (int i = 0; i < 4; i++) {
                try {
                    stack.pop();
                } catch (Stack.EmptyStackException e) {
                    fail("Should not have prevented pop on filled stack");
                }
            }
            stack.push("k");

            if (stack.size() != 1) {
                fail("Size was not changed appropriately, expected 1, got " + stack.size());
            }
            if (stack.isEmpty()) {
                fail("Expected isEmpty to return false, got true");
            }

            try {
                if (!stack.peek().equals("k")) {
                    fail("Expected the top of the stack to be \"k\", got " + stack.peek());
                }
            } catch (Stack.EmptyStackException e) {
                fail("Should not have prevented peek on filled stack");
            }

            try {
                var top = stack.pop();
                if (!top.equals("k")) {
                    fail("Expected the top of the stack to be \"k\", got \"" + top + "\"");
                }
            } catch (Stack.EmptyStackException e){
                fail("Should not have prevented pop on filled stack");
            }

            if(failures - failsAtStart == 0)
                pass("Successfully pushed \"k\" to the top of the stack after emptying");
        } catch (Exception e) {
            fail("Unexpected exception thrown");
            e.printStackTrace();
        }
    }

    private void testIsEmpty(){
        try {
            Stack<String> stack = new LinkedListStack<String>("PLACEHOLDER");;

            if (!stack.isEmpty()) {
                fail("Expected the stack to be empty, got false");
            }
            else pass("Stack methods show it is empty, as expected");
        } catch (Exception e) {
            fail("Unexpected exception thrown");
            e.printStackTrace();
        }
    }

    private void testSizeOnEmpty(){
        try {
            Stack<String> stack = new LinkedListStack<String>("PLACEHOLDER");;

            if (stack.size() != 0) {
                fail("Expected size to return 0, got " + stack.size());
            }
            else pass("Stack methods show it is empty, as expected");
        } catch (Exception e) {
            fail("Unexpected exception thrown");
            e.printStackTrace();
        }
    }

    private void testPop(){
        try {
            int failsAtStart = failures;
            Stack<String> stack = new LinkedListStack<String>("PLACEHOLDER");;
            stack.push("a");
            stack.push("b");
            stack.push("c");
            stack.push("d");

            try {
                var top = stack.pop();

                if (!top.equals("d")) {
                    fail("Expected the top of the stack to be \"d\", got \"" + top + "\"");
                }

                if (stack.size() != 3) {
                    fail("Size was not changed appropriately, expected 3, got " + stack.size());
                }
                if (stack.isEmpty()) {
                    fail("Expected isEmpty to return false, got true");
                }
                try {
                    if (!stack.peek().equals("c")) {
                        fail("Expected the top of the stack to be \"c\", got " + stack.peek());
                    }
                } catch (Stack.EmptyStackException e) {
                    fail("Should not have prevented pop on filled stack");
                }

                if(failures - failsAtStart == 0)
                    pass("Successfully popped the top of the stack");

            } catch (Stack.EmptyStackException e){
                fail("Should not have prevented pop on filled stack");
            }
        } catch (Exception e) {
            fail("Unexpected exception thrown");
            e.printStackTrace();
        }
    }

    private void testPopOnFilled(){
        try {
            int failsAtStart = failures;
            Stack<String> stack = new LinkedListStack<String>("PLACEHOLDER");;
            stack.push("a");
            stack.push("b");
            stack.push("c");
            stack.push("d");

            var values = new String[]{"d", "c", "b", "a"};
            boolean foundError = false;
            for(int i = 0; i < 4 && !foundError; i++){
                try {
                    var top = stack.pop();
                    if (!top.equals(values[i])) {
                        fail("Expected the top of the stack to be \"" + values[i] + "\", got \"" + top + "\"");
                        foundError = true;
                    }
                } catch (Stack.EmptyStackException e){
                    fail("Should not have prevented pop on filled stack");
                }
            }

            if(!foundError){
                if(stack.size() != 0){
                    fail("Size was not changed appropriately, expected 0, got " + stack.size());
                }
                else if(!stack.isEmpty()){
                    fail("Expected stack to be empty, got false");
                }
                else if(failsAtStart - failures == 0)
                    pass("Successfully popped the entire stack");
            }

        } catch (Exception e) {
            fail("Unexpected exception thrown");
            e.printStackTrace();
        }
    }

    private void testPopOnEmpty(){
        try {
            Stack<String> stack = new LinkedListStack<String>("PLACEHOLDER");;

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

    private void testOverlyPop(){
        try{
            Stack<String> stack = new LinkedListStack<String>("PLACEHOLDER");;
            stack.push("a");
            stack.push("b");
            stack.push("c");

            int count = 0;
            try{
                for(int i = 0; i < 4; i++){
                    stack.pop();
                    count++;
                }

                fail("Should not be able to pop an empty stack");
            } catch (Stack.EmptyStackException e) {
                if(count == 3)
                    pass("Successfully rejected popping an empty stack");
                else
                    fail("Expected to allow 3 pops, got " + count);
            }
        } catch (Exception e) {
            fail("Unexpected exception thrown");
            e.printStackTrace();
        }
    }

    private void testPeekOnEmpty(){
        try {
            Stack<String> stack = new LinkedListStack<String>("PLACEHOLDER");;

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

    private void testPeekOnEmptied(){
        try {
            Stack<String> stack = new LinkedListStack<String>("PLACEHOLDER");;
            stack.push("a");
            stack.push("b");
            stack.push("c");

            for(int i = 0; i < 3; i++){
                try {
                    stack.pop();
                } catch (Stack.EmptyStackException e){
                    fail("Should not have prevented pop on filled stack");
                }
            }

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

