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

            params[x] = new DVector(58);
            final int nThreads = 5;

            //final String[] fields = new String[]{"sepal-length", "sepal-width", "petal-length", "petal-width", "label"};
            final String[] spamFields = new String[]{"word_freq_make", "word_freq_address", "word_freq_all", "word_freq_3d", "word_freq_our", "word_freq_over", "word_freq_remove", "word_freq_internet", "word_freq_order", "word_freq_mail", "word_freq_receive",
                    "word_freq_will", "word_freq_people", "word_freq_report", "word_freq_addresses", "word_freq_free", "word_freq_business", "word_freq_email", "word_freq_you", "word_freq_credit",
                    "word_freq_your", "word_freq_font", "word_freq_000", "word_freq_money", "word_freq_hp", "word_freq_hpl", "word_freq_george", "word_freq_650", "word_freq_lab", "word_freq_labs", "word_freq_telnet", "word_freq_857",
                    "word_freq_data", "word_freq_415", "word_freq_85", "word_freq_technology", "word_freq_1999", "word_freq_parts", "word_freq_pm", "word_freq_direct", "word_freq_cs", "word_freq_meeting", "word_freq_original", "word_freq_project",
                    "word_freq_re", "word_freq_edu", "word_freq_table", "word_freq_conference", "char_freq_;", "char_freq_(", "char_freq_[", "char_freq_!", "char_freq_$", "char_freq_#", "capital_run_length_average", "capital_run_length_longest", "capital_run_length_total", "Label"};

            //DVectorFrame frame = CSVDataReader.readCSV("ML_Data/iris/iris.data", spamFields);
            DVectorFrame frame = CSVDataReader.readCSV("ML_Data/Spambase/spambase.data", spamFields);
            List<DVectorFrame> splittedList = frame.split(nThreads, spamFields);

            ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
            int[] X_indices = Ints.toArray(ContiguousSet.create(Range.closed(0, 56), DiscreteDomain.integers()));

            for (int i = 0; i < nThreads; i++) {
                System.out.println("Thread No "+i);
                executorService.execute(new RunnableSGD(params[x], splittedList.get(0), X_indices, 57, splittedList.get(0).getVec(0).elements.length));
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
