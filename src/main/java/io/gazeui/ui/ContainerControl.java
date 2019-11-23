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

public class ContainerControl extends Control {
    
    // To generate the automatic ID for controls, we have to know when they are added.
    // So the use of a custom control collection.
    private ControlCollection controls;
    
    public ControlCollection getControls() {
        if (this.controls == null) {
            this.controls = new ControlCollection(this);
        }
        
        return this.controls;
    }
    
    @Override
    protected ContainerControl clone() {
        ContainerControl newContainerControl = (ContainerControl)super.clone();
        
        newContainerControl.controls = new ControlCollection(newContainerControl);
        
        for (Control control : this.getControls()) {
            newContainerControl.getControls().add(control.clone());
        }
        
        return newContainerControl;
    }
    
    @Override
    protected String getRenderScript(Control previousControlState) {
        StringBuilder sbAddControlsScript = new StringBuilder();
        
        if (previousControlState == null) {
            for (Control childControl : this.getControls()) {
                sbAddControlsScript.append(childControl.getRenderScript(null));
                sbAddControlsScript.append('\n');
            }
            
            return sbAddControlsScript.toString();
        } else {
            ContainerControl previousContainerControlState = (ContainerControl)previousControlState;
            StringBuilder sbRemoveControlsScript = new StringBuilder();
            StringBuilder sbUpdateControlsScript = new StringBuilder();
            
            // 1. Remove
            for (Control previousChildControlState : previousContainerControlState.getControls()) {
                if (!this.getControls().contains(previousChildControlState.getSourceControl())) {
                    String removeControlScript = String.format(
                            "var ctl = document.getElementById('%s');\n" + 
                            "ctl.remove();", previousChildControlState.getSourceControl().getClientId());
                    
                    sbRemoveControlsScript.append(removeControlScript);
                    sbRemoveControlsScript.append('\n');
                }
            }
            
            // 2. Update and Add
            for (Control childControl : this.getControls()) {
                boolean childControlFound = false;
                
                for (Control previousChildControlState : previousContainerControlState.getControls()) {
                    if (previousChildControlState.getSourceControl() == childControl) {
                        String updateControlScript = childControl.getRenderScript(previousChildControlState);
                        
                        if (!updateControlScript.isEmpty()) {
                            sbUpdateControlsScript.append(updateControlScript);
                            sbUpdateControlsScript.append('\n');
                        }
                        
                        childControlFound = true;
                        break;
                    }
                }
                
                if (!childControlFound) {
                    sbAddControlsScript.append(childControl.getRenderScript(null));
                    sbAddControlsScript.append('\n');
                }
            }
            
            StringBuilder sbScript = new StringBuilder(sbRemoveControlsScript.length() +
                    sbUpdateControlsScript.length() + sbAddControlsScript.length());
            
            sbScript.append(sbRemoveControlsScript);
            sbScript.append(sbUpdateControlsScript);
            sbScript.append(sbAddControlsScript);
            
            return sbScript.toString();
        }
    }
}