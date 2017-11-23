package org.datazup.pathextractor;

/**
 * Created by admin@datazup on 3/14/16.
 */

public interface AbstractVariableSet<T> {
    boolean containsKey(String key);
    T get(String key);
    void set(String key, T value);
    Object remove(String argumentName);
}
