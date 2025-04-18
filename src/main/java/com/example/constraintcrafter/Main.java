package com.example.constraintcrafter;

public class Main {
    public static void main(String[] args) throws Exception {
        String templatePath = null;
        String xsdPath = null;
        String outDir = ".";
        for (int i = 0; i < args.length; i++) {
            if ("--template".equals(args[i]) || "-t".equals(args[i])) {
                if (i + 1 < args.length) {
                    templatePath = args[++i];
                } else {
                    System.err.println("Missing value for --template option");
                    System.exit(1);
                }
            } else if (xsdPath == null) {
                xsdPath = args[i];
            } else if (outDir.equals(".") && !args[i].equals(templatePath)) {
                outDir = args[i];
            } else {
                System.err.println("Unknown argument: " + args[i]);
                System.exit(1);
            }
        }
        if (xsdPath == null) {
            System.err.println("Usage: java -jar constraintcrafter.jar [--template <template.xml>] <schema.xsd> [outputDir]");
            System.exit(1);
        }
        TestCaseGenerator generator = (templatePath == null)
            ? new TestCaseGenerator(xsdPath, outDir)
            : new TestCaseGenerator(xsdPath, outDir, templatePath);
        generator.generateAll();
    }
}
