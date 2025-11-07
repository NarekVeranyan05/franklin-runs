package ca.umanitoba.cs.veranyan.assets;

import com.google.common.base.Preconditions;

public class LinkedListStack<T> implements Stack<T> {
    private final T placeholder;
    private Node top;
    private int size;

    /**
     * Constructor for Stack
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

        return popped.data;
    }

     // TODO get rid of this
     @Override
     public String toString() {
         var builder = new StringBuilder();
         builder.append("");

         Node currNode = top;

         System.out.println("size: " + size);
         while(!currNode.data.equals(placeholder)){
             builder.append(currNode.data).append("\n");
             currNode = currNode.next;
         }

         return builder.toString();
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

    private void checkLinkedListStack(){
        Preconditions.checkNotNull(top, "top cannot be null");
        Preconditions.checkState(size >= 0, "size cannot be negative");
    }

    private class Node{
        Node next;
        T data;

        /**
         * Constructor for placeholder node
         */
        public Node(){
            this.next = this;
            this.data = placeholder;
        }

        public Node(Node next, T data){
            this.next = next;
            this.data = data;

            checkNode();
        }

        private void checkNode(){
            Preconditions.checkNotNull(next, "next cannot be null");
        }
    }
}
