package de.tuberlin.sgd.core;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import com.google.common.primitives.Ints;

import java.util.Random;

public final class SGD {

    // Disallow instantiation.
    private SGD() {}

    /**
     * Base class for convex loss functions.
     */
    public static interface ConvexLossFunction {

        /**
         * Convex Loss Functions:
         *      + Square loss: L(y, y') = 0.5 * (p - y)²
         *      + Hinge loss: L(y, y') = max{0, 1 − y * y'}
         *      + Exponential loss: L(y, y') = exp(− y * y')
         *      + Logistic loss: L(y, y') = log2(1 + exp(−y * y'))
         *
         * (All of these are convex upper bounds on 0-1 loss)
         */

        /** Evaluate the loss function. */
        public abstract double loss(double p, double y);

        /** Evaluate the derivative of the loss function with respect to
         the prediction `p`. */
        public abstract double dloss(double p, double y);
    }

    /**
     * Squared loss traditional used in linear regression.
     */
    public static class SquaredLossFunction implements ConvexLossFunction {

        @Override
        public double loss(double p, double y) {
            return 0.5 * (p - y) * (p - y);
        }

        @Override
        public double dloss(double p, double y) {
            return (p - y);
        }
    }

    /**
     * Base class for SGD classification and regression.
     */
    public static abstract class SGDBase {

        /** The loss function to be used. */
        protected ConvexLossFunction lossFunction = new SquaredLossFunction();

        /** The initial learning rate. */
        protected double alpha = 0.001;

        /** The number of passes over the training data (aka epochs). */
        protected int numIterations = 1500000;

        /** Fit linear model. */
        public abstract DVector fit(
                final DVector params,
                final DVectorFrame frame,
                final int[] X_indices,
                final int Y_index,
                final int numSamples);

        /** Predict using the linear model. */
        public abstract DVector predict(
                final DVector params,
                final DVectorFrame frame,
                final int[] X_indices,
                final int numSamples);
    }

    /**
     * Linear model fitted by minimizing a regularized empirical loss with SGD.
     */
    public static class SGDRegressor extends SGDBase {

        @Override
        public DVector fit(
                final DVector params,
                final DVectorFrame frame,
                final int[] X_indices,
                final int Y_index,
                final int numSamples) {

            return doPlainSGD(
                    params,
                    frame,
                    X_indices,
                    Y_index,
                    lossFunction,
                    alpha,
                    numIterations,
                    numSamples
            );
        }

        @Override
        public DVector predict(
                final DVector params,
                final DVectorFrame frame,
                final int[] X_indices,
                final int numSamples) {
            return null;
        }

        private DVector doPlainSGD(
                final DVector params,
                final DVectorFrame frame,
                final int[] X_indices,
                final int Y_index,
                final ConvexLossFunction lossFunction,
                double alpha,
                final int numIterations,
                final int numSamples) {

            if (lossFunction instanceof SquaredLossFunction) {

                for (int epoch = 0; epoch < numIterations; ++epoch) {

                    for (int i = 0; i < numSamples; ++i) {

                        // -- Compute the prediction (p) --

                        // Dot product of a sample x and the parameter vector.
                        int m = 1;
                        double p = params.elements[0];
                        for (int j : X_indices) {
                            p += frame.getVec(j).elements[i] * params.elements[m];
                            m++;
                        }

                        // -- Minimize the loss function --

                        // Compute parameter Θ(0).
                        params.elements[0] = params.elements[0] - alpha * lossFunction.dloss(p, frame.getVec(Y_index).elements[i]);

                        // Compute parameters Θ(1..m).
                        int n = 1;
                        for (int k : X_indices) {
                            params.elements[n] = params.elements[n] - alpha * lossFunction.dloss(p, frame.getVec(Y_index).elements[i]) * frame.getVec(k).elements[i];
                            n++;
                        }
                    }
                    if(epoch % 100 == 0)
                        alpha=alpha/2.0;
                }

            } else
                throw new IllegalStateException();

            return params;
        }
    }

    // ---------------------------------------------------

    public static void main(final String[] args) {

        final DVector params = new DVector(11);
        Random random = new Random(1000);

        for(int x=0; x<params.elements.length;) {
            params.elements[x++] = Math.abs(random.nextDouble());
        }

       // final String[] fields = new String[] {"sepal-length", "sepal-width", "petal-length", "petal-width", "label"};
        final String[] fields= new String[]{"1","2","3","4","5","6","7","8","9","10","Label"};
        final DVectorFrame frame = CSVDataReader.readCSV("ML_Data/SynthaticData/Linear/data.data", fields);

        final SGDRegressor sgd = new SGDRegressor();
        int[] X_indices = Ints.toArray(ContiguousSet.create(Range.closed(0, 9), DiscreteDomain.integers()));
        //sgd.fit(params, frame, new int[]{0, 1, 2, 3}, 4, frame.getVec(0).elements.length);

        sgd.fit(params, frame, X_indices, 10, frame.getVec(0).elements.length);

       // System.out.println(params.elements[0]);
        System.out.println(params.elements[1]);
        System.out.println(params.elements[2]);
        System.out.println(params.elements[3]);
        System.out.println(params.elements[4]);
        System.out.println(params.elements[5]);
        System.out.println(params.elements[6]);
        System.out.println(params.elements[7]);
        System.out.println(params.elements[8]);
        System.out.println(params.elements[9]);
        System.out.println(params.elements[10]);



        /*final DVector params = new DVector(2);
        final DVector col0 = new DVector(4);
        col0.set( new double[] {-1, 0, 1, 2} );
        final DVector col1 = new DVector(4);
        col1.set( new double[] {0, 1, 2, 1} );
        final DVectorFrame frame = new DVectorFrame(new String[] {"col0", "col1"});
        frame.setVec(0, col0);
        frame.setVec(1, col1);

        final SGDRegressor sgd = new SGDRegressor();
        sgd.fit(params, frame, new int[] {0}, 1, 4);

        System.out.println(params.elements[0]);
        System.out.println(params.elements[1]);*/
    }
}