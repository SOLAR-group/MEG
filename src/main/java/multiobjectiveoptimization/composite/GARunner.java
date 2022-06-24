package multiobjectiveoptimization.composite;

import multiobjectiveoptimization.objective.ClassificationObjective;
import multiobjectiveoptimization.objective.diversity.builder.DiversityStrategyFactory;
import multiobjectiveoptimization.objective.diversity.composite.EnsembleDiversityStrategy;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GARunner extends AbstractAlgorithmRunner {

    public static void main(String[] args) throws IOException {
        // args[0] = data path
        // args[1] = runID
        // args[2] = output result dir
        // args[3] = fitness metric

        Path dataPath = Paths.get(args[0]);
        System.out.println(dataPath);

        String fitness1 = "MCC";
        if (args.length >= 4) {
            fitness1 = args[3];
        }

        long startTime = System.currentTimeMillis();

        ClassificationObjective objective1 = ClassificationObjective.valueOf(fitness1);
        SingleObjectiveCompositeEnsembleGenerationProblem problem = new SingleObjectiveCompositeEnsembleGenerationProblem(dataPath.toString(), objective1);

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

        Algorithm<CompositeSolution> ga = new GeneticAlgorithmBuilder<>(problem, compositeCrossover, compositeMutation)
                .setPopulationSize(100)
                .setMaxEvaluations(10000)
                .build();

        ga.run();

        long stopTime = System.currentTimeMillis();
        long computingTime = stopTime - startTime;

        CompositeSolution result = ga.getResult();
        try {
            printSolution(result, args[1], args[2], DiversityStrategyFactory.buildStrategy(objective1), objective1.isDiversity());
        } catch (Exception ignored) {
        }

        try (FileWriter fw = new FileWriter(Paths.get(args[2], "TIME_" + args[1] + ".txt").toString())) {
            fw.write(computingTime + "\n");
        }
        System.out.println("END!");
    }

    private static void printSolution(CompositeSolution solution, String runID, String outputResultDir, EnsembleDiversityStrategy diversityStrategy, boolean isDivBeingOptimised) {
        Path objValPath = Paths.get(outputResultDir, "FUN_" + runID + ".csv");
        Path valPath = Paths.get(outputResultDir, "VAR_" + runID + ".csv");

        File resultDir = new File(outputResultDir);
        if (!resultDir.exists()) {
            resultDir.mkdirs();
        }

        double objective1 = solution.getObjective(0);

        if (isDivBeingOptimised) {
            objective1 = objective1 != 0 ? diversityStrategy.convertDiversityToFitness(objective1) : 0;
        } else {
            objective1 = objective1 != 0 ? 1 / objective1 : 0;
        }

        solution.setObjective(0, objective1);

        List<CompositeSolution> solutionSet = new ArrayList<>();
        solutionSet.add(solution);

        new SolutionListOutput(solutionSet)
                .setVarFileOutputContext(new DefaultFileOutputContext(valPath.toString(), ","))
                .setFunFileOutputContext(new DefaultFileOutputContext(objValPath.toString(), ","))
                .print();
    }
}