//
// Copyright (c) 2019-2020 Rosberg Linhares (rosberglinhares@gmail.com)
// Licensed under the MIT license. See LICENSE file in the project root for full license information.
//

package io.gazeui.springboot;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.gazeui.RenderScriptWriter;
import io.gazeui.Window;
import io.gazeui.springboot.configuration.PropertiesConfiguration;
import io.gazeui.springboot.configuration.WebConfiguration;
import io.gazeui.springboot.http.MediaTypeExtensions;

@RestController
@RequestMapping(path = "${" + PropertiesConfiguration.PROPERTY_KEY_GAZEUI_BASE_PATH + "}")
public class GazeUIController {
    
    private static final String CREATE_INITIAL_UI_URL_PATH = "create-initial-ui.mjs";
    private static final String PROCESS_SERVER_UI_EVENT_URL_PATH = "process-server-ui-event.js";
    
    private final Window viewStateWindow;
    private final WebConfiguration gazeUIWebConfig;
    private String initialHtml;
    
    @Autowired
    public GazeUIController(Window viewStateWindow, WebConfiguration gazeUIWebConfig) {
        this.viewStateWindow = viewStateWindow;
        this.gazeUIWebConfig = gazeUIWebConfig;
    }
    
    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String getInitialHtml() {
        if (this.initialHtml == null) {
            StringBuilder sbInitialHtml = new StringBuilder();
            
            // Regarding the title tag, The HTML 5.2 specification says¹:
            // 
            //    1. If the document is an iframe srcdoc document or if title information is available from a
            //       higher-level protocol: Zero or more elements of metadata content, of which no more than one is a
            //       title element and no more than one is a base element.
            //       Otherwise: One or more elements of metadata content, of which exactly one is a title element and no
            //       more than one is a base element.
            //    2. The title element is a required child in most situations, but when a higher-level protocol provides
            //       title information, e.g., in the Subject line of an e-mail when HTML is used as an e-mail authoring
            //       format, the title element can be omitted.
            //    3. If it’s reasonable for the Document to have no title, then the title element is probably not
            //       required. See the head element’s content model for a description of when the element is required.
            //    
            //    Although it is not so clear to us if according to the specification the title is required, we are
            //    considering it required because the W3C Validator will give an error if no title tag is found.
            //    Beyond that, the specification enforces that the title element must contain at least one non-whitespace
            //    character². One solution to this is to deliver upfront the title content in the HTML below, but to
            //    achieve this we would have to instantiate the main window class (a possible heavy operation) here
            //    in this method to get its title, and this could result in a high waiting time for the user get any
            //    content. Although this HTML will give an error when checked by the W3C Validator regarding the title
            //    be empty, we are favoring performance.
            //    
            //      [1]: https://www.w3.org/TR/html52/document-metadata.html#document-metadata
            //      [2]: https://www.w3.org/TR/html52/document-metadata.html#the-title-element
            //      [3]: https://stackoverflow.com/a/28688879/2160765
            sbInitialHtml.append(
                    "<!DOCTYPE html>\n" + 
                    "<html>\n" + 
                    "<head>\n" + 
                    "  <meta charset='UTF-8'>\n" + 
                    "  <title></title>\n");
            
            this.getHtmlBaseUrl().ifPresent(htmlBaseUrl -> {
                // A base element is necessary when the GazeUI base path does not end in '/'
                sbInitialHtml.append(String.format("  <base href='%s'>\n", htmlBaseUrl));
            });
            
            // Modules are deferred and use strict mode automatically. A deferred script is executed after the document
            // has been parsed. This behavior is necessary because the page contents must be available in order to the
            // script be correctly executed.
            sbInitialHtml.append(String.format("  <script type='module' src='%s'></script>\n",
                    GazeUIController.CREATE_INITIAL_UI_URL_PATH));
            
            sbInitialHtml.append(
                    "</head>\n" + 
                    "<body>\n" + 
                    "</body>\n" + 
                    "</html>");
            
            this.initialHtml = sbInitialHtml.toString();
        }
        
        return this.initialHtml;
    }
    
    private Optional<String> getHtmlBaseUrl() {
        // Set a <base> element is necessary because 'child-path' relative to 'http://localhost/parent-path/' is
        // 'http://localhost/parent-path/child-path' but 'child-path' relative to 'http://localhost/parent-path' is
        // 'http://localhost/child-path'.
        // So when the GazeUI base path is '/level1/level2', for example, we have to set the HTML base element
        // to 'level2/'.
        
        String gazeUIBasePath = this.gazeUIWebConfig.getEnableGazeUIAnnotation().basePath();
        String htmlBaseUrl;
        
        if (gazeUIBasePath.isEmpty() || gazeUIBasePath.endsWith("/")) {
            htmlBaseUrl = null;
        } else {
            int posLastSlash = gazeUIBasePath.lastIndexOf("/");
            
            if (posLastSlash != -1) {
                htmlBaseUrl = gazeUIBasePath.substring(posLastSlash + 1);
            } else {
                htmlBaseUrl = gazeUIBasePath;
            }
            
            htmlBaseUrl += "/";
        }
        
        return Optional.ofNullable(htmlBaseUrl);
    }
    
    @GetMapping(
            path = "/" + GazeUIController.CREATE_INITIAL_UI_URL_PATH,
            produces = MediaTypeExtensions.TEXT_JAVASCRIPT_VALUE)
    public String getInitialUICreationScript() {
        RenderScriptWriter writer = new RenderScriptWriter(RenderScriptWriter.USE_STATIC_IMPORTS);
        this.viewStateWindow.renderCreation(writer);
        
        return writer.toString();
    }
    
    @PostMapping(
            path = "/" + GazeUIController.PROCESS_SERVER_UI_EVENT_URL_PATH,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaTypeExtensions.TEXT_JAVASCRIPT_VALUE)
    public String processServerUIEvent(@RequestBody ServerUIEventInfo serverUIEventInfo) {
        Window previousViewStateWindow = this.viewStateWindow.clone();
        
        this.viewStateWindow.processUIEvent(serverUIEventInfo.getControlId(), serverUIEventInfo.getEventName());
        
        // It was not possible to use this generated script as a JavaScript ES6 module:
        // 
        //   1. It is not possible to evaluate (using 'eval()' or 'window.Function()') the code of module. See links
        //      [1] and [2].
        //   2. The module loader API could also be used to create modules from strings, but it is out of date and is
        //      undergoing revision³.
        //   3. Data URIs could be used⁴, but it would not be possible to import other modules⁵.
        //   4. Once the module loader API is out of date, we choose not to use polyfills like ES Module Loader
        //      Polyfill⁶ or SystemJS.
        //   
        //     [1]: https://exploringjs.com/es6/ch_modules.html#_can-i-eval-the-code-of-module
        //     [2]: https://2ality.com/2019/10/eval-via-import.html#eval()-does-not-support-export-and-import
        //     [3]: https://github.com/whatwg/loader#status
        //     [4]: https://2ality.com/2019/10/eval-via-import.html
        //     [5]: https://stackoverflow.com/questions/59941483/importing-nested-javascript-es6-modules-to-a-module-created-from-a-string-of-cod
        //     [6]: https://github.com/ModuleLoader/es-module-loader
        //     [7]: https://github.com/tc39/proposal-dynamic-import
        RenderScriptWriter writer = new RenderScriptWriter(RenderScriptWriter.USE_DYNAMIC_IMPORTS);
        this.viewStateWindow.renderUpdate(writer, previousViewStateWindow);
        
        if (!writer.isEmpty()) {
            StringBuilder sbScript = new StringBuilder();
            
            // Here is not necessary to use a closure because this code will be already executed in a limited scope.
            sbScript.append("'use strict';\n");
            sbScript.append("\n");
            sbScript.append(writer.toString());
            
            return sbScript.toString();
        } else {
            return "";
        }
    }
}