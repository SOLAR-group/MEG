package dataanalysis.pe;

import classifiers.builder.ClassifiersBuilder;
import classifiers.builder.EnsembleBuilder;
import dataanalysis.printer.ResultsPrinter;
import evaluation.ModelEvaluation;
import evaluation.MultipleEvaluationsResults;
import org.jetbrains.annotations.Nullable;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.MultipleClassifiersCombiner;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class MainPE implements Callable<Integer> {
    protected ClassifiersBuilder classifiersBuilder = new ClassifiersBuilder();

    @Option(names = {"-V", "--version"}, versionHelp = true, description = "display version info")
    boolean versionInfoRequested;

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "display this help message")
    boolean usageHelpRequested;

    @Option(names = {"--trainingInstance", "-t"},
            description = "The path to the instance for training.",
            required = true)
    private String trainingInstancePath;

    @Option(names = {"--testingInstance", "-T"},
            description = "The path to the instance for testing.")
    private String testingInstancePath;

    @Option(names = {"--var", "-v"},
            description = "The path to the VAR.csv file with the solutions.",
            required = true)
    private String varPath;

    @Option(names = {"--fun", "-f",},
            description = "The path to the FUN.csv file with the solutions' objectives.",
            required = true)
    private String funPath;

    @Option(names = {"--outputPath", "-o"},
            description = "The output path for the resulting CSV.",
            required = true)
    private String outputPath;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new MainPE()).execute(args);
    }

    @Override
    public Integer call() throws Exception {
        System.out.println("Parsing started!");
        SolutionSetParserPE parser = new SolutionSetParserPE();
        // Parsing VAR file into DoubleSolutions
        List<DoubleSolution> doubleSolutions = parser.parseSolutionSet(varPath);
        parser.parseFun(doubleSolutions, funPath);
        System.out.println("Parsing done! There are " + doubleSolutions.size() + " solutions.");

        List<AbstractClassifier> classifiers = new ArrayList<>();
        for (DoubleSolution solution : doubleSolutions) {
            AbstractClassifier classifier = buildClassifier(solution, solution.getVariable(0).intValue());
            classifiers.add(classifier);
        }

        System.out.println("Building ensemble started!");
        EnsembleBuilder builder = new EnsembleBuilder();
        MultipleClassifiersCombiner ensemble = builder.buildMajorityVoting(classifiers);
        System.out.println("Building ensemble done!");

        Integer returnCode = identifyTestInstance();
        if (returnCode != null) return returnCode;

        List<MultipleEvaluationsResults> allResults = new ArrayList<>();
        System.out.println("Evaluating ensemble.");
        MultipleEvaluationsResults results = ModelEvaluation.evaluateClassifierNoCrossValidationDifferentInstances(ensemble, trainingInstancePath, testingInstancePath);
        allResults.add(results);
        System.out.println("Evaluation done!");

        ResultsPrinter<DoubleSolution> printer = new ResultsPrinter<>(Paths.get(this.outputPath));
        printer.printResults(doubleSolutions, allResults);

        return 0;
    }

    public AbstractClassifier buildClassifier(DoubleSolution doubleSolution, int classifierIndex) throws Exception {
        return switch (classifierIndex) {
            case 0 -> classifiersBuilder.buildKNN(doubleSolution, 1);
            case 1 -> classifiersBuilder.buildSVM(doubleSolution, 2);
            case 2 -> classifiersBuilder.buildTree(doubleSolution, 3);
            case 3 -> classifiersBuilder.buildNaiveBayes(doubleSolution, 4);
            default -> null;
        };
    }

    @Nullable
    private Integer identifyTestInstance() {
        if (this.testingInstancePath == null || this.testingInstancePath.isBlank()) {
            Path path = Paths.get(this.trainingInstancePath).toAbsolutePath();
            final String filePrefix = path.getFileName().toString().split("-")[0];
            File parentFile = path.getParent().toFile();
            File[] files = parentFile.listFiles(file -> file.getName().startsWith(filePrefix) && file.getName().endsWith("last.arff"));
            if (files.length != 0) {
                this.testingInstancePath = Paths.get(parentFile.getAbsolutePath(), files[0].getName()).toAbsolutePath().toString();
            } else {
                System.err.println("Ops! I could not find a testing dataset. Are you sure you provided one or if there is one with \"last\" as suffix?");
                return -1;
            }
        }
        return null;
    }
}
