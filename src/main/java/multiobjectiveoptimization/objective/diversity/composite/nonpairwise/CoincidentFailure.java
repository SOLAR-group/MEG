package multiobjectiveoptimization.objective.diversity.composite.nonpairwise;

import evaluation.EvaluationResult;
import multiobjectiveoptimization.objective.diversity.composite.AbstractEnsembleDiversityStrategy;

import java.util.List;

public class CoincidentFailure extends AbstractEnsembleDiversityStrategy {
    public CoincidentFailure() {
        super(false);
    }

    @Override
    public double calcDiversityHook(List<EvaluationResult> baseClassifierResults) {
        int L = baseClassifierResults.size();
        int N = baseClassifierResults.get(0).getPredictions().size();

        int[] correctClfsInEnsembleFractionDistrib = new int[L + 1];

        for (int j = 0; j < N; j++) {
            final int i = j;
            long nClassifiersIncorrect = baseClassifierResults.stream().map(baseClassifierResult -> baseClassifierResult.getPredictions().get(i))
                    .filter(prediction -> prediction.predicted() != prediction.actual())
                    .count();

            correctClfsInEnsembleFractionDistrib[(int) nClassifiersIncorrect]++;
        }

        if (correctClfsInEnsembleFractionDistrib[0] == N) {
            // for all instances, all classifiers in the ensemble incorrectly classified it i.e. p0 = 1.0
            return 0;
        }

        double p0 = (double) correctClfsInEnsembleFractionDistrib[0] / N;
        double pSum = 0;

        for (int i = 1; i <= L; i++) {
            double pi = (double) correctClfsInEnsembleFractionDistrib[i] / N;
            pSum += ((double) (L - i) / (L - 1)) * pi;
        }

        return (1 / (1 - p0)) * pSum;
    }
}
