package multiobjectiveoptimization.objective.diversity.composite.nonpairwise;

import evaluation.EvaluationResult;
import multiobjectiveoptimization.objective.ClassificationObjective;
import multiobjectiveoptimization.objective.diversity.builder.DiversityStrategyFactory;
import multiobjectiveoptimization.objective.diversity.composite.AbstractEnsembleDiversityStrategy;
import multiobjectiveoptimization.objective.diversity.composite.EnsembleDiversityStrategy;

import java.util.List;

public class WAD extends AbstractEnsembleDiversityStrategy {
    public WAD() {
        super(false);
    }

    @Override
    public double calcDiversityHook(List<EvaluationResult> baseClassifierResults) {
        // This should never be called
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public double calcDiversityHook(EvaluationResult ensembleResults, List<EvaluationResult> baseClassifierResults) {
        EnsembleDiversityStrategy div = DiversityStrategyFactory.buildStrategy(ClassificationObjective.DISAGREEMENT); // currently disagreement
        double divResult = div.calcDiversity(ensembleResults, baseClassifierResults);
        double precision = ensembleResults.getPrecision();
        if (precision == 0 && divResult == 0) {
            return 0.0;
        }
        return (precision * divResult) / (0.5 * precision + 0.5 * divResult);
    }
}
