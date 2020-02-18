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
    protected void render(RenderScriptWriter writer, Control previousControlState) {
        if (previousControlState == null) {
            this.renderCreation(writer);
        } else {
            this.renderUpdate(writer, (Button)previousControlState);
        }
    }
    
    private void renderCreation(RenderScriptWriter writer) {
        boolean moduleNeeded = false;
        
        writer.format("let %s = document.createElement('button');\n", this.getClientId());
        writer.format("%1$s.id = '%1$s';\n", this.getClientId());
        
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
            writer.format("%s.textContent = '%s';\n", this.getClientId(), this.getText());
        }
        
        // Here we are accessing the variable directly to avoid the unnecessary creation of the collection
        // when there are no handlers.
        if (this.clickHandlers != null && !this.clickHandlers.isEmpty()) {
            moduleNeeded = true;
            
            writer.format("%s.addEventListener('click', %s.onClickHandler, {\n", this.getClientId(), MODULE_NAME);
            writer.print(
                "    capture: false,\n" +
                "    passive: true\n" +
                "});\n");
        }
        
        if (moduleNeeded) {
            writer.importModule(MODULE_NAME, MODULE_PATH);
        }
    }
    
    private void renderUpdate(RenderScriptWriter writer, Button previousButton) {
        RenderScriptWriter localWriter = new RenderScriptWriter();
        boolean moduleNeeded = false;
        
        String currentText = Optional.ofNullable(this.getText()).orElse("");
        String previousText = Optional.ofNullable(previousButton.getText()).orElse("");
        
        if (!currentText.equals(previousText)) {
            // TODO: JavaScript escape
            localWriter.format("%s.textContent = '%s';\n", this.getClientId(), currentText);
        }
        
        if (previousButton.getClickHandlers().isEmpty() &&
                this.clickHandlers != null && !this.clickHandlers.isEmpty()) {
            moduleNeeded = true;
            
            localWriter.format("%s.addEventListener('click', %s.onClickHandler, {\n", this.getClientId(),
                    MODULE_NAME);
            localWriter.print(
                "    capture: false,\n" +
                "    passive: true\n" +
                "});\n");
        } else if (!previousButton.getClickHandlers().isEmpty() && this.getClickHandlers().isEmpty()) {
            moduleNeeded = true;
            
            localWriter.format("%s.removeEventListener('click', %s.onClickHandler, {\n", this.getClientId(),
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