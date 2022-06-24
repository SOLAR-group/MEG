package multiobjectiveoptimization.objective.diversity.composite.pairwise;

import com.google.common.collect.Lists;
import evaluation.EvaluationResult;
import multiobjectiveoptimization.objective.diversity.composite.CoolMocker;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QStatisticTest {
    protected MathContext mc = new MathContext(3, RoundingMode.HALF_UP);
    QStatistic qStatistic = new QStatistic();

    @Test
    void DivIsNotUndefinedAndIsPositiveWhenAllClassifiersAreCorrect() throws Exception {
        //        AbstractList<Instance> dummyInstances = createInstances(new int[]{1, 0, 1, 0});
        //
        //        Classifier[] clfs = new Classifier[]{
        //                new AlwaysCorrectClassifier(),
        //                new AlwaysCorrectClassifier(),
        //        };
        //
        //        assertDoesNotThrow(() -> qStatistic.calcDiversity(clfs, dummyInstances));
        //
        //        double div = qStatistic.calcDiversity(clfs, dummyInstances);
        //        assertEquals(div, 0.8);
        Integer[] actuals = new Integer[]{1, 0, 1, 0};

        EvaluationResult result1 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 1, 0});
        EvaluationResult result2 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 1, 0});

        double div = qStatistic.calcDiversity(Lists.newArrayList(result1, result2));
        assertEquals(0.8, div);
    }

    @Test
    void DivIsNotUndefinedWhenAllClassifiersAreWrong() throws Exception {
        //        AbstractList<Instance> dummyInstances = createInstances(new int[]{1, 0, 1, 0});
        //
        //        Classifier[] clfs = new Classifier[]{
        //                new AlwaysIncorrectClassifier(),
        //                new AlwaysIncorrectClassifier(),
        //        };
        //
        //        assertDoesNotThrow(() -> qStatistic.calcDiversity(clfs, dummyInstances));
        //
        //        double div = qStatistic.calcDiversity(clfs, dummyInstances);
        //        assertEquals(div, 0.8);
        Integer[] actuals = new Integer[]{1, 0, 1, 0};

        EvaluationResult result1 = CoolMocker.mockResult(actuals, new Integer[]{0, 1, 0, 1});
        EvaluationResult result2 = CoolMocker.mockResult(actuals, new Integer[]{0, 1, 0, 1});

        double div = qStatistic.calcDiversity(Lists.newArrayList(result1, result2));
        assertEquals(0.8, div);
    }

    @Test
    void DivPositiveWhenClassifiersArePositivelyCorrelated() throws Exception {
        //        AbstractList<Instance> dummyInstances = createInstances(new int[]{1, 1, 0, 0});
        //
        //        int[] preds = new int[]{1, 0, 0, 1}; // set up predictions such that N11 is 2 and N00 is 2
        //
        //        Classifier[] clfs = new Classifier[]{
        //                new SometimesCorrectClassifier(preds),
        //                new SometimesCorrectClassifier(preds),
        //        };
        //
        //        double div = qStatistic.calcDiversity(clfs, dummyInstances);
        //        assertTrue(div > 0.0);
        //
        //        BigDecimal divDec = new BigDecimal(div, mc);
        //        BigDecimal divTest = new BigDecimal(0.923, mc);
        //
        //        assertEquals(divDec, divTest);
        Integer[] actuals = new Integer[]{1, 1, 0, 0};

        EvaluationResult result1 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 0, 1});
        EvaluationResult result2 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 0, 1});

        double div = qStatistic.calcDiversity(Lists.newArrayList(result1, result2));
        assertTrue(div > 0.0);
        BigDecimal divDec = new BigDecimal(div, mc);
        BigDecimal divTest = new BigDecimal(0.923, mc);

        assertEquals(divDec, divTest);
    }

    @Test
    void DivNegativeWhenClassifiersAreNegativelyCorrelated() throws Exception {
        //        AbstractList<Instance> dummyInstances = createInstances(new int[]{1, 1, 0, 0});
        //
        //        // set up predictions such that for all instances, clf1 and clf2's correct predictions don't overlap
        //        Classifier[] clfs = new Classifier[]{
        //                new SometimesCorrectClassifier(new int[]{1, 0, 0, 1}), // clf1 classifies correctly 1st and 3rd
        //                new SometimesCorrectClassifier(new int[]{0, 1, 1, 0}), // clf2 classifies correctly 2nd and 4th
        //        };
        //
        //        double div = qStatistic.calcDiversity(clfs, dummyInstances);
        //        assertTrue(div < 0.0);
        //
        //        BigDecimal divDec = new BigDecimal(div, mc);
        //        BigDecimal divTest = new BigDecimal(-0.923, mc);
        //
        //        assertEquals(divDec, divTest);
        Integer[] actuals = new Integer[]{1, 1, 0, 0};

        EvaluationResult result1 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 0, 1});
        EvaluationResult result2 = CoolMocker.mockResult(actuals, new Integer[]{0, 1, 1, 0});

        double div = qStatistic.calcDiversity(Lists.newArrayList(result1, result2));
        assertTrue(div < 0.0);
        BigDecimal divDec = new BigDecimal(div, mc);
        BigDecimal divTest = new BigDecimal(-0.923, mc);

        assertEquals(divDec, divTest);
    }

    @Test
    void DivTakesAverageBetweenAllPairs() throws Exception {
        // in this test case we check that simply adding same classifiers in the ensemble does not change the calculated diversity,
        // since it is the average between all pairs of classifiers in the ensemble
        //        AbstractList<Instance> dummyInstances = createInstances(new int[]{1, 0, 1, 0});
        //
        //        List<Classifier> clfsList = new ArrayList<>();
        //        clfsList.add(new AlwaysIncorrectClassifier());
        //        clfsList.add(new AlwaysIncorrectClassifier());
        //
        //        double divBefore = qStatistic.calcDiversity(clfsList.toArray(new Classifier[0]), dummyInstances);
        //        assertEquals(divBefore, 0.8);
        //
        //        clfsList.add(new AlwaysIncorrectClassifier());
        //        double divAfter = qStatistic.calcDiversity(clfsList.toArray(new Classifier[0]), dummyInstances);
        //        assertTrue(Math.abs(divAfter - divBefore) < 1e-4);
        Integer[] actuals = new Integer[]{1, 0, 1, 0};

        EvaluationResult result1 = CoolMocker.mockResult(actuals, new Integer[]{0, 1, 0, 1});
        EvaluationResult result2 = CoolMocker.mockResult(actuals, new Integer[]{0, 1, 0, 1});

        double divBefore = qStatistic.calcDiversity(Lists.newArrayList(result1, result2));
        assertEquals(0.8, divBefore);

        EvaluationResult result3 = CoolMocker.mockResult(actuals, new Integer[]{0, 1, 0, 1});
        double divAfter = qStatistic.calcDiversity(Lists.newArrayList(result1, result2, result3));
        assertTrue(Math.abs(divAfter - divBefore) < 1e-4);
    }

    @Test
    void FitnessFunctionIsSameAsDiversity() {
        assertEquals(qStatistic.convertDiversityToFitness(0.3), 0.3);
    }

    @Test
    void FitnessFunctionIsNotMaxWhenDiversityIsZero() {
        assertEquals(qStatistic.convertDiversityToFitness(0), 0);
    }
}
