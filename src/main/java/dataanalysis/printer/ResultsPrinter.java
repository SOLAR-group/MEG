package dataanalysis.printer;

import evaluation.EvaluationResult;
import evaluation.MultipleEvaluationsResults;
import org.uma.jmetal.solution.Solution;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ResultsPrinter<T extends Solution<?>> {

    protected Path outputPath;
    protected String filePrefix = "TEST";

    public ResultsPrinter(Path outputPath) {
        this.outputPath = outputPath;
    }

    public ResultsPrinter(Path outputPath, String filePrefix) {
        this.outputPath = outputPath;
        this.filePrefix = filePrefix;
    }

    public Path getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(Path outputPath) {
        this.outputPath = outputPath;
    }

    public String getFilePrefix() {
        return filePrefix;
    }

    public void setFilePrefix(String filePrefix) {
        this.filePrefix = filePrefix;
    }

    public void printResults(List<T> solutions, List<MultipleEvaluationsResults> testResults, int numberOfSplits) {
        if (this.outputPath == null) {
            throw new IllegalArgumentException("No output path was given.");
        } else if (this.outputPath.toFile().isFile()) {
            throw new IllegalArgumentException("The output path should be a directory or nonexistent.");
        }
        this.outputPath.toFile().mkdirs();
        for (int split = 0; split < numberOfSplits; split++) {
            Path resultingFile = Paths.get(this.outputPath.toString(), this.filePrefix + "_SPLIT_" + (split + 1) + ".csv");
            printResults(solutions, testResults, split, resultingFile);
        }
    }

    public void printResults(List<T> solutions, List<MultipleEvaluationsResults> testResults) {
        if (this.outputPath == null) {
            throw new IllegalArgumentException("No output path was given.");
        }
        this.outputPath.toAbsolutePath().toFile().getParentFile().mkdirs();
        Path resultingFile = Paths.get(this.outputPath.toString());
        printResults(solutions, testResults, 0, resultingFile);
    }

    private void printResults(List<T> solutions, List<MultipleEvaluationsResults> testResults, int splitID, Path resultingFile) {
        try (FileWriter fw = new FileWriter(resultingFile.toFile())) {
            fw.write("FITNESS_1,FITNESS_2,PRECISION,RECALL,F_MEASURE,MCC,TP,FP,FN,TN\n");
            for (int solutionNumber = 0, resultsSize = testResults.size(); solutionNumber < resultsSize; solutionNumber++) {
                MultipleEvaluationsResults multipleResult = testResults.get(solutionNumber);
                EvaluationResult result = multipleResult.get(splitID);
                T solution = solutions.get(solutionNumber);

                fw.write(String.valueOf(solution.getObjective(0)));
                fw.write(",");
                fw.write(String.valueOf(solution.getObjective(1)));
                fw.write(",");
                fw.write(String.valueOf(result.getPrecision()));
                fw.write(",");
                fw.write(String.valueOf(result.getRecall()));
                fw.write(",");
                fw.write(String.valueOf(result.getFMeasure()));
                fw.write(",");
                fw.write(String.valueOf(result.getMCC()));
                fw.write(",");
                fw.write(String.valueOf(result.getNumTruePositives()));
                fw.write(",");
                fw.write(String.valueOf(result.getNumFalsePositives()));
                fw.write(",");
                fw.write(String.valueOf(result.getNumFalseNegatives()));
                fw.write(",");
                fw.write(String.valueOf(result.getNumTrueNegatives()));
                fw.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
