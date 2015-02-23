package de.tuberlin.sgd.core.old.v0;

public class StochasticGradientDescentDemo {

    public static void main(final String[] args) {

        final double alpha = 0.005;
        final int numOfIterations = 500000;
        final int numOfParams = 2;
        final double params[] = new double[numOfParams];
        final int numOfExamples = 4;
        final int numOfFeatures = 2;
        final double exFeatures[][] = new double[numOfExamples][numOfFeatures];
        final double exPredictions[] = new double[numOfExamples];

        exFeatures[0][1] = -1;
        exPredictions[0] = 0;
        exFeatures[1][1] = 0;
        exPredictions[1] = 1;
        exFeatures[2][1] = 1;
        exPredictions[2] = 2;
        exFeatures[3][1] = 2;
        exPredictions[3] = 1;

        for (int p = 0; p < numOfIterations; ++p) {
            for (int i = 0; i < numOfExamples; ++i) {
                double y = params[0];
                for (int j = 1; j < numOfParams; ++j)
                    y += params[j] * exFeatures[i][j];
                params[0] = params[0] + alpha * (exPredictions[i] - y);
                for (int j = 1; j < numOfParams; ++j) {
                    // compute all weights from 1 to w
                    params[j] = params[j] + alpha * (exPredictions[i] - y) * exFeatures[i][j];
                }
            }
        }

        System.out.println(params[0]);
        System.out.println(params[1]);
    }
}
