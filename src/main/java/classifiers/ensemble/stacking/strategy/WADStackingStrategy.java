package classifiers.ensemble.stacking.strategy;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class WADStackingStrategy extends AbstractSimpleStackingStrategy {
    public WADStackingStrategy(List<Pair<String, Double>> descendingOrderWAD) {
        super(descendingOrderWAD);
    }

    @Override
    public String getName() {
        return "WAD";
    }
}
