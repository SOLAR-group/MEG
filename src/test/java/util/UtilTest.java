package util;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class UtilTest {
    @Test
    void getEntrySetSortedByVal() {
        Map<Integer, Double> map = new HashMap<>();
        map.put(0, 1.23);
        map.put(1, 2.2);
        map.put(2, 2.3);
        map.put(3, 10.4);
        map.put(4, -100d);
        map.put(5, 0d);
        map.put(6, 2.2);

        Pair<Integer, Double>[] expected = new Pair[]{
                new ImmutablePair(3, 10.4),
                new ImmutablePair(2, 2.3),
                new ImmutablePair(1, 2.2),
                new ImmutablePair(6, 2.2),
                new ImmutablePair(0, 1.23),
                new ImmutablePair(5, 0d),
                new ImmutablePair(4, -100d),
        };

        List<Map.Entry<Integer, Double>> res = Utils.getEntrySetListSortedByValDescending(map);

        for (int i = 0; i < res.size(); i++) {
            Map.Entry<Integer, Double> entry = res.get(i);
            assertEquals(entry.getKey(), expected[i].getLeft());
            assertEquals(entry.getValue(), expected[i].getRight());
        }
    }

    @Test
    void computeAverageDoubleNaNs() {
        double result = Utils.computeDoubleAverage(Arrays.asList(Double.NaN, Double.NaN, Double.NaN));
        assertEquals(result, Double.NaN);

        double result2 = Utils.computeDoubleAverage(Arrays.asList(Double.NaN, 1d,2d));
        assertEquals(result2, Double.NaN);

        double result3 = Utils.computeDoubleAverage(Arrays.asList(Double.NaN));
        assertEquals(result3, Double.NaN);

        double result4 = Utils.computeDoubleAverage(new ArrayList<>());
        assertEquals(result4, Double.NaN);

        double result5 = Utils.computeDoubleAverage(Arrays.asList(Double.NEGATIVE_INFINITY, Double.NaN));
        assertEquals(result5, Double.NaN);

        double result6 = Utils.computeDoubleAverage(Arrays.asList(Double.MAX_VALUE));
        assertEquals(result6, Double.MAX_VALUE);

        double result7 = Utils.computeDoubleAverage(Arrays.asList(Double.MAX_VALUE, Double.MAX_VALUE));
        assertEquals(result7, Double.POSITIVE_INFINITY);
    }
}
