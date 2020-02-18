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

package io.gazeui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ControlCollectionTests {

    private ControlCollection<Control> controlCollection;
    private Button b1;
    private Button b2;
    private Button b3;
    private Button b4;
    private Button b5;
    private Button b6;
    private Button b7;
    
    @BeforeEach
    void setUp() {
        this.controlCollection = new ControlCollection<>(new ContainerControl<>());
        
        this.b1 = new Button("b1");
        this.b2 = new Button("b2");
        this.b3 = new Button("b3");
        this.b4 = new Button("b4");
        this.b5 = new Button("b5");
        this.b6 = new Button("b6");
        this.b7 = new Button("b7");
    }
    
    @Test
    void addControlNoDuplicates() {
        controlCollection.add(b1);
        controlCollection.add(b2);
        controlCollection.add(b3);
        
        assertEquals(3, controlCollection.size());
        assertEquals(b1, controlCollection.get(0));
        assertEquals(b2, controlCollection.get(1));
        assertEquals(b3, controlCollection.get(2));
    }
    
    @Test
    void addControlWithDuplicates() {
        controlCollection.add(b1);
        controlCollection.add(b2);
        controlCollection.add(b3);
        controlCollection.add(b4);
        controlCollection.add(b1);
        controlCollection.add(b2);
        controlCollection.add(b2);
        
        assertEquals(4, controlCollection.size());
        assertEquals(b3, controlCollection.get(0));
        assertEquals(b4, controlCollection.get(1));
        assertEquals(b1, controlCollection.get(2));
        assertEquals(b2, controlCollection.get(3));
    }
    
    @Test
    void addIndexControlNoDuplicates() {
        controlCollection.add(b1);
        controlCollection.add(b2);
        controlCollection.add(b3);
        controlCollection.add(b4);
        controlCollection.add(1, b5);
        controlCollection.add(4, b6);
        controlCollection.add(2, b7);
        
        assertEquals(7, controlCollection.size());
        assertEquals(b1, controlCollection.get(0));
        assertEquals(b5, controlCollection.get(1));
        assertEquals(b7, controlCollection.get(2));
        assertEquals(b2, controlCollection.get(3));
        assertEquals(b3, controlCollection.get(4));
        assertEquals(b6, controlCollection.get(5));
        assertEquals(b4, controlCollection.get(6));
    }
    
    @Test
    void addIndexControlWithDuplicates() {
        controlCollection.add(b1);
        controlCollection.add(b2);
        controlCollection.add(b3);
        controlCollection.add(b4);
        controlCollection.add(1, b5);
        controlCollection.add(3, b1);
        controlCollection.add(0, b3);
        controlCollection.add(2, b2);
        
        assertEquals(5, controlCollection.size());
        assertEquals(b3, controlCollection.get(0));
        assertEquals(b5, controlCollection.get(1));
        assertEquals(b2, controlCollection.get(2));
        assertEquals(b1, controlCollection.get(3));
        assertEquals(b4, controlCollection.get(4));
    }
    
    @Test
    void addAllCollection() {
        Collection<Control> collectionToAdd = new ArrayList<>();
        collectionToAdd.add(b2);
        collectionToAdd.add(b5);
        collectionToAdd.add(b3);
        collectionToAdd.add(b2);
        collectionToAdd.add(b6);
        collectionToAdd.add(b7);
        collectionToAdd.add(b2);
        collectionToAdd.add(b6);
        
        controlCollection.add(b1);
        controlCollection.add(b2);
        controlCollection.add(b3);
        controlCollection.add(b4);
        
        controlCollection.addAll(collectionToAdd);
        
        assertEquals(7, controlCollection.size());
        assertEquals(b1, controlCollection.get(0));
        assertEquals(b4, controlCollection.get(1));
        assertEquals(b2, controlCollection.get(2));
        assertEquals(b5, controlCollection.get(3));
        assertEquals(b3, controlCollection.get(4));
        assertEquals(b6, controlCollection.get(5));
        assertEquals(b7, controlCollection.get(6));
    }
    
    @Test
    void addAllIndexCollection() {
        Collection<Control> collectionToAdd = new ArrayList<>();
        collectionToAdd.add(b2);
        collectionToAdd.add(b5);
        collectionToAdd.add(b3);
        collectionToAdd.add(b2);
        collectionToAdd.add(b6);
        collectionToAdd.add(b7);
        collectionToAdd.add(b2);
        collectionToAdd.add(b6);
        
        controlCollection.add(b1);
        controlCollection.add(b2);
        controlCollection.add(b3);
        controlCollection.add(b4);
        
        controlCollection.addAll(1, collectionToAdd);
        
        assertEquals(7, controlCollection.size());
        assertEquals(b1, controlCollection.get(0));
        assertEquals(b2, controlCollection.get(1));
        assertEquals(b5, controlCollection.get(2));
        assertEquals(b3, controlCollection.get(3));
        assertEquals(b6, controlCollection.get(4));
        assertEquals(b7, controlCollection.get(5));
        assertEquals(b4, controlCollection.get(6));
    }
    
    @Test
    void setNonExistingElements() {
        controlCollection.add(b1);
        controlCollection.add(b2);
        controlCollection.add(b3);
        controlCollection.add(b4);
        
        controlCollection.set(1, b5);
        controlCollection.set(3, b6);
        
        assertEquals(4, controlCollection.size());
        assertEquals(b1, controlCollection.get(0));
        assertEquals(b5, controlCollection.get(1));
        assertEquals(b3, controlCollection.get(2));
        assertEquals(b6, controlCollection.get(3));
    }
    
    @Test
    void setShouldSwapExistingElements() {
        controlCollection.add(b1);
        controlCollection.add(b2);
        controlCollection.add(b3);
        controlCollection.add(b4);
        controlCollection.add(b5);
        
        controlCollection.set(2, b3);
        controlCollection.set(3, b2);
        controlCollection.set(0, b5);
        controlCollection.set(4, b1);
        
        assertEquals(5, controlCollection.size());
        assertEquals(b5, controlCollection.get(0));
        assertEquals(b3, controlCollection.get(1));
        assertEquals(b4, controlCollection.get(2));
        assertEquals(b2, controlCollection.get(3));
        assertEquals(b1, controlCollection.get(4));
    }
    
    @SuppressWarnings("unlikely-arg-type")
    @Test
    void removeObject() {
        controlCollection.add(b1);
        controlCollection.add(b2);
        controlCollection.add(b3);
        controlCollection.add(b4);
        controlCollection.add(b5);
        controlCollection.add(b6);
        
        controlCollection.remove(b2);
        controlCollection.remove(null);
        controlCollection.remove("dummyString");
        controlCollection.remove(new Object());
        controlCollection.remove(b4);
        controlCollection.remove(b4);
        controlCollection.remove(b7);
        controlCollection.remove(b6);
        
        assertEquals(3, controlCollection.size());
        assertEquals(b1, controlCollection.get(0));
        assertEquals(b3, controlCollection.get(1));
        assertEquals(b5, controlCollection.get(2));
    }
    
    @Test
    void removeIndex() {
        controlCollection.add(b1);
        controlCollection.add(b2);
        controlCollection.add(b3);
        controlCollection.add(b4);
        controlCollection.add(b5);
        
        controlCollection.remove(2);
        controlCollection.remove(3);
        controlCollection.remove(0);
        
        assertEquals(2, controlCollection.size());
        assertEquals(b2, controlCollection.get(0));
        assertEquals(b4, controlCollection.get(1));
    }
    
    @Test
    void removeAll() {
        Collection<Object> collectionToRemove = new ArrayList<>();
        collectionToRemove.add(b2);
        collectionToRemove.add(null);
        collectionToRemove.add("dummyString");
        collectionToRemove.add(b4);
        collectionToRemove.add(b4);
        collectionToRemove.add(b6);
        collectionToRemove.add(b6);
        collectionToRemove.add(new Object());
        
        controlCollection.add(b1);
        controlCollection.add(b2);
        controlCollection.add(b3);
        controlCollection.add(b4);
        controlCollection.add(b5);
        
        controlCollection.removeAll(collectionToRemove);
        
        assertEquals(3, controlCollection.size());
        assertEquals(b1, controlCollection.get(0));
        assertEquals(b3, controlCollection.get(1));
        assertEquals(b5, controlCollection.get(2));
    }
    
    @Test
    void retainAll() {
        Collection<Object> collectionToRetain = new ArrayList<>();
        collectionToRetain.add(b1);
        collectionToRetain.add(null);
        collectionToRetain.add("dummyString");
        collectionToRetain.add(b3);
        collectionToRetain.add(b3);
        collectionToRetain.add(b5);
        collectionToRetain.add(new Object());
        collectionToRetain.add(b6);
        collectionToRetain.add(b6);
        
        controlCollection.add(b1);
        controlCollection.add(b2);
        controlCollection.add(b3);
        controlCollection.add(b4);
        controlCollection.add(b5);
        
        controlCollection.retainAll(collectionToRetain);
        
        assertEquals(3, controlCollection.size());
        assertEquals(b1, controlCollection.get(0));
        assertEquals(b3, controlCollection.get(1));
        assertEquals(b5, controlCollection.get(2));
    }
    
    @Test
    void clear() {
        controlCollection.add(b1);
        controlCollection.add(b2);
        controlCollection.add(b3);
        controlCollection.add(b4);
        
        controlCollection.clear();
        
        assertEquals(0, controlCollection.size());
    }
    
    @Test
    void subList() {
        controlCollection.add(b1);
        controlCollection.add(b2);
        controlCollection.add(b3);
        controlCollection.add(b4);
        controlCollection.add(b5);
        controlCollection.add(b6);
        controlCollection.add(b7);
        
        controlCollection.subList(2, 5).clear();
        
        assertEquals(4, controlCollection.size());
        assertEquals(b1, controlCollection.get(0));
        assertEquals(b2, controlCollection.get(1));
        assertEquals(b6, controlCollection.get(2));
        assertEquals(b7, controlCollection.get(3));
    }
    
    @Test
    void iteratorRemove() {
        controlCollection.add(b1);
        controlCollection.add(b2);
        controlCollection.add(b3);
        controlCollection.add(b4);
        controlCollection.add(b5);
        
        Iterator<Control> it = controlCollection.iterator();
        
        it.next();
        it.next();
        it.remove();
        it.next();
        it.next();
        it.remove();
        
        assertEquals(3, controlCollection.size());
        assertEquals(b1, controlCollection.get(0));
        assertEquals(b3, controlCollection.get(1));
        assertEquals(b5, controlCollection.get(2));
    }
    
    @Test
    void listIteratorRemove() {
        controlCollection.add(b1);
        controlCollection.add(b2);
        controlCollection.add(b3);
        controlCollection.add(b4);
        
        ListIterator<Control> it = controlCollection.listIterator();
        
        it.next();
        it.next();
        it.next();
        it.previous();
        it.previous();
        
        it.remove();
        
        assertEquals(3, controlCollection.size());
        assertEquals(b1, controlCollection.get(0));
        assertEquals(b3, controlCollection.get(1));
        assertEquals(b4, controlCollection.get(2));
    }

    @Test
    void listIteratorAddShouldAllowsNonExisting() {
        controlCollection.add(b1);
        controlCollection.add(b2);
        controlCollection.add(b3);
        
        ListIterator<Control> it = controlCollection.listIterator();
        
        it.next();
        it.next();
        it.add(b4);
        it.add(b5);
        it.next();
        it.previous();
        it.previous();
        it.previous();
        it.previous();
        it.previous();
        it.add(b6);
        
        assertEquals(6, controlCollection.size());
        assertEquals(b6, controlCollection.get(0));
        assertEquals(b1, controlCollection.get(1));
        assertEquals(b2, controlCollection.get(2));
        assertEquals(b4, controlCollection.get(3));
        assertEquals(b5, controlCollection.get(4));
        assertEquals(b3, controlCollection.get(5));
    }
    
    @Test
    void listIteratorAddShouldThrowsExceptionWhenAddExisting() {
        controlCollection.add(b1);
        controlCollection.add(b2);
        controlCollection.add(b3);
        
        ListIterator<Control> it = controlCollection.listIterator();
        it.next();
        
        assertThrows(UnsupportedOperationException.class, () -> it.add(b2));
        
        it.add(b4);
        
        assertThrows(UnsupportedOperationException.class, () -> it.add(b4));
    }
    
    @Test
    void listIteratorSetShouldAllowsSetNonExisting() {
        controlCollection.add(b1);
        controlCollection.add(b2);
        controlCollection.add(b3);
        controlCollection.add(b4);
        
        ListIterator<Control> it = controlCollection.listIterator();
        
        it.next();
        it.set(b5);
        it.next();
        it.next();
        it.next();
        it.remove();
        it.previous();
        it.set(b4);
        it.previous();
        it.previous();
        it.set(b6);
        
        assertEquals(3, controlCollection.size());
        assertEquals(b6, controlCollection.get(0));
        assertEquals(b2, controlCollection.get(1));
        assertEquals(b4, controlCollection.get(2));
    }
    
    @Test
    void listIteratorSetShouldThrowsExceptionWhenSetExisting() {
        controlCollection.add(b1);
        controlCollection.add(b2);
        controlCollection.add(b3);
        
        ListIterator<Control> it = controlCollection.listIterator();
        it.next();
        
        assertThrows(UnsupportedOperationException.class, () -> it.set(b3));
        
        it.add(b4);
        
        assertThrows(UnsupportedOperationException.class, () -> it.set(b4));
    }
}