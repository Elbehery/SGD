package de.tuberlin.sgd.core;

import com.google.common.base.Preconditions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVDataReader {

    public static DVectorFrame readCSV(final String csvFile, final String[] fields) {
        Preconditions.checkNotNull(fields);
        Preconditions.checkNotNull(csvFile);
        final DVectorFrame frame = new DVectorFrame(fields);
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
                final DVector v = DVector.createFrom(data[i++]);
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
