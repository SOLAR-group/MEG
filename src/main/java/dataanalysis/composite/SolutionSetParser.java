package dataanalysis.composite;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.binarysolution.impl.DefaultBinarySolution;
import org.uma.jmetal.solution.compositesolution.CompositeSolution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.solution.integersolution.impl.DefaultIntegerSolution;
import org.uma.jmetal.util.binarySet.BinarySet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
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
public class SolutionSetParser {

    public static HashMap<Integer, Integer> count = new HashMap<>();

    /**
     * Number of objectives for the solutions
     */
    private int numberOfObjectives = 2;

    /**
     * Constructor
     */
    public SolutionSetParser() {
    }

    /**
     * Constructor
     *
     * @param numberOfObjectives the number of objectives of the problem
     */
    public SolutionSetParser(int numberOfObjectives) {
        this.numberOfObjectives = numberOfObjectives;
    }

    /**
     * Reads the content of a file and parses it into a CompositeSolution.
     *
     * @param filePath the path to the VAR file
     * @return a set of {@link CompositeSolution}
     */
    public List<CompositeSolution> parseSolutionSet(String filePath) {
        return parseSolutionSet(Paths.get(filePath));
    }

    /**
     * Reads the content of a file and parses it into a CompositeSolution.
     *
     * @param filePath the path to the VAR file
     * @return a set of {@link CompositeSolution}
     */
    public List<CompositeSolution> parseSolutionSet(Path filePath) {
        List<CompositeSolution> result = new ArrayList<>();
        try {
            // Reads all lines from the file
            List<String> lines = Files.readAllLines(filePath);
            if (lines.isEmpty()) {
                System.err.println("The file is empty: " + filePath);
            } else {
                // For each 4 lines, we parse the content
                // The first 3 lines are composed of: 1) BinarySolution; 2) DoubleSolution; and 3) IntegerSolution
                // The 4th line is a blank line
                for (int i = 0; i < lines.size(); i += 4) {
                    if (!lines.get(i).isBlank()) {
                        BinarySolution classifiers = parseBinarySolution(lines.get(i));
                        DoubleSolution parameters = parseDoubleSolution(lines.get(i + 1));
                        IntegerSolution strategy = parseIntegerSolution(lines.get(i + 2));
                        count.merge(strategy.getVariable(0), 1, Integer::sum);
                        CompositeSolution solution = new CompositeSolution(Lists.newArrayList(classifiers, parameters, strategy));
                        result.add(solution);
                    } else {
                        i -= 3;
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
     * Reads the binary variables of a line. Usually, the first line of the solution text representation.
     *
     * @param line the line to parse
     * @return the BinarySolution with the classifiers from the line
     * @throws IllegalArgumentException if the string is not formatted correctly, or the bit set has something that is
     *                                  not a bit
     */
    protected BinarySolution parseBinarySolution(String line) throws IllegalArgumentException {
        // Splits the line with space
        String[] split = line.split(" ");
        // The first content of the split is simply "Variables:", so we ignore it
        char[] chars = split[1].toCharArray();
        int numberOfBits = chars.length;
        BinarySet binarySet = new BinarySet(numberOfBits);
        // Parses the characters into boolean
        for (int i = 0; i < numberOfBits; i++) {
            if (chars[i] != '0' && chars[i] != '1') {
                throw new IllegalArgumentException("Found something that is not a bit.");
            }
            binarySet.set(i, '1' == chars[i]);
        }
        // This is needed for the BinarySolution
        List<Integer> bitsPerVariable = new ArrayList<>();
        bitsPerVariable.add(numberOfBits);
        // Creates the binary solution and sets the binary set in it
        BinarySolution binarySolution = new DefaultBinarySolution(bitsPerVariable, this.numberOfObjectives);
        binarySolution.setVariable(0, binarySet);
        return binarySolution;
    }

    /**
     * Reads the double variables of a line. Usually, the second line of the solution text representation.
     *
     * @param line the line to parse
     * @return the DoubleSolution with the parameters from the line
     */
    protected DoubleSolution parseDoubleSolution(String line) {
        // Splits the line with space
        String[] split = line.split(" ");
        List<Double> doubles = new ArrayList<>();
        // The first content of the split is simply ",Variables:", so we ignore it
        // Stops when it reaches the end of the list of parameters ("Objectives:")
        for (int i = 1; i < split.length && !split[i].equals("Objectives:"); i++) {
            String parameter = split[i];
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
     * Parses the integer variable of a line. Usually, the third line of the solution text representation.
     *
     * @param line the line to parse
     * @return the IntegerSolution with the strategy from the line
     */
    protected IntegerSolution parseIntegerSolution(String line) {
        // Splits the line with space
        String[] split = line.split(" ");
        // The first content of the split is simply ",Variables:", so we ignore it
        int variable = Integer.parseInt(split[1]);
        // We need a list of bounds for the IntegerSolution
        List<Pair<Integer, Integer>> bounds = new ArrayList<>();
        // This is our fixed bounds, but it will never be used here
        bounds.add(new ImmutablePair<>(0, 3));
        IntegerSolution integerSolution = new DefaultIntegerSolution(bounds, this.numberOfObjectives);
        integerSolution.setVariable(0, variable);
        return integerSolution;
    }

    /**
     * Reads the content of a FUN file, parses the fitness, and save it into the given CompositeSolutions.
     *
     * @param solutions the list of solutions to save the results in
     * @param filePath  the path to the VAR file
     */
    public void parseFun(List<CompositeSolution> solutions, String filePath) {
        this.parseFun(solutions, Paths.get(filePath));
    }

    /**
     * Reads the content of a FUN file, parses the fitness, and save it into the given CompositeSolutions.
     *
     * @param solutions the list of solutions to save the results in
     * @param filePath  the path to the VAR file
     */
    public void parseFun(List<CompositeSolution> solutions, Path filePath) {
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
                    CompositeSolution solution = solutions.get(i);
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

    public static void main(String[] args) throws IOException {
        File rootFile = new File("C:\\Users\\Giovani\\OneDrive - University College London\\experiments\\MOEA-DP\\ICSE-2022-Artefact\\results\\output-DISAGREEMENT-MCC");
        SolutionSetParser parser = new SolutionSetParser(2);
        Files.walk(rootFile.toPath().toAbsolutePath())
                .filter(file -> file.getFileName().toString().startsWith("VAR"))
                .forEach(parser::parseSolutionSet);

        System.out.println("Count:");
        System.out.println(SolutionSetParser.count.toString());
    }
}
