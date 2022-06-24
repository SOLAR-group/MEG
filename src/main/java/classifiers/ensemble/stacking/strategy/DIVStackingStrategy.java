package classifiers.ensemble.stacking.strategy;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public class DIVStackingStrategy extends AbstractSimpleStackingStrategy {
    public DIVStackingStrategy(List<Pair<String, Double>> descendingOrderDiv) {
        super(descendingOrderDiv);
    }

    @Override
    public String getName() {
        return "DIV";
    }
}
