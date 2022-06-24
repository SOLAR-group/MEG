package multiobjectiveoptimization.composite;

import dataanalysis.composite.SolutionSetParser;
import multiobjectiveoptimization.objective.ClassificationObjective;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.compositesolution.CompositeSolution;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CompositeEnsembleGenerationProblemTest {

    public static final String DATA = "data/Arff_XV/jruby-1.1-first.arff";

    @Test
    void evaluate() {
        SolutionSetParser parser = new SolutionSetParser();
        List<CompositeSolution> compositeSolutions = parser.parseSolutionSet("src/test/resources/parser/VAR_3.csv");
        CompositeEnsembleGenerationProblem problem = new CompositeEnsembleGenerationProblem(DATA);
        CompositeSolution solution = compositeSolutions.get(0);
        problem.evaluate(solution);
        assertFalse(solution.getObjective(0) <= 0);
        assertNotEquals(Double.NaN, solution.getObjective(0));
        assertNotEquals(Double.MAX_VALUE, solution.getObjective(0));
        assertFalse(solution.getObjective(1) <= 0);
        assertNotEquals(Double.NaN, solution.getObjective(1));
        assertNotEquals(Double.MAX_VALUE, solution.getObjective(1));
    }

    @Test
    void evaluatePrecision() {
        SolutionSetParser parser = new SolutionSetParser();
        List<CompositeSolution> compositeSolutions = parser.parseSolutionSet("src/test/resources/parser/VAR_3.csv");
        CompositeEnsembleGenerationProblem problem = new CompositeEnsembleGenerationProblem(DATA, ClassificationObjective.DISAGREEMENT, ClassificationObjective.PRECISION);
        CompositeSolution solution = compositeSolutions.get(0);
        problem.evaluate(solution);
        assertFalse(solution.getObjective(0) <= 0);
        assertNotEquals(Double.NaN, solution.getObjective(0));
        assertFalse(solution.getObjective(1) <= 0);
        assertNotEquals(Double.NaN, solution.getObjective(1));
        assertNotEquals(Double.MAX_VALUE, solution.getObjective(0));
        assertNotEquals(Double.MAX_VALUE, solution.getObjective(1));
    }

    @Test
    void evaluatePrecisionWAD() {
        SolutionSetParser parser = new SolutionSetParser();
        List<CompositeSolution> compositeSolutions = parser.parseSolutionSet("src/test/resources/parser/VAR_3.csv");
        CompositeEnsembleGenerationProblem problem = new CompositeEnsembleGenerationProblem(DATA, ClassificationObjective.WAD, ClassificationObjective.PRECISION);
        CompositeSolution solution = compositeSolutions.get(0);
        problem.evaluate(solution);
        assertFalse(solution.getObjective(0) <= 0);
        assertNotEquals(Double.NaN, solution.getObjective(0));
        assertFalse(solution.getObjective(1) <= 0);
        assertNotEquals(Double.NaN, solution.getObjective(1));
        assertNotEquals(Double.MAX_VALUE, solution.getObjective(0));
        assertNotEquals(Double.MAX_VALUE, solution.getObjective(1));
    }

    @Test
    void evaluate2() {
        SolutionSetParser parser = new SolutionSetParser();
        List<CompositeSolution> compositeSolutions = parser.parseSolutionSet("src/test/resources/parser/VAR_4.csv");
        CompositeEnsembleGenerationProblem problem = new CompositeEnsembleGenerationProblem(DATA);
        CompositeSolution solution = compositeSolutions.get(0);
        problem.evaluate(solution);
        assertFalse(solution.getObjective(0) <= 0);
        assertNotEquals(Double.NaN, solution.getObjective(0));
        assertNotEquals(Double.MAX_VALUE, solution.getObjective(0));
        assertFalse(solution.getObjective(1) <= 0);
        assertNotEquals(Double.NaN, solution.getObjective(1));
        assertNotEquals(Double.MAX_VALUE, solution.getObjective(1));
    }

    @Test
    void evaluateShouldNotBeNaN() {
        SolutionSetParser parser = new SolutionSetParser();
        List<CompositeSolution> compositeSolutions = parser.parseSolutionSet("src/test/resources/parser/VAR_ONE_CLASSIFIER.csv");
        CompositeEnsembleGenerationProblem problem = new CompositeEnsembleGenerationProblem(DATA);
        CompositeSolution solution = compositeSolutions.get(0);
        problem.evaluate(solution);
        assertEquals(Double.MAX_VALUE, solution.getObjective(0));
        assertFalse(solution.getObjective(1) <= 0);
        assertNotEquals(Double.NaN, solution.getObjective(1));
        assertNotEquals(Double.MAX_VALUE, solution.getObjective(1));
    }

    @Test
    void evaluateShouldNotBeNaN2() {
        SolutionSetParser parser = new SolutionSetParser();
        List<CompositeSolution> compositeSolutions = parser.parseSolutionSet("src/test/resources/parser/VAR_ONE_CLASSIFIER.csv");
        CompositeEnsembleGenerationProblem problem = new CompositeEnsembleGenerationProblem(DATA, ClassificationObjective.WAD, ClassificationObjective.MCC);
        CompositeSolution solution = compositeSolutions.get(0);
        problem.evaluate(solution);
        assertEquals(Double.MAX_VALUE, solution.getObjective(0));
        assertFalse(solution.getObjective(1) <= 0);
        assertNotEquals(Double.NaN, solution.getObjective(1));
        assertNotEquals(Double.MAX_VALUE, solution.getObjective(1));
    }

    @Test
    void createSolution() {
        CompositeEnsembleGenerationProblem problem = new CompositeEnsembleGenerationProblem(DATA);
        CompositeSolution solution = problem.createSolution();
        assertNotNull(solution);
    }
}