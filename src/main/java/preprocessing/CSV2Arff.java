package preprocessing;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class CSV2Arff {

    // Convert csv files to arff format.
    public static void main(String[] args) throws Exception {
        CSV2Arff to = new CSV2Arff();

        String dirPath = "data/CSV_XV";
        File dir = new File(dirPath);
        File[] csvList = dir.listFiles();
        if (csvList != null) {
            for (File csv : csvList) {
                String csvPath = csv.toString();
                to.setBinaryLabel(csvPath);

                // Load CSV
                CSVLoader loader = new CSVLoader();
                loader.setSource(new File(csvPath));
                Instances data = loader.getDataSet();

                // Save arff
                ArffSaver saver = new ArffSaver();
                saver.setInstances(data);
                String arffName = csv.toString().replace(".csv", ".arff");
                arffName = arffName.replace("CSV", "Arff");
                saver.setFile(new File(arffName));
                saver.writeBatch();
            }
        }
    }

    // Map the bug attribute to 0 or 1, where 0 represents defect-free and 1 represents defective.
    public void setBinaryLabel(String fileName) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String row = reader.readLine();
        ArrayList<String> instances = new ArrayList<>();
        instances.add(row);
        while ((row = reader.readLine()) != null) {
            String[] attributes = row.split(",");
            int bug = Integer.parseInt(attributes[attributes.length - 1]);
            if (bug >= 1) {
                attributes[attributes.length - 1] = "1";
                row = Arrays.toString(attributes);
                row = row.substring(1, row.length() - 1);
                row = row.replaceAll(",\\s", ",");
            }
            instances.add(row);
        }
        reader.close();

        FileWriter writer = new FileWriter(fileName);
        for (String instance : instances) {
            writer.append(instance);
            writer.append("\n");
        }
        writer.flush();
        writer.close();
    }
}
