package multiobjectiveoptimization.pe;

import multiobjectiveoptimization.composite.SinglePointCV;
import multiobjectiveoptimization.objective.ClassificationObjective;
import org.uma.jmetal.operator.mutation.impl.SimpleRandomMutation;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ExtendedNSGAIIRunner extends AbstractAlgorithmRunner {

    public static void main(String[] args) throws IOException {
        // args[0] = data path
        // args[1] = runID
        // args[2] = output result dir
        // args[3] = fitness metric

        Path dataPath = Paths.get(args[0]);
        String fitness2 = "MCC";
        if (args.length == 4) {
            fitness2 = args[3];
        }
        System.out.println(dataPath);

        long startTime = System.currentTimeMillis();

        PopulationEnsembleGenerationProblem problem = new PopulationEnsembleGenerationProblem(dataPath.toString(), ClassificationObjective.valueOf(fitness2));
        // 90 - 100 %
        SinglePointCV<Double, DoubleSolution> crossoverOperator = new SinglePointCV<>(0.95);

        double paraMutationProb = 1 / (double) 15;
        SimpleRandomMutation mutationOperator = new SimpleRandomMutation(paraMutationProb);

        ExtendedNSGAII nsgaii = new ExtendedNSGAII(problem,
                10000,
                100,
                100,
                100,
                crossoverOperator,
                mutationOperator,
                new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>()),
                new DominanceComparator<>(),
                new SequentialSolutionListEvaluator<>());
        nsgaii.run();

        long stopTime = System.currentTimeMillis();
        long computingTime = stopTime - startTime;

        List<DoubleSolution> population = nsgaii.getResult();

        // Not needed because of printSolutionSet below?
        // printFinalSolutionSet(population);

        printSolutionSet(population, args[1], args[2]);
        try (FileWriter fw = new FileWriter(Paths.get(args[2], "TIME_" + args[1] + ".txt").toString())) {
            fw.write(computingTime + "\n");
        }
    }

    private static void printSolutionSet(List<DoubleSolution> population, String runID, String outputResultDir) {
        System.out.println(population.size());
        for (DoubleSolution p : population) {
            System.out.println("Objectives");
            System.out.println(p.getObjectives()[0]);
            System.out.println(p.getObjectives()[1]);
            System.out.println("Variables");
            System.out.println(p);
        }

        Path objValPath = Paths.get(outputResultDir, "FUN_" + runID + ".csv");
        Path valPath = Paths.get(outputResultDir, "VAR_" + runID + ".csv");

        File resultDir = new File(outputResultDir);
        if (!resultDir.exists()) {
            resultDir.mkdirs();
        }

        for (DoubleSolution solution : population) {
            double objective1 = solution.getObjective(0);
            solution.setObjective(0, objective1 != 0 ? 1 / objective1 : 0);
            double objective2 = solution.getObjective(1);
            solution.setObjective(1, objective2 != 0 ? 1 / objective2 : 0);
        }

        new SolutionListOutput(population)
                .setVarFileOutputContext(new DefaultFileOutputContext(valPath.toString(), ","))
                .setFunFileOutputContext(new DefaultFileOutputContext(objValPath.toString(), ","))
                .print();
    }
}