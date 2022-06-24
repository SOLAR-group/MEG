package multiobjectiveoptimization.objective.diversity;

import java.util.List;

/**
 * This class computes the Negative Correlation Learning measure.
 */
public class NCL {

    /**
     * Computes the NCL.
     *
     * @param ensemblePredictions         list of predictions of the ensemble
     * @param classifierPredictions       list of predictions of the classifier being evaluated
     * @param otherClassifiersPredictions lists of predictions of the list of classifiers different from the classifier
     *                                    being evaluated
     *
     * @return the NCL of the classifier
     */
    public double computeNCL(List<Double> ensemblePredictions, List<Double> classifierPredictions, List<List<Double>> otherClassifiersPredictions) {
        int numDataPoints = ensemblePredictions.size();
        double sum = 0;
        for (int dataPoint = 0; dataPoint < numDataPoints; dataPoint++) {
            double difference = getDifference(ensemblePredictions, classifierPredictions, dataPoint);
            double sumOfDifferences = getSumOfOthersDifferences(ensemblePredictions, otherClassifiersPredictions, dataPoint);
            sum += difference * sumOfDifferences;
        }
        return sum;
    }

    /**
     * Computes the sum of the differences between the predictions of an ensemble and a list of classifiers.
     *
     * @param ensemblePredictions    list of predictions of the ensemble
     * @param classifiersPredictions lists of predictions of the list of classifiers
     * @param dataPoint              the index of the data point
     *
     * @return the sum of differences
     */
    public double getSumOfOthersDifferences(List<Double> ensemblePredictions, List<List<Double>> classifiersPredictions, int dataPoint) {
        double sum = 0;
        for (List<Double> classifierPredictions : classifiersPredictions) {
            sum += getDifference(ensemblePredictions, classifierPredictions, dataPoint);
        }
        return sum;
    }

    /**
     * Computes a simple difference between the ensemble predictions and a classifier prediction.
     *
     * @param ensemblePredictions   list of predictions of the ensemble
     * @param classifierPredictions list of predictions of the classifier
     * @param dataPoint             the index of the data point
     *
     * @return the difference between the predictions
     */
    public double getDifference(List<Double> ensemblePredictions, List<Double> classifierPredictions, int dataPoint) {
        Double ensemblePrediction = ensemblePredictions.get(dataPoint);
        Double prediction = classifierPredictions.get(dataPoint);
        return prediction - ensemblePrediction;
    }
}
