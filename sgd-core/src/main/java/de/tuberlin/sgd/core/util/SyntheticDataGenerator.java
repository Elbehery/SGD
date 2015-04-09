package de.tuberlin.sgd.core.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class SyntheticDataGenerator {

    private static final String NEW_LINE_SEPARATOR = "\n";

    public static double[][] linearFunctionSyntheticGenearator(int row, int col) {

        final double[][] syntheticData = new double[row][col + 1];
        final double[] syntheticParameter = new double[col];
        Random random = new Random();

        for(int x=0; x<syntheticParameter.length;)
                syntheticParameter[x++]=random.nextDouble();


        for (int i = 0; i < row; i++) {
            double label = 0;
            for (int j = 0; j < col; j++) {
                syntheticData[i][j] = random.nextDouble();
                label += (syntheticData[i][j]*syntheticParameter[j]);
            }
            syntheticData[i][col] = label + Math.abs(random.nextGaussian());
        }


    try {
        File file = new File("../SGD/ML_Data/SynthaticData/Linear/parameters.data");
        if(!file.exists()) {
            file.createNewFile();
        }
        FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
        paramtersCSVWriter(syntheticParameter,fileWriter);
        fileWriter.flush();
        fileWriter.close();
    }catch (IOException e){
        System.out.println(e.getMessage());
    }

        return syntheticData;
    }





    public static void CSVWriter(double[][] data, FileWriter writer) throws IOException {

        CSVFormat format = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
        CSVPrinter csvPrinter = new CSVPrinter(writer, format);

        for (int i = 0; i < data.length; i++) {
            ArrayList<Double> record = new ArrayList<>();
            for(double x:data[i]){
                record.add(x);
            }
            csvPrinter.printRecord(record);
            record=null;
        }
    }





    public static void paramtersCSVWriter(double[] data, FileWriter writer) throws IOException {

        CSVFormat format = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
        CSVPrinter csvPrinter = new CSVPrinter(writer, format);

        for (int i = 0; i < data.length; i++) {
            ArrayList<Double> record = new ArrayList<>();
            record.add(data[i]);
            csvPrinter.printRecord(record);
            record=null;
        }
    }






    public static void main(String[] args) {

        double[][] data = SyntheticDataGenerator.linearFunctionSyntheticGenearator(10, 10);

        FileWriter fileWriter = null;

        try {

            File file = new File("../SGD/ML_Data/SynthaticData/Linear/data.data");
            if(!file.exists()) {
                file.createNewFile();
            }
            fileWriter = new FileWriter(file.getAbsoluteFile());

            SyntheticDataGenerator.CSVWriter(data, fileWriter);
            fileWriter.flush();
            fileWriter.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
}
