package util;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Utils {
    public static <K, V extends Comparable<? super V>> List<Map.Entry<K, V>> getEntrySetListSortedByValDescending(Map<K, V> map) {
        return map.entrySet().stream().sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue())).collect(Collectors.toList());
    }

    /**
     * Computes the average of a double list.
     *
     * @param doubles the list of doubles
     *
     * @return the average, or {@link Double#NaN} if the list is empty
     */
    @NotNull
    public static Double computeDoubleAverage(List<Double> doubles) {
        return doubles.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(Double.NaN);
    }

    public static boolean directoryIsInvalid(Path directoryPath) {
        return (Files.exists(directoryPath) && !Files.isDirectory(directoryPath))
                || (!Files.exists(directoryPath) && !directoryPath.toFile().mkdirs());
    }

    public static boolean fileIsInvalid(Path filePath) {
        return !Files.exists(filePath)
                || Files.isDirectory(filePath)
                || !Files.isReadable(filePath);
    }
}
