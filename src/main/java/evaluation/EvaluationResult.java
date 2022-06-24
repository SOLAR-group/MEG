package evaluation;

import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.Prediction;

import java.util.List;

/**
 * This class encapsulates a single Cross Validation Result
 */
public class EvaluationResult {

    /**
     * The resulting object. It encapsulates all the results from the Cross Validation.
     */
    protected Evaluation evaluationResult;

    /**
     * The diversity result of the cross validation.
     */
    protected Double diversity = 0.0;

    public EvaluationResult(Evaluation evaluationResult) {
        this.evaluationResult = evaluationResult;
    }

    public EvaluationResult(Evaluation evaluationResult, Double diversity) {
        this.evaluationResult = evaluationResult;
        this.diversity = diversity;
    }

    public Evaluation getEvaluationResult() {
        return evaluationResult;
    }

    public void setEvaluationResult(Evaluation evaluationResult) {
        this.evaluationResult = evaluationResult;
    }

    public Double getDiversity() {
        return diversity;
    }

    public void setDiversity(Double diversity) {
        this.diversity = diversity;
    }

    public List<Prediction> getPredictions() {
        return evaluationResult.predictions();
    }

    public double getIncorrect() {
        return evaluationResult.incorrect();
    }

    public double getCorrect() {
        return evaluationResult.correct();
    }

    public double getErrorRate() {
        return evaluationResult.errorRate();
    }

    public double getNumTruePositives() {
        return evaluationResult.numTruePositives(1);
    }

    public double getNumTrueNegatives() {
        return evaluationResult.numTrueNegatives(1);
    }

    public double getNumFalsePositives() {
        return evaluationResult.numFalsePositives(1);
    }

    public double getNumFalseNegatives() {
        return evaluationResult.numFalseNegatives(1);
    }

    public double getMCC() {
        return evaluationResult.matthewsCorrelationCoefficient(1);
    }

    public double getRecall() {
        return evaluationResult.recall(1);
    }

    public double getPrecision() {
        return evaluationResult.precision(1);
    }

    public double getFMeasure() {
        return evaluationResult.fMeasure(1);
    }
}