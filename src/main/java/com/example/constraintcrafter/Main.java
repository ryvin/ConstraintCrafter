package com.example.constraintcrafter;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: java -jar constraintcrafter.jar <schema.xsd> [outputDir]");
            System.exit(1);
        }
        String xsdPath = args[0];
        String outDir = args.length > 1 ? args[1] : ".";
        TestCaseGenerator generator = new TestCaseGenerator(xsdPath, outDir);
        generator.generateAll();
    }
}
