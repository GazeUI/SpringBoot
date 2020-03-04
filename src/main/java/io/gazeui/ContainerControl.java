//
// Copyright (c) 2019-2020 Rosberg Linhares (rosberglinhares@gmail.com)
// Licensed under the MIT license. See LICENSE file in the project root for full license information.
//

package io.gazeui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import io.gazeui.collections.Lists;

/**
 * Represents a control that can function as a container for other controls.
 * 
 * @param <T> the type of controls in this container
 */
public class ContainerControl<T extends Control> extends Control {

    private static final Comparator<Control> clientIdComparator;
    private static final Function<Control, String> funcControlToClientId;
    
    static {
        clientIdComparator = new Comparator<Control>() {
            @Override
            public int compare(Control c1, Control c2) {
                return c1.getClientId().get().compareTo(c2.getClientId().get());
            }
        };
        
        funcControlToClientId = ((Function<Control, Optional<String>>) Control::getClientId)
                .andThen(Optional::get);
    }
    
    private List<T> controls;
    
    public List<T> getControls() {
        if (this.controls == null) {
            // To generate the automatic ID for controls, we have to know when they are added.
            // So the use of a custom control collection.
            this.controls = new ControlCollection<>(this);
        }
        
        return this.controls;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected ContainerControl<T> clone() {
        ContainerControl<T> clonedContainerControl = (ContainerControl<T>)super.clone();
        
        // The cloned collection will not suffer any operation, so it is not necessary to be a
        // ControlCollection
        clonedContainerControl.controls = new ArrayList<>(this.getControls().size());
        
        // Doing a deep copy of child controls
        for (T control : this.getControls()) {
            clonedContainerControl.getControls().add((T)control.clone());
        }
        
        return clonedContainerControl;
    }
    
    /**
     * A script that will be responsible to create the container for child controls on the client side.
     */
    protected String creationScript() {
        return String.format(
                "let %1$s = document.createElement('div');\n" +
                "%1$s.id = '%1$s';\n", this.getClientId().get());
    }
    
    @Override
    protected void renderCreation(RenderScriptWriter writer) {
        writer.print(this.creationScript());
        
        for (Control childControl : this.getControls()) {
            childControl.renderCreation(writer);
            writer.format("%s.appendChild(%s);\n", this.identificationToken(),
                    childControl.identificationToken());
        }
    }
    
    @Override
    protected void renderUpdate(RenderScriptWriter writer, Control previousControlState) {
        @SuppressWarnings("unchecked")
        ContainerControl<T> previousContainerState = (ContainerControl<T>)previousControlState;
        
        // We expect that operations of adding, removing and changing child controls order will not
        // be so common. So we check first for the case which at most updates on child controls were
        // made. Doing that we avoid running the Longest Common Subsequence algorithm (a heavy operation)
        // for this simple case.
        if (this.listsWithSameStructure(this.getControls(), previousContainerState.getControls())) {
            Iterator<T> currentChildControlsIterator = this.getControls().iterator();
            Iterator<T> previousChildControlsIterator = previousContainerState.getControls().iterator();
            
            while (currentChildControlsIterator.hasNext()) {
                Control childControl = currentChildControlsIterator.next();
                Control previousChildControl = previousChildControlsIterator.next();
                
                childControl.renderUpdate(writer, previousChildControl);
            }
        } else {
            RenderScriptWriter writerRemove = new RenderScriptWriter();
            RenderScriptWriter writerUpdate = new RenderScriptWriter();
            RenderScriptWriter writerAddAndChangeOrder = new RenderScriptWriter();
            
            List<T> lcs = Lists.longestCommonSubsequence(this.getControls(),
                    previousContainerState.getControls(), clientIdComparator);
            
            // These maps are used only to have constant-time performance for get operations.
            // Doing that we avoid quadratic time complexity O(n^2).
            Map<String, Control> lcsMap = Lists.toMap(lcs, funcControlToClientId, Function.identity());
            Map<String, Control> currentChildControlsMap = Lists.toMap(this.getControls(),
                    funcControlToClientId, Function.identity());
            Map<String, Control> previousChildControlsMap = Lists.toMap(previousContainerState.getControls(),
                    funcControlToClientId, Function.identity());
            
            // 1. Remove
            
            for (Control previousChildControl : previousContainerState.getControls()) {
                if (!currentChildControlsMap.containsKey(previousChildControl.getClientId().get())) {
                    writerRemove.print(previousChildControl.selectionScript());
                    writerRemove.format("%s.remove();\n", previousChildControl.identificationToken());
                }
            }
            
            // 2. Update, Add and Order Changed
            
            ListIterator<T> reverseListIterator = this.getControls().listIterator(this.getControls().size());
            Control previousLoopChildControl = null;
            // If a variable pointing to the previous control in the loop was already created
            boolean previousLoopChildControlIdentified = false;
            
            // Here we are iterating in reverse order to make possible use the Node.insertBefore()
            // DOM method. At 12/2019, the ChildNode.after() method is marked experimental in the
            // MDN website and is not supported by Safari:
            //   [1]: https://developer.mozilla.org/en-US/docs/Web/API/ChildNode/after
            //   [2]: https://caniuse.com/#feat=mdn-api_childnode_after
            while (reverseListIterator.hasPrevious()) {
                Control childControl = reverseListIterator.previous();
                // If a variable pointing to the control was already created
                boolean childControlIdentified = false;
                
                // There is five different situations to a control here:
                // 
                //   1. Belongs to the lcs
                //     1.1. Was not updated
                //     1.2. Was updated
                //   
                //   2. Does not belong to the lcs
                //     2.1. Was moved
                //       2.1.1. Was not updated
                //       2.1.2. Was updated
                //     2.2. Was added
                //
                
                if (previousChildControlsMap.containsKey(childControl.getClientId().get())) {
                    Control previousChildControlState = previousChildControlsMap.get(
                            childControl.getClientId().get());
                    
                    RenderScriptWriter localWriterUpdate = new RenderScriptWriter();
                    childControl.renderUpdate(localWriterUpdate, previousChildControlState);
                    
                    if (!localWriterUpdate.isEmpty()) {
                        writerUpdate.print(localWriterUpdate);
                        childControlIdentified = true;
                    }
                }
                
                if (!lcsMap.containsKey(childControl.getClientId().get())) {
                    if (previousChildControlsMap.containsKey(childControl.getClientId().get())) {
                        // The element changed its order
                        if (!childControlIdentified) {
                            writerAddAndChangeOrder.print(childControl.selectionScript());
                            childControlIdentified = true;
                        }
                    } else {
                        // The element was added
                        childControl.renderCreation(writerAddAndChangeOrder);
                        childControlIdentified = true;
                    }
                    
                    String previousLoopChildControlIdentificationToken;
                    
                    if (previousLoopChildControl != null) {
                        if (!previousLoopChildControlIdentified) {
                            writerAddAndChangeOrder.print(previousLoopChildControl.selectionScript());
                        }
                        
                        previousLoopChildControlIdentificationToken = previousLoopChildControl
                                .identificationToken();
                    } else {
                        // If referenceNode is null, the newNode is inserted at the end of the list
                        // of child nodes.
                        previousLoopChildControlIdentificationToken = null;
                    }
                    
                    writerAddAndChangeOrder.format("%s.insertBefore(%s, %s);\n", this.identificationToken(),
                            childControl.identificationToken(), previousLoopChildControlIdentificationToken);
                }
                
                previousLoopChildControl = childControl;
                previousLoopChildControlIdentified = childControlIdentified;
            }
            
            if (!writerAddAndChangeOrder.isEmpty()) {
                writer.print(writerRemove);
                writer.print(writerUpdate);
                writer.print(this.selectionScript());
                writer.print(writerAddAndChangeOrder);
            } else {
                writer.print(writerRemove);
                writer.print(writerUpdate);
            }
        }
    }
    
    private boolean listsWithSameStructure(List<T> list1, List<T> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        
        Iterator<T> it1 = list1.iterator();
        Iterator<T> it2 = list2.iterator();
        
        while (it1.hasNext()) {
            if (clientIdComparator.compare(it1.next(), it2.next()) != 0) {
                return false;
            }
        }
        
        return true;
    }
}