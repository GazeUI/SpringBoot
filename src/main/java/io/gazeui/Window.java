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

import io.gazeui.util.OptionalExtensions;

public class Window extends ContainerControl<WebPage> {

    // Ideally this class would be declared final and with a private constructor, but none of these options work because
    // this class is used as a Spring component.
    
    private static final String WINDOW_ID = "window";
    private static final String PAGE_ID = "page";
    
    public static Window createInstance(Class<? extends WebPage> initialPageClass) {
        WebPage initialPage;
        
        try {
            initialPage = initialPageClass.getDeclaredConstructor().newInstance();
        } catch (InvocationTargetException ex) {
            // Rethrow any possible exception thrown by the WebPage subclass constructor
            throw new RuntimeException(ex.getCause());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | NoSuchMethodException | SecurityException ex) {
            throw new GazeUIException(ErrorMessage.UNEXPECTED_ERROR_CREATING_INITIAL_PAGE.getMessage(), ex);
        }
        
        return new Window(initialPage);
    }
    
    public Window(WebPage initialPage) {
        this.getControls().add(initialPage);
    }
    
    public Optional<WebPage> getChildPage() {
        if (!this.getControls().isEmpty()) {
            return Optional.of(this.getControls().get(0));
        } else {
            return Optional.empty();
        }
    }
    
    @Override
    public Window clone() {
        // This method is only to make the clone method visible to the GazeUIController.
        return (Window)super.clone();
    }
    
    @Override
    public void renderCreation(RenderScriptWriter writer) {
        this.getChildPage().ifPresent((WebPage page) -> {
            page.renderCreation(writer);
        });
    }
    
    @Override
    public void renderUpdate(RenderScriptWriter writer, Control previousControlState) {
        Window previousWindow = (Window)previousControlState;
        
        if (this.getChildPage().isPresent() && previousWindow.getChildPage().isPresent() &&
                this.getChildPage().get().getClass() == previousWindow.getChildPage().get().getClass()) {
            // Update
            this.getChildPage().get().renderUpdate(writer, previousWindow.getChildPage().get());
        } else {
            previousWindow.getChildPage().ifPresent((WebPage previousPage) -> {
                writer.importModule("DomFunctions", "./dom-functions.mjs");
                writer.println("DomFunctions.clearCurrentWebPage();");
            });
            
            this.getChildPage().ifPresent((WebPage page) -> {
                page.renderCreation(writer);
            });
        }
    }
    
    public void processUIEvent(String controlId, String eventName) {
        Optional<? extends Control> optionalControl = this.getDescendantControlById(controlId);
        
        OptionalExtensions.ifPresentOrElseThrow(optionalControl, control -> {
            String processEventMethodName = String.format("processOn%sEvent", eventName);
            
            try {
                Method method = control.getClass().getDeclaredMethod(processEventMethodName);
                method.invoke(control);
            } catch (InvocationTargetException ex) {
                // Rethrow any possible exception thrown by the WebPage subclass constructor
                throw new RuntimeException(ex.getCause());
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException ex) {
                String errorMessage = String.format(ErrorMessage.UNEXPECTED_ERROR_PROCESSING_EVENT.getMessage(),
                        eventName, control.toString());
                
                throw new GazeUIException(errorMessage, ex);
            }
        }, () -> {
            String errorMessage = String.format(ErrorMessage.COULD_NOT_PROCESS_EVENT_CONTROL_ID_NOT_FOUND.getMessage(),
                    eventName, controlId);
            
            return new NoSuchElementException(errorMessage);
        });
    }
    
    private Optional<? extends Control> getDescendantControlById(String controlId) {
        switch (controlId) {
        case WINDOW_ID:
            return Optional.of(this);
            
        case PAGE_ID:
            return this.getChildPage();
            
        default:
            // If it is neither the window nor the page, look at their descendant controls
            return this.getChildPage().flatMap(page -> this.getDescendantControlById(page, controlId));
        }
    }
    
    private Optional<Control> getDescendantControlById(ContainerControl<?> ancestor, String controlId) {
        Optional<Control> foundControl = Optional.empty();
        
        for (Control childControl : ancestor.getControls()) {
            if (childControl.getClientId().get().equals(controlId)) {
                foundControl = Optional.of(childControl);
                break;
            } else if (childControl instanceof ContainerControl) {
                foundControl = this.getDescendantControlById((ContainerControl<?>)childControl, controlId);
                
                if (foundControl.isPresent()) {
                    break;
                }
            }
        }
        
        return foundControl;
    }
}