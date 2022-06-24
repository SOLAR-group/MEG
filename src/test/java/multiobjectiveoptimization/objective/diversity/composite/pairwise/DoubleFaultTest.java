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

public class DoubleFaultTest {
    protected MathContext mc = new MathContext(3, RoundingMode.HALF_UP);
    DoubleFault doubleFault = new DoubleFault();

    @Test
    void DivIsZeroWhenNoTwoClassifiersMakeSameErrors() throws Exception {
        //        AbstractList<Instance> dummyInstances = createInstances(new int[] {1,1,0,1,0});
        //
        //        Classifier[] clfs = new Classifier[] {
        //            new SometimesCorrectClassifier(new int[] {1,1,0,0,1}), // wrong on last two
        //            new SometimesCorrectClassifier(new int[] {1,1,1,1,0}), // wrong on 3rd
        //            new SometimesCorrectClassifier(new int[] {0,0,0,1,0}), // wrong on first two
        //        };
        //
        //        double div = doubleFault.calcDiversity(clfs, dummyInstances);
        //        assertEquals(div, 0.0);
        Integer[] actuals = new Integer[]{1, 1, 0, 1, 0};

        EvaluationResult result1 = CoolMocker.mockResult(actuals, new Integer[]{1, 1, 0, 0, 1});
        EvaluationResult result2 = CoolMocker.mockResult(actuals, new Integer[]{1, 1, 1, 1, 0});
        EvaluationResult result3 = CoolMocker.mockResult(actuals, new Integer[]{0, 0, 0, 1, 0});

        double div = doubleFault.calcDiversity(Lists.newArrayList(result1, result2, result3));
        assertEquals(0.0, div);
    }

    @Test
    void DivIsOneWhenAllPairsOfClassifiersPredictEverythingWrongly() throws Exception {
        //        AbstractList<Instance> dummyInstances = createInstances(new int[]{1, 1, 0, 1, 0});
        //
        //        Classifier[] clfs = new Classifier[]{
        //                new AlwaysIncorrectClassifier(),
        //                new AlwaysIncorrectClassifier(),
        //                new AlwaysIncorrectClassifier(),
        //        };
        //
        //        double div = doubleFault.calcDiversity(clfs, dummyInstances);
        //        assertEquals(div, 1.0);
        Integer[] actuals = new Integer[]{1, 1, 0, 1, 0};

        EvaluationResult result1 = CoolMocker.mockResult(actuals, new Integer[]{0, 0, 1, 0, 1});
        EvaluationResult result2 = CoolMocker.mockResult(actuals, new Integer[]{0, 0, 1, 0, 1});
        EvaluationResult result3 = CoolMocker.mockResult(actuals, new Integer[]{0, 0, 1, 0, 1});

        double div = doubleFault.calcDiversity(Lists.newArrayList(result1, result2, result3));
        assertEquals(1.0, div);
    }

    @Test
    void DivIsOneHalfWhenAllPairsMakeSameMistakesInHalfOfInstances() throws Exception {
        //        AbstractList<Instance> dummyInstances = createInstances(new int[]{1, 1, 0, 1});
        //
        //        Classifier[] clfs = new Classifier[]{
        //                new SometimesCorrectClassifier(new int[]{0, 0, 0, 1}), // first half wrong
        //                new SometimesCorrectClassifier(new int[]{0, 0, 0, 1}), // same
        //                new SometimesCorrectClassifier(new int[]{0, 0, 0, 1}), // same
        //        };
        //
        //        double div = doubleFault.calcDiversity(clfs, dummyInstances);
        //        assertEquals(div, 0.5);
        Integer[] actuals = new Integer[]{1, 1, 0, 1};

        EvaluationResult result1 = CoolMocker.mockResult(actuals, new Integer[]{0, 0, 0, 1});
        EvaluationResult result2 = CoolMocker.mockResult(actuals, new Integer[]{0, 0, 0, 1});
        EvaluationResult result3 = CoolMocker.mockResult(actuals, new Integer[]{0, 0, 0, 1});

        double div = doubleFault.calcDiversity(Lists.newArrayList(result1, result2, result3));
        assertEquals(0.5, div);
    }

    @Test
    void DivWorksAsExpectedInOtherCases() throws Exception {
        //        AbstractList<Instance> dummyInstances = createInstances(new int[]{1, 0, 1, 0, 0});
        //
        //        Classifier[] clfs1 = new Classifier[]{
        //                new SometimesCorrectClassifier(new int[]{0, 1, 1, 1, 1}),
        //                new SometimesCorrectClassifier(new int[]{0, 1, 0, 0, 1}),
        //                new SometimesCorrectClassifier(new int[]{1, 0, 0, 1, 1}),
        //        };
        //
        //        double div = doubleFault.calcDiversity(clfs1, dummyInstances);
        //
        //        BigDecimal divDec = new BigDecimal(div, mc);
        //        BigDecimal divTest = new BigDecimal(0.467, mc);
        //
        //        assertEquals(divDec, divTest);
        //
        //        Classifier[] clfs2 = new Classifier[]{
        //                new SometimesCorrectClassifier(new int[]{1, 0, 1, 0, 1}),
        //                new SometimesCorrectClassifier(new int[]{0, 0, 1, 1, 1}),
        //                new SometimesCorrectClassifier(new int[]{0, 1, 0, 1, 0}),
        //        };
        //
        //        double div2 = doubleFault.calcDiversity(clfs2, dummyInstances);
        //        assertTrue(Math.abs(div2 - 0.2) < 1e-4);

        Integer[] actuals = new Integer[]{1, 0, 1, 0, 0};

        EvaluationResult result1 = CoolMocker.mockResult(actuals, new Integer[]{0, 1, 1, 1, 1});
        EvaluationResult result2 = CoolMocker.mockResult(actuals, new Integer[]{0, 1, 0, 0, 1});
        EvaluationResult result3 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 0, 1, 1});

        double div = doubleFault.calcDiversity(Lists.newArrayList(result1, result2, result3));
        BigDecimal divDec = new BigDecimal(div, mc);
        BigDecimal divTest = new BigDecimal(0.467, mc);
        assertEquals(divDec, divTest);

        result1 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 1, 0, 1});
        result2 = CoolMocker.mockResult(actuals, new Integer[]{0, 0, 1, 1, 1});
        result3 = CoolMocker.mockResult(actuals, new Integer[]{0, 1, 0, 1, 0});

        div = doubleFault.calcDiversity(Lists.newArrayList(result1, result2, result3));
        assertTrue(Math.abs(div - 0.2) < 1e-4);
    }

    @Test
    void FitnessFunctionIsSameAsDiversity() {
        assertEquals(doubleFault.convertDiversityToFitness(0.3), 0.3);
    }

    @Test
    void FitnessFunctionIsNotMaxWhenDiversityIsZero() {
        assertEquals(doubleFault.convertDiversityToFitness(0), 0);
    }
}
