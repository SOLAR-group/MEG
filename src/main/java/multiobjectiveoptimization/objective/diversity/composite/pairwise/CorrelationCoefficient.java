package multiobjectiveoptimization.objective.diversity.composite.pairwise;

import evaluation.EvaluationResult;

import java.util.Set;

public class CorrelationCoefficient extends AbstractPairwiseEnsembleDiversityStrategy {
    public CorrelationCoefficient() {
        super(true);
    }

    @Override
    public double calcDiv_ij(EvaluationResult clf_i, EvaluationResult clf_j){
        Set<Integer> correctIndices_i = getCorrectlyPredictedIndices(clf_i);
        Set<Integer> correctIndices_j = getCorrectlyPredictedIndices(clf_j);
        Set<Integer> incorrectIndices_i = getIncorrectlyPredictedIndices(clf_i);
        Set<Integer> incorrectIndices_j = getIncorrectlyPredictedIndices(clf_j);

        double N11 = getN11(correctIndices_i, correctIndices_j);
        double N00 = getN00(incorrectIndices_i, incorrectIndices_j);
        double N01 = getN01(correctIndices_i, correctIndices_j);
        double N10 = getN01(correctIndices_j, correctIndices_i);

        if (N11 == 0 || N00 == 0 || N01 == 0 || N10 == 0) {
            // Yate's correction
            N11 += 0.5;
            N00 += 0.5;
            N01 += 0.5;
            N10 += 0.5;
        }

        double denom = (N11 + N10) * (N01 + N00) * (N11 + N01) * (N10 + N00);

        return (N11 * N00 - N01 * N10) / Math.sqrt(denom);
    }
}
