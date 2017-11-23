package org.datazup.ring;

/**
 * Created by admin@datazup on 11/24/16.
 */
public interface IClosableWrapper<T> {
    void close();
    T get();
}
