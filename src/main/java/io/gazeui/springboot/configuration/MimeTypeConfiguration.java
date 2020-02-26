//
// Copyright (c) 2020 Rosberg Linhares (rosberglinhares@gmail.com)
// Licensed under the MIT license. See LICENSE file in the project root for full license information.
//

package io.gazeui.springboot.configuration;

import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Configuration;

import io.gazeui.springboot.http.MediaTypeExtensions;

@Configuration
public class MimeTypeConfiguration implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        MimeMappings mimeMappings = new MimeMappings(MimeMappings.DEFAULT);
        
        // See the Google's note recommendation about using the .mjs extension for JavaScript modules:
        // https://v8.dev/features/modules#mjs
        mimeMappings.add("mjs", MediaTypeExtensions.TEXT_JAVASCRIPT_VALUE);
        
        factory.setMimeMappings(mimeMappings);
    }
}