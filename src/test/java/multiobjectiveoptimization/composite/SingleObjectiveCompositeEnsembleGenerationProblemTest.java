package multiobjectiveoptimization.composite;

import dataanalysis.composite.SolutionSetParser;
import multiobjectiveoptimization.objective.ClassificationObjective;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.compositesolution.CompositeSolution;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SingleObjectiveCompositeEnsembleGenerationProblemTest {

    public static final String DATA = "data/Arff_XV/jruby-1.1-first.arff";

    @Test
    void evaluate() {
        SolutionSetParser parser = new SolutionSetParser();
        List<CompositeSolution> compositeSolutions = parser.parseSolutionSet("src/test/resources/parser/VAR_3.csv");
        SingleObjectiveCompositeEnsembleGenerationProblem problem = new SingleObjectiveCompositeEnsembleGenerationProblem(DATA);
        CompositeSolution solution = compositeSolutions.get(0);
        problem.evaluate(solution);
        assertFalse(solution.getObjective(0) <= 0);
        assertNotEquals(Double.NaN, solution.getObjective(0));
        assertEquals(solution.getObjective(0), solution.getObjective(1));
        assertNotEquals(Double.MAX_VALUE, solution.getObjective(0));
        assertNotEquals(Double.MAX_VALUE, solution.getObjective(1));
    }

    @Test
    void evaluatePrecision() {
        SolutionSetParser parser = new SolutionSetParser();
        List<CompositeSolution> compositeSolutions = parser.parseSolutionSet("src/test/resources/parser/VAR_3.csv");
        SingleObjectiveCompositeEnsembleGenerationProblem problem = new SingleObjectiveCompositeEnsembleGenerationProblem(DATA, ClassificationObjective.PRECISION);
        CompositeSolution solution = compositeSolutions.get(0);
        problem.evaluate(solution);
        assertFalse(solution.getObjective(0) <= 0);
        assertNotEquals(Double.NaN, solution.getObjective(0));
        assertEquals(solution.getObjective(0), solution.getObjective(1));
        assertNotEquals(Double.MAX_VALUE, solution.getObjective(0));
        assertNotEquals(Double.MAX_VALUE, solution.getObjective(1));
    }

    @Test
    void evaluateDiv() {
        SolutionSetParser parser = new SolutionSetParser();
        List<CompositeSolution> compositeSolutions = parser.parseSolutionSet("src/test/resources/parser/VAR_3.csv");
        SingleObjectiveCompositeEnsembleGenerationProblem problem = new SingleObjectiveCompositeEnsembleGenerationProblem(DATA, ClassificationObjective.DISAGREEMENT);
        CompositeSolution solution = compositeSolutions.get(0);
        problem.evaluate(solution);
        assertFalse(solution.getObjective(0) <= 0);
        assertNotEquals(Double.NaN, solution.getObjective(0));
    }

    @Test
    void evaluateWad() {
        SolutionSetParser parser = new SolutionSetParser();
        List<CompositeSolution> compositeSolutions = parser.parseSolutionSet("src/test/resources/parser/VAR_3.csv");
        SingleObjectiveCompositeEnsembleGenerationProblem problem = new SingleObjectiveCompositeEnsembleGenerationProblem(DATA, ClassificationObjective.WAD);
        CompositeSolution solution = compositeSolutions.get(0);
        problem.evaluate(solution);
        assertFalse(solution.getObjective(0) <= 0);
        assertNotEquals(Double.NaN, solution.getObjective(0));
    }

    @Test
    void createSolution() {
        SingleObjectiveCompositeEnsembleGenerationProblem problem = new SingleObjectiveCompositeEnsembleGenerationProblem(DATA);
        CompositeSolution solution = problem.createSolution();
        assertNotNull(solution);
    }
}