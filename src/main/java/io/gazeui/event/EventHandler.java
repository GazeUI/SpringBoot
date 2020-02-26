//
// Copyright (c) 2019 Rosberg Linhares (rosberglinhares@gmail.com)
// Licensed under the MIT license. See LICENSE file in the project root for full license information.
//

package io.gazeui.event;

import java.util.EventListener;

@FunctionalInterface
public interface EventHandler<T extends EventArgs> extends EventListener {
    
    public void handle(T eventArgs);
}