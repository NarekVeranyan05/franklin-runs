package ca.umanitoba.cs.veranyan.model.assets;

import java.util.EmptyStackException;

/**
 * Interface representing a stack data structure.
 *
 * @param <T> the type of elements stored in the stack
 */
public interface Stack<T> {

    /**
     * Pushes a new item to the top of the stack.
     *
     * @param item the item to be pushed
     */
    void push(T item);

    /**
     * Pops and returns the item at the top of the stack.
     *
     * @return the item at the top of the stack
     * @throws EmptyStackException if the stack is empty
     */
    T pop() throws EmptyStackException;

    /**
     * Returns the item at the top of the stack without removing it.
     *
     * @return the item at the top of the stack
     * @throws EmptyStackException if the stack is empty
     */
    T peek() throws EmptyStackException;

    /**
     * Returns the number of items currently in the stack.
     *
     * @return the stack size
     */
    int size();

    /**
     * Determines whether the stack is empty.
     *
     * @return true if the stack is empty, false otherwise
     */
    boolean isEmpty();

    /** * Exception thrown when attempting to access an empty stack. */
    class EmptyStackException extends RuntimeException {
        public EmptyStackException(String message) {
            super(message);
        }
    }
}
