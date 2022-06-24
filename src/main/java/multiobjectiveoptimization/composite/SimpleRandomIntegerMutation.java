package multiobjectiveoptimization.composite;

import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

public class SimpleRandomIntegerMutation implements MutationOperator<IntegerSolution> {
    private final double mutationProbability;
    private final JMetalRandom randomGenerator;


    public SimpleRandomIntegerMutation(double mutationProbability) {
        if (mutationProbability < 0) {
            throw new JMetalException("Mutation probability is negative: " + mutationProbability);
        }
        this.mutationProbability = mutationProbability;
        this.randomGenerator = JMetalRandom.getInstance();
    }

    @Override
    public double getMutationProbability() {
        return mutationProbability;
    }

    @Override
    public IntegerSolution execute(IntegerSolution integerSolution) {
        if (null == integerSolution) {
            throw new JMetalException("Null parameter");
        } else {
            this.doMutation(this.mutationProbability, integerSolution);
            return integerSolution;
        }
    }

    private void doMutation(double probability, IntegerSolution solution) {
        for (int i = 0; i < solution.getNumberOfVariables(); ++i) {
            if (this.randomGenerator.nextDouble() <= probability) {
                int value = this.randomGenerator.nextInt(solution.getLowerBound(i), solution.getUpperBound(i));
                solution.setVariable(i, value);
            }
        }

    }
}
