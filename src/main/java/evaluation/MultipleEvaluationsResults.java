package evaluation;

import multiobjectiveoptimization.objective.ClassificationObjective;
import org.jetbrains.annotations.NotNull;
import util.Utils;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.Prediction;

import java.util.*;

/**
 * A class that holds a set of results of multiple Cross-Validation execution. The class can hold multiple results, each
 * of which representing a single run.
 */
public class MultipleEvaluationsResults implements List<EvaluationResult> {

    /**
     * The name of the evaluated classifier.
     */
    protected final String classifierName;

    /**
     * The name of the instance being evaluated.
     */
    protected final String instanceName;

    /**
     * The list of results, one for each Cross Validation.
     */
    protected final List<EvaluationResult> results;

    /**
     * Constructs the result holder object.
     *
     * @param classifierName the name of the classifier being evaluated
     * @param instanceName   the name of the instance being predicted
     */
    public MultipleEvaluationsResults(String classifierName, String instanceName) {
        this.classifierName = classifierName;
        this.instanceName = instanceName;
        this.results = new ArrayList<>();
    }

    /**
     * Gets the name of the classifier being evaluated.
     *
     * @return the name of the classifier being evaluated
     */
    public String getClassifierName() {
        return this.classifierName;
    }

    /**
     * Gets the name of the instance being evaluated.
     *
     * @return the name of the instance being evaluated
     */
    public String getInstanceName() {
        return this.instanceName;
    }

    /**
     * Gets the list of precisions.
     *
     * @return list of precisions
     */
    public List<Double> getPrecisions() {
        return this.results.stream().map(EvaluationResult::getPrecision).toList();
    }

    /**
     * Gets the list of MCCs.
     *
     * @return list of MCCs
     */
    public List<Double> getMCCs() {
        return this.results.stream().map(EvaluationResult::getMCC).toList();
    }

    /**
     * Gets the lists of predictions.
     *
     * @return the predictions of all runs
     */
    public List<List<Prediction>> getPredictions() {
        return this.results.stream().map(EvaluationResult::getPredictions).toList();
    }

    /**
     * Gets the list of Recalls.
     *
     * @return the list of recalls
     */
    public List<Double> getRecalls() {
        return this.results.stream().map(EvaluationResult::getRecall).toList();
    }

    /**
     * Gets the list of F-Measures.
     *
     * @return the list of F-Measures
     */
    public List<Double> getFMeasures() {
        return this.results.stream().map(EvaluationResult::getFMeasure).toList();
    }

    /**
     * Gets the list of numbers of True Positives.
     *
     * @return the list of numbers of True Positives
     */
    public List<Double> getNumTruePositives() {
        return this.results.stream().map(EvaluationResult::getNumTruePositives).toList();
    }

    /**
     * Gets the list of numbers of True Negatives.
     *
     * @return the list of numbers of True Negatives
     */
    public List<Double> getNumTrueNegatives() {
        return this.results.stream().map(EvaluationResult::getNumTrueNegatives).toList();
    }

    /**
     * Gets the list of numbers of False Positives.
     *
     * @return the list of numbers of False Positives
     */
    public List<Double> getNumFalsePositives() {
        return this.results.stream().map(EvaluationResult::getNumFalsePositives).toList();
    }

    /**
     * Gets the list of numbers of False Negatives.
     *
     * @return the list of numbers of False Negatives
     */
    public List<Double> getNumFalseNegatives() {
        return this.results.stream().map(EvaluationResult::getNumFalseNegatives).toList();
    }

    /**
     * Gets the list of diversity measures.
     *
     * @return the list of diversity measures
     */
    public List<Double> getDiversities() {
        return this.results.stream().map(EvaluationResult::getDiversity).toList();
    }

    /**
     * Adds the results of a given run.
     *
     * @param evaluation the Evaluation object used for the model evaluation
     */
    public void addRunResult(Evaluation evaluation) {
        this.addRunResult(new EvaluationResult(evaluation));
    }

    /**
     * Adds the results of a given run.
     *
     * @param evaluation the Evaluation object used for the model evaluation
     * @param diversity  the diversity result
     */
    public void addRunResult(Evaluation evaluation,
                             Double diversity) {
        this.addRunResult(new EvaluationResult(evaluation, diversity));
    }

    public void addRunResult(EvaluationResult evaluationResult) {
        this.add(evaluationResult);
    }

    /**
     * Computes the average MCC of all runs.
     *
     * @return the average MCC of all runs, or {@link Double#NaN} if the list of MCCs is empty
     */
    public Double getAverageMCC() {
        return Utils.computeDoubleAverage(this.getMCCs());
    }

    /**
     * Computes the average Precision of all runs.
     *
     * @return the average Precision of all runs, or {@link Double#NaN} if the list of Precisions is empty
     */
    public Double getAveragePrecision() {
        return Utils.computeDoubleAverage(this.getPrecisions());
    }

    /**
     * Computes the average diversity of all runs.
     *
     * @return the average diversity of all runs, or {@link Double#NaN} if the list of diversities is empty
     */
    public Double getAverageDiversity() {
        return Utils.computeDoubleAverage(this.getDiversities());
    }

    /**
     * Computes the average Recall of all runs.
     *
     * @return the average Recall of all runs, or {@link Double#NaN} if the list of Recalls is empty
     */
    public Double getAverageRecall() {
        return Utils.computeDoubleAverage(this.getRecalls());
    }

    /**
     * Computes the average F-Measure of all runs.
     *
     * @return the average F-Measure of all runs, or {@link Double#NaN} if the list of F-Measures is empty
     */
    public Double getAverageFMeasure() {
        return Utils.computeDoubleAverage(this.getFMeasures());
    }

    /**
     * Computes the average number of True Positives of all runs.
     *
     * @return the average number True Positives of all runs, or {@link Double#NaN} if the list of True Positives is
     * empty
     */
    public Double getAverageTruePositives() {
        return Utils.computeDoubleAverage(this.getNumTruePositives());
    }

    /**
     * Computes the average number of True Negatives of all runs.
     *
     * @return the average number True Negatives of all runs, or {@link Double#NaN} if the list of True Negatives is
     * empty
     */
    public Double getAverageTrueNegatives() {
        return Utils.computeDoubleAverage(this.getNumTrueNegatives());
    }

    /**
     * Computes the average number of False Positives of all runs.
     *
     * @return the average number False Positives of all runs, or {@link Double#NaN} if the list of False Positives is
     * empty
     */
    public Double getAverageFalsePositives() {
        return Utils.computeDoubleAverage(this.getNumFalsePositives());
    }

    /**
     * Computes the average number of False Negatives of all runs.
     *
     * @return the average number False Negatives of all runs, or {@link Double#NaN} if the list of False Negatives is
     * empty
     */
    public Double getAverageFalseNegatives() {
        return Utils.computeDoubleAverage(this.getNumFalseNegatives());
    }

    /**
     * Computes the average prediction of all runs.
     *
     * @return the average prediction of all runs, or an empty list of predictions if no runs was done
     *
     * @throws IllegalArgumentException if the size of the predictions is different from one run to another
     */
    public List<Double> getAveragePredictions() {
        List<Double> avgPredictions = new ArrayList<>();
        List<List<Prediction>> predictions = this.getPredictions();
        if (!predictions.isEmpty()) {
            int numDataPoints = predictions.get(0).size();
            if (predictions.stream()
                    .anyMatch(list -> list.size() != numDataPoints)) {
                throw new IllegalArgumentException("Ops! The number of predictions is different from one run to another.");
            }
            // For each data point
            for (int dataPointIndex = 0; dataPointIndex < numDataPoints; dataPointIndex++) {
                // Gets the final so we can use in the lambda expression
                final int finalIndex = dataPointIndex;
                // Average the prediction across all runs
                Double average = predictions.stream()
                        .mapToDouble(predictionArray -> predictionArray.get(finalIndex).predicted())
                        .average()
                        .orElse(Double.NaN);
                avgPredictions.add(average);
            }
        }
        return avgPredictions;
    }

    /**
     * Gets the average result using one of the {@link ClassificationObjective} enums.
     *
     * @param objective the objective to retrieve
     *
     * @return the average result of the objective, or {@link Double#NaN} if it cannot be found or if it is null
     */
    public Double getAverageFromObjectiveEnum(ClassificationObjective objective) {
        if (objective == null) {
            return Double.NaN;
        }
        return switch (objective) {
            case MCC -> this.getAverageMCC();
            case PRECISION -> this.getAveragePrecision();
            case DISAGREEMENT, WAD -> this.getAverageDiversity();
            default -> Double.NaN;
        };
    }

    @Override
    public int size() {
        return this.results.size();
    }

    @Override
    public boolean isEmpty() {
        return this.results.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.results.contains(o);
    }

    @NotNull
    @Override
    public Iterator<EvaluationResult> iterator() {
        return this.results.iterator();
    }

    @NotNull
    @Override
    public Object @NotNull [] toArray() {
        return this.results.toArray();
    }

    @NotNull
    @Override
    public <T> T @NotNull [] toArray(@NotNull T[] a) {
        return this.results.toArray(a);
    }

    @Override
    public boolean add(EvaluationResult evaluationResult) {
        return this.results.add(evaluationResult);
    }

    @Override
    public boolean remove(Object o) {
        return this.results.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return this.results.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends EvaluationResult> c) {
        return this.results.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends EvaluationResult> c) {
        return this.results.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return this.results.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return this.results.retainAll(c);
    }

    @Override
    public void clear() {
        this.results.clear();
    }

    @Override
    public EvaluationResult get(int index) {
        return this.results.get(index);
    }

    @Override
    public EvaluationResult set(int index, EvaluationResult element) {
        return this.results.set(index, element);
    }

    @Override
    public void add(int index, EvaluationResult element) {
        this.results.add(index, element);
    }

    @Override
    public EvaluationResult remove(int index) {
        return this.results.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.results.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.results.lastIndexOf(o);
    }

    @NotNull
    @Override
    public ListIterator<EvaluationResult> listIterator() {
        return this.results.listIterator();
    }

    @NotNull
    @Override
    public ListIterator<EvaluationResult> listIterator(int index) {
        return this.results.listIterator(index);
    }

    @NotNull
    @Override
    public List<EvaluationResult> subList(int fromIndex, int toIndex) {
        return this.results.subList(fromIndex, toIndex);
    }
}
