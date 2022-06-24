package multiobjectiveoptimization.pe;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

import java.util.Comparator;
import java.util.List;

public class ExtendedNSGAII extends NSGAII<DoubleSolution> {

    public ExtendedNSGAII(PopulationEnsembleGenerationProblem problem,
                          int maxEvaluations,
                          int populationSize,
                          int matingPoolSize,
                          int offspringPopulationSize,
                          CrossoverOperator<DoubleSolution> crossoverOperator,
                          MutationOperator<DoubleSolution> mutationOperator,
                          SelectionOperator<List<DoubleSolution>, DoubleSolution> selectionOperator,
                          SolutionListEvaluator<DoubleSolution> evaluator) {
        super(problem, maxEvaluations, populationSize, matingPoolSize, offspringPopulationSize, crossoverOperator, mutationOperator, selectionOperator, evaluator);
    }

    public ExtendedNSGAII(PopulationEnsembleGenerationProblem problem,
                          int maxEvaluations,
                          int populationSize,
                          int matingPoolSize,
                          int offspringPopulationSize,
                          CrossoverOperator<DoubleSolution> crossoverOperator,
                          MutationOperator<DoubleSolution> mutationOperator,
                          SelectionOperator<List<DoubleSolution>, DoubleSolution> selectionOperator,
                          Comparator<DoubleSolution> dominanceComparator,
                          SolutionListEvaluator<DoubleSolution> evaluator) {
        super(problem, maxEvaluations, populationSize, matingPoolSize, offspringPopulationSize, crossoverOperator, mutationOperator, selectionOperator, dominanceComparator, evaluator);
    }

    @Override
    protected List<DoubleSolution> evaluatePopulation(List<DoubleSolution> population) {
        PopulationEnsembleGenerationProblem problem = (PopulationEnsembleGenerationProblem) getProblem();
        problem.evaluatePopulation(population);
        return super.evaluatePopulation(population);
    }
}
