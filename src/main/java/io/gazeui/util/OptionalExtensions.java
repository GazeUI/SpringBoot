//
// Copyright (c) 2020 Rosberg Linhares (rosberglinhares@gmail.com)
// Licensed under the MIT license. See LICENSE file in the project root for full license information.
//

package io.gazeui.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class OptionalExtensions {
    
    private OptionalExtensions() {
        // No instances allowed
    }
    
    public static <T, X extends Throwable> void ifPresentOrElseThrow(Optional<T> optional, Consumer<? super T> action,
            Supplier<? extends X> exceptionSupplier) throws X {
        if (optional.isPresent()) {
            action.accept(optional.get());
        } else {
            throw exceptionSupplier.get();
        }
    }
}