package com.example.constraintcrafter;

import com.example.constraintcrafter.SchemaModel;
import com.example.constraintcrafter.model.ElementInfo;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class TestCaseGenerator {
    private static final Logger LOGGER = Logger.getLogger(TestCaseGenerator.class.getName());
    private final List<ElementInfo> elements;
    private final File posCardDir;
    private final File posEnumDir;
    private final File negCardDir;
    private final File negEnumDir;
    private List<String> xsdLines;
    private final Path templatePath;

    public TestCaseGenerator(List<ElementInfo> elements, String outDir, String schemaName) {
        this(elements, outDir, schemaName, (String)null);
    }

    /**
     * Load schema and specify custom template path (can be null for default).
     */
    public TestCaseGenerator(List<ElementInfo> elements, String outDir, String schemaName, String templatePathArg) {
        this.elements = elements;
        File base = new File(outDir, schemaName);
        this.posCardDir = new File(base, "positive/cardinality");
        this.posEnumDir = new File(base, "positive/enumeration");
        this.negCardDir = new File(base, "negative/cardinality");
        this.negEnumDir = new File(base, "negative/enumeration");
        this.xsdLines = null;
        for (File dir : new File[]{posCardDir, posEnumDir, negCardDir, negEnumDir}) dir.mkdirs();
        LOGGER.info("Initialized output directory: " + base.getAbsolutePath());
        this.templatePath = templatePathArg != null ? Paths.get(templatePathArg) : Paths.get("src/main/resources/templates/PDS_template.xml");
    }

    /**
     * Create generator by loading schema and deriving schema name from XSD file.
     */
    public TestCaseGenerator(String xsdPath, String outDir) throws Exception {
        this(xsdPath, outDir, null);
    }

    public TestCaseGenerator(String xsdPath, String outDir, String templatePathArg) throws Exception {
        this(SchemaModel.load(xsdPath).getElements(),
             outDir,
             new File(xsdPath).getName().replaceFirst("\\.xsd$", ""),
             templatePathArg);
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
        int lineNum = findLineNumber(name);
        File fMin = new File(posCardDir, name + "_min_occurs_ok.xml");
        writeTemplateCardinality(name, min, fMin, "Positive: minOccurs=" + min + " for element '" + name + "' defined at schema line " + lineNum);
        int betweenCount = (unbounded || max > min) ? min + 1 : min;
        File fBetween = new File(posCardDir, name + "_between_ok.xml");
        writeTemplateCardinality(name, betweenCount, fBetween, "Positive: betweenOccurs=" + betweenCount + " for element '" + name + "' defined at schema line " + lineNum);
        if (!unbounded) {
            File fMax = new File(posCardDir, name + "_max_occurs_ok.xml");
            writeTemplateCardinality(name, max, fMax, "Positive: maxOccurs=" + max + " for element '" + name + "' defined at schema line " + lineNum);
        }
    }

    private void generateNegativeCardinality(ElementInfo info) throws IOException {
        String name = info.getName();
        int min = info.getMinOccurs();
        int max = info.getMaxOccurs();
        int below = Math.max(0, min - 1);
        int lineNum1 = findLineNumber(name);
        File fBelow = new File(negCardDir, name + "_below_min_fail.xml");
        writeTemplateCardinality(name, below, fBelow, "Failure: below minOccurs=" + min + " for element '" + name + "' defined at schema line " + lineNum1);
        if (max >= 0) {
            int lineNum2 = findLineNumber(name);
            File fAbove = new File(negCardDir, name + "_above_max_fail.xml");
            writeTemplateCardinality(name, max + 1, fAbove, "Failure: above maxOccurs=" + max + " for element '" + name + "' defined at schema line " + lineNum2);
        }
    }

    private void generatePositiveEnumeration(ElementInfo info) throws IOException {
        String name = info.getName();
        for (String v : info.getEnumValues()) {
            String safeV = v.replaceAll("[\\\\/:*?\"<>|]", "_");
            File f = new File(posEnumDir, name + "_" + safeV + "_ok.xml");
            int lineNum = findLineNumber(name);
            writeTemplateEnumeration(name, v, f, "Positive enumeration: value '" + v + "' for element '" + name + "' defined at schema line " + lineNum);
        }
    }

    private void generateNegativeEnumeration(ElementInfo info) throws IOException {
        String name = info.getName();
        int lineNum = findLineNumber(name);
        File f = new File(negEnumDir, name + "_invalid_fail.xml");
        writeTemplateEnumeration(name, "INVALID", f, "Failure: invalid enumeration for element '" + name + "' defined at schema line " + lineNum);
    }

    private Document loadTemplate() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder builder = dbf.newDocumentBuilder();
        return builder.parse(templatePath.toFile());
    }

    private void writeDocument(Document doc, File file) throws Exception {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty("indent", "yes");
        transformer.transform(new DOMSource(doc), new StreamResult(file));
    }

    private void writeTemplateCardinality(String name, int count, File file, String comment) throws IOException {
        try {
            Document doc = loadTemplate();
            Element root = doc.getDocumentElement();
            NodeList existing = doc.getElementsByTagName(name);
            while (existing.getLength() > 0) {
                existing.item(0).getParentNode().removeChild(existing.item(0));
            }
            for (int i = 0; i < count; i++) {
                root.appendChild(doc.createElement(name));
            }
            doc.insertBefore(doc.createComment(comment), root);
            writeDocument(doc, file);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private void writeTemplateEnumeration(String name, String value, File file, String comment) throws IOException {
        try {
            Document doc = loadTemplate();
            Element root = doc.getDocumentElement();
            NodeList existing = doc.getElementsByTagName(name);
            while (existing.getLength() > 0) {
                existing.item(0).getParentNode().removeChild(existing.item(0));
            }
            Element el = doc.createElement(name);
            el.setTextContent(value);
            root.appendChild(el);
            doc.insertBefore(doc.createComment(comment), root);
            writeDocument(doc, file);
        } catch (Exception e) {
            throw new IOException(e);
        }
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
