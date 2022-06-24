package dataanalysis.pe;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SolutionSetParserPETest {

    @Test
    void testParseSolutionSet() {
        SolutionSetParserPE parser = new SolutionSetParserPE();
        Collection<DoubleSolution> doubleSolutions = parser.parseSolutionSet("src/test/resources/parser/VAR_PE.csv");
        assertEquals(8, doubleSolutions.size());
    }

    @Test
    void testParseSolutionSetIncorrect() {
        SolutionSetParserPE parser = new SolutionSetParserPE();
        Collection<DoubleSolution> doubleSolutions = parser.parseSolutionSet("not/a/valid/path.csv");
        assertEquals(0, doubleSolutions.size());
    }

    @Test
    void testParseSolutionSetIncorrect2() {
        SolutionSetParserPE parser = new SolutionSetParserPE();
        Collection<DoubleSolution> doubleSolutions = parser.parseSolutionSet("src/test/resources/parser/VAR_INVALID.csv");
        assertEquals(0, doubleSolutions.size());
    }

    @Test
    void testParseSolutionSetIncorrect3() {
        SolutionSetParserPE parser = new SolutionSetParserPE();
        Collection<DoubleSolution> doubleSolutions = parser.parseSolutionSet("src/test/resources/parser/VAR_EMPTY.csv");
        assertEquals(0, doubleSolutions.size());
    }

    @Test
    void parseDoubleSolutionCorrect() {
        SolutionSetParserPE parser = new SolutionSetParserPE();
        DoubleSolution doubleSolution = parser.parseDoubleSolution("0.814354582064323,1.330937001800833,9.476687002362809,1.4440256764073665,15.818547935992543,46.01234383063028,25.83538564038052,16.83184483792354,23.414672094310927,0.2816619271466199,0.2601889494263248,0.03347637751841191,0.07386031981260512,0.11078918708358716");
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
        SolutionSetParserPE parser = new SolutionSetParserPE();
        assertThrows(NumberFormatException.class, () -> parser.parseDoubleSolution("Not a valid string"));
        assertThrows(NumberFormatException.class, () -> parser.parseDoubleSolution("Variables: 0.814354582064323 1.330937001800833 9.476687002362809 1.4440256764073665 15.818547935992543 46.01234383063028 25.83538564038052 16.83184483792354 23.414672094310927 0.2816619271466199 0.2601889494263248 0.03347637751841191 0.07386031981260512 0.11078918708358716 0.0 0.0 Constraints: \tAlgorithmAttributes: {}"));
    }

    @Test
    void testParseFun() {
        SolutionSetParserPE parser = new SolutionSetParserPE();
        List<DoubleSolution> doubleSolutions = parser.parseSolutionSet("src/test/resources/parser/VAR_PE.csv");
        assertEquals(8, doubleSolutions.size());
        parser.parseFun(doubleSolutions, "src/test/resources/parser/FUN.csv");
        assertArrayEquals(new double[]{2.0, 0.56}, doubleSolutions.get(0).getObjectives());
        assertArrayEquals(new double[]{8.0, 0.20}, doubleSolutions.get(1).getObjectives());
        assertArrayEquals(new double[]{2.0, 0.56}, doubleSolutions.get(2).getObjectives());
        assertArrayEquals(new double[]{8.0, 0.32}, doubleSolutions.get(3).getObjectives());
        assertArrayEquals(new double[]{6.0, 0.35}, doubleSolutions.get(4).getObjectives());
        assertArrayEquals(new double[]{2.0, 0.55}, doubleSolutions.get(5).getObjectives());
        assertArrayEquals(new double[]{3.0, 0.52}, doubleSolutions.get(6).getObjectives());
        assertArrayEquals(new double[]{2.0, 0.52}, doubleSolutions.get(7).getObjectives());
    }

    @Test
    void testParseFunDifferentSizes() {
        SolutionSetParserPE parser = new SolutionSetParserPE();
        List<DoubleSolution> doubleSolutions = parser.parseSolutionSet("src/test/resources/parser/VAR_PE.csv");
        assertEquals(8, doubleSolutions.size());
        parser.parseFun(doubleSolutions, "src/test/resources/parser/FUN_2.csv");
        assertArrayEquals(new double[]{0.0, 0.0}, doubleSolutions.get(0).getObjectives());
        assertArrayEquals(new double[]{0.0, 0.0}, doubleSolutions.get(1).getObjectives());
        assertArrayEquals(new double[]{0.0, 0.0}, doubleSolutions.get(2).getObjectives());
        assertArrayEquals(new double[]{0.0, 0.0}, doubleSolutions.get(3).getObjectives());
        assertArrayEquals(new double[]{0.0, 0.0}, doubleSolutions.get(4).getObjectives());
        assertArrayEquals(new double[]{0.0, 0.0}, doubleSolutions.get(5).getObjectives());
        assertArrayEquals(new double[]{0.0, 0.0}, doubleSolutions.get(6).getObjectives());
        assertArrayEquals(new double[]{0.0, 0.0}, doubleSolutions.get(7).getObjectives());
    }

    @Test
    void testParseFunInvalidPath() {
        SolutionSetParserPE parser = new SolutionSetParserPE();
        List<DoubleSolution> doubleSolutions = parser.parseSolutionSet("src/test/resources/parser/VAR_PE.csv");
        assertEquals(8, doubleSolutions.size());
        parser.parseFun(doubleSolutions, "not/a/valid/path.csv");
        assertArrayEquals(new double[]{0.0, 0.0}, doubleSolutions.get(0).getObjectives());
        assertArrayEquals(new double[]{0.0, 0.0}, doubleSolutions.get(1).getObjectives());
        assertArrayEquals(new double[]{0.0, 0.0}, doubleSolutions.get(2).getObjectives());
        assertArrayEquals(new double[]{0.0, 0.0}, doubleSolutions.get(3).getObjectives());
        assertArrayEquals(new double[]{0.0, 0.0}, doubleSolutions.get(4).getObjectives());
        assertArrayEquals(new double[]{0.0, 0.0}, doubleSolutions.get(5).getObjectives());
        assertArrayEquals(new double[]{0.0, 0.0}, doubleSolutions.get(6).getObjectives());
        assertArrayEquals(new double[]{0.0, 0.0}, doubleSolutions.get(7).getObjectives());
    }
}