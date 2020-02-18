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
    private String htmlBaseUrl;
    
    @Autowired
    public WebConfiguration(ApplicationContext applicationContext) {
        // Get the first EnableGazeUI annotation and ignore the other ones
        String beanNameWithEnableGazeUI = applicationContext.getBeanNamesForAnnotation(EnableGazeUI.class)[0];
        this.enableGazeUIAnnotation = applicationContext.findAnnotationOnBean(beanNameWithEnableGazeUI, EnableGazeUI.class);
        
        this.setHtmlBaseUrl(this.enableGazeUIAnnotation.basePath());
    }
    
    private void setHtmlBaseUrl(String gazeUIBasePath) {
        // Set a <base> element is necessary because 'child-path' relative to 'http://localhost/parent-path/' is
        // 'http://localhost/parent-path/child-path' but 'child-path' relative to 'http://localhost/parent-path' is
        // 'http://localhost/child-path'.
        // So when the GazeUI base path is '/level1/level2', for example, we have to set the HTML base element
        // to 'level2/'.
        if (gazeUIBasePath.isEmpty() || gazeUIBasePath.endsWith("/")) {
            this.htmlBaseUrl = null;
        } else {
            int posLastSlash = gazeUIBasePath.lastIndexOf("/");
            
            if (posLastSlash != -1) {
                this.htmlBaseUrl = gazeUIBasePath.substring(posLastSlash + 1);
            } else {
                this.htmlBaseUrl = gazeUIBasePath;
            }
            
            this.htmlBaseUrl += "/";
        }
    }
    
    public EnableGazeUI getEnableGazeUIAnnotation() {
        return this.enableGazeUIAnnotation;
    }
    
    public String getHtmlBaseUrl() {
        return this.htmlBaseUrl;
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