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

package io.gazeui.ui;

enum ErrorMessage {
    CONTROL_COLLECTION_MUST_HAVE_OWNER("The collection must have an owner"),
    CONTROL_COLLECTION_ADD_SET_EXISTING_ELEMENT_USING_ITERATOR("It is not possible to add/set an existing element to the controls collection using an iterator"),
    
    HTML_VALIDATION_TITLE_MUST_NOT_BE_EMPTY("According to the HTML specification, the title element must contain at least one non-whitespace character"),
    
    COULD_NOT_PROCESS_EVENT("Could not process event '%s' on control '%s'"),
    COULD_NOT_PROCESS_EVENT_CONTROL_ID_NOT_FOUND("Could not process event '%s': Control Id '%s' not found");
    
    private final String message;
    
    private ErrorMessage(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return this.message;
    }
}