package multiobjectiveoptimization.composite;

import dataanalysis.composite.MEGTest;
import multiobjectiveoptimization.objective.ClassificationObjective;
import multiobjectiveoptimization.objective.diversity.builder.DiversityStrategyFactory;
import multiobjectiveoptimization.objective.diversity.composite.EnsembleDiversityStrategy;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.CompositeCrossover;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.mutation.impl.CompositeMutation;
import org.uma.jmetal.operator.mutation.impl.SimpleRandomMutation;
import org.uma.jmetal.solution.compositesolution.CompositeSolution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import util.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "run",
        description = "Runs MEG to generate ensembles.",
        showDefaultValues = true
)
public class MEGRun extends AbstractAlgorithmRunner implements Callable<Integer> {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "display this help message")
    boolean usageHelpRequested;

    @Option(names = {"-t", "--trainingInstance"},
            description = "The path to the instance for training.",
            required = true
    )
    private String trainingInstancePath;

    @Option(names = {"-o", "--outputPath"},
            description = "The output path for the resulting CSVs.",
            required = true)
    private String outputPath;

    @Option(names = {"-r", "--runId"},
            description = "The run ID of this execution.",
            required = true)
    private int runID;

    @Option(names = {"-d", "--diversityObjective"},
            description = "The diversity objective. Possible objectives: ${COMPLETION-CANDIDATES}",
            defaultValue = "DISAGREEMENT",
            required = true)
    private ClassificationObjective diversityObjective;

    @Option(names = {"-a", "--accuracyObjective"},
            description = "The accuracy objective. Possible objectives: ${COMPLETION-CANDIDATES}",
            defaultValue = "MCC",
            required = true)
    private ClassificationObjective accuracyObjective;

    @Spec
    private CommandSpec spec;

    public static void main(String[] args) throws IOException {
        new CommandLine(new MEGTest()).execute(args);
    }

    private static void printSolutionSet(List<CompositeSolution> population, String runID, String outputResultDir,
                                         EnsembleDiversityStrategy diversityStrategy) {
        Path objValPath = Paths.get(outputResultDir, "FUN_" + runID + ".csv");
        Path valPath = Paths.get(outputResultDir, "VAR_" + runID + ".csv");

        File resultDir = new File(outputResultDir);
        if (!resultDir.exists()) {
            resultDir.mkdirs();
        }

        for (CompositeSolution solution : population) {
            double objective1 = solution.getObjective(0);
            double correctedObjective1 = objective1 != 0 ? diversityStrategy.convertDiversityToFitness(objective1) : 0;
            solution.setObjective(0, correctedObjective1);
            double objective2 = solution.getObjective(1);
            solution.setObjective(1, objective2 != 0 ? 1 / objective2 : 0);
        }

        new SolutionListOutput(population)
                .setVarFileOutputContext(new DefaultFileOutputContext(valPath.toString(), ","))
                .setFunFileOutputContext(new DefaultFileOutputContext(objValPath.toString(), ","))
                .print();
    }

    @Override
    public Integer call() throws Exception {
        validateParameters();

        Path dataPath = Paths.get(this.trainingInstancePath);

        long startTime = System.currentTimeMillis();

        CompositeEnsembleGenerationProblem problem = new CompositeEnsembleGenerationProblem(dataPath.toString(), diversityObjective, accuracyObjective);

        // 90 - 100 %
        SinglePointCrossover singlePointCVClfs = new SinglePointCrossover(0.95);
        SinglePointCV<Double, DoubleSolution> singlePointCVParas = new SinglePointCV<>(0.95);
        SinglePointCV<Integer, IntegerSolution> singlePointCVStrategies = new SinglePointCV<>(0.95);

        ArrayList<CrossoverOperator<?>> crossoverOperators = new ArrayList<>();
        crossoverOperators.add(singlePointCVClfs);
        crossoverOperators.add(singlePointCVParas);
        crossoverOperators.add(singlePointCVStrategies);
        CompositeCrossover compositeCrossover = new CompositeCrossover(crossoverOperators);

        double clfMutationProb = 1 / (double) 15;
        BitFlipMutation bitFlipMutation = new BitFlipMutation(clfMutationProb);
        double paraMutationProb = 1 / (double) 15;
        SimpleRandomMutation simpleRandomMutation = new SimpleRandomMutation(paraMutationProb);
        double strategyMutationProb = 1 / (double) 4;
        SimpleRandomIntegerMutation simpleRandomMutationStrategy = new SimpleRandomIntegerMutation(strategyMutationProb);
        ArrayList<MutationOperator<?>> mutationOperators = new ArrayList<>();
        mutationOperators.add(bitFlipMutation);
        mutationOperators.add(simpleRandomMutation);
        mutationOperators.add(simpleRandomMutationStrategy);
        CompositeMutation compositeMutation = new CompositeMutation(mutationOperators);

        NSGAIIBuilder<CompositeSolution> nsgaiiBuilder = new NSGAIIBuilder<>(problem, compositeCrossover, compositeMutation, 100);
        nsgaiiBuilder.setMaxEvaluations(10000);  // 10000
        NSGAII<CompositeSolution> nsgaii = nsgaiiBuilder.build();
        nsgaii.run();
        long stopTime = System.currentTimeMillis();
        long computingTime = stopTime - startTime;

        List<CompositeSolution> population = nsgaii.getResult();
        try {
            printSolutionSet(population, String.valueOf(this.runID), this.outputPath, DiversityStrategyFactory.buildStrategy(diversityObjective));
        } catch (Exception ignored) {
        }

        try (FileWriter fw = new FileWriter(Paths.get(this.outputPath, "TIME_" + this.runID + ".txt").toString())) {
            fw.write(computingTime + "\n");
        }
        System.out.println("END!");
        return 0;
    }

    private void validateParameters() {
        Path trainingInstancePath = Paths.get(this.trainingInstancePath).toAbsolutePath();
        if (Utils.fileIsInvalid(trainingInstancePath)) {
            throw new ParameterException(spec.commandLine(), "The training file does not exist or is invalid.");
        }

        Path outputPath = Paths.get(this.outputPath).toAbsolutePath();
        if (Utils.directoryIsInvalid(outputPath)) {
            throw new ParameterException(spec.commandLine(), "The output directory is not a directory or it cannot be created.");
        }
    }
}