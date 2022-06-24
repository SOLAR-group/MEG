package dataanalysis.composite;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.compositesolution.CompositeSolution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.binarySet.BinarySet;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SolutionSetParserTest {

    @Test
    void testParseSolutionSet() {
        SolutionSetParser parser = new SolutionSetParser();
        Collection<CompositeSolution> compositeSolutions = parser.parseSolutionSet("src/test/resources/parser/VAR.csv");
        assertEquals(8, compositeSolutions.size());
    }

    @Test
    void testParseSolutionSetIncorrect() {
        SolutionSetParser parser = new SolutionSetParser();
        Collection<CompositeSolution> compositeSolutions = parser.parseSolutionSet("not/a/valid/path.csv");
        assertEquals(0, compositeSolutions.size());
    }

    @Test
    void testParseSolutionSetIncorrect2() {
        SolutionSetParser parser = new SolutionSetParser();
        Collection<CompositeSolution> compositeSolutions = parser.parseSolutionSet("src/test/resources/parser/VAR_INVALID.csv");
        assertEquals(0, compositeSolutions.size());
    }

    @Test
    void testParseSolutionSetIncorrect3() {
        SolutionSetParser parser = new SolutionSetParser();
        Collection<CompositeSolution> compositeSolutions = parser.parseSolutionSet("src/test/resources/parser/VAR_EMPTY.csv");
        assertEquals(0, compositeSolutions.size());
    }

    @Test
    void parseBinarySolutionCorrect() {
        SolutionSetParser parser = new SolutionSetParser();
        BinarySolution binarySolution = parser.parseBinarySolution("Variables: 01101011011001 Objectives: 0.0 0.0 Constraints: \tAlgorithmAttributes: {}");
        BinarySet binarySet = binarySolution.getVariables().get(0);
        assertEquals("01101011011001", binarySet.toString());
    }

    @Test
    void parseBinarySolutionIncorrect() {
        SolutionSetParser parser = new SolutionSetParser();
        assertThrows(IllegalArgumentException.class, () -> parser.parseBinarySolution("Not a valid string"));
        assertThrows(IllegalArgumentException.class, () -> parser.parseBinarySolution("Variables:01101011011001 Objectives: 0.0 0.0 Constraints: \tAlgorithmAttributes: {}"));
    }

    @Test
    void parseDoubleSolutionCorrect() {
        SolutionSetParser parser = new SolutionSetParser();
        DoubleSolution doubleSolution = parser.parseDoubleSolution(",Variables: 0.814354582064323 1.330937001800833 9.476687002362809 1.4440256764073665 15.818547935992543 46.01234383063028 25.83538564038052 16.83184483792354 23.414672094310927 0.2816619271466199 0.2601889494263248 0.03347637751841191 0.07386031981260512 0.11078918708358716 Objectives: 0.0 0.0 Constraints: \tAlgorithmAttributes: {}");
        List<Double> expected = Lists.newArrayList(0.814354582064323,
                1.330937001800833,
                9.476687002362809,
                1.4440256764073665,
                15.818547935992543,
                46.01234383063028,
                25.83538564038052,
                16.83184483792354,
                23.414672094310927,
                0.2816619271466199,
                0.2601889494263248,
                0.03347637751841191,
                0.07386031981260512,
                0.11078918708358716);
        assertIterableEquals(expected, doubleSolution.getVariables());
    }

    @Test
    void parseDoubleSolutionIncorrect() {
        SolutionSetParser parser = new SolutionSetParser();
        assertThrows(NumberFormatException.class, () -> parser.parseDoubleSolution("Not a valid string"));
        assertThrows(NumberFormatException.class, () -> parser.parseDoubleSolution("Variables: 0.814354582064323 1.330937001800833 9.476687002362809 1.4440256764073665 15.818547935992543 46.01234383063028 25.83538564038052 16.83184483792354 23.414672094310927 0.2816619271466199 0.2601889494263248 0.03347637751841191 0.07386031981260512 0.11078918708358716 0.0 0.0 Constraints: \tAlgorithmAttributes: {}"));
    }

    @Test
    void parseIntegerSolutionCorrect() {
        SolutionSetParser parser = new SolutionSetParser();
        IntegerSolution doubleSolution = parser.parseIntegerSolution(",Variables: 0 Objectives: 0.0 0.0 Constraints: \tAlgorithmAttributes: {}");
        List<Integer> expected = Lists.newArrayList(0);
        assertIterableEquals(expected, doubleSolution.getVariables());
    }

    @Test
    void parseIntegerSolutionIncorrect() {
        SolutionSetParser parser = new SolutionSetParser();
        assertThrows(NumberFormatException.class, () -> parser.parseIntegerSolution("Not a valid string"));
        assertThrows(NumberFormatException.class, () -> parser.parseIntegerSolution(",Variables: a 0.0 0.0 Constraints: \tAlgorithmAttributes: {}"));
    }

    @Test
    void testParseFun() {
        SolutionSetParser parser = new SolutionSetParser();
        List<CompositeSolution> compositeSolutions = parser.parseSolutionSet("src/test/resources/parser/VAR.csv");
        assertEquals(8, compositeSolutions.size());
        parser.parseFun(compositeSolutions, "src/test/resources/parser/FUN.csv");
        assertArrayEquals(new double[]{2.0, 0.56}, compositeSolutions.get(0).getObjectives());
        assertArrayEquals(new double[]{8.0, 0.20}, compositeSolutions.get(1).getObjectives());
        assertArrayEquals(new double[]{2.0, 0.56}, compositeSolutions.get(2).getObjectives());
        assertArrayEquals(new double[]{8.0, 0.32}, compositeSolutions.get(3).getObjectives());
        assertArrayEquals(new double[]{6.0, 0.35}, compositeSolutions.get(4).getObjectives());
        assertArrayEquals(new double[]{2.0, 0.55}, compositeSolutions.get(5).getObjectives());
        assertArrayEquals(new double[]{3.0, 0.52}, compositeSolutions.get(6).getObjectives());
        assertArrayEquals(new double[]{2.0, 0.52}, compositeSolutions.get(7).getObjectives());
    }

    @Test
    void testParseFunDifferentSizes() {
        SolutionSetParser parser = new SolutionSetParser();
        List<CompositeSolution> compositeSolutions = parser.parseSolutionSet("src/test/resources/parser/VAR.csv");
        assertEquals(8, compositeSolutions.size());
        parser.parseFun(compositeSolutions, "src/test/resources/parser/FUN_2.csv");
        assertArrayEquals(new double[]{0.0, 0.0}, compositeSolutions.get(0).getObjectives());
        assertArrayEquals(new double[]{0.0, 0.0}, compositeSolutions.get(1).getObjectives());
        assertArrayEquals(new double[]{0.0, 0.0}, compositeSolutions.get(2).getObjectives());
        assertArrayEquals(new double[]{0.0, 0.0}, compositeSolutions.get(3).getObjectives());
        assertArrayEquals(new double[]{0.0, 0.0}, compositeSolutions.get(4).getObjectives());
        assertArrayEquals(new double[]{0.0, 0.0}, compositeSolutions.get(5).getObjectives());
        assertArrayEquals(new double[]{0.0, 0.0}, compositeSolutions.get(6).getObjectives());
        assertArrayEquals(new double[]{0.0, 0.0}, compositeSolutions.get(7).getObjectives());
    }

    @Test
    void testParseFunInvalidPath() {
        SolutionSetParser parser = new SolutionSetParser();
        List<CompositeSolution> compositeSolutions = parser.parseSolutionSet("src/test/resources/parser/VAR.csv");
        assertEquals(8, compositeSolutions.size());
        parser.parseFun(compositeSolutions, "not/a/valid/path.csv");
        assertArrayEquals(new double[]{0.0, 0.0}, compositeSolutions.get(0).getObjectives());
        assertArrayEquals(new double[]{0.0, 0.0}, compositeSolutions.get(1).getObjectives());
        assertArrayEquals(new double[]{0.0, 0.0}, compositeSolutions.get(2).getObjectives());
        assertArrayEquals(new double[]{0.0, 0.0}, compositeSolutions.get(3).getObjectives());
        assertArrayEquals(new double[]{0.0, 0.0}, compositeSolutions.get(4).getObjectives());
        assertArrayEquals(new double[]{0.0, 0.0}, compositeSolutions.get(5).getObjectives());
        assertArrayEquals(new double[]{0.0, 0.0}, compositeSolutions.get(6).getObjectives());
        assertArrayEquals(new double[]{0.0, 0.0}, compositeSolutions.get(7).getObjectives());
    }
}