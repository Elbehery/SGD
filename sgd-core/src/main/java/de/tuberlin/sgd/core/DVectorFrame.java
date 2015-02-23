package de.tuberlin.sgd.core;

import com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.Map;

public class DVectorFrame {

    private final DVector[] dVectors;

    private final Map<String, Integer> vecIndices;

    public DVectorFrame(final String[] names) {
        Preconditions.checkNotNull(names);
        this.dVectors = new DVector[names.length];
        this.vecIndices = new HashMap<String,Integer>();
        int i = 0;
        for(String n : names)
            vecIndices.put(n, i++);
    }

    public void setVec(final String name, final DVector fv) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(fv);
        final Integer i = vecIndices.get(name);
        Preconditions.checkState(i != null);
        dVectors[i] = fv;
    }

    public void setVec(final int i, final DVector fv) {
        Preconditions.checkArgument(i >= 0 && i < dVectors.length);
        Preconditions.checkNotNull(fv);
        dVectors[i] = fv;
    }

    public DVector getVec(final String name) {
        Preconditions.checkNotNull(name);
        final Integer i = vecIndices.get(name);
        Preconditions.checkState(i != null);
        return dVectors[i];
    }

    public DVector getVec(final int i) {
        Preconditions.checkArgument(i >= 0 && i < dVectors.length);
        return dVectors[i];
    }

    public int getNumberOfVecs() {
        return dVectors.length;
    }
}