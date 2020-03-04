//
// Copyright (c) 2020 Rosberg Linhares (rosberglinhares@gmail.com)
// Licensed under the MIT license. See LICENSE file in the project root for full license information.
//

package io.gazeui;

import java.util.Optional;

import io.gazeui.text.Strings;

public abstract class WebPage extends ContainerControl<Control> {
    
    private String title;
    // The client ID must be unique per page because it will be used as the HTML ID attribute.
    private int controlsCounter = 0;
    
    public WebPage() {
    }
    
    public WebPage(String title) {
        this.setTitle(title);
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(String title) {
        if (!Strings.isNullOrBlank(title)) {
            this.title = title;
        } else {
            throw new IllegalArgumentException(
                    ErrorMessage.HTML_VALIDATION_TITLE_MUST_NOT_BE_EMPTY.getMessage());
        }
    }
    
    String generateAutomaticControlId() {
        return String.format("ctl%02d", ++this.controlsCounter);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(String.format(", Title: '%s'", Optional.ofNullable(this.getTitle()).orElse("")));
        
        return sb.toString();
    }
    
    @Override
    protected String creationScript() {
        // It is not necessary to create a container for the Page, because document.body will be used as such.
        return "";
    }
    
    @Override
    protected String selectionScript() {
        // It is not necessary to run any selection script, because document.body can be directly accessed.
        return "";
    }
    
    @Override
    protected String identificationToken() {
        return "document.body";
    }
    
    @Override
    protected void renderCreation(RenderScriptWriter writer) {
        if (Strings.isNullOrBlank(this.getTitle())) {
            // According to the HTML 5.2 specification, the title element must contain at least one
            // non-whitespace character. See https://www.w3.org/TR/html52/document-metadata.html#the-title-element
            // for details.
            this.setTitle(this.getClass().getSimpleName());
        }
        
        // TODO: JavaScript escape
        writer.format("document.title = '%s';\n", this.getTitle());
        
        // Add the default ContainerControl script
        super.renderCreation(writer);
    }
    
    @Override
    protected void renderUpdate(RenderScriptWriter writer, Control previousControlState) {
        WebPage previousPage = (WebPage)previousControlState;
        
        if (!this.getTitle().equals(previousPage.getTitle())) {
            // TODO: JavaScript escape
            writer.format("document.title = '%s';\n", this.getTitle());
        }
        
        // Add the default ContainerControl script
        super.renderUpdate(writer, previousPage);
    }
}