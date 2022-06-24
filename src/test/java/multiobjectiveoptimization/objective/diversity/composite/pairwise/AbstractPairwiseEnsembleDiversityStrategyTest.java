package multiobjectiveoptimization.objective.diversity.composite.pairwise;

import com.google.common.collect.Lists;
import evaluation.EvaluationResult;
import multiobjectiveoptimization.objective.diversity.composite.CoolMocker;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AbstractPairwiseEnsembleDiversityStrategyTest {
    @Test
    void getCorrectlyPredictedIndices() {
        AbstractPairwiseEnsembleDiversityStrategy strategy = new Disagreement();
        Integer[] actuals = new Integer[]{0, 0, 0, 0, 1};
        Integer[] preds = new Integer[]{1, 0, 0, 1, 1};

        EvaluationResult evalResult1 = CoolMocker.mockResult(actuals, preds);

        Set<Integer> correctIndices = strategy.getCorrectlyPredictedIndices(evalResult1);
        Set<Integer> answer = new HashSet<>(Arrays.asList(1, 2, 4));
        assertTrue(CollectionUtils.isEqualCollection(correctIndices, answer));
    }

    @Test
    void getIncorrectlyPredictedIndices() {
        AbstractPairwiseEnsembleDiversityStrategy strategy = new Disagreement();
        Integer[] actuals = new Integer[]{0, 0, 0, 0, 1};
        Integer[] preds = new Integer[]{1, 0, 0, 1, 1};
        EvaluationResult evalResult1 = CoolMocker.mockResult(actuals, preds);

        Set<Integer> correctIndices = strategy.getIncorrectlyPredictedIndices(evalResult1);
        Set<Integer> answer = new HashSet<>(Arrays.asList(0, 3));
        assertTrue(CollectionUtils.isEqualCollection(correctIndices, answer));

        Integer[] actuals2 = new Integer[]{0};
        Integer[] preds2 = new Integer[]{1};
        EvaluationResult evalResult2 = CoolMocker.mockResult(actuals2, preds2);
        Set<Integer> correctIndices2 = strategy.getIncorrectlyPredictedIndices(evalResult2);
        Set<Integer> answer2 = new HashSet<>(Arrays.asList(0));
        assertTrue(CollectionUtils.isEqualCollection(correctIndices2, answer2));

        EvaluationResult evalResult3 = CoolMocker.mockResult(new Integer[]{}, new Integer[]{});
        Set<Integer> correctIndices3 = strategy.getIncorrectlyPredictedIndices(evalResult3);
        Set<Integer> answer3 = new HashSet<>();
        assertTrue(CollectionUtils.isEqualCollection(correctIndices3, answer3));
    }

    @Test
    void getN11() {
        AbstractPairwiseEnsembleDiversityStrategy strategy = new Disagreement();
        Set<Integer> set1 = new HashSet<>(Arrays.asList(1, 2, 3, 4));
        Set<Integer> set2 = new HashSet<>(Arrays.asList(0, 3, 5, 6));
        int n11 = strategy.getN11(set1, set2);
        assertEquals(n11, 1);

        // edge cases
        Set<Integer> set1_edge = new HashSet<>(Arrays.asList());
        Set<Integer> set2_edge = new HashSet<>(Arrays.asList());
        int n11_edge = strategy.getN11(set1_edge, set2_edge);
        assertEquals(n11_edge, 0);

        int n11_edge2 = strategy.getN11(set1_edge, set2);
        assertEquals(n11_edge2, 0);
    }

    @Test
    void getN00() {
        AbstractPairwiseEnsembleDiversityStrategy strategy = new Disagreement();
        Set<Integer> set1 = new HashSet<>(Arrays.asList(1, 2, 3, 4));
        Set<Integer> set2 = new HashSet<>(Arrays.asList(0, 3, 5, 6));
        int n11 = strategy.getN00(set1, set2);
        assertEquals(n11, 1);

        // edge cases
        Set<Integer> set1_edge = new HashSet<>(Arrays.asList());
        Set<Integer> set2_edge = new HashSet<>(Arrays.asList());
        int n11_edge = strategy.getN00(set1_edge, set2_edge);
        assertEquals(n11_edge, 0);

        int n11_edge2 = strategy.getN00(set1_edge, set2);
        assertEquals(n11_edge2, 0);
    }

    @Test
    void getN01() {
        AbstractPairwiseEnsembleDiversityStrategy strategy = new Disagreement();
        Set<Integer> set1 = new HashSet<>(Arrays.asList(0, 1, 2, 5));
        Set<Integer> set2 = new HashSet<>(Arrays.asList(1, 2, 8, 10, 11));
        assertEquals(strategy.getN01(set1, set2), 3);
        assertEquals(strategy.getN01(set2, set1), 2);

        Set<Integer> set3 = new HashSet<>();
        assertEquals(strategy.getN01(set1, set3), 0);
        assertEquals(strategy.getN01(set3, set1), 4);
        assertEquals(strategy.getN01(set3, set3), 0);

    }

    @Test
    void testNullClassifiers() {
        AbstractPairwiseEnsembleDiversityStrategy strategy = new Disagreement();
        double result = strategy.calcDiversity(null);
        assertTrue(Double.isNaN(result));
        assertEquals(Double.MAX_VALUE, strategy.convertDiversityToFitness(result));
    }

    @Test
    void testEmptyClassifiers() {
        AbstractPairwiseEnsembleDiversityStrategy strategy = new Disagreement();

        double result = strategy.calcDiversity(Lists.newArrayList());
        assertTrue(Double.isNaN(result));
        assertEquals(Double.MAX_VALUE, strategy.convertDiversityToFitness(result));
    }

    @Test
    void test1Classifier() {
        AbstractPairwiseEnsembleDiversityStrategy strategy = new Disagreement();
        Integer[] actuals = new Integer[]{0, 0, 0, 0, 1};
        Integer[] preds = new Integer[]{1, 0, 0, 1, 1};

        EvaluationResult evalResult1 = CoolMocker.mockResult(actuals, preds);

        double result = strategy.calcDiversity(Lists.newArrayList(evalResult1));
        assertTrue(Double.isNaN(result));
        assertEquals(Double.MAX_VALUE, strategy.convertDiversityToFitness(result));
    }
}
