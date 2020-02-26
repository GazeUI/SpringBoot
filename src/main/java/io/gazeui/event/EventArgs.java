//
// Copyright (c) 2019 Rosberg Linhares (rosberglinhares@gmail.com)
// Licensed under the MIT license. See LICENSE file in the project root for full license information.
//

package io.gazeui.event;

import java.util.EventObject;

import io.gazeui.Control;

@SuppressWarnings("serial")
public class EventArgs extends EventObject {

    public EventArgs(Control source) {
        super(source);
    }
    
    @Override
    public Control getSource() {
        return (Control)super.getSource();
    }
}