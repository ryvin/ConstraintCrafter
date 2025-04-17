package com.example.constraintcrafter;

import com.example.constraintcrafter.model.ElementInfo;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SchemaModel {
    private final List<ElementInfo> elements;

    private SchemaModel(List<ElementInfo> elements) {
        this.elements = elements;
    }

    public static SchemaModel load(String xsdPath) throws Exception {
        // parse XSD as DOM
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse(new File(xsdPath));
        // find the first xs:sequence
        NodeList seqList = doc.getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema", "sequence");
        if (seqList.getLength() == 0) {
            throw new IllegalArgumentException("No xs:sequence found in schema");
        }
        Element seq = (Element) seqList.item(0);
        List<ElementInfo> infos = new ArrayList<>();
        NodeList children = seq.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (!(node instanceof Element) || !"element".equals(node.getLocalName())) continue;
            Element el = (Element) node;
            String name = el.getAttribute("name");
            String minStr = el.getAttribute("minOccurs");
            int min = minStr.isEmpty() ? 1 : Integer.parseInt(minStr);
            String maxStr = el.getAttribute("maxOccurs");
            int max;
            if (maxStr.isEmpty()) {
                max = 1;
            } else if ("unbounded".equals(maxStr)) {
                max = -1;
            } else {
                max = Integer.parseInt(maxStr);
            }
            // collect enumeration values if any
            List<String> enumVals = new ArrayList<>();
            NodeList enumList = el.getElementsByTagNameNS(
                "http://www.w3.org/2001/XMLSchema", "enumeration");
            for (int j = 0; j < enumList.getLength(); j++) {
                Element enumEl = (Element) enumList.item(j);
                String val = enumEl.getAttribute("value");
                enumVals.add(val);
            }
            infos.add(new ElementInfo(name, min, max, enumVals));
        }
        return new SchemaModel(infos);
    }

    public List<ElementInfo> getElements() {
        return elements;
    }
}
