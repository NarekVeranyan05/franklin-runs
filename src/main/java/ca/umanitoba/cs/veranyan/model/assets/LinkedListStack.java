package ca.umanitoba.cs.veranyan.model.assets;

import ca.umanitoba.cs.comp2450.stack.Stack;
import com.google.common.base.Preconditions;

/**
 * LinkedList implementation of the {@link Stack} interface
 * @param <T>
 */
public class LinkedListStack<T> implements Stack<T>{
    private final T placeholder;
    private Node top;
    private int size;

    /**
     * Constructor for stack
     * @param placeholder the placeholder value for the dummy node
     */
    public LinkedListStack(T placeholder){
        this.placeholder = placeholder;
        this.top = new Node();
        this.size = 0;

        checkLinkedListStack();
    }

    /**
     * Pushes a new item to the top of the stack
     * @param item the item to be pushed
     */
    @Override
    public void push(T item) {
        Preconditions.checkNotNull(item, "item cannot be null");
        checkLinkedListStack();

        top = new Node(top, item);
        size++;

        checkLinkedListStack();
    }

    /**
     * Pops and returns the entry at the top of the stack
     *
     * @return the item contained in the entry at the top
     * @throws EmptyStackException if the stack is empty
     */
    @Override
    public T pop() throws EmptyStackException {
        checkLinkedListStack();

        if(isEmpty())
            throw new EmptyStackException("empty stack");

        var popped = this.top;

        this.top = this.top.next;
        size--;

        checkLinkedListStack();

        return popped.data;
    }

    /**
     * Peeks the top of the stack without popping
     * @return the item at the top of the stack
     * @throws EmptyStackException if the stack is empty
     */
    @Override
    public T peek() throws EmptyStackException {
        checkLinkedListStack();

        if(isEmpty())
            throw new EmptyStackException("empty stack");

        return top.data;
    }

    @Override
    public int size() {
        checkLinkedListStack();

        return size;
    }

    @Override
    public boolean isEmpty() {
        checkLinkedListStack();

        return (size == 0);
    }

    /**
     * Invariants for {@link LinkedListStack}
     */
    private void checkLinkedListStack(){
        Preconditions.checkNotNull(placeholder, "placeholder cannot be null");
        Preconditions.checkNotNull(top, "top cannot be null");
        Preconditions.checkState(size >= 0, "size cannot be negative");
    }

    /**
     * Inner {@link Node} class to for the {@link LinkedListStack}
     */
    private class Node{
        Node next;
        T data;

        /**
         * Constructor for placeholder {@link Node}
         */
        public Node(){
            this.next = this;
            this.data = placeholder;

            checkNode();
        }

        public Node(Node next, T data){
            this.next = next;
            this.data = data;

            checkNode();
        }

        /**
         * Class invariants for {@link Node}
         */
        private void checkNode(){
            Preconditions.checkNotNull(placeholder, "placeholder cannot be null");
            Preconditions.checkNotNull(next, "next cannot be null");
            Preconditions.checkNotNull(data, "data cannot be null");
        }
    }
}
