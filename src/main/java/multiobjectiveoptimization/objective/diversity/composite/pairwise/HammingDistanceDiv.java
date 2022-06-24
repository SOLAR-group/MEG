package multiobjectiveoptimization.objective.diversity.composite.pairwise;

import com.google.common.collect.Streams;
import evaluation.EvaluationResult;

public class HammingDistanceDiv extends AbstractPairwiseEnsembleDiversityStrategy {
    public HammingDistanceDiv() {
        super(false);
    }

    @Override
    public double calcDiv_ij(EvaluationResult clf1Result, EvaluationResult clf2Result) {
        return Streams.zip(clf1Result.getPredictions().stream(), clf2Result.getPredictions().stream()
                        , (clf1FoldResult, clf2FoldResult) -> clf1FoldResult.predicted() != clf2FoldResult.predicted() ? 1 : 0)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);
    }
}
