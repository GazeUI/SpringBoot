//
// Copyright (c) 2019 Rosberg Linhares (rosberglinhares@gmail.com)
// Licensed under the MIT license. See LICENSE file in the project root for full license information.
//

package io.gazeui.text;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StringsTests {

    @Test
    void isNullOrBlankWhenNullShouldReturnTrue() {
        assertTrue(Strings.isNullOrBlank(null));
    }
    
    @Test
    void isNullOrBlankWhenEmptyShouldReturnTrue() {
        assertTrue(Strings.isNullOrBlank(""));
    }
    
    @Test
    void isNullOrBlankWhenBlankShouldReturnTrue() {
        assertTrue(Strings.isNullOrBlank(" "));
        assertTrue(Strings.isNullOrBlank("     "));
        assertTrue(Strings.isNullOrBlank("\r\n   \n   \t\r "));
    }
    
    @Test
    void isNullOrBlankWhenNonBlankShouldReturnFalse() {
        assertFalse(Strings.isNullOrBlank("test"));
        assertFalse(Strings.isNullOrBlank("t "));
        assertFalse(Strings.isNullOrBlank(" t"));
        assertFalse(Strings.isNullOrBlank("          t          "));
    }
}