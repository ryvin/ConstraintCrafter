package com.example.constraintcrafter;

import com.example.constraintcrafter.model.ElementInfo;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class SchemaModelTest {

    @Test
    void loadSimpleSchema() throws Exception {
        SchemaModel model = SchemaModel.load("src/test/resources/schema/Simple.xsd");
        List<ElementInfo> elems = model.getElements();
        assertEquals(3, elems.size());

        ElementInfo a = elems.stream().filter(e -> e.getName().equals("A")).findFirst().orElse(null);
        assertNotNull(a);
        assertEquals(1, a.getMinOccurs());
        assertEquals(1, a.getMaxOccurs());
        assertFalse(a.hasEnum());

        ElementInfo b = elems.stream().filter(e -> e.getName().equals("B")).findFirst().orElse(null);
        assertNotNull(b);
        assertEquals(0, b.getMinOccurs());
        assertEquals(2, b.getMaxOccurs());
        assertFalse(b.hasEnum());

        ElementInfo c = elems.stream().filter(e -> e.getName().equals("C")).findFirst().orElse(null);
        assertNotNull(c);
        assertEquals(1, c.getMinOccurs());
        assertEquals(1, c.getMaxOccurs());
        assertTrue(c.hasEnum());
        assertEquals(List.of("X","Y"), c.getEnumValues());
    }
}
