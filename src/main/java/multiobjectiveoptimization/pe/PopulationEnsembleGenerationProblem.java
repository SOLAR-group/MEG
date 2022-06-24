package multiobjectiveoptimization.pe;

import classifiers.builder.ClassifiersBuilder;
import classifiers.builder.EnsembleBuilder;
import evaluation.ModelEvaluation;
import evaluation.MultipleEvaluationsResults;
import multiobjectiveoptimization.objective.ClassificationObjective;
import multiobjectiveoptimization.objective.diversity.NCL;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.Precision;
import org.uma.jmetal.problem.AbstractGenericProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import preprocessing.Preprocessing;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.MultipleClassifiersCombiner;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PopulationEnsembleGenerationProblem extends AbstractGenericProblem<DoubleSolution> {

    // Currently input path of dataset manually.
    protected String arffPath;
    protected ClassifiersBuilder classifiersBuilder = new ClassifiersBuilder();
    protected EnsembleBuilder ensembleBuilder = new EnsembleBuilder();
    protected Preprocessing process = new Preprocessing();
    protected ClassificationObjective fitness2Enum = ClassificationObjective.MCC;
    protected MultipleEvaluationsResults ensembleResults;
    protected Map<DoubleSolution, MultipleEvaluationsResults> populationResults;
    protected NCL ncl = new NCL();
    int counter = 0;

    public PopulationEnsembleGenerationProblem(String arffPath) {
        this.arffPath = arffPath;
    }

    public PopulationEnsembleGenerationProblem(String arffPath, ClassificationObjective objective) {
        this(arffPath);
        this.fitness2Enum = objective;
    }

    /**
     * Evaluates the whole populations. This SHOULD ALWAYS be called before {@link #evaluate(DoubleSolution)}.
     * <p>
     * This is needed to compute the diversity measure of the Pareto ensemble.
     *
     * @param allSolutions the list of all solutions in the population.
     */
    public void evaluatePopulation(List<DoubleSolution> allSolutions) {
        this.populationResults = new HashMap<>();
        List<AbstractClassifier> allClassifiers = new ArrayList<>();
        // Build all classifiers
        for (DoubleSolution doubleSolution : allSolutions) {
            try {
                counter++;
                System.out.println("Evaluation Counter: " + counter);
                int classifierIndex = doubleSolution.getVariable(0).intValue();
                AbstractClassifier classifier = buildClassifier(doubleSolution, classifierIndex);
                // Save untrained classifiers
                allClassifiers.add(classifier);
                // Make a copy of it
                classifier = (AbstractClassifier) AbstractClassifier.makeCopy(classifier);
                // Train and execute it
                MultipleEvaluationsResults classifierResult = executeClassifier(classifier);
                // Save results
                this.populationResults.put(doubleSolution, classifierResult);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            // Build the ensemble with all untrained classifiers
            MultipleClassifiersCombiner ensemble = ensembleBuilder.buildMajorityVoting(allClassifiers);
            // Train and execute it
            // Save ensemble results
            this.ensembleResults = executeClassifier(ensemble);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void evaluate(DoubleSolution doubleSolution) {
        if (this.populationResults == null || populationResults.isEmpty()) {
            throw new IllegalArgumentException("OPS! Did you forget to call evaluatePopulation()?");
        }

        // Get the results saved from the #evaluatePopulation method
        MultipleEvaluationsResults result = this.populationResults.get(doubleSolution);
        if (result == null || result.isEmpty()) {
            doubleSolution.setObjective(0, Double.MAX_VALUE);
            doubleSolution.setObjective(1, Double.MAX_VALUE);
            System.out.println("Solution: " + doubleSolution);
            return;
        }

        // Fitness 1 = NCL
        // Create the set of other results
        Map<DoubleSolution, MultipleEvaluationsResults> othersResults = new HashMap<>(populationResults);
        // Remove the classifier being evaluated
        othersResults.remove(doubleSolution);
        // Map to the prediction
        List<List<Double>> othersPredictions = othersResults.values().stream()
                .map(MultipleEvaluationsResults::getAveragePredictions)
                .toList();
        // Compute NCL
        if (ensembleResults == null || ensembleResults.isEmpty()) {
            doubleSolution.setObjective(0, Double.MAX_VALUE);
        } else {
            double fitness1 = ncl.computeNCL(ensembleResults.getAveragePredictions(), result.getAveragePredictions(), othersPredictions);
            // NCL is minimisation, so keep it as is
            doubleSolution.setObjective(0, fitness1);
        }

        // Fitness 2
        double fitness2 = result.getAverageFromObjectiveEnum(fitness2Enum);
        // MCC or Precision
        if (fitness2 <= 0) {
            doubleSolution.setObjective(1, Double.MAX_VALUE);
        } else {
            doubleSolution.setObjective(1, 1 / Precision.round(fitness2, 2));
        }
        System.out.println("Solution: " + doubleSolution);
        doubleSolution.setAttribute("STATS", result);
    }

    private MultipleEvaluationsResults executeClassifier(AbstractClassifier classifier) throws Exception {
        // execute
        return ModelEvaluation.evaluateClassifierBootstrapping(classifier, this.arffPath, 1);
    }


    public DoubleSolution createSolution() {
        List<Pair<Double, Double>> bounds = new ArrayList<>();
        // Classifier
        bounds.add(new ImmutablePair<>(0.0, 4.0));
        // KNN
        bounds.add(new ImmutablePair<>(1.0, 16.0));
        // SVM
        bounds.add(new ImmutablePair<>(1.0, 51.0));
        // Tree
        bounds.add(new ImmutablePair<>(0.0, 0.3));
        // NB
        bounds.add(new ImmutablePair<>(0.0, 2.0));
        return new DefaultDoubleSolution(bounds, 2);
    }
}
