//
// Copyright (c) 2019-2020 Rosberg Linhares (rosberglinhares@gmail.com)
// Licensed under the MIT license. See LICENSE file in the project root for full license information.
//

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
    protected void renderCreation(RenderScriptWriter writer) {
        writer.format("let %s = document.createElement('span');\n", this.getClientId().get());
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
    }
    
    @Override
    protected void renderUpdate(RenderScriptWriter writer, Control previousControlState) {
        Label previousLabel = (Label)previousControlState;
        String currentText = Optional.ofNullable(this.getText()).orElse("");
        String previousText = Optional.ofNullable(previousLabel.getText()).orElse("");
        
        if (!currentText.equals(previousText)) {
            writer.print(this.selectionScript());
            
            // TODO: JavaScript escape
            writer.format("%s.textContent = '%s';\n", this.getClientId().get(), currentText);
        }
    }
}