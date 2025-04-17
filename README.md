# ConstraintCrafter

ConstraintCrafter is a Java CLI tool that reads an XML Schema (XSD) and auto‑generates positive & negative XML test files for:

- **Cardinality** (`minOccurs`/`maxOccurs`)
- **Enumeration** (valid vs invalid facet values)

It also provides a JUnit harness to validate all generated cases.

## Project Layout

```
constraintcrafter/
├── pom.xml
├── README.md
├── task.md
├── src
│   ├── main
│   │   ├── java/com/example/constraintcrafter
│   │   └── resources/schema
│   └── test
│       ├── java/com/example/constraintcrafter
│       └── resources/schema
└── target/          ← build outputs & generated tests
```

## Prerequisites

- Java 11+
- Maven 3.6+

## Build & Run

```bash
mvn clean package

# generate tests
java -jar target/constraintcrafter.jar \
  -s path/to/schema.xsd \
  -o target/generated-tests

# run validation
mvn test
```

## TDD Workflow

1. Write a failing test in `src/test/java`
2. Implement minimal code in `src/main/java` to make it pass
3. Repeat per feature

See `task.md` for the full plan.
