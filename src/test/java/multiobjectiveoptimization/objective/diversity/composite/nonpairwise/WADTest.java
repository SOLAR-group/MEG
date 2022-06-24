package multiobjectiveoptimization.objective.diversity.composite.nonpairwise;

import com.google.common.collect.Lists;
import evaluation.EvaluationResult;
import multiobjectiveoptimization.objective.diversity.composite.CoolMocker;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class WADTest {

    WAD wad = new WAD();

    @Test
    void calcDiversity() {
        assertThrows(UnsupportedOperationException.class, () -> wad.calcDiversity(List.of(new EvaluationResult(null), new EvaluationResult(null))));
    }

    @Test
    void testCalcDiversityDisagreementZero() {
        Integer[] actuals = new Integer[]{0, 0, 0, 0};
        Integer[] preds = new Integer[]{1, 0, 0, 1}; // set up predictions such that N11 is 2 and N00 is 2

        EvaluationResult ensembleResult = CoolMocker.mockResult(actuals, preds);
        EvaluationResult result1 = CoolMocker.mockResult(actuals, preds);
        EvaluationResult result2 = CoolMocker.mockResult(actuals, preds);

        double div = wad.calcDiversity(ensembleResult, Lists.newArrayList(result1, result2));
        assertEquals(0.0, div);
    }

    @Test
    void testCalcDiversityPrecisionZero() {
        Integer[] actuals = new Integer[]{0, 0, 0, 0};

        EvaluationResult ensembleResult = CoolMocker.mockResult(actuals, actuals);
        EvaluationResult result1 = CoolMocker.mockResult(actuals, actuals);
        EvaluationResult result2 = CoolMocker.mockResult(actuals, actuals);

        when(ensembleResult.getPrecision()).thenReturn(0.8);

        double div = wad.calcDiversity(ensembleResult, Lists.newArrayList(result1, result2));
        assertEquals(0.0, div);
    }

    @Test
    void testCalcDiversityWorkingAsIntended() {
        Integer[] actuals = new Integer[]{0, 0, 0, 0};

        EvaluationResult ensembleResult = CoolMocker.mockResult(actuals, actuals);
        EvaluationResult result1 = CoolMocker.mockResult(actuals, new Integer[]{1, 0, 0, 1});
        EvaluationResult result2 = CoolMocker.mockResult(actuals, new Integer[]{0, 1, 1, 0});

        when(ensembleResult.getPrecision()).thenReturn(0.8);

        double div = wad.calcDiversity(ensembleResult, Lists.newArrayList(result1, result2));
        assertEquals(0.888888, div, 0.0001);
    }
}