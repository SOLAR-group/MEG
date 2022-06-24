package multiobjectiveoptimization.objective.diversity.composite;

import evaluation.EvaluationResult;

import java.util.List;

public abstract class AbstractEnsembleDiversityStrategy implements EnsembleDiversityStrategy {
    protected boolean shouldBeMinimized;

    protected AbstractEnsembleDiversityStrategy(boolean shouldBeMinimized) {
        this.shouldBeMinimized = shouldBeMinimized;
    }

    protected abstract double calcDiversityHook(List<EvaluationResult> baseClassifierResults);

    protected double calcDiversityHook(EvaluationResult ensembleResults, List<EvaluationResult> baseClassifierResults) {
        return calcDiversityHook(baseClassifierResults);
    }

    @Override
    public final double calcDiversity(EvaluationResult ensembleResults, List<EvaluationResult> baseClassifierResults) {
        return validate(baseClassifierResults) ? calcDiversityHook(ensembleResults, baseClassifierResults) : Double.NaN;
    }

    @Override
    public final double calcDiversity(List<EvaluationResult> baseClassifierResults) {
        return validate(baseClassifierResults) ? calcDiversityHook(baseClassifierResults) : Double.NaN;
    }

    protected boolean validate(List<EvaluationResult> baseClassifierResults) {
        return baseClassifierResults != null && baseClassifierResults.size() > 1;
    }

    @Override
    public double convertDiversityToFitness(double diversityMeasure) {
        if (Double.isNaN(diversityMeasure)) {
            return Double.MAX_VALUE;
        } else if (shouldBeMinimized) {
            return diversityMeasure;
        } else if (diversityMeasure == 0) {
            return Double.MAX_VALUE;
        }

        return 1 / diversityMeasure;
    }
}
