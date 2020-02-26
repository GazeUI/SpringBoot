//
// Copyright (c) 2020 Rosberg Linhares (rosberglinhares@gmail.com)
// Licensed under the MIT license. See LICENSE file in the project root for full license information.
//

package io.gazeui.springboot.configuration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

@Configuration
public class PropertiesConfiguration {
    
    public static final String PROPERTY_KEY_GAZEUI_BASE_PATH = "gazeui.base-path";
    private static final String PROPERTY_SOURCE_NAME = "localProperties";
    
    @Autowired
    public void setLocalProperties(ConfigurableEnvironment env, WebConfiguration gazeUIWebConfig) {
        Map<String, Object> propertiesMap = new HashMap<>();
        propertiesMap.put(PROPERTY_KEY_GAZEUI_BASE_PATH, gazeUIWebConfig.getEnableGazeUIAnnotation().basePath());
        
        MutablePropertySources propertySources = env.getPropertySources();
        propertySources.addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, propertiesMap));
    }
}