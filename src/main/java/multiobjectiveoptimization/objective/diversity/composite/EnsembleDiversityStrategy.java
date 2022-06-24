package multiobjectiveoptimization.objective.diversity.composite;

import evaluation.EvaluationResult;

import java.util.List;

public interface EnsembleDiversityStrategy {
    /**
     * Returns appropriate fitness value based on the objective to maximize diversity since JMetal frames objectives as
     * a minimization problem
     *
     * @param diversityMeasure diversity measure returned by calcDiversity()
     *
     * @return either  1 / diversityMeasure if higher diversityMeasure == higher diversity or      diversityMeasure if
     * lower diversityMeasure  == higher diversity
     */
    double convertDiversityToFitness(double diversityMeasure);

    double calcDiversity(List<EvaluationResult> baseClassifierResults);

    double calcDiversity(EvaluationResult ensembleResults, List<EvaluationResult> baseClassifierResults);
}
