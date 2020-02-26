//
// Copyright (c) 2019-2020 Rosberg Linhares (rosberglinhares@gmail.com)
// Licensed under the MIT license. See LICENSE file in the project root for full license information.
//

package io.gazeui.springboot.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import io.gazeui.WebPage;
import io.gazeui.springboot.configuration.WebConfiguration;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(WebConfiguration.class)
public @interface EnableGazeUI {
    
    Class<? extends WebPage> initialPage();
    
    String basePath() default "";
}