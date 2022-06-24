package multiobjectiveoptimization.objective.diversity.composite.pairwise;

import com.google.common.collect.Lists;
import evaluation.EvaluationResult;
import multiobjectiveoptimization.objective.diversity.composite.CoolMocker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DisagreementTest {

    Disagreement dis = new Disagreement();

    @Test
    void DivIsZeroWhenAllClassifierPredictionsLineUp() {
        // div should be zero when for each pair of classifiers: either they make same mistakes or same correct predictions
        Integer[] actuals = new Integer[]{0, 0, 0, 0};
        Integer[] preds = new Integer[]{1, 0, 0, 1}; // set up predictions such that N11 is 2 and N00 is 2

        EvaluationResult result = CoolMocker.mockResult(actuals, preds);

        double div = dis.calcDiversity(Lists.newArrayList(result, result));
        assertEquals(0.0, div);
    }

    @Test
    void DivIsOneWhenAllClassifierPredictionsNeverLineUp() {
        Integer[] actuals = new Integer[]{0, 0, 0, 0};

        EvaluationResult result = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 0, 1});
        EvaluationResult result2 = CoolMocker.mockResult(actuals, new Integer[]{0, 1, 1, 0});

        double div = dis.calcDiversity(Lists.newArrayList(result, result2));
        assertEquals(1.0, div);
    }

    @Test
    void DivIsOneHalfWhenAllClassifierPredictionsAgreeOnHalfOfInstances() {
        // In other words, div = 0.5 when classifiers are pairwise statistically independent
        Integer[] actuals = new Integer[]{0, 0, 0, 0};

        EvaluationResult result = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 1, 1});
        EvaluationResult result2 = CoolMocker.mockResult(actuals, new Integer[]{0, 1, 1, 1});

        double div = dis.calcDiversity(Lists.newArrayList(result, result2));
        assertEquals(0.5, div);
    }

    @Test
    void DivWorksAsExpectedInOtherCases() throws Exception {
        Integer[] actuals = new Integer[]{0, 0, 0, 0};

        EvaluationResult result = CoolMocker.mockResult(actuals, new Integer[]{0, 1, 1, 0});
        EvaluationResult result2 = CoolMocker.mockResult(actuals, new Integer[]{1, 1, 0, 1});
        EvaluationResult result3 = CoolMocker.mockResult(actuals, new Integer[]{1, 1, 0, 0});

        double div = dis.calcDiversity(Lists.newArrayList(result, result2, result3));
        assertEquals(0.5, div);

        result = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 1, 0});
        result2 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 0, 0});
        result3 = CoolMocker.mockResult(actuals, new Integer[]{0, 1, 0, 0});

        div = dis.calcDiversity(Lists.newArrayList(result, result2, result3));
        assertEquals(0.5, div);

        result = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 0, 0});
        result2 = CoolMocker.mockResult(actuals, new Integer[]{1, 1, 1, 1});
        result3 = CoolMocker.mockResult(actuals, new Integer[]{0, 1, 1, 0});

        div = dis.calcDiversity(Lists.newArrayList(result, result2, result3));
        assertEquals(2 / (double) 3, div);
    }

    @Test
    void FitnessFunctionIsSameAsDiversity() {
        assertEquals(dis.convertDiversityToFitness(0.2), 5);
    }

    @Test
    void FitnessFunctionIsMaxWhenDivIsZero() {
        assertEquals(dis.convertDiversityToFitness(0), Double.MAX_VALUE);
    }
}
