//
// Copyright (c) 2019-2020 Rosberg Linhares (rosberglinhares@gmail.com)
// Licensed under the MIT license. See LICENSE file in the project root for full license information.
//

package io.gazeui.springboot.http;

public final class MediaTypeExtensions {
    
    private MediaTypeExtensions() {
        // No instances allowed
    }
    
    // The 'text/javascript' MIME type seams obsolete, but it is the right one to be used.
    // See https://stackoverflow.com/a/59774584/2160765 for details.
    public static final String TEXT_JAVASCRIPT_VALUE = "text/javascript";
}