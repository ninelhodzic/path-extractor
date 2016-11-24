package org.datazup.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by ninel on 11/25/16.
 */

public class Tuple<T, D> {
    private final T key;
    private final D value;
    public Tuple(T key, D value) {
        this.key = key;
        this.value = value;
    }


    public T getKey() {
        return key;
    }

    public D getValue() {
        return value;
    }


    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != getClass())
            return false;

        Tuple rhs = (Tuple)obj;
        return new EqualsBuilder()
                .append(key, rhs.key)
                .append(value, rhs.value)
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                // if deriving: appendSuper(super.hashCode()).
                        append(key).
                        append(value).
                        toHashCode();
    }

    public String toString(){
        return key.toString()+value.toString();
    }
}