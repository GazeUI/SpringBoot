//
// Copyright (c) 2019-2020 Rosberg Linhares (rosberglinhares@gmail.com)
// Licensed under the MIT license. See LICENSE file in the project root for full license information.
//

package io.gazeui;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import io.gazeui.event.EventArgs;
import io.gazeui.event.EventHandler;

public class Button extends Control {
    
    private static final String MODULE_NAME = "Button";
    private static final String MODULE_PATH = "./button/button.mjs";
    
    private String text;
    private List<EventHandler<EventArgs>> clickHandlers;
    
    public Button() {
    }
    
    public Button(String text) {
        this.setText(text);
    }
    
    public String getText() {
        return this.text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public void addOnClickHandler(EventHandler<EventArgs> onClickHandler) {
        if (!this.getClickHandlers().contains(onClickHandler)) {
            this.getClickHandlers().add(onClickHandler);
        }
    }
    
    public void removeOnClickHandler(EventHandler<EventArgs> onClickHandler) {
        this.getClickHandlers().remove(onClickHandler);
    }
    
    void processOnClickEvent() {
        EventArgs eventArgs = new EventArgs(this);
        
        for (EventHandler<EventArgs> clickHandler : this.getClickHandlers()) {
            clickHandler.handle(eventArgs);
        }
    }
    
    private List<EventHandler<EventArgs>> getClickHandlers() {
        if (this.clickHandlers == null) {
            this.clickHandlers = new LinkedList<>();
        }
        
        return this.clickHandlers;
    }
    
    @Override
    protected Button clone() {
        Button clonedButton = (Button)super.clone();
        
        if (this.clickHandlers != null) {
            // Doing a shallow copy of the list of handlers
            clonedButton.clickHandlers = new LinkedList<>(this.clickHandlers);
        }
        
        return clonedButton;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(String.format(", Text: '%s'", Optional.ofNullable(this.getText()).orElse("")));
        
        return sb.toString();
    }
    
    @Override
    protected void renderCreation(RenderScriptWriter writer) {
        boolean moduleNeeded = false;
        
        writer.format("let %s = document.createElement('button');\n", this.getClientId().get());
        writer.format("%1$s.id = '%1$s';\n", this.getClientId().get());
        
        // According to the MDN website¹:
        //
        // There is still a security risk whenever you use innerHTML to set strings over which you have no
        // control [...]. For that reason, it is recommended that you do not use innerHTML when inserting plain text;
        // instead, use Node.textContent. This doesn't parse the passed content as HTML, but instead inserts it as
        // raw text.
        // 
        //   [1]: https://developer.mozilla.org/en-US/docs/Web/API/Element/innerHTML
        
        if (this.getText() != null && !this.getText().isEmpty()) {
            // TODO: JavaScript escape
            writer.format("%s.textContent = '%s';\n", this.getClientId().get(), this.getText());
        }
        
        // Here we are accessing the variable directly to avoid the unnecessary creation of the collection
        // when there are no handlers.
        if (this.clickHandlers != null && !this.clickHandlers.isEmpty()) {
            moduleNeeded = true;
            
            writer.format("%s.addEventListener('click', %s.onClickHandler, {\n", this.getClientId().get(), MODULE_NAME);
            writer.print(
                "    capture: false,\n" +
                "    passive: true\n" +
                "});\n");
        }
        
        if (moduleNeeded) {
            writer.importModule(MODULE_NAME, MODULE_PATH);
        }
    }
    
    @Override
    protected void renderUpdate(RenderScriptWriter writer, Control previousControlState) {
        Button previousButton = (Button)previousControlState;
        RenderScriptWriter localWriter = new RenderScriptWriter();
        boolean moduleNeeded = false;
        
        String currentText = Optional.ofNullable(this.getText()).orElse("");
        String previousText = Optional.ofNullable(previousButton.getText()).orElse("");
        
        if (!currentText.equals(previousText)) {
            // TODO: JavaScript escape
            localWriter.format("%s.textContent = '%s';\n", this.getClientId().get(), currentText);
        }
        
        if (previousButton.getClickHandlers().isEmpty() &&
                this.clickHandlers != null && !this.clickHandlers.isEmpty()) {
            moduleNeeded = true;
            
            localWriter.format("%s.addEventListener('click', %s.onClickHandler, {\n", this.getClientId().get(),
                    MODULE_NAME);
            localWriter.print(
                "    capture: false,\n" +
                "    passive: true\n" +
                "});\n");
        } else if (!previousButton.getClickHandlers().isEmpty() && this.getClickHandlers().isEmpty()) {
            moduleNeeded = true;
            
            localWriter.format("%s.removeEventListener('click', %s.onClickHandler, {\n", this.getClientId().get(),
                    MODULE_NAME);
            localWriter.print(
                "    capture: false,\n" +
                "    passive: true\n" +
                "});\n");
        }
        
        if (!localWriter.isEmpty()) {
            if (moduleNeeded) {
                writer.importModule(MODULE_NAME, MODULE_PATH);
            }
            
            writer.print(this.selectionScript());
            writer.print(localWriter);
        }
    }
}