package de.tuberlin.sgd.core;

import com.google.common.base.Preconditions;

import java.util.List;

public class DVector {

    public final double[] elements;

    public DVector(final int size) {
        Preconditions.checkArgument(size > 0);
        this.elements = new double[size];
    }

    public void set(final double[] data) {
        Preconditions.checkNotNull(data);
        Preconditions.checkArgument(elements.length == data.length);
        System.arraycopy(data, 0, elements, 0, data.length);
    }

    static DVector createFrom(final List<Double> values) {
        Preconditions.checkNotNull(values);
        final DVector vec = new DVector(values.size());
        int i = 0;
        for (double val : values)
            vec.elements[i++] = val;
        return vec;
    }
}
