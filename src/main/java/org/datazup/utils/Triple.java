package org.datazup.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by admin@datazup on 4/26/16.
 */
public class Triple <F, S, T> {
    private F first;
    private S second;
    private T third;

    public Triple(){}
    public Triple(F first, S second, T third){
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != getClass())
            return false;

        Triple rhs = (Triple)obj;
        return new EqualsBuilder()
                .append(first, rhs.first)
                .append(second, rhs.second)
                .append(third, rhs.third)
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                // if deriving: appendSuper(super.hashCode()).
                        append(first).
                        append(second).
                        append(third).
                        toHashCode();
    }

    public F getFirst() {
        return first;
    }

    public void setFirst(F first) {
        this.first = first;
    }

    public S getSecond() {
        return second;
    }

    public void setSecond(S second) {
        this.second = second;
    }

    public T getThird() {
        return third;
    }

    public void setThird(T third) {
        this.third = third;
    }
}
