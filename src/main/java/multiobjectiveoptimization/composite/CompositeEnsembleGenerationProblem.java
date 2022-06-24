package multiobjectiveoptimization.composite;

import classifiers.builder.ClassifiersBuilder;
import classifiers.builder.EnsembleBuilder;
import evaluation.ModelEvaluation;
import evaluation.MultipleEvaluationsResults;
import multiobjectiveoptimization.objective.ClassificationObjective;
import multiobjectiveoptimization.objective.diversity.builder.DiversityStrategyFactory;
import multiobjectiveoptimization.objective.diversity.composite.EnsembleDiversityStrategy;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.Precision;
import org.uma.jmetal.problem.AbstractGenericProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.binarysolution.impl.DefaultBinarySolution;
import org.uma.jmetal.solution.compositesolution.CompositeSolution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.solution.integersolution.impl.DefaultIntegerSolution;
import preprocessing.Preprocessing;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.MultipleClassifiersCombiner;

import java.util.*;

// cnstru, data set,
public class CompositeEnsembleGenerationProblem extends AbstractGenericProblem<CompositeSolution> {
    protected Preprocessing process = new Preprocessing();
    protected String arffPath;
    protected ClassifiersBuilder classifiersBuilder = new ClassifiersBuilder();
    protected EnsembleBuilder ensembleBuilder = new EnsembleBuilder();
    protected ClassificationObjective fitness1Enum = ClassificationObjective.DISAGREEMENT;
    protected ClassificationObjective fitness2Enum = ClassificationObjective.MCC;
    protected EnsembleDiversityStrategy diversityStrategy;
    int counter = 0;

    public CompositeEnsembleGenerationProblem(String arffPath) {
        this.arffPath = arffPath;
        this.diversityStrategy = DiversityStrategyFactory.buildStrategy(fitness1Enum);
    }

    public CompositeEnsembleGenerationProblem(String arffPath, ClassificationObjective objective1, ClassificationObjective objective2) {
        this.arffPath = arffPath;
        this.fitness1Enum = objective1;
        this.fitness2Enum = objective2;
        this.diversityStrategy = DiversityStrategyFactory.buildStrategy(fitness1Enum);
    }

    @Override
    public void evaluate(CompositeSolution compositeSolution) {
        try {
            counter++;
            System.out.println("Evaluation Counter: " + (counter + 1));
            System.out.println("Solution: " + compositeSolution.toString());

            // Get subsolutions
            BinarySolution baseClfs = (BinarySolution) compositeSolution.getVariable(0);
            DoubleSolution parameters = (DoubleSolution) compositeSolution.getVariable(1);
            IntegerSolution ensembleMethod = (IntegerSolution) compositeSolution.getVariable(2);

            // Build classifiers
            final List<AbstractClassifier> classifiers = classifiersBuilder.buildClassifiers(baseClfs, parameters);
            // If all 0s
            if (classifiers.isEmpty()) {
                compositeSolution.setObjective(0, Double.MAX_VALUE);
                compositeSolution.setObjective(1, Double.MAX_VALUE);
                System.out.println("Objective Results: " + Arrays.toString(compositeSolution.getObjectives()));
                return;
            }

            // Run classifiers
            Map<AbstractClassifier, MultipleEvaluationsResults> classifierResults = new HashMap<>();

            for (AbstractClassifier classifier : classifiers) {
                MultipleEvaluationsResults result = ModelEvaluation.evaluateClassifierBootstrapping(classifier, this.arffPath, 1);
                classifierResults.put(classifier, result);
            }

            // Sort classifiers in descending order of their eval metric result
            classifiers.sort(Comparator.comparing(o -> -1 * classifierResults.get(o).getAverageFromObjectiveEnum(fitness2Enum)));

            // Get ensemble method
            int method = ensembleMethod.getVariable(0);

            // Build ensemble
            MultipleClassifiersCombiner ensemble = ensembleBuilder.buildEnsemble(method, classifiers);

            // Run classifier
            MultipleEvaluationsResults ensembleResult = ModelEvaluation.evaluateClassifierBootstrapping(ensemble,
                    this.arffPath,
                    1,
                    diversityStrategy,
                    classifierResults.values().stream().toList());

            // div
            double fitness1 = ensembleResult.getAverageFromObjectiveEnum(fitness1Enum);
            if (diversityStrategy != null) {
                fitness1 = diversityStrategy.convertDiversityToFitness(fitness1);
            }
            compositeSolution.setObjective(0, fitness1);

            double fitness2 = ensembleResult.getAverageFromObjectiveEnum(fitness2Enum);
            // MCC or Precision
            if (fitness2 <= 0) {
                compositeSolution.setObjective(1, Double.MAX_VALUE);
            } else {
                compositeSolution.setObjective(1, 1 / Precision.round(fitness2, 2));
            }
            System.out.println("Objective Results: " + Arrays.toString(compositeSolution.getObjectives()));
            compositeSolution.setAttribute("STATS", ensembleResult);
        } catch (Exception e) {
            e.printStackTrace();
            compositeSolution.setObjective(0, Double.MAX_VALUE);
            compositeSolution.setObjective(1, Double.MAX_VALUE);
            System.out.println("Objective Results: " + Arrays.toString(compositeSolution.getObjectives()));
        }
    }

    @Override
    public CompositeSolution createSolution() {
        int numberOfObjectives = 2;
        /* --------------- Binary Solution ----------------*/
        List<Integer> bitsPerVariable = new ArrayList<>();
        bitsPerVariable.add(15);
        BinarySolution binarySolution = new DefaultBinarySolution(bitsPerVariable, numberOfObjectives);

        /* --------------- Double Solution ----------------*/
        List<Pair<Double, Double>> bounds = new ArrayList<>();
        // Left double represent the id of a base classifier
        // Right double represent the parameter
        // Fitness func, bounds
        // NB 0 2   KNN 1 15       3 SVM kernel
        // truncate

        // NB
        bounds.add(new ImmutablePair<>(0.0, 2.0));
        // NB -D
        bounds.add(new ImmutablePair<>(0.0, 2.0));
        // NB -K
        bounds.add(new ImmutablePair<>(0.0, 2.0));
        // ["-K", "-D"]

        // KNN -K
        bounds.add(new ImmutablePair<>(1.0, 16.0));
        bounds.add(new ImmutablePair<>(1.0, 16.0));
        bounds.add(new ImmutablePair<>(1.0, 16.0));
        // 15 15.9999 -> 15
        // SVM -C check other paras
        bounds.add(new ImmutablePair<>(1.0, 51.0));
        bounds.add(new ImmutablePair<>(1.0, 51.0));
        bounds.add(new ImmutablePair<>(1.0, 51.0));
        bounds.add(new ImmutablePair<>(1.0, 51.0));
        // SVM -E check values
        // bounds.add(new ImmutablePair<Double, Double>(1.0, 51.0));
        // Tree -C
        bounds.add(new ImmutablePair<>(0.0, 0.3));
        bounds.add(new ImmutablePair<>(0.0, 0.3));
        bounds.add(new ImmutablePair<>(0.0, 0.3));
        bounds.add(new ImmutablePair<>(0.0, 0.3));
        bounds.add(new ImmutablePair<>(0.0, 0.3));
        DoubleSolution doubleSolution = new DefaultDoubleSolution(bounds, 2);

        /* --------------- Integer Solution ---------------- */
        List<Pair<Integer, Integer>> bounds2 = new ArrayList<>();
        bounds2.add(new ImmutablePair<>(0, 3));
        IntegerSolution integerSolution = new DefaultIntegerSolution(bounds2, 2);

        List<Solution<?>> solutions = new ArrayList<>();
        solutions.add(binarySolution);
        solutions.add(doubleSolution);
        solutions.add(integerSolution);

        return new CompositeSolution(solutions);
    }

}
