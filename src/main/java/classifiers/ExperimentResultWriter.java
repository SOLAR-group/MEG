package classifiers;

import com.google.common.math.Stats;
import org.apache.commons.math3.util.Precision;
import weka.classifiers.Evaluation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ExperimentResultWriter {
    protected static final int NUM_DECIMAL_POINT = 3;
    private final FileWriter fileWriter;

    public ExperimentResultWriter(Path fullFilePath) throws IOException {
        File file = fullFilePath.toFile();
        if (!file.exists()) {
            // create parent directory first
            File directory = new File(file.getParentFile().getAbsolutePath());
            directory.mkdirs();
            file.createNewFile();
        }
        this.fileWriter = new FileWriter(file);
    }

    public void writeNewline(String line) throws IOException {
        fileWriter.write(line);
        fileWriter.write('\n');
    }

    public void writeStats(List<Double> resultsBetweenRuns, String measureName) throws IOException {
        writeNewline("============ OVERALL " + measureName + " STATS ==============");

        for(int i = 0 ; i < resultsBetweenRuns.size(); i++) {
	    writeNewline(String.format("Run %d: %.3f", i+1, (double)resultsBetweenRuns.get(i)));
        }

        newline();

        Stats stats = Stats.of(resultsBetweenRuns);
        double mean = stats.mean();
        writeNewline("The average " + measureName + " between runs: ");
        writeNewline(String.valueOf(Precision.round(mean, NUM_DECIMAL_POINT)));

        double std = stats.populationStandardDeviation();
        writeNewline("The standard deviation of " + measureName + " between runs: ");
        writeNewline(String.valueOf(Precision.round(std, NUM_DECIMAL_POINT)));

        newline();
    }

    public void writeRunResults(List<Evaluation> runResults) throws IOException {
        List<Double> mccs = runResults.stream().map(e -> e.matthewsCorrelationCoefficient(1)).toList();
        writeStats(mccs, "MCC");
        List<Double> precisions = runResults.stream().map(e -> e.precision(1)).toList();
        writeStats(precisions, "PRECISION");
    }

    public void close() throws IOException {
        fileWriter.close();
    }

    public void newline() throws IOException {
        fileWriter.write('\n');
    }

    public List<Double> writeMap(List<Map.Entry<String, Double>> sortedMapEntries, boolean collectValues) throws IOException {
        List<Double> list = new ArrayList<>();
        for (Map.Entry<String, Double> mapping : sortedMapEntries) {
            if (collectValues) {
                list.add(mapping.getValue());
            }
            writeNewline(mapping.getKey() + ": " + Precision.round(mapping.getValue(), NUM_DECIMAL_POINT));
        }
        fileWriter.write('\n');
        return list;
    }
}
