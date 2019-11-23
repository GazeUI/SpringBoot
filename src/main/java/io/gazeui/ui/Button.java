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
    protected String getRenderScript(Control previousControlState) {
        StringBuilder sbRenderScript = new StringBuilder();
        
        if (previousControlState == null) {
            sbRenderScript.append("var btn = document.createElement('button');\n");
            sbRenderScript.append(String.format("btn.id = '%s';\n", this.getClientId()));
            
            if (this.getText() != null && !this.getText().isEmpty()) {
                // TODO: JavaScript escape
                sbRenderScript.append(String.format("btn.textContent = '%s';\n", this.getText()));
            }
            
            sbRenderScript.append("document.body.appendChild(btn);");
        } else {
            Button previousButtonState = (Button)previousControlState;
            
            if (!this.getText().equals(previousButtonState.getText())) {
                sbRenderScript.append(String.format(
                        "var btn = document.getElementById('%s');\n" + 
                        "btn.textContent = '%s';", this.getClientId(), this.getText()));
            }
        }
        
        return sbRenderScript.toString();
    }
}