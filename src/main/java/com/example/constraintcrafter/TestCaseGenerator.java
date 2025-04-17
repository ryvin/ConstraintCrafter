package com.example.constraintcrafter;

import com.example.constraintcrafter.SchemaModel;
import com.example.constraintcrafter.model.ElementInfo;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

public class TestCaseGenerator {
    private static final Logger LOGGER = Logger.getLogger(TestCaseGenerator.class.getName());
    private final List<ElementInfo> elements;
    private final File posCardDir;
    private final File posEnumDir;
    private final File negCardDir;
    private final File negEnumDir;
    private List<String> xsdLines;

    public TestCaseGenerator(List<ElementInfo> elements, String outDir, String schemaName) {
        this.elements = elements;
        File base = new File(outDir, schemaName);
        this.posCardDir = new File(base, "positive/cardinality");
        this.posEnumDir = new File(base, "positive/enumeration");
        this.negCardDir = new File(base, "negative/cardinality");
        this.negEnumDir = new File(base, "negative/enumeration");
        this.xsdLines = null;
        // create directories
        for (File dir : new File[]{posCardDir, posEnumDir, negCardDir, negEnumDir}) {
            dir.mkdirs();
        }
        LOGGER.info("Initialized output directory: " + base.getAbsolutePath());
    }

    /**
     * Create generator by loading schema and deriving schema name from XSD file.
     */
    public TestCaseGenerator(String xsdPath, String outDir) throws Exception {
        this(SchemaModel.load(xsdPath).getElements(),
             outDir,
             new File(xsdPath).getName().replaceFirst("\\.xsd$", ""));
        // load schema lines for failure annotations
        this.xsdLines = Files.readAllLines(Paths.get(xsdPath));
    }

    public void generateAll() throws IOException {
        LOGGER.info("Starting test case generation for " + elements.size() + " elements");
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
        boolean unbounded = max < 0;
        // positive minOccurs
        if (xsdLines != null) {
            File f = new File(posCardDir, name + "_min_occurs_ok.xml");
            StringBuilder sb = new StringBuilder();
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            int lineNum = findLineNumber(name);
            sb.append("<!-- Positive: minOccurs=" + min + " for element '" + name + "' defined at schema line " + lineNum + " -->\n");
            sb.append("<root>\n");
            for (int i = 0; i < min; i++) sb.append("  <" + name + "/>\n");
            sb.append("</root>");
            Files.writeString(f.toPath(), sb.toString());
        } else {
            writeXmlContent(name, min, new File(posCardDir, name + "_min_occurs_ok.xml"));
        }
        // positive betweenOccurs
        int betweenCount = (unbounded || max > min) ? min + 1 : min;
        if (xsdLines != null) {
            File f = new File(posCardDir, name + "_between_ok.xml");
            StringBuilder sb = new StringBuilder();
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            int lineNum = findLineNumber(name);
            sb.append("<!-- Positive: betweenOccurs=" + betweenCount + " for element '" + name + "' defined at schema line " + lineNum + " -->\n");
            sb.append("<root>\n");
            for (int i = 0; i < betweenCount; i++) sb.append("  <" + name + "/>\n");
            sb.append("</root>");
            Files.writeString(f.toPath(), sb.toString());
        } else {
            writeXmlContent(name, betweenCount, new File(posCardDir, name + "_between_ok.xml"));
        }
        // positive maxOccurs (if bounded)
        if (!unbounded) {
            if (xsdLines != null) {
                File f = new File(posCardDir, name + "_max_occurs_ok.xml");
                StringBuilder sb = new StringBuilder();
                sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                int lineNum = findLineNumber(name);
                sb.append("<!-- Positive: maxOccurs=" + max + " for element '" + name + "' defined at schema line " + lineNum + " -->\n");
                sb.append("<root>\n");
                for (int i = 0; i < max; i++) sb.append("  <" + name + "/>\n");
                sb.append("</root>");
                Files.writeString(f.toPath(), sb.toString());
            } else {
                writeXmlContent(name, max, new File(posCardDir, name + "_max_occurs_ok.xml"));
            }
        }
    }

    private void generateNegativeCardinality(ElementInfo info) throws IOException {
        String name = info.getName();
        int min = info.getMinOccurs();
        int max = info.getMaxOccurs();
        // negative below minOccurs
        int below = Math.max(0, min - 1);
        File belowFile = new File(negCardDir, name + "_below_min_fail.xml");
        StringBuilder sb1 = new StringBuilder();
        sb1.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        int lineNum1 = findLineNumber(name);
        sb1.append("<!-- Failure: below minOccurs=" + min + " for element '" + name + "' defined at schema line " + lineNum1 + " -->\n");
        sb1.append("<root>\n");
        for (int i = 0; i < below; i++) {
            sb1.append("  <" + name + "/>\n");
        }
        sb1.append("</root>");
        Files.writeString(belowFile.toPath(), sb1.toString());
        // negative above maxOccurs (if bounded)
        if (max >= 0) {
            File aboveFile = new File(negCardDir, name + "_above_max_fail.xml");
            StringBuilder sb2 = new StringBuilder();
            sb2.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            int lineNum2 = findLineNumber(name);
            sb2.append("<!-- Failure: above maxOccurs=" + max + " for element '" + name + "' defined at schema line " + lineNum2 + " -->\n");
            sb2.append("<root>\n");
            for (int i = 0; i < max + 1; i++) {
                sb2.append("  <" + name + "/>\n");
            }
            sb2.append("</root>");
            Files.writeString(aboveFile.toPath(), sb2.toString());
        }
    }

    private void generatePositiveEnumeration(ElementInfo info) throws IOException {
        String name = info.getName();
        for (String v : info.getEnumValues()) {
            // sanitize enumeration value for filename
            String safeV = v.replaceAll("[\\\\/:*?\"<>|]", "_");
            File f = new File(posEnumDir, name + "_" + safeV + "_ok.xml");
            StringBuilder sb = new StringBuilder();
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            if (xsdLines != null) {
                int lineNum = findLineNumber(name);
                sb.append("<!-- Positive enumeration: value '" + v + "' for element '" + name + "' defined at schema line " + lineNum + " -->\n");
            }
            sb.append("<root>\n");
            sb.append("  <" + name + ">" + v + "</" + name + ">\n");
            sb.append("</root>");
            Files.writeString(f.toPath(), sb.toString());
        }
    }

    private void generateNegativeEnumeration(ElementInfo info) throws IOException {
        String name = info.getName();
        File f = new File(negEnumDir, name + "_invalid_fail.xml");
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        int lineNum = findLineNumber(name);
        sb.append("<!-- Failure: invalid enumeration for element '" + name + "' defined at schema line " + lineNum + " -->\n");
        sb.append("<root>\n");
        sb.append("  <" + name + ">INVALID</" + name + ">\n");
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

    /**
     * Find the line number of the element definition in the XSD file.
     */
    private int findLineNumber(String name) {
        if (xsdLines == null) { return -1; }
        for (int i = 0; i < xsdLines.size(); i++) {
            if (xsdLines.get(i).contains("element") && xsdLines.get(i).contains("name=\"" + name + "\"")) {
                return i + 1;
            }
        }
        return -1;
    }
}
