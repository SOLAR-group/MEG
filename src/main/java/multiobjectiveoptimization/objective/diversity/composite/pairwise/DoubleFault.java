package multiobjectiveoptimization.objective.diversity.composite.pairwise;

import evaluation.EvaluationResult;

import java.util.Set;

public class DoubleFault extends AbstractPairwiseEnsembleDiversityStrategy {
    public DoubleFault() {
        super(true);
    }

    @Override
    public double calcDiv_ij(EvaluationResult clf_i, EvaluationResult clf_j) {
        Set<Integer> incorrectIndices_i = getIncorrectlyPredictedIndices(clf_i);
        Set<Integer> incorrectIndices_j = getIncorrectlyPredictedIndices(clf_j);

        int N00 = getN00(incorrectIndices_i, incorrectIndices_j);

        return (double) N00 / clf_i.getPredictions().size();
    }
}
