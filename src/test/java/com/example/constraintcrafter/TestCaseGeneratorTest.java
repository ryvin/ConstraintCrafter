package com.example.constraintcrafter;

import com.example.constraintcrafter.model.ElementInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Files;

class TestCaseGeneratorTest {

    @TempDir
    Path tempDir;

    @Test
    void generatesCardinalityFiles() throws IOException {
        ElementInfo info = new ElementInfo("B", 0, 2, Collections.emptyList());
        TestCaseGenerator gen = new TestCaseGenerator(List.of(info), tempDir.toString(), "Simple");
        gen.generateAll();

        Path posCard = tempDir.resolve("Simple/positive/cardinality");
        assertTrue(Files.exists(posCard.resolve("B_min_occurs_ok.xml")));
        assertTrue(Files.exists(posCard.resolve("B_between_ok.xml")));
        assertTrue(Files.exists(posCard.resolve("B_max_occurs_ok.xml")));

        Path negCard = tempDir.resolve("Simple/negative/cardinality");
        assertTrue(Files.exists(negCard.resolve("B_below_min_fail.xml")));
        assertTrue(Files.exists(negCard.resolve("B_above_max_fail.xml")));
    }

    @Test
    void generatesEnumerationFiles() throws IOException {
        List<String> enums = List.of("X","Y");
        ElementInfo info = new ElementInfo("C", 1, 1, enums);
        TestCaseGenerator gen = new TestCaseGenerator(List.of(info), tempDir.toString(), "Simple");
        gen.generateAll();

        Path posEnum = tempDir.resolve("Simple/positive/enumeration");
        assertTrue(Files.exists(posEnum.resolve("C_X_ok.xml")));
        assertTrue(Files.exists(posEnum.resolve("C_Y_ok.xml")));

        Path negEnum = tempDir.resolve("Simple/negative/enumeration");
        assertTrue(Files.exists(negEnum.resolve("C_invalid_fail.xml")));
    }
}
