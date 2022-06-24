package classifiers.ensemble.stacking.strategy;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public class MCCStackingStrategy extends AbstractSimpleStackingStrategy {
    public MCCStackingStrategy(List<Pair<String, Double>> descendingOrderMCC) {
        super(descendingOrderMCC);
    }

    @Override
    public String getName() {
        return "MCC";
    }
}
