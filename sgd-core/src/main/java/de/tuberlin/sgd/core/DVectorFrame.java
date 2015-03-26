package de.tuberlin.sgd.core;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DVectorFrame {

    private final DVector[] dVectors;

    private final Map<String, Integer> vecIndices;

    public DVectorFrame(final String[] names) {
        Preconditions.checkNotNull(names);
        this.dVectors = new DVector[names.length];
        this.vecIndices = new HashMap<String, Integer>();
        int i = 0;
        for (String n : names)
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


    public List<DVectorFrame> split(int numberOfFrames, String[] fields) {

        List<DVectorFrame> splittedList = new ArrayList<DVectorFrame>();

        int frameSize = dVectors[0].elements.length;
        int counter = 0;
        int step = frameSize / numberOfFrames;

        for (int i = 0; i < numberOfFrames; i++) {
            DVectorFrame frame = new DVectorFrame(fields);

            for (int j = 0; j < frame.dVectors.length; j++) {
                if (frame.dVectors[j] == null) {
                    frame.dVectors[j] = new DVector(step);
                }
                try {
                    System.arraycopy(this.dVectors[j].elements, counter, frame.dVectors[j].elements, 0, step);

                } catch (ArrayStoreException | ArrayIndexOutOfBoundsException e) {
                    System.out.println(e.getMessage());
                }
            }
            counter += step;
            splittedList.add(frame);
        }

        return splittedList;
    }
}