package multiobjectiveoptimization.composite;

import multiobjectiveoptimization.objective.ClassificationObjective;
import org.uma.jmetal.problem.AbstractGenericProblem;
import org.uma.jmetal.solution.compositesolution.CompositeSolution;

public class SingleObjectiveCompositeEnsembleGenerationProblem extends AbstractGenericProblem<CompositeSolution> {

    protected CompositeEnsembleGenerationProblem delegate;
    protected ClassificationObjective objective;

    public SingleObjectiveCompositeEnsembleGenerationProblem(String arffPath) {
        this.objective = ClassificationObjective.MCC;
        this.delegate = new CompositeEnsembleGenerationProblem(arffPath, this.objective, this.objective);
    }

    public SingleObjectiveCompositeEnsembleGenerationProblem(String arffPath, ClassificationObjective objective) {
        this.objective = objective;
        this.delegate = new CompositeEnsembleGenerationProblem(arffPath, this.objective, this.objective);
    }

    @Override
    public void evaluate(CompositeSolution solution) {
        this.delegate.evaluate(solution);
        if (!objective.isDiversity()) {
            solution.setObjective(0, solution.getObjective(1));
        }
    }

    @Override
    public CompositeSolution createSolution() {
        return this.delegate.createSolution();
    }
}
