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

import java.util.Optional;

public class Label extends Control {
    
    private String text;
    
    public Label() {
    }
    
    public Label(String text) {
        this.setText(text);
    }
    
    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
    @Override
    protected void render(RenderScriptWriter writer, Control previousControlState) {
        if (previousControlState == null) {
            this.renderCreation(writer);
        } else {
            this.renderUpdate(writer, (Label)previousControlState);
        }
    }
    
    private void renderCreation(RenderScriptWriter writer) {
        writer.format("let %s = document.createElement('span');\n", this.getClientId());
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
    }
    
    private void renderUpdate(RenderScriptWriter writer, Label previousControlState) {
        String currentText = Optional.ofNullable(this.getText()).orElse("");
        String previousText = Optional.ofNullable(previousControlState.getText()).orElse("");
        
        if (!currentText.equals(previousText)) {
            writer.print(this.selectionScript());
            
            // TODO: JavaScript escape
            writer.format("%s.textContent = '%s';\n", this.getClientId(), currentText);
        }
    }
}