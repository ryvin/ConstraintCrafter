# TDD Plan & Tasks

## 1) ElementInfo Value Object

- [x] **Test**: `ElementInfoTest`  
  – getters, `hasEnum()`  
- [x] **Implement**: `ElementInfo` class

## 2) SchemaModel → List<ElementInfo>

- [x] **Fixture**: `src/test/resources/schema/Simple.xsd`  
- [x] **Test**: `SchemaModelTest`  
  – load XSD, assert 3 elements (A, B, C) with correct `minOccurs`/`maxOccurs` and enum values  
- [x] **Implement**: `SchemaModel.load()` + `getElements()` + `getEnumValues()`

## 3) Cardinality Test Generation

- [x] **Test**: `TestCaseGeneratorTest` for `ElementInfo("B",0,2,[])`  
  – positive: files for min, between, max occurrences  
  – negative: files for below‑min and above‑max  
- [x] **Implement**: `generatePositiveCardinality` & `generateNegativeCardinality`

## 4) Enumeration Test Generation

- [x] **Test**: same for `ElementInfo("C",1,1,["X","Y"])`  
  – positive: one file per value  
  – negative: one invalid file  
- [x] **Implement**: `generatePositiveEnumeration` & `generateNegativeEnumeration`

## 5) End‑to‑End & Folder Structure

- [x] **Test**: `EndToEndTest`  
  – run `Main` on `Simple.xsd` → output tree:  
    ```
    Simple/
      positive/cardinality/*.xml
      positive/enumeration/*.xml
      negative/cardinality/*.xml
      negative/enumeration/*.xml
    ```  
- [x] **Implement**: CLI `Main`, directory setup in `TestCaseGenerator` ctor

## 6) Refactor & Polish

- [x] Cleanup code, add logging, handle `unbounded` defaults, improve error messages  
- [ ] Extend support for nested types, attributes, other XSD facets

## 7) Future Enhancements

- [ ] CLI argument parsing improvements
- [ ] Custom XML template support
