package ca.umanitoba.cs.veranyan.assets;

/**
 * This is a generic interface for a stack.
 *
 * @see <a href="
 * ">Java Generics Tutorial</a>
 * @param <T> The class type that will be used when instances of classes
 *           implementing this interface are constructed.
 */
public interface Stack<T> {

    /**
     * Push something on to the top of the stack. The stack's size will increase
     * by one.
     *
     * @param item the item that should become the top of the stack.
     */
    void push(T item);

    /**
     * Pop the top of the stack off. The stack's size may be reduced by one, if
     * the stack is not already empty. If the stack is empty, this method will
     * return {@code null}.
     *
     * @return the item on the top of the stack, or {@code null} if the stack is
     * empty.
     * @throws EmptyStackException when the stack is empty (we can't pop an empty stack).
     */
    T pop() throws EmptyStackException;

    /**
     * How many items are currently on the stack? Always returns zero or a
     * positive number.
     *
     * @return the number of items on the stack.
     */
    int size();

    /**
     * A convenience test to see if the stack is currently empty (its size is
     * zero and the underlying storage container has no elements).
     *
     * @return {@code true} if the stack is empty, {@code false} otherwise.
     */
    boolean isEmpty();

    /**
     * Look at the thing that's on the top of the stack without {@link
     * Stack#pop()}ing it off the top of the stack.
     *
     * @return the item on the top of the stack.
     * @throws EmptyStackException when the stack is empty (we can't peek an empty stack).
     */
    T peek() throws EmptyStackException;

    /**
     * An exception raised when trying to pop or peek an empty {@link Stack}.
     */
    class EmptyStackException extends RuntimeException {
        public EmptyStackException(String message) {
            super(message);
        }
    }
}
