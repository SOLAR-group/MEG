package multiobjectiveoptimization.objective.diversity.composite.nonpairwise;

import evaluation.EvaluationResult;
import multiobjectiveoptimization.objective.diversity.composite.AbstractEnsembleDiversityStrategy;

import java.util.List;

public class Entropy extends AbstractEnsembleDiversityStrategy {
    // range: [0,1]

    public Entropy() {
        super(false);
    }

    @Override
    public double calcDiversityHook(List<EvaluationResult> baseClassifierResults) {
        int N = baseClassifierResults.get(0).getPredictions().size();
        int nClassifiers = baseClassifierResults.size();
        double div = 0;

        // this is the maximum number of classifiers in the ensemble that predict minority labels
        // i.e. if the ensemble consists of 5 classifiers then the most diverse combination should, according to this measure,
        // have 3 classifiers that predict one label and 2 classifiers that predict the alternative.
        double maxMinorityClfsNum = nClassifiers - Math.ceil(nClassifiers / 2.0d);

        for (int j = 0; j < N; j++) {
            final int i = j;

            long nClassifiersCorrect = baseClassifierResults.stream().map(baseClassifierResult -> baseClassifierResult.getPredictions().get(i))
                    .filter(prediction -> prediction.predicted() == prediction.actual())
                    .count();

            div += (1 / maxMinorityClfsNum) * Math.min(nClassifiersCorrect, nClassifiers - nClassifiersCorrect);
        }

        return div / N;
    }
}
