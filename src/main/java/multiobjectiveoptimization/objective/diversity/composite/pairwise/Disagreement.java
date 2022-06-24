package multiobjectiveoptimization.objective.diversity.composite.pairwise;

import evaluation.EvaluationResult;

import java.util.Set;

public class Disagreement extends AbstractPairwiseEnsembleDiversityStrategy {
    public Disagreement() {
        super(false);
    }

    @Override
    public double calcDiv_ij(EvaluationResult clf_i, EvaluationResult clf_j){
        Set<Integer> correctIndices_i = getCorrectlyPredictedIndices(clf_i);
        Set<Integer> correctIndices_j = getCorrectlyPredictedIndices(clf_j);
        Set<Integer> incorrectIndices_i = getIncorrectlyPredictedIndices(clf_i);
        Set<Integer> incorrectIndices_j = getIncorrectlyPredictedIndices(clf_j);

        int N11 = getN11(correctIndices_i, correctIndices_j);
        int N00 = getN00(incorrectIndices_i, incorrectIndices_j);
        int N01 = getN01(correctIndices_i, correctIndices_j);
        // we can reverse the arg order to get N10
        int N10 = getN01(correctIndices_j, correctIndices_i);
        // assert((N10 + N01 + N11 + N00) == clf_i.getPredictions().size());

        return (double) (N10 + N01) / (N00 + N11 + N10 + N01);
    }
}
