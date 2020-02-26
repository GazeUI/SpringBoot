//
// Copyright (c) 2019 Rosberg Linhares (rosberglinhares@gmail.com)
// Licensed under the MIT license. See LICENSE file in the project root for full license information.
//

package io.gazeui;

@SuppressWarnings("serial")
public class GazeUIException extends RuntimeException {
    
    public GazeUIException(String message) {
        super(message);
    }
    
    public GazeUIException(String message, Throwable cause) {
        super(message, cause);
    }
}