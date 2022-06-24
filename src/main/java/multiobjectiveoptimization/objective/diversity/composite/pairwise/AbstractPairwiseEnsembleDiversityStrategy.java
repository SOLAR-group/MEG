package multiobjectiveoptimization.objective.diversity.composite.pairwise;

import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import evaluation.EvaluationResult;
import multiobjectiveoptimization.objective.diversity.composite.AbstractEnsembleDiversityStrategy;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractPairwiseEnsembleDiversityStrategy extends AbstractEnsembleDiversityStrategy {
    protected AbstractPairwiseEnsembleDiversityStrategy(boolean shouldMinimize) {
        super(shouldMinimize);
    }

    public abstract double calcDiv_ij(EvaluationResult clf1Result, EvaluationResult clf2Result);

    /**
     * Builds up and returns a set containing indices of test data which are correctly classified
     *
     * @param clfResults
     *
     * @return {@link java.util.Set} containing correctly predicted instance indices
     *
     * @throws Exception if Weka cannot classify an instance
     */
    protected Set<Integer> getCorrectlyPredictedIndices(EvaluationResult clfResults) {
        return Streams.mapWithIndex(clfResults.getPredictions().stream(), (prediction, l) -> prediction.predicted() == prediction.actual() ? (int) l : -1)
                .filter(i -> i != -1)
                .collect(Collectors.toSet());
    }

    protected Set<Integer> getIncorrectlyPredictedIndices(EvaluationResult clfResults) {
        return Streams.mapWithIndex(clfResults.getPredictions().stream(), (prediction, l) -> prediction.predicted() != prediction.actual() ? (int) l : -1)
                .filter(i -> i != -1)
                .collect(Collectors.toSet());
    }

    protected int getN11(Set<Integer> correctIndices_i, Set<Integer> correctIndices_j) {
        return Sets.intersection(correctIndices_i, correctIndices_j).size();
    }

    protected int getN00(Set<Integer> incorrectIndices_i, Set<Integer> incorrectIndices_j) {
        return Sets.intersection(incorrectIndices_i, incorrectIndices_j).size();
    }

    protected int getN01(Set<Integer> correctIndices_i, Set<Integer> correctIndices_j) {
        return Sets.difference(correctIndices_j, correctIndices_i).size();
    }

    @Override
    public double calcDiversityHook(List<EvaluationResult> baseClassifierResults) {
        int nClassifiers = baseClassifierResults.size();
        double div = 0;
        for (int i = 0; i < baseClassifierResults.size() - 1; i++) {
            for (int j = i + 1; j < baseClassifierResults.size(); j++) {
                EvaluationResult clf1Result = baseClassifierResults.get(i);
                EvaluationResult clf2Result = baseClassifierResults.get(j);
                div += calcDiv_ij(clf1Result, clf2Result);
            }
        }

        return (2.0 * div) / (nClassifiers * (nClassifiers - 1));
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
