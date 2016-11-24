package org.datazup.ring;

/**
 * Created by ninel on 11/24/16.
 */
public interface IClosableWrapper<T> {
    void close();
    T get();
}
