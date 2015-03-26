package de.tuberlin.sgd.core;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import com.google.common.primitives.Ints;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MultiSGD {

    final static int numOfIteration = 1;

    private static volatile DVector[] params = new DVector[numOfIteration];


    public static void main(String[] args) {

        for (int x = 0; x < numOfIteration; x++) {

            params[x] = new DVector(97);
            final int nThreads = 5;

            //final String[] fields = new String[]{"sepal-length", "sepal-width", "petal-length", "petal-width", "label"};

            final String[] buzzFields= new String[]{"NCD_0","NCD_1","NCD_2","NCD_3","NCD_4","NCD_5","NCD_6","NCD_7",
                                                     "BL_0","BL_1","BL_2","BL_3","BL_4","BL_5","BL_6","BL_7",
                                                     "NAD_0","NAD_1","NAD_2","NAD_3","NAD_4","NAD_5","NAD_6","NAD_7",
                                                     "AI_0","AI_1","AI_2","AI_3","AI_4","AI_5","AI_6","AI_7",
                                                     "NAC_0","NAC_1","NAC_2","NAC_3","NAC_4","NAC_5","NAC_6","NAC_7",
                                                     "ND_0","ND_1","ND_2","ND_3","ND_4","ND_5","ND_6","ND_7",
                                                     "CS_0","CS_1","CS_2","CS_3","CS_4","CS_5","CS_6","CS_7",
                                                     "AT_0","AT_1","AT_2","AT_3","AT_4","AT_5","AT_6","AT_7",
                                                     "NA_0","NA_1","NA_2","NA_3","NA_4","NA_5","NA_6","NA_7",
                                                     "ADL_0","ADL_1","ADL_2","ADL_3","ADL_4","ADL_5","ADL_6","ADL_7",
                                                     "AS(NA)_0","AS(NA)_1","AS(NA)_2","AS(NA)_3","AS(NA)_4","AS(NA)_5","AS(NA)_6","AS(NA)_7",
                                                     "AS(NAC)_0","AS(NAC)_1","AS(NAC)_2","AS(NAC)_3","AS(NAC)_4","AS(NAC)_5","AS(NAC)_6","AS(NAC)_7",
                                                     "prediction"};


            //DVectorFrame frame = CSVDataReader.readCSV("ML_Data/iris/iris.data", spamFields);
            DVectorFrame frame = CSVDataReader.readCSV("ML_Data/Buzz/TomsHardware.data", buzzFields);
            List<DVectorFrame> splittedList = frame.split(nThreads, buzzFields);

            ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
            int[] X_indices = Ints.toArray(ContiguousSet.create(Range.closed(0, 95), DiscreteDomain.integers()));

            for (int i = 0; i < nThreads; i++) {
                System.out.println("Thread No "+i);
                executorService.execute(new RunnableSGD(params[x], splittedList.get(i), X_indices, 96, splittedList.get(i).getVec(0).elements.length));
            }

            executorService.shutdown();

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
