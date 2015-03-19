package de.tuberlin.sgd.core;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MultiSGD {

    final static int numOfIteration = 10;
    private static volatile DVector[] params = new DVector[numOfIteration];

    public static void main(String[] args) {

        for(int x=0; x<numOfIteration; x ++) {

            params[x] = new DVector(5);

            final int nThreads = 6;
            final String[] fields = new String[]{"sepal-length", "sepal-width", "petal-length", "petal-width", "label"};
            DVectorFrame frame = CSVDataReader.readCSV("ML_Data/iris/iris.data", fields);

            List<DVectorFrame> splittedList = frame.split(nThreads, fields);

            ExecutorService executorService = Executors.newFixedThreadPool(nThreads);

            for (int i = 0; i < nThreads; i++) {
                executorService.execute(new RunnableSGD(params[x], splittedList.get(i), new int[]{0, 1, 2, 3}, 4, splittedList.get(i).getVec(0).elements.length));
            }

            executorService.shutdown();

            System.out.println(params[x].elements[0]);
            System.out.println(params[x].elements[1]);
            System.out.println(params[x].elements[2]);
            System.out.println(params[x].elements[3]);
            System.out.println(params[x].elements[4]);

            System.out.println();
        }

    }
}
