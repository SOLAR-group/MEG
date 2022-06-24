package multiobjectiveoptimization.composite;

import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.checking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.List;

public class SinglePointCV<T, S extends Solution<T>> implements CrossoverOperator<S> {
    private final double crossoverProbability;
    private final JMetalRandom randomGenerator;

    public SinglePointCV(double crossoverProbability) {
        if (crossoverProbability < 0) {
            throw new JMetalException("Crossover probability is negative: " + crossoverProbability);
        }
        this.crossoverProbability = crossoverProbability;
        this.randomGenerator = JMetalRandom.getInstance();
    }

    @Override
    public double getCrossoverProbability() {
        return crossoverProbability;
    }

    @Override
    public int getNumberOfRequiredParents() {
        return 2;
    }

    @Override
    public int getNumberOfGeneratedChildren() {
        return 2;
    }

    @Override
    public List<S> execute(List<S> solutions) {
        Check.isNotNull(solutions);
        Check.that(solutions.size() == 2, "There must be two parents instead of " + solutions.size());
        return doCrossover(this.crossoverProbability, solutions.get(0), solutions.get(1));
    }

    public List<S> doCrossover(double probability, S parent1, S parent2) {
        S offspring1 = (S) parent1.copy();
        S offspring2 = (S) parent2.copy();

        if (randomGenerator.nextDouble() < probability) {
            int totalNumberOfGenesParent1 = parent1.getNumberOfVariables();
            int totalNumberOfGenesParent2 = parent2.getNumberOfVariables();

            int crossoverPointParent1 = randomGenerator.nextInt(0, totalNumberOfGenesParent1 - 1);
            int crossoverPointParent2 = randomGenerator.nextInt(0, totalNumberOfGenesParent2 - 1);

            for (int i = crossoverPointParent1; i < totalNumberOfGenesParent1; i++) {
                offspring2.setVariable(i, parent1.getVariable(i));
            }
            for (int i = crossoverPointParent2; i < totalNumberOfGenesParent2; i++) {
                offspring1.setVariable(i, parent2.getVariable(i));
            }
        }

        List<S> offspring = new ArrayList<>();
        offspring.add(offspring1);
        offspring.add(offspring2);

        return offspring;
    }
}
