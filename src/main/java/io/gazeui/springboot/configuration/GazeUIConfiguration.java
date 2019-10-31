/*
 * MIT License
 * 
 * Copyright (c) 2019 Rosberg Linhares (rosberglinhares@gmail.com)
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

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import io.gazeui.springboot.annotation.EnableGazeUI;

@Configuration
@ComponentScan("io.gazeui.springboot")
public class GazeUIConfiguration implements ApplicationContextAware {
	
	private Class<?> mainWindowClass;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		String beanNameWithEnableGazeUI = applicationContext.getBeanNamesForAnnotation(EnableGazeUI.class)[0];
    	EnableGazeUI enableGazeUIAnnotation = applicationContext.findAnnotationOnBean(beanNameWithEnableGazeUI, EnableGazeUI.class);
    	
    	this.mainWindowClass = enableGazeUIAnnotation.mainWindowClass();
	}
	
	public Class<?> getMainWindowClass() {
		return this.mainWindowClass;
	}
}