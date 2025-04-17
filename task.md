# TDD Plan & Tasks

## 1) ElementInfo Value Object

- **Test**: `ElementInfoTest`  
  – getters, `hasEnum()`  
- **Implement**: `ElementInfo` class

## 2) SchemaModel → List<ElementInfo>

- **Fixture**: `src/test/resources/schema/Simple.xsd`  
- **Test**: `SchemaModelTest`  
  – load XSD, assert 3 elements (A, B, C) with correct `minOccurs`/`maxOccurs` and enum values  
- **Implement**: `SchemaModel.load()` + `getElements()` + `getEnumValues()`

## 3) Cardinality Test Generation

- **Test**: `TestCaseGeneratorTest` for `ElementInfo("B",0,2,[])`  
  – positive: files for min, between, max occurrences  
  – negative: files for below‑min and above‑max  
- **Implement**: `generatePositiveCardinality` & `generateNegativeCardinality`

## 4) Enumeration Test Generation

- **Test**: same for `ElementInfo("C",1,1,["X","Y"])`  
  – positive: one file per value  
  – negative: one invalid file  
- **Implement**: `generatePositiveEnumeration` & `generateNegativeEnumeration`

## 5) End‑to‑End & Folder Structure

- **Test**: `EndToEndTest`  
  – run `Main` on `Simple.xsd` → output tree:  
    ```
    Simple/
      positive/cardinality/*.xml
      positive/enumeration/*.xml
      negative/cardinality/*.xml
      negative/enumeration/*.xml
    ```  
- **Implement**: CLI `Main`, directory setup in `TestCaseGenerator` ctor

## 6) Refactor & Polish

- Cleanup code, add logging, handle `unbounded` defaults, improve error messages  
- Extend support for nested types, attributes, other XSD facets
