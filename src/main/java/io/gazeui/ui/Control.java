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

public abstract class Control implements Cloneable {
    
    private String id;
    // If this is a cloned control, stores the control from which this control was cloned.
    private Control sourceControl;
    
    public Control() {
    }
    
    public Control(String id) {
        this.setId(id);
    }
    
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        // TODO: Validate control Id
        this.id = id;
    }

    protected Control getSourceControl() {
        return this.sourceControl;
    }

    private void setSourceControl(Control sourceControl) {
        this.sourceControl = sourceControl;
    }
    
    protected abstract String getRenderScript(Control previousControlState);
    
    @Override
    protected Control clone() {
        try {
            Control newControl = (Control)super.clone();
            newControl.setSourceControl(this);
            
            return newControl;
        } catch (CloneNotSupportedException ex) {
            // Never happens, once Control is implementing Cloneable. 
            throw new RuntimeException(ex);
        }
    }
}