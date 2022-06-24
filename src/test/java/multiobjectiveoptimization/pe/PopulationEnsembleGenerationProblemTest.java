package multiobjectiveoptimization.pe;

import dataanalysis.composite.SolutionSetParser;
import multiobjectiveoptimization.objective.ClassificationObjective;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.compositesolution.CompositeSolution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class PopulationEnsembleGenerationProblemTest {

    public static final String DATA = "data/Arff_XV/jruby-1.1-first.arff";

    @Test
    void evaluateFail() {
        SolutionSetParser parser = new SolutionSetParser();
        List<CompositeSolution> compositeSolutions = parser.parseSolutionSet("src/test/resources/parser/VAR_2.csv");
        final DoubleSolution solution = (DoubleSolution) compositeSolutions.get(0).getVariable(1);
        final PopulationEnsembleGenerationProblem problem = new PopulationEnsembleGenerationProblem(DATA);
        assertThrows(IllegalArgumentException.class, () -> problem.evaluate(solution));
    }

    @Test
    void evaluateSuccess() {
        SolutionSetParser parser = new SolutionSetParser();
        List<CompositeSolution> compositeSolutions = parser.parseSolutionSet("src/test/resources/parser/VAR_2.csv");
        List<DoubleSolution> solutions = compositeSolutions.stream()
                .map(solution -> (DoubleSolution) solution.getVariable(1))
                .collect(Collectors.toList());
        final PopulationEnsembleGenerationProblem problem = new PopulationEnsembleGenerationProblem(DATA);
        problem.evaluatePopulation(solutions);
        assertNotNull(problem.populationResults);
        assertNotNull(problem.ensembleResults);
        assertFalse(problem.populationResults.isEmpty());
        assertFalse(problem.ensembleResults.isEmpty());

        DoubleSolution solution = solutions.get(0);
        problem.evaluate(solution);
        assertNotEquals(0, solution.getObjective(0));
        assertNotEquals(Double.NaN, solution.getObjective(0));
        assertFalse(solution.getObjective(1) <= 0);
        assertNotEquals(Double.NaN, solution.getObjective(1));
        assertNotEquals(Double.MAX_VALUE, solution.getObjective(0));
        assertNotEquals(Double.MAX_VALUE, solution.getObjective(1));
    }

    @Test
    void evaluateSuccessPrecision() {
        SolutionSetParser parser = new SolutionSetParser();
        List<CompositeSolution> compositeSolutions = parser.parseSolutionSet("src/test/resources/parser/VAR_2.csv");
        List<DoubleSolution> solutions = compositeSolutions.stream()
                .map(solution -> (DoubleSolution) solution.getVariable(1))
                .collect(Collectors.toList());
        final PopulationEnsembleGenerationProblem problem = new PopulationEnsembleGenerationProblem(DATA, ClassificationObjective.PRECISION);
        problem.evaluatePopulation(solutions);
        assertNotNull(problem.populationResults);
        assertNotNull(problem.ensembleResults);
        assertFalse(problem.populationResults.isEmpty());
        assertFalse(problem.ensembleResults.isEmpty());

        DoubleSolution solution = solutions.get(0);
        problem.evaluate(solution);
        assertNotEquals(0, solution.getObjective(0));
        assertNotEquals(Double.NaN, solution.getObjective(0));
        assertFalse(solution.getObjective(1) <= 0);
        assertNotEquals(Double.NaN, solution.getObjective(1));
        assertNotEquals(Double.MAX_VALUE, solution.getObjective(0));
        assertNotEquals(Double.MAX_VALUE, solution.getObjective(1));
    }

    @Test
    void createSolution() {
        PopulationEnsembleGenerationProblem problem = new PopulationEnsembleGenerationProblem(DATA);
        DoubleSolution solution = problem.createSolution();
        assertNotNull(solution);
    }
}