/*
 * MIT License
 * 
 * Copyright (c) 2019 Rosberg Linhares (rosberglinhares@gmail.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.gazeui.collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ListsTests {
    
    private Comparator<Object> genericEqualsComparator;
    
    @BeforeEach
    void setUp() {
        this.genericEqualsComparator = new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                return o1.equals(o2) ? 0 : -1;
            }
        };
    }
    
    @Test
    void longestCommonSubsequenceEmptyCollections() {
        List<String> l1;
        List<String> l2;
        List<String> result;
        
        l1 = Collections.emptyList();
        l2 = Collections.emptyList();
        result = Lists.longestCommonSubsequence(l1, l2, genericEqualsComparator);
        assertTrue(result.isEmpty());
        
        l1 = Arrays.asList("X", "M", "J", "Y", "A", "U", "Z");
        l2 = Collections.emptyList();
        
        result = Lists.longestCommonSubsequence(l1, l2, genericEqualsComparator);
        assertTrue(result.isEmpty());
        
        result = Lists.longestCommonSubsequence(l2, l1, genericEqualsComparator);
        assertTrue(result.isEmpty());
    }
    
    @Test
    void longestCommonSubsequenceSameCollections() {
        List<String> l1;
        List<String> l2;
        List<String> result;
        
        l1 = Arrays.asList("X", "M", "J", "Y", "A", "U", "Z");
        l2 = Arrays.asList("X", "M", "J", "Y", "A", "U", "Z");
        result = Lists.longestCommonSubsequence(l1, l2, genericEqualsComparator);
        assertIterableEquals(l1, result);
        
        l1 = Arrays.asList("Y");
        l2 = Arrays.asList("Y");
        result = Lists.longestCommonSubsequence(l1, l2, genericEqualsComparator);
        assertIterableEquals(l1, result);
    }
    
    @Test
    void longestCommonSubsequenceExistingCommonSubsequence() {
        List<String> listString1;
        List<String> listString2;
        List<String> resultString;
        
        listString1 = Arrays.asList("A", "B", "C", "D", "G", "H");
        listString2 = Arrays.asList("A", "E", "D", "F", "H", "R");
        resultString = Lists.longestCommonSubsequence(listString1, listString2, genericEqualsComparator);
        assertIterableEquals(Arrays.asList("A", "D", "H"), resultString);
        
        listString1 = Arrays.asList("A", "G", "G", "T", "A", "B");
        listString2 = Arrays.asList("G", "X", "T", "X", "A", "Y", "B");
        resultString = Lists.longestCommonSubsequence(listString1, listString2, genericEqualsComparator);
        assertIterableEquals(Arrays.asList("G", "T", "A", "B"), resultString);
        
        listString1 = Arrays.asList("X", "M", "J", "Y", "A", "U", "Z");
        listString2 = Arrays.asList("M", "Z", "J", "A", "W", "X", "U");
        resultString = Lists.longestCommonSubsequence(listString1, listString2, genericEqualsComparator);
        assertIterableEquals(Arrays.asList("M", "J", "A", "U"), resultString);
        
        listString1 = Arrays.asList("X", "M", "J", "Y", "A", "U", "Z");
        listString2 = Arrays.asList("J");
        resultString = Lists.longestCommonSubsequence(listString1, listString2, genericEqualsComparator);
        assertIterableEquals(Arrays.asList("J"), resultString);
        
        listString1 = Arrays.asList("X", "M", "J", "Y", "A", "U", "Z");
        listString2 = Arrays.asList("M", "Z");
        resultString = Lists.longestCommonSubsequence(listString1, listString2, genericEqualsComparator);
        assertIterableEquals(Arrays.asList("M", "Z"), resultString);
        
        List<Integer> listInteger1 = Arrays.asList(1, 2, 3, 4, 1);
        List<Integer> listInteger2 = Arrays.asList(3, 4, 1, 2, 1, 3);
        List<Integer> resultInteger;
        
        resultInteger = Lists.longestCommonSubsequence(listInteger1, listInteger2, genericEqualsComparator);
        assertIterableEquals(Arrays.asList(3, 4, 1), resultInteger);
    }
    
    @Test
    void longestCommonSubsequenceNonExistingCommonSubsequence() {
        List<String> list1;
        List<String> list2;
        List<String> result;
        
        list1 = Arrays.asList("A", "B", "C", "D", "G", "H");
        list2 = Arrays.asList("Z", "Y", "X", "W", "V", "U");
        
        result = Lists.longestCommonSubsequence(list1, list2, genericEqualsComparator);
        assertTrue(result.isEmpty());
    }
    
    private static class DummyClass {
        private String fieldOne;
        private int fieldTwo;
        
        public DummyClass(String fieldOne, int fieldTwo) {
            this.fieldOne = fieldOne;
            this.fieldTwo = fieldTwo;
        }
        
        public String getFieldOne() {
            return fieldOne;
        }
        
        public int getFieldTwo() {
            return fieldTwo;
        }
    }
    
    @Test
    void toMapValueValue() {
        DummyClass obj1 = new DummyClass("One", 1);
        DummyClass obj2 = new DummyClass("Two", 2);
        DummyClass obj3 = new DummyClass("Three", 3);
        List<DummyClass> list = Arrays.asList(obj1, obj2, obj3);
        
        Map<String, Integer> result = Lists.toMap(list, DummyClass::getFieldOne, DummyClass::getFieldTwo);
        
        assertEquals(3, result.size());
        assertEquals(1, result.get("One"));
        assertEquals(2, result.get("Two"));
        assertEquals(3, result.get("Three"));
    }
    
    @Test
    void toMapValueObject() {
        DummyClass obj1 = new DummyClass("One", 1);
        DummyClass obj2 = new DummyClass("Two", 2);
        DummyClass obj3 = new DummyClass("Three", 3);
        List<DummyClass> list = Arrays.asList(obj1, obj2, obj3);
        
        Map<String, DummyClass> result = Lists.toMap(list, DummyClass::getFieldOne, Function.identity());
        
        assertEquals(3, result.size());
        assertEquals(obj1, result.get("One"));
        assertEquals(obj2, result.get("Two"));
        assertEquals(obj3, result.get("Three"));
    }
    
    @Test
    void toMapObjectValue() {
        DummyClass obj1 = new DummyClass("One", 1);
        DummyClass obj2 = new DummyClass("Two", 2);
        DummyClass obj3 = new DummyClass("Three", 3);
        List<DummyClass> list = Arrays.asList(obj1, obj2, obj3);
        
        Map<DummyClass, String> result = Lists.toMap(list, Function.identity(), DummyClass::getFieldOne);
        
        assertEquals(3, result.size());
        assertEquals("One", result.get(obj1));
        assertEquals("Two", result.get(obj2));
        assertEquals("Three", result.get(obj3));
    }
}