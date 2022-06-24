package dataanalysis.composite;

import classifiers.builder.EnsembleBuilder;
import dataanalysis.printer.ResultsPrinter;
import evaluation.ModelEvaluation;
import evaluation.MultipleEvaluationsResults;
import org.uma.jmetal.solution.compositesolution.CompositeSolution;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import util.Utils;
import weka.classifiers.MultipleClassifiersCombiner;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "test",
        description = "Test MEG's results.",
        showDefaultValues = true
)
public class MEGTest implements Callable<Integer> {

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

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new MEGTest()).execute(args);
    }

    @Override
    public Integer call() throws Exception {
        validateParameters();

        System.out.println("Parsing started!");
        SolutionSetParser parser = new SolutionSetParser();

        List<CompositeSolution> compositeSolutions = parser.parseSolutionSet(varPath);
        parser.parseFun(compositeSolutions, funPath);
        System.out.println("Parsing done! There are " + compositeSolutions.size() + " solutions.");

        System.out.println("Building ensembles started!");
        EnsembleBuilder builder = new EnsembleBuilder();
        List<MultipleClassifiersCombiner> ensembles = builder.buildEnsembles(compositeSolutions);
        System.out.println("Building ensembles done!");

        List<MultipleEvaluationsResults> allResults = new ArrayList<>();
        for (int i = 0, ensemblesSize = ensembles.size(); i < ensemblesSize; i++) {
            System.out.println("Evaluating ensemble #" + (i + 1) + " out of " + ensemblesSize + " ensembles.");
            MultipleClassifiersCombiner ensemble = ensembles.get(i);
            MultipleEvaluationsResults results = ModelEvaluation.evaluateClassifierNoCrossValidationDifferentInstances(ensemble, trainingInstancePath, testingInstancePath);
            allResults.add(results);
            System.out.println("Evaluation done!");
        }

        ResultsPrinter<CompositeSolution> printer = new ResultsPrinter<>(Paths.get(this.outputPath));
        printer.printResults(compositeSolutions, allResults);
        return 0;
    }

    private void identifyTestInstance() {
        if (this.testingInstancePath == null || this.testingInstancePath.isBlank()) {
            Path path = Paths.get(this.trainingInstancePath).toAbsolutePath();
            final String filePrefix = path.getFileName().toString().split("-")[0];
            File parentFile = path.getParent().toFile();
            File[] files = parentFile.listFiles(file -> file.getName().startsWith(filePrefix) && file.getName().endsWith("last.arff"));
            if (files.length != 0) {
                this.testingInstancePath = Paths.get(parentFile.getAbsolutePath(), files[0].getName()).toAbsolutePath().toString();
            } else {
                throw new CommandLine.ParameterException(spec.commandLine(), "Ops! I could not find a testing dataset. Are you sure you provided one or whether there is one with \"last\" as suffix?");
            }
        }
    }

    private void validateParameters() {
        Path trainingInstancePath = Paths.get(this.trainingInstancePath).toAbsolutePath();
        if (Utils.fileIsInvalid(trainingInstancePath)) {
            throw new CommandLine.ParameterException(spec.commandLine(), "The training file does not exist or is invalid.");
        }

        if (this.testingInstancePath == null) identifyTestInstance();
        Path testingInstancePath = Paths.get(this.testingInstancePath).toAbsolutePath();
        if (Utils.fileIsInvalid(testingInstancePath)) {
            throw new CommandLine.ParameterException(spec.commandLine(), "The testing file does not exist or is invalid.");
        }

        Path varPath = Paths.get(this.varPath).toAbsolutePath();
        if (Utils.fileIsInvalid(varPath)) {
            throw new CommandLine.ParameterException(spec.commandLine(), "The VAR file does not exist or is invalid.");
        }

        Path funPath = Paths.get(this.funPath).toAbsolutePath();
        if (Utils.fileIsInvalid(funPath)) {
            throw new CommandLine.ParameterException(spec.commandLine(), "The FUN file does not exist or is invalid.");
        }

        Path outputPath = Paths.get(this.outputPath).getParent().toAbsolutePath();
        if (Utils.directoryIsInvalid(outputPath)) {
            throw new CommandLine.ParameterException(spec.commandLine(), "The output file's parent directory cannot be created.");
        }
    }

}
