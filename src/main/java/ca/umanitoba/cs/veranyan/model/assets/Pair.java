package ca.umanitoba.cs.veranyan.model.assets;

import com.google.common.base.Preconditions;

/**
 * The {@code Pair} immutable class represents a key-value pair of objects of two types
 * @param <T> the key of the {@code Pair}
 * @param <S> the value of the {@code Pair}
 */
public class Pair<T, S> {
    private final T firstComponent;
    private final S secondComponent;

    public Pair(T firstComponent, S secondComponent) {
        this.firstComponent = firstComponent;
        this.secondComponent = secondComponent;

        checkPair();
    }

    public T getFirst() {
        checkPair();

        return firstComponent;
    }

    public S getSecond() {
        checkPair();

        return secondComponent;
    }

    private void checkPair(){
        Preconditions.checkNotNull(firstComponent, "firstComponent cannot be null");
        Preconditions.checkNotNull(secondComponent, "secondComponent cannot be null");
    }
}