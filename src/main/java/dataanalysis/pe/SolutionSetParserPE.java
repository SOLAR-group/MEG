package dataanalysis.pe;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.solution.compositesolution.CompositeSolution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A class that parses a composite solution from a VAR file.
 * <p>
 * ATTENTION: this class is highly coupled with our representation. Hence, if the representation changes, then this
 * class should be updated too.
 *
 * @see #parseSolutionSet(String)
 * @see #parseSolutionSet(Path)
 */
public class SolutionSetParserPE {

    /**
     * Number of objectives for the solutions
     */
    private int numberOfObjectives = 2;

    /**
     * Constructor
     */
    public SolutionSetParserPE() {
    }

    /**
     * Constructor
     *
     * @param numberOfObjectives the number of objectives of the problem
     */
    public SolutionSetParserPE(int numberOfObjectives) {
        this.numberOfObjectives = numberOfObjectives;
    }

    /**
     * Reads the content of a file and parses it into a CompositeSolution.
     *
     * @param filePath the path to the VAR file
     *
     * @return a set of {@link CompositeSolution}
     */
    public List<DoubleSolution> parseSolutionSet(String filePath) {
        return parseSolutionSet(Paths.get(filePath));
    }

    /**
     * Reads the content of a file and parses it into a DoubleSolution.
     *
     * @param filePath the path to the VAR file
     *
     * @return a set of {@link CompositeSolution}
     */
    public List<DoubleSolution> parseSolutionSet(Path filePath) {
        List<DoubleSolution> result = new ArrayList<>();
        try {
            // Reads all lines from the file
            List<String> lines = Files.readAllLines(filePath);
            if (lines.isEmpty()) {
                System.err.println("The file is empty: " + filePath);
            } else {
                for (String line : lines) {
                    if (!line.isBlank()) {
                        DoubleSolution solution = parseDoubleSolution(line);
                        result.add(solution);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Could not open the file: " + filePath);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Ops! Something went wrong while parsing the file: " + filePath);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Reads the double variables of a line.
     *
     * @param line the line to parse
     *
     * @return the DoubleSolution with the parameters from the line
     */
    protected DoubleSolution parseDoubleSolution(String line) {
        // Splits the line with space
        String[] split = line.split(",");
        List<Double> doubles = new ArrayList<>();
        // Stops when it reaches the end of the list of parameters
        for (String parameter : split) {
            double doubleValue = Double.parseDouble(parameter);
            doubles.add(doubleValue);
        }
        // We need a list of bounds for the DoubleSolution
        final List<Pair<Double, Double>> bounds = new ArrayList<>(doubles.size());
        doubles.forEach(value -> bounds.add(new ImmutablePair<>(value, value)));
        DoubleSolution doubleSolution = new DefaultDoubleSolution(bounds, this.numberOfObjectives);
        // Sets the variables
        for (int i = 0; i < doubles.size(); i++) {
            Double doubleValue = doubles.get(i);
            doubleSolution.setVariable(i, doubleValue);
        }
        return doubleSolution;
    }

    /**
     * Reads the content of a FUN file, parses the fitness, and save it into the given DoubleSolution.
     *
     * @param solutions the list of solutions to save the results in
     * @param filePath  the path to the VAR file
     */
    public void parseFun(List<DoubleSolution> solutions, String filePath) {
        this.parseFun(solutions, Paths.get(filePath));
    }

    /**
     * Reads the content of a FUN file, parses the fitness, and save it into the given DoubleSolution.
     *
     * @param solutions the list of solutions to save the results in
     * @param filePath  the path to the VAR file
     */
    public void parseFun(List<DoubleSolution> solutions, Path filePath) {
        try {
            // Reads all lines from the file
            List<String> lines = Files.readAllLines(filePath);
            if (lines.isEmpty()) {
                System.err.println("The file is empty: " + filePath);
            } else {
                List<ImmutablePair<Double, Double>> funs = lines.stream()
                        .filter(line -> !line.trim().isBlank())
                        .map(line -> {
                            line = line.trim().replaceAll("\\r|\\n", "");
                            String[] split = line.split(",");
                            return new ImmutablePair<>(Double.valueOf(split[0]), Double.valueOf(split[1]));
                        })
                        .collect(Collectors.toList());
                if (funs.size() != solutions.size()) {
                    throw new Exception("Ops! The number of solutions in the VAR and FUN files is different.");
                }
                for (int i = 0, solutionsSize = solutions.size(); i < solutionsSize; i++) {
                    DoubleSolution solution = solutions.get(i);
                    ImmutablePair<Double, Double> fun = funs.get(i);
                    solution.setObjective(0, fun.getLeft());
                    solution.setObjective(1, fun.getRight());
                }
            }
        } catch (IOException e) {
            System.err.println("Could not open the file: " + filePath);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Ops! Something went wrong while parsing the file: " + filePath);
            e.printStackTrace();
        }
    }
}
