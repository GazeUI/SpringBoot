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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.NoSuchElementException;
import java.util.Optional;

import io.gazeui.text.Strings;

public abstract class Window extends ContainerControl {

    public static Window createInstance(Class<? extends Window> windowSubclass) {
        try {
            Window viewStateWindow = windowSubclass.getDeclaredConstructor().newInstance();
            return viewStateWindow;
        } catch (InvocationTargetException ex) {
            // Rethrow the any possible exception thrown by the Window subclass constructor
            throw new RuntimeException(ex.getCause());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | NoSuchMethodException | SecurityException ex) {
            throw new GazeUIException(ErrorMessage.UNEXPECTED_ERROR_CREATING_MAIN_WINDOW.getMessage(), ex);
        }
    }
    
    private String title;
    // The client ID must be unique per browser window because it will be used as the HTML ID attribute.
    private int controlsCounter = 0;
    
    public Window() {
    }
    
    public Window(String title) {
        this.setTitle(title);
    }
    
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        if (!Strings.isNullOrBlank(title)) {
            this.title = title;
        } else {
            throw new IllegalArgumentException(ErrorMessage.HTML_VALIDATION_TITLE_MUST_NOT_BE_EMPTY.getMessage());
        }
    }
    
    String generateAutomaticControlId() {
        return String.format("ctl%02d", ++this.controlsCounter);
    }
    
    @Override
    public Window clone() {
        // This method is only to make the clone method visible to the GazeUIController.
        return (Window)super.clone();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(String.format(", Title: '%s'", Optional.ofNullable(this.getTitle()).orElse("")));
        
        return sb.toString();
    }
    
    @Override
    protected String creationScript() {
        // It is not necessary to create a container for the Window, because document.body will be used as such.
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
    public void render(RenderScriptWriter writer, Control previousControlState) {
        if (previousControlState == null) {
            this.renderCreation(writer);
        } else {
            this.renderUpdate(writer, (Window)previousControlState);
        }
    }
    
    private void renderCreation(RenderScriptWriter writer) {
        if (Strings.isNullOrBlank(this.getTitle())) {
            // According to the HTML 5.2 specification, the title element must contain at least one non-whitespace
            // character. See https://www.w3.org/TR/html52/document-metadata.html#the-title-element for details.
            this.setTitle(this.getClass().getSimpleName());
        }
        
        // TODO: JavaScript escape
        writer.format("document.title = '%s';\n", this.getTitle());
        
        // Add the default ContainerControl script
        super.render(writer, null);
    }
    
    private void renderUpdate(RenderScriptWriter writer, Window previousControlState) {
        if (!this.getTitle().equals(previousControlState.getTitle())) {
            // TODO: JavaScript escape
            writer.format("document.title = '%s';\n", this.getTitle());
        }
        
        // Add the default ContainerControl script
        super.render(writer, previousControlState);
    }
    
    public void processUIEvent(String controlId, String eventName) {
        Control control = this.getDescendantControlById(this, controlId);
        
        if (control != null) {
            String processEventMethodName = String.format("processOn%sEvent", eventName);
            
            try {
                Method method = control.getClass().getDeclaredMethod(processEventMethodName);
                method.invoke(control);
            } catch (InvocationTargetException ex) {
                // Rethrow the any possible exception thrown by the Window subclass constructor
                throw new RuntimeException(ex.getCause());
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException ex) {
                String errorMessage = String.format(ErrorMessage.UNEXPECTED_ERROR_PROCESSING_EVENT.getMessage(),
                        eventName, control.toString());
                
                throw new GazeUIException(errorMessage, ex);
            }
        } else {
            String errorMessage = String.format(ErrorMessage.COULD_NOT_PROCESS_EVENT_CONTROL_ID_NOT_FOUND.getMessage(),
                    eventName, controlId);
            
            throw new NoSuchElementException(errorMessage);
        }
    }
    
    private Control getDescendantControlById(ContainerControl ancestor, String controlId) {
        for (Control childControl : ancestor.getControls()) {
            if (childControl.getClientId().equals(controlId)) {
                return childControl;
            } else if (childControl instanceof ContainerControl) {
                Control foundControl = this.getDescendantControlById((ContainerControl)childControl, controlId);
                
                if (foundControl != null) {
                    return foundControl;
                }
            }
        }
        
        return null;
    }
}