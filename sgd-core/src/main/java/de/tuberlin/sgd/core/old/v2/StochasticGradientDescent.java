package de.tuberlin.sgd.core.old.v2;

import com.google.common.base.Preconditions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StochasticGradientDescent {

    public static class DVec {

        public final double[] elements;

        public DVec(final int size) {
            Preconditions.checkArgument(size > 0);
            this.elements = new double[size];
        }

        public void set(final double[] data) {
            Preconditions.checkNotNull(data);
            Preconditions.checkArgument(elements.length == data.length);
            System.arraycopy(data, 0, elements, 0, data.length);
        }

        static DVec createFrom(final List<Double> values) {
            Preconditions.checkNotNull(values);
            final DVec vec = new DVec(values.size());
            int i = 0;
            for (double val : values)
                vec.elements[i++] = val;
            return vec;
        }
    }

    public static class DVecFrame {

        private final DVec[] vecs;

        private final Map<String, Integer> vecIndices;

        public DVecFrame(final String[] names) {
            Preconditions.checkNotNull(names);
            this.vecs = new DVec[names.length];
            this.vecIndices = new HashMap<String,Integer>();
            int i = 0;
            for(String n : names)
                vecIndices.put(n, i++);
        }

        public void setVec(final String name, final DVec fv) {
            Preconditions.checkNotNull(name);
            Preconditions.checkNotNull(fv);
            final Integer i = vecIndices.get(name);
            Preconditions.checkState(i != null);
            vecs[i] = fv;
        }

        public void setVec(final int i, final DVec fv) {
            Preconditions.checkArgument(i >= 0 && i < vecs.length);
            Preconditions.checkNotNull(fv);
            vecs[i] = fv;
        }

        public DVec getVec(final String name) {
            Preconditions.checkNotNull(name);
            final Integer i = vecIndices.get(name);
            Preconditions.checkState(i != null);
            return vecs[i];
        }

        public DVec getVec(final int i) {
            Preconditions.checkArgument(i >= 0 && i < vecs.length);
            return vecs[i];
        }

        public int getNumberOfVecs() {
            return vecs.length;
        }
    }

    public static class CSVDataReader {

        public static DVecFrame readCSV(final String csvFile, final String[] fields) {
            Preconditions.checkNotNull(fields);
            Preconditions.checkNotNull(csvFile);
            final DVecFrame frame = new DVecFrame(fields);
            final String cvsSplitBy = ",";
            final List<Double>[] data = new ArrayList[fields.length];
            BufferedReader br = null;
            try {
                String line = "";
                br = new BufferedReader(new FileReader(csvFile));
                while ((line = br.readLine()) != null) {
                    final String[] example = line.split(cvsSplitBy);
                    int i = 0;
                    for (final String f : example) {
                        if (data[i] == null)
                            data[i] = new ArrayList<>();
                        data[i++].add(Double.valueOf(f));
                    }
                }
                int i = 0;
                for (final String f : fields) {
                    final DVec v = DVec.createFrom(data[i++]);
                    frame.setVec(f, v);
                }
                return frame;
            } catch (Exception e) {
                throw new IllegalStateException(e);
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
        }
    }


    /**
     * Classification and regression using Stochastic Gradient Descent (SGD).
     * Stochastic Gradient Descent (SGD) is a simple yet very efficient approach
     * to discriminative learning of linear classifiers under convex loss functions
     * such as (linear) Support Vector Machines and Logistic Regression.
     */
    public static class SGD {

        /**
         * http://scikit-learn.org/stable/modules/sgd.html
         * https://github.com/scikit-learn/scikit-learn/blob/master/sklearn/linear_model/stochastic_gradient.py
         */


        private final DVec params;

        private double alpha;

        public SGD(final DVec params) {
            Preconditions.checkNotNull(params);
            this.params = params;
            this.alpha = 0.005;
        }

        public void optimize(final DVecFrame frame, final int resultVec) {
            Preconditions.checkNotNull(frame);

            for (int i = 0; i < frame.getVec(0).elements.length; ++i) {

                double y = params.elements[0];
                for (int j = 1; j < params.elements.length; ++j)
                    for (int k = 0; k < frame.getNumberOfVecs() - 1; ++k)
                        y += params.elements[j] * frame.getVec(k).elements[i];

                params.elements[0] = params.elements[0] + alpha * (frame.getVec(resultVec).elements[i] - y);

                for (int l = 1; l < params.elements.length; ++l) {
                    for (int m = 0; m < frame.getNumberOfVecs() - 1; ++m)
                        params.elements[l] += alpha * (frame.getVec(resultVec).elements[i] - y) * frame.getVec(m).elements[i];
                }
            }
        }
    }

    public static void main(final String[] args) {

        final DVec params = new DVec(4);
        String[] fields = new String[] {"sepal-length", "sepal-width", "petal-length", "petal-width", "label"};
        final DVecFrame frame = CSVDataReader.readCSV("ML_Data/iris/iris.data", fields);
        final SGD sgd = new SGD(params);
        final int numOfIterations = 8;
        for (int p = 0; p < numOfIterations; ++p) {
            sgd.optimize(frame, 4);
        }
        System.out.println(params.elements[0]);
        System.out.println(params.elements[1]);
        System.out.println(params.elements[2]);
        System.out.println(params.elements[3]);

        /*final DVec params = new DVec(2);
        final DVec col0 = new DVec(4);
        col0.set( new double[] {-1, 0, 1, 2} );
        final DVec col1 = new DVec(4);
        col1.set( new double[] {0, 1, 2, 1} );
        final DVecFrame frame = new DVecFrame(new String[] {"col0", "col1"});
        frame.setVec(0, col0);
        frame.setVec(1, col1);
        final SGD sgd = new SGD(params);
        final int numOfIterations = 500000;
        for (int p = 0; p < numOfIterations; ++p) {
            sgd.optimize(frame, 1);
        }
        // 0.7988916441285822
        // 0.3951641122099793
        System.out.println(params.elements[0]);
        System.out.println(params.elements[1]);*/
    }
}
