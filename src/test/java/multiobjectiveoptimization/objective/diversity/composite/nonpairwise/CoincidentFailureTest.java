package multiobjectiveoptimization.objective.diversity.composite.nonpairwise;

import com.google.common.collect.Lists;
import evaluation.EvaluationResult;
import multiobjectiveoptimization.objective.diversity.composite.CoolMocker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CoincidentFailureTest {
    CoincidentFailure cfd = new CoincidentFailure();

    @Test
    void DivIsZeroWhenAllClassifiersAlwaysCorrect() throws Exception {
        //        AbstractList<Instance> dummyInstances = createInstances(new int[] {1,0,1});
        //
        //        Classifier[] clfs = new Classifier[] {
        //                new AlwaysCorrectClassifier(),
        //                new AlwaysCorrectClassifier(),
        //                new AlwaysCorrectClassifier(),
        //        };
        //
        //        double div = cfd.calcDiversity(clfs, dummyInstances);
        //        assertEquals(div, 0.0);

        Integer[] actuals = new Integer[]{1, 0, 1};

        EvaluationResult result1 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 1});
        EvaluationResult result2 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 1});
        EvaluationResult result3 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 1});

        double div = cfd.calcDiversity(Lists.newArrayList(result1, result2, result3));
        assertEquals(0.0, div);
    }

    @Test
    void DivIsZeroWhenAllClassifiersAlwaysIncorrect() throws Exception {
        //        AbstractList<Instance> dummyInstances = createInstances(new int[] {1,0,1});
        //
        //        Classifier[] clfs = new Classifier[] {
        //            new AlwaysIncorrectClassifier(),
        //            new AlwaysIncorrectClassifier(),
        //            new AlwaysIncorrectClassifier(),
        //        };
        //
        //        double div = cfd.calcDiversity(clfs, dummyInstances);
        //        assertEquals(div, 0.0);

        Integer[] actuals = new Integer[]{1, 0, 1};

        EvaluationResult result1 = CoolMocker.mockResult(actuals, new Integer[]{0, 1, 0});
        EvaluationResult result2 = CoolMocker.mockResult(actuals, new Integer[]{0, 1, 0});
        EvaluationResult result3 = CoolMocker.mockResult(actuals, new Integer[]{0, 1, 0});

        double div = cfd.calcDiversity(Lists.newArrayList(result1, result2, result3));
        assertEquals(0.0, div);
    }

    @Test
    void DivIsZeroWhenAllClassifiersSimultaneouslyEitherWrongOrCorrect() throws Exception {
        //        AbstractList<Instance> dummyInstances = createInstances(new int[] {1,0,1,1,0});
        //
        //        int[] preds = new int[] {1,1,0,1,1}; // correct, wrong, wrong, correct, wrong
        //        Classifier[] clfs = new Classifier[] {
        //            new SometimesCorrectClassifier(preds),
        //            new SometimesCorrectClassifier(preds),
        //            new SometimesCorrectClassifier(preds),
        //            new SometimesCorrectClassifier(preds),
        //            new SometimesCorrectClassifier(preds),
        //        };
        //
        //        double div = cfd.calcDiversity(clfs, dummyInstances);
        //        assertEquals(div, 0.0);

        Integer[] actuals = new Integer[]{1, 1, 0, 1, 1};

        EvaluationResult result1 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 1, 1, 0});
        EvaluationResult result2 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 1, 1, 0});
        EvaluationResult result3 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 1, 1, 0});
        EvaluationResult result4 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 1, 1, 0});
        EvaluationResult result5 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 1, 1, 0});

        double div = cfd.calcDiversity(Lists.newArrayList(result1, result2, result3, result4, result5));
        assertEquals(0.0, div);
    }

    @Test
    void DivIsOneWhenAtMostOneClassifierFailsOnAnInstance() throws Exception {
        //        AbstractList<Instance> dummyInstances = createInstances(new int[] {1,0,1,1,0});
        //
        //        // first classifier makes mistake on first instance and so on..
        //        Classifier[] clfs = new Classifier[] {
        //            new SometimesCorrectClassifier(new int[] {0,0,1,1,0}),
        //            new SometimesCorrectClassifier(new int[] {1,1,1,1,0}),
        //            new SometimesCorrectClassifier(new int[] {1,0,0,1,0}),
        //            new SometimesCorrectClassifier(new int[] {1,0,1,0,0}),
        //            new SometimesCorrectClassifier(new int[] {1,0,1,1,1}),
        //        };
        //
        //        double div = cfd.calcDiversity(clfs, dummyInstances);
        //        assertEquals(div, 1.0);
        Integer[] actuals = new Integer[]{1, 0, 1, 1, 0};

        EvaluationResult result1 = CoolMocker.mockResult(actuals, new Integer[]{0, 0, 1, 1, 0});
        EvaluationResult result2 = CoolMocker.mockResult(actuals, new Integer[]{1, 1, 1, 1, 0});
        EvaluationResult result3 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 0, 1, 0});
        EvaluationResult result4 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 1, 0, 0});
        EvaluationResult result5 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 1, 1, 1});

        double div = cfd.calcDiversity(Lists.newArrayList(result1, result2, result3, result4, result5));
        assertEquals(1.0, div);
    }

    @Test
    void DivDecreasesAsCoincidentMistakesIncreases() throws Exception {
        //        AbstractList<Instance> dummyInstances = createInstances(new int[] {1,0,1});
        //
        //        // all test instances fail on exactly 2/5 classifiers only
        //        Classifier[] clfs1 = new Classifier[] {
        //            new SometimesCorrectClassifier(new int[] {1,1,1}),
        //            new SometimesCorrectClassifier(new int[] {1,1,1}),
        //            new SometimesCorrectClassifier(new int[] {0,0,1}),
        //            new SometimesCorrectClassifier(new int[] {0,0,0}),
        //            new SometimesCorrectClassifier(new int[] {1,0,0}),
        //        };
        //
        //        double div1 = cfd.calcDiversity(clfs1, dummyInstances);
        //        assertTrue(div1 > 0.5);
        //
        //        // now all test instances fail on exactly 4/5 classifiers
        //        Classifier[] clfs2 = new Classifier[] {
        //            new SometimesCorrectClassifier(new int[] {1,0,0}),
        //            new SometimesCorrectClassifier(new int[] {0,1,0}),
        //            new SometimesCorrectClassifier(new int[] {0,1,1}),
        //            new SometimesCorrectClassifier(new int[] {0,1,0}),
        //            new SometimesCorrectClassifier(new int[] {0,1,0}),
        //        };
        //
        //        double div2 = cfd.calcDiversity(clfs2, dummyInstances);
        //        assertTrue(div2 < 0.5);
        //
        //        assertTrue(div2 < div1);

        Integer[] actuals = new Integer[]{1, 0, 1};

        EvaluationResult result1 = CoolMocker.mockResult(actuals, new Integer[]{1, 1, 1});
        EvaluationResult result2 = CoolMocker.mockResult(actuals, new Integer[]{1, 1, 1});
        EvaluationResult result3 = CoolMocker.mockResult(actuals, new Integer[]{0, 0, 1});
        EvaluationResult result4 = CoolMocker.mockResult(actuals, new Integer[]{0, 0, 0});
        EvaluationResult result5 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 0});

        double div1 = cfd.calcDiversity(Lists.newArrayList(result1, result2, result3, result4, result5));
        assertTrue(div1 > 0.5);

        result1 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 0});
        result2 = CoolMocker.mockResult(actuals, new Integer[]{0, 1, 0});
        result3 = CoolMocker.mockResult(actuals, new Integer[]{0, 1, 1});
        result4 = CoolMocker.mockResult(actuals, new Integer[]{0, 1, 0});
        result5 = CoolMocker.mockResult(actuals, new Integer[]{0, 1, 0});

        double div2 = cfd.calcDiversity(Lists.newArrayList(result1, result2, result3, result4, result5));
        assertTrue(div2 < 0.5);
    }

    @Test
    void FitnessFunctionIsSameAsDiversity() {
        assertEquals(cfd.convertDiversityToFitness(0.2), 5);
    }

    @Test
    void FitnessFunctionIsMaxWhenDivIsZero() {
        assertEquals(cfd.convertDiversityToFitness(0), Double.MAX_VALUE);
    }
}
