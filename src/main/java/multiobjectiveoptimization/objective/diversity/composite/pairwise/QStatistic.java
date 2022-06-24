package multiobjectiveoptimization.objective.diversity.composite.pairwise;

import evaluation.EvaluationResult;

import java.util.Set;

public class QStatistic extends AbstractPairwiseEnsembleDiversityStrategy {
    public QStatistic() {
        super(true);
    }

    @Override
    public double calcDiv_ij(EvaluationResult clf_i, EvaluationResult clf_j){
        Set<Integer> correctIndices_i = getCorrectlyPredictedIndices(clf_i);
        Set<Integer> correctIndices_j = getCorrectlyPredictedIndices(clf_j);
        Set<Integer> incorrectIndices_i = getIncorrectlyPredictedIndices(clf_i);
        Set<Integer> incorrectIndices_j = getIncorrectlyPredictedIndices(clf_j);

        // there are 4 edge cases we must consider where denom is 0 and thus Q-Statistics for a pair is undefined
        // Case 1) N11 == 0 && N01 == 0
        // Case 2) N11 == 0 && N10 == 0
        // In case 1 & 2, either 1 or 2 out of a pair of classifiers always WRONGLY classifies every single instance

        // Case 3) N00 == 0 && N01 == 0
        // Case 4) N00 == 0 && N10 == 0
        // In case 3 & 4, either 1 or 2 out of a pair of classifiers always CORRECTlY classifies every single instance

        // To deal with undefined QStat values, we apply Yate's correction (Reynolds, 1984)
        // which is done by adding 0.5 to all N11, N00 and N01, N10 when any one of them is 0
        // (as cited in Lloyd, Kennedy and Yoder, 2013, p.485)

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

        double denom = N11 * N00 + N01 * N10;
        return (N11 * N00 - N01 * N10) / denom;
    }

}
