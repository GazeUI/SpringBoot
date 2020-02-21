/*
 * MIT License
 * 
 * Copyright (c) 2020 Rosberg Linhares (rosberglinhares@gmail.com)
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

/**
 * 
 */
package io.gazeui.util;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

class OptionalExtensionsTests {
    
    @Test
    void ifPresentOrElseThrowWhenActionIsNotCalledShouldFail() {
        Optional<Object> optional = Optional.of(new Object());
        AtomicBoolean actionCalled = new AtomicBoolean(false);
        
        OptionalExtensions.ifPresentOrElseThrow(optional, obj -> {
            actionCalled.set(true);
        }, () -> new RuntimeException());
        
        if (!actionCalled.get()) {
            fail("Action was not called");
        }
    }
    
    @Test
    void ifPresentOrElseThrowWhenArgumentAndOriginalAreNotTheSameShouldFail() {
        Object originalObject = new Object();
        Optional<Object> optional = Optional.of(originalObject);
        
        OptionalExtensions.ifPresentOrElseThrow(optional, obj -> {
            assertSame(originalObject, obj, "Original object and action's input argument must refer to the same object");
        }, () -> new RuntimeException());
    }
    
    @Test
    void ifPresentOrElseThrowWhenOptionalEmptyShouldThrow() {
        Optional<Object> optional = Optional.empty();
        
        assertThrows(IndexOutOfBoundsException.class, () -> {
            OptionalExtensions.ifPresentOrElseThrow(optional, obj -> {}, () -> new IndexOutOfBoundsException());
        });
    }
}