//
// Copyright (c) 2019-2020 Rosberg Linhares (rosberglinhares@gmail.com)
// Licensed under the MIT license. See LICENSE file in the project root for full license information.
//

package io.gazeui;

enum ErrorMessage {
    CONTROL_COLLECTION_MUST_HAVE_OWNER("The collection must have an owner"),
    CONTROL_COLLECTION_ADD_SET_EXISTING_ELEMENT_USING_ITERATOR("It is not possible to add/set an existing element to the controls collection using an iterator"),
    
    HTML_VALIDATION_TITLE_MUST_NOT_BE_EMPTY("According to the HTML specification, the title element must contain at least one non-whitespace character"),
    
    UNEXPECTED_ERROR_CREATING_INITIAL_PAGE("Unexpected error trying to create the initial page"),
    UNEXPECTED_ERROR_PROCESSING_EVENT("Unexpected error processing event '%s' on control '%s'"),
    COULD_NOT_PROCESS_EVENT_CONTROL_ID_NOT_FOUND("Could not process event '%s': Control Id '%s' not found");
    
    private final String message;
    
    private ErrorMessage(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return this.message;
    }
}