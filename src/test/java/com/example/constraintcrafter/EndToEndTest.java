package com.example.constraintcrafter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

class EndToEndTest {

    @TempDir
    Path tempDir;

    @Test
    void runsMainOnSimpleSchema() throws Exception {
        String xsdPath = "src/test/resources/schema/Simple.xsd";
        Main.main(new String[]{xsdPath, tempDir.toString()});

        Path base = tempDir.resolve("Simple");
        assertTrue(Files.isDirectory(base));

        Path posCard = base.resolve("positive/cardinality");
        assertTrue(Files.isDirectory(posCard));
        try (Stream<Path> files = Files.list(posCard)) {
            assertEquals(3, files.count());
        }

        Path posEnum = base.resolve("positive/enumeration");
        assertTrue(Files.isDirectory(posEnum));
        try (Stream<Path> files = Files.list(posEnum)) {
            assertEquals(2, files.count());
        }

        Path negCard = base.resolve("negative/cardinality");
        assertTrue(Files.isDirectory(negCard));
        try (Stream<Path> files = Files.list(negCard)) {
            assertEquals(2, files.count());
        }

        Path negEnum = base.resolve("negative/enumeration");
        assertTrue(Files.isDirectory(negEnum));
        try (Stream<Path> files = Files.list(negEnum)) {
            assertEquals(1, files.count());
        }
    }
}
