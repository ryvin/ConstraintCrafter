![ConstraintCrafter Logo](assets/constraintcrafter-logo.png) 

# ConstraintCrafter

> Building better XML tests, one constraint at a time.

[![Build](https://img.shields.io/github/actions/workflow/status/ryvin/ConstraintCrafter/maven.yml?branch=main)](https://github.com/ryvin/ConstraintCrafter/actions)
[![Maven Central](https://img.shields.io/maven-central/v/com.example/constraintcrafter)](https://search.maven.org/artifact/com.example/constraintcrafter)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Output Structure](#output-structure)
- [Project Layout](#project-layout)
- [Contributing](#contributing)
- [Author](#author)
- [Architecture](#architecture)
- [Testing & Workflow](#testing--workflow)
- [Examples & Diagrams](#examples--diagrams)
- [Why Cardinality & Enumeration?](#why-cardinality--enumeration)
- [License](#license)

## Overview

ConstraintCrafter is a Java CLI tool that reads an XML Schema (XSD) and auto-generates positive and negative XML test files for schema constraints. It helps ensure your XML processing code handles both valid and invalid inputs correctly.

## Features

- **Automated Test Generation**: Generates XML test cases for all `minOccurs`/`maxOccurs` and `xs:enumeration` constraints.
- **Schema Support**: Resolves includes, imports, namespaces, and references.
- **Test Comments**: Annotates each file with schema line numbers and failure reasons.
- **Organized Output**: Categorizes tests under `positive`/`negative` and `cardinality`/`enumeration`.
- **Extensible Architecture**: Easily add new constraint generators.

## Installation

Requires Java 11+ and Maven 3.6+.

```bash
git clone https://github.com/ryvin/ConstraintCrafter.git
cd ConstraintCrafter
mvn clean package
```

## Usage

```bash
java -jar target/constraintcrafter-0.1.0-shaded.jar <schema.xsd> [output-dir]
```

- `<schema.xsd>`: Path to your XSD file
- `[output-dir]`: Optional (default: `test-output/<schemaName>`)

### Example

```bash
java -jar target/constraintcrafter-0.1.0-shaded.jar src/test/resources/schema/Simple.xsd test-output/Simple
```

## Output Structure

```
test-output/<SchemaName>/
├── positive/
│   ├── cardinality/
│   └── enumeration/
└── negative/
    ├── cardinality/
    └── enumeration/
```

Each XML file is named `<element>_<constraint>_<ok|fail>.xml` with comments indicating the schema line and test type.

## Project Layout

```
constraintcrafter/
├── pom.xml
├── README.md
├── src/
│   ├── main/java/com/example/constraintcrafter
│   └── test/java/com/example/constraintcrafter
└── target/
```

## Contributing

Contributions welcome! Please:

1. Fork the repo
2. Create a feature branch (`git checkout -b feature/new-constraint`)
3. Commit your changes and add tests
4. Open a Pull Request

## Author

Raul Pineda

## Architecture

ConstraintCrafter follows a modular architecture:

- **Main CLI**: `com.example.constraintcrafter.Main` parses CLI arguments and invokes `TestCaseGenerator`.
- **SchemaModel**: Loads XSD and extracts `ElementInfo`.
- **ElementInfo**: Encapsulates element name, cardinality, and enumeration values.
- **TestCaseGenerator**: Coordinates positive and negative test generation per element.
- **Generators**: `CardinalityTestGenerator` & `EnumerationTestGenerator` handle specific constraints.

## Testing & Workflow

- **TDD Approach**: Write unit tests in `src/test/java` before implementing features.
- **Run Tests**: `mvn test` or `mvn clean verify` to build and validate.
- **End-to-End**: `EndToEndTest` ensures full-schema generation on sample XSDs.
- **CI Integration**: GitHub Actions pipeline builds, tests, and checks code on each PR.

## Examples & Diagrams

Below is a sample XSD element with both cardinality and enumeration:

```xml
<xs:element name="status" minOccurs="1" maxOccurs="3">
  <xs:simpleType>
    <xs:restriction base="xs:string">
      <xs:enumeration value="NEW"/>
      <xs:enumeration value="IN_PROGRESS"/>
      <xs:enumeration value="DONE"/>
    </xs:restriction>
  </xs:simpleType>
</xs:element>
```

### Generated Positive Cardinality XML

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- Positive: minOccurs=1 for element 'status' defined at schema line 2 -->
<root>
  <status/>
</root>
```

### Flowchart

![Generation Flow](assets/constraintcrafter-flowchart.png)

### Who Benefits?

- **QA Engineers**: Quickly validate edge cases and boundary conditions.  
- **Schema Authors**: Confirm schema definitions produce expected tests.  
- **Developers**: Integrate into CI for automated regression tests.

## Why Cardinality & Enumeration?

ConstraintCrafter focuses on two core constraint types:

- **Cardinality** (`minOccurs`/`maxOccurs`): Defines the allowable range of element occurrences. Automated tests cover boundary and beyond-boundary scenarios (empty, minimum, maximum, overflow) to catch off-by-one bugs.
- **Enumeration** (`xs:enumeration`): Restricts element values to a predefined set. Tests verify both valid enum values and invalid (out-of-range) inputs.

By generating positive and negative cases for these constraints, ConstraintCrafter ensures your XML parsers and validators handle both compliant and non-compliant inputs robustly, reducing bugs and improving schema coverage.

## License

Distributed under the Apache License 2.0  2025 Raul Pineda. See `LICENSE` for details.

 2025 Raul Pineda
