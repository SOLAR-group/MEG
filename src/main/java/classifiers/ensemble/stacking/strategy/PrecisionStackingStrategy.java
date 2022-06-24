package classifiers.ensemble.stacking.strategy;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public class PrecisionStackingStrategy extends AbstractSimpleStackingStrategy {
    public PrecisionStackingStrategy(List<Pair<String, Double>> descendingOrderPrecision) {
        super(descendingOrderPrecision);
    }

    @Override
    public String getName() {
        return "PRECISION";
    }
}
