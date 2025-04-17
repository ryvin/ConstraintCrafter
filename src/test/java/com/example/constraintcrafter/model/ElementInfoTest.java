package com.example.constraintcrafter.model;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class ElementInfoTest {

    @Test
    void gettersAndHasEnum() {
        // no enums
        ElementInfo e1 = new ElementInfo("Foo", 1, 2, Collections.emptyList());
        assertEquals("Foo", e1.getName());
        assertEquals(1,     e1.getMinOccurs());
        assertEquals(2,     e1.getMaxOccurs());
        assertFalse(e1.hasEnum());

        // with enums
        List<String> enums = List.of("X","Y");
        ElementInfo e2 = new ElementInfo("Bar", 0, -1, enums);
        assertEquals("Bar",        e2.getName());
        assertEquals(0,            e2.getMinOccurs());
        assertEquals(-1,           e2.getMaxOccurs());
        assertTrue(e2.hasEnum());
        assertEquals(enums,        e2.getEnumValues());
    }
}
