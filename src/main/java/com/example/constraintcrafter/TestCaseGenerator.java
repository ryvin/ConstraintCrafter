package com.example.constraintcrafter;

import com.example.constraintcrafter.SchemaModel;
import com.example.constraintcrafter.model.ElementInfo;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class TestCaseGenerator {
    private final List<ElementInfo> elements;
    private final File posCardDir;
    private final File posEnumDir;
    private final File negCardDir;
    private final File negEnumDir;

    public TestCaseGenerator(List<ElementInfo> elements, String outDir, String schemaName) {
        this.elements = elements;
        File base = new File(outDir, schemaName);
        this.posCardDir = new File(base, "positive/cardinality");
        this.posEnumDir = new File(base, "positive/enumeration");
        this.negCardDir = new File(base, "negative/cardinality");
        this.negEnumDir = new File(base, "negative/enumeration");
        // create directories
        for (File dir : new File[]{posCardDir, posEnumDir, negCardDir, negEnumDir}) {
            dir.mkdirs();
        }
    }

    /**
     * Create generator by loading schema and deriving schema name from XSD file.
     */
    public TestCaseGenerator(String xsdPath, String outDir) throws Exception {
        this(SchemaModel.load(xsdPath).getElements(),
             outDir,
             new File(xsdPath).getName().replaceFirst("\\.xsd$", ""));
    }

    public void generateAll() throws IOException {
        for (ElementInfo info : elements) {
            if (info.getMinOccurs() != info.getMaxOccurs()) {
                generatePositiveCardinality(info);
                generateNegativeCardinality(info);
            }
            if (info.hasEnum()) {
                generatePositiveEnumeration(info);
                generateNegativeEnumeration(info);
            }
        }
    }

    private void generatePositiveCardinality(ElementInfo info) throws IOException {
        String name = info.getName();
        int min = info.getMinOccurs();
        int max = info.getMaxOccurs();
        int between = max > min ? min + 1 : max;
        writeXmlContent(name, min, new File(posCardDir, name + "_min_occurs_ok.xml"));
        writeXmlContent(name, between, new File(posCardDir, name + "_between_ok.xml"));
        writeXmlContent(name, max, new File(posCardDir, name + "_max_occurs_ok.xml"));
    }

    private void generateNegativeCardinality(ElementInfo info) throws IOException {
        String name = info.getName();
        int below = Math.max(0, info.getMinOccurs() - 1);
        int above = info.getMaxOccurs() + 1;
        writeXmlContent(name, below, new File(negCardDir, name + "_below_min_fail.xml"));
        writeXmlContent(name, above, new File(negCardDir, name + "_above_max_fail.xml"));
    }

    private void generatePositiveEnumeration(ElementInfo info) throws IOException {
        String name = info.getName();
        for (String v : info.getEnumValues()) {
            File f = new File(posEnumDir, name + "_" + v + "_ok.xml");
            StringBuilder sb = new StringBuilder();
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            sb.append("<root>\n");
            sb.append("  <").append(name).append(">").append(v).append("</").append(name).append(">\n");
            sb.append("</root>");
            Files.writeString(f.toPath(), sb.toString());
        }
    }

    private void generateNegativeEnumeration(ElementInfo info) throws IOException {
        String name = info.getName();
        File f = new File(negEnumDir, name + "_invalid_fail.xml");
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<root>\n");
        sb.append("  <").append(name).append(">INVALID</").append(name).append(">\n");
        sb.append("</root>");
        Files.writeString(f.toPath(), sb.toString());
    }

    private void writeXmlContent(String name, int count, File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<root>\n");
        for (int i = 0; i < count; i++) {
            sb.append("  <").append(name).append("/>\n");
        }
        sb.append("</root>");
        Files.writeString(file.toPath(), sb.toString());
    }
}
