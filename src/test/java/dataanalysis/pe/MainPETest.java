package dataanalysis.pe;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MainPETest {

    @Test
    void main() throws IOException {
        File outputDir = new File("src/test/resources/non-existent");
        try {
            MainPE.main(new String[]{
                    "-t", "data/Arff_XV/jruby-1.1-first.arff",
                    "-v", "src/test/resources/parser/VAR_PE.csv",
                    "-f", "src/test/resources/parser/FUN.csv",
                    "-o", outputDir.getPath() + "/TEST_3.csv"
            });
            assertTrue(outputDir.isDirectory());
            List<Path> listFiles = Files.list(outputDir.toPath()).toList();
            assertEquals(1, listFiles.size());
            for (Path file : listFiles) {
                List<String> lines = Files.readAllLines(file);
                assertFalse(lines.isEmpty());
                assertEquals(2, lines.size());
            }
        } finally {
            FileUtils.deleteDirectory(outputDir.getAbsoluteFile());
        }
    }
}