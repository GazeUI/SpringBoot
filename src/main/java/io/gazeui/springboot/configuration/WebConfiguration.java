//
// Copyright (c) 2019-2020 Rosberg Linhares (rosberglinhares@gmail.com)
// Licensed under the MIT license. See LICENSE file in the project root for full license information.
//

package io.gazeui.springboot.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.gazeui.Window;
import io.gazeui.springboot.annotation.EnableGazeUI;

@Configuration
@ComponentScan("io.gazeui.springboot")
public class WebConfiguration {
    
    private static final String CLASSPATH_STATIC_RESOURCE_LOCATION = "classpath:/static/";
    
    private final EnableGazeUI enableGazeUIAnnotation;
    
    @Autowired
    public WebConfiguration(ApplicationContext applicationContext) {
        // Get the first EnableGazeUI annotation and ignore the other ones
        String beanNameWithEnableGazeUI = applicationContext.getBeanNamesForAnnotation(EnableGazeUI.class)[0];
        this.enableGazeUIAnnotation = applicationContext.findAnnotationOnBean(
                beanNameWithEnableGazeUI, EnableGazeUI.class);
    }
    
    public EnableGazeUI getEnableGazeUIAnnotation() {
        return this.enableGazeUIAnnotation;
    }
    
    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                String urlPattern = WebConfiguration.this.enableGazeUIAnnotation.basePath() + "/**";
                
                registry.addResourceHandler(urlPattern)
                    .addResourceLocations(WebConfiguration.CLASSPATH_STATIC_RESOURCE_LOCATION);
            }
        };
    }
    
    @Bean
    @SessionScope
    public Window window() {
        return Window.createInstance(this.enableGazeUIAnnotation.initialPage());
    }
}