//
// Copyright (c) 2019 Rosberg Linhares (rosberglinhares@gmail.com)
// Licensed under the MIT license. See LICENSE file in the project root for full license information.
//

package io.gazeui.text;

public final class Strings {
    
    private Strings() {
        // No instances allowed
    }
    
    /**
     * Returns true if the string is null, empty or contains only white space characters, otherwise
     * false. This is equivalent to the JDK 11 String#isBlank method.
     */
    public static boolean isNullOrBlank(String str) {
        if (str != null) {
            return str.chars().allMatch(Character::isWhitespace);
        } else {
            return true;
        }
    }
}