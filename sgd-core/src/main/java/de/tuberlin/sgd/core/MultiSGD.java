package de.tuberlin.sgd.core;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import com.google.common.primitives.Ints;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class MultiSGD {

    final static int numOfIteration = 5;

    private static volatile DVector[] params = new DVector[numOfIteration];


    public static void main(String[] args) {

        for (int x = 0; x < numOfIteration; x++) {

            params[x] = new DVector(11);
            final int nThreads = 5;

            //final String[] fields = new String[]{"sepal-length", "sepal-width", "petal-length", "petal-width", "label"};

            final String[] linearDataFields= new String[]{"1","2","3","4","5","6","7","8","9","10","Label"};


            //DVectorFrame frame = CSVDataReader.readCSV("ML_Data/iris/iris.data", spamFields);
            DVectorFrame frame = CSVDataReader.readCSV("ML_Data/SynthaticData/Linear/data.csv", linearDataFields);
            List<DVectorFrame> splittedList = frame.split(nThreads, linearDataFields);

            ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
            int[] X_indices = Ints.toArray(ContiguousSet.create(Range.closed(0,9), DiscreteDomain.integers()));

            for (int i = 0; i < nThreads; i++) {
                System.out.println("Thread No "+i);
                executorService.execute(new RunnableSGD(params[x], splittedList.get(i), X_indices, 10, splittedList.get(i).getVec(0).elements.length));
            }

            executorService.shutdown();

            /*try{
                executorService.awaitTermination(100, TimeUnit.MILLISECONDS);
            }catch (InterruptedException e){

                System.out.println(e.getMessage());
            }*/

        }

        for (int y = 0; y < numOfIteration; y++) {
            System.out.print("Delta of Iteration " + y + " = ");
            double delta = 0;
            for (int h = 0; h < params[y].elements.length - 1; h++) {
                delta = delta + (params[y].elements[h + 1] - params[y].elements[h]);
            }
            System.out.println(delta);

        }

    }
}
