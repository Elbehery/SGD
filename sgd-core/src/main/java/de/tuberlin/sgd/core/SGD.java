package de.tuberlin.sgd.core;

import java.math.BigDecimal;

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

        public abstract BigDecimal dloss(BigDecimal p, BigDecimal y);
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

        @Override
        public BigDecimal dloss(BigDecimal p, BigDecimal y) {
            return p.subtract(y);
        }
    }

    /**
     * Base class for SGD classification and regression.
     */
    public static abstract class SGDBase {

        /** The loss function to be used. */
        protected ConvexLossFunction lossFunction = new SquaredLossFunction();

        /** The initial learning rate. */
        protected double alpha = 0.005;

        /** The number of passes over the training data (aka epochs). */
        protected int numIterations = 13000;

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
                final double alpha,
                final int numIterations,
                final int numSamples) {

            if (lossFunction instanceof SquaredLossFunction) {

                for (int epoch = 0; epoch < numIterations; ++epoch) {

                    for (int i = 0; i < numSamples; ++i) {

                        // -- Compute the prediction (p) --

                        // Dot product of a sample x and the parameter vector.
                        int m = 1;
                        BigDecimal p = params.elements[0];
                        for (int j : X_indices)
                            p = p.add(frame.getVec(j).elements[i].multiply(params.elements[m++])) ;

                        // -- Minimize the loss function --

                        // Compute parameter Θ(0).
                        params.elements[0] = params.elements[0].subtract(BigDecimal.valueOf(alpha).multiply(lossFunction.dloss(p, frame.getVec(Y_index).elements[i]))) ;

                        // Compute parameters Θ(1..m).
                        int n = 1;
                        for (int k : X_indices) {
                            params.elements[n] = params.elements[n].subtract(BigDecimal.valueOf(alpha).multiply(lossFunction.dloss(p, frame.getVec(Y_index).elements[i]).multiply(frame.getVec(k).elements[i])));
                            n++;
                        }
                    }
                }

            } else
                throw new IllegalStateException();

            return params;
        }
    }

    // ---------------------------------------------------

    public static void main(final String[] args) {

        final DVector params = new DVector(5);
        final String[] fields = new String[] {"sepal-length", "sepal-width", "petal-length", "petal-width", "label"};
        final DVectorFrame frame = CSVDataReader.readCSV("ML_Data/iris/iris.data", fields);

        final SGDRegressor sgd = new SGDRegressor();
        sgd.fit(params, frame, new int[]{0, 1, 2, 3}, 4, frame.getVec(0).elements.length);

        System.out.println(params.elements[0]);
        System.out.println(params.elements[1]);
        System.out.println(params.elements[2]);
        System.out.println(params.elements[3]);
        System.out.println(params.elements[4]);


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