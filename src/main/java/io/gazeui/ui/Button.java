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

import java.util.Optional;

public class Button extends Control {
    
    private String text;
    
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
    
    @Override
    public String toString() {
        return String.format("(%s, Text: %s)", this.getClass().getSimpleName(), this.getText());
    }
    
    @Override
    protected String getRenderScript(Control previousControlState) {
        if (previousControlState == null) {
            return this.getCreateRenderScript();
        } else {
            return this.getUpdateRenderScript((Button)previousControlState);
        }
    }
    
    private String getCreateRenderScript() {
        StringBuilder sbScript = new StringBuilder();
        
        sbScript.append(String.format("var %s = document.createElement('button');\n", this.getClientId()));
        sbScript.append(String.format("%1$s.id = '%1$s';\n", this.getClientId()));
        
        if (this.getText() != null && !this.getText().isEmpty()) {
            // TODO: JavaScript escape
            sbScript.append(String.format("%s.textContent = '%s';\n", this.getClientId(),
                    this.getText()));
        }
        
        return sbScript.toString();
    }
    
    private String getUpdateRenderScript(Button previousControlState) {
        StringBuilder sbScript = new StringBuilder();
        
        String currentText = Optional.ofNullable(this.getText()).orElse("");
        String previousText = Optional.ofNullable(previousControlState.getText()).orElse("");
        
        if (!currentText.equals(previousText)) {
            // TODO: JavaScript escape
            sbScript.append(String.format("%s.textContent = '%s';\n", this.getClientId(), currentText));
        }
        
        if (sbScript.length() > 0) {
            sbScript.insert(0, this.selectionScript());
        }
        
        return sbScript.toString();
    }
}