//
// Copyright (c) 2020 Rosberg Linhares (rosberglinhares@gmail.com)
// Licensed under the MIT license. See LICENSE file in the project root for full license information.
//

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
            assertSame(originalObject, obj, "Original object and action's input argument must refer " +
                    "to the same object");
        }, () -> new RuntimeException());
    }
    
    @Test
    void ifPresentOrElseThrowWhenOptionalEmptyShouldThrow() {
        Optional<Object> optional = Optional.empty();
        
        assertThrows(IndexOutOfBoundsException.class, () -> {
            OptionalExtensions.ifPresentOrElseThrow(optional, obj -> {},
                    () -> new IndexOutOfBoundsException());
        });
    }
}