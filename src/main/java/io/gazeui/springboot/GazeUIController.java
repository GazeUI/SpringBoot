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

package io.gazeui.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.gazeui.springboot.configuration.PropertiesConfiguration;
import io.gazeui.springboot.configuration.WebConfiguration;
import io.gazeui.springboot.http.MediaTypeExtensions;
import io.gazeui.ui.Window;

@RestController
@RequestMapping(path = "${" + PropertiesConfiguration.PROPERTY_KEY_GAZEUI_BASE_PATH + "}")
public class GazeUIController {
    
    private static final String CREATE_INITIAL_UI_URL_PATH = "create-initial-ui";
    private static final String PROCESS_SERVER_UI_EVENT_URL_PATH = "process-server-ui-event";
    
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
            
            if (this.gazeUIWebConfig.getHtmlBaseUrl() != null) {
                // A base element is necessary when the GazeUI base path does not end in '/'
                sbInitialHtml.append(String.format("  <base href='%s'>\n", this.gazeUIWebConfig.getHtmlBaseUrl()));
            }
            
            // The defer attribute allows the script to be executed after the document has been parsed.
            // This is necessary because the page contents must be available in order to the script be correctly
            // executed.
            sbInitialHtml.append(String.format("  <script defer src='%s'></script>\n",
                    GazeUIController.CREATE_INITIAL_UI_URL_PATH));
            
            // The 'no-store' cache mode bypass the cache completely.
            sbInitialHtml.append(
                    "  <script>\n" + 
                    "    async function processServerUIEvent(controlId, eventName) {\n" + 
                    "        let eventInfo = {\n" + 
                    "            controlId: controlId,\n" + 
                    "            eventName: eventName\n" + 
                    "        };\n" + 
                    "        \n" + 
                    "        let fetchOptions = {\n" + 
                    "            method: 'POST',\n" + 
                    "            cache: 'no-store',\n" + 
                    "            headers: {\n" + 
                    "                'Content-Type': 'application/json'\n" + 
                    "            },\n" + 
                    "            body: JSON.stringify(eventInfo)\n" + 
                    "        };\n" + 
                    "        \n");
            
            sbInitialHtml.append(String.format("        let response = await fetch('%s', fetchOptions);\n",
                    GazeUIController.PROCESS_SERVER_UI_EVENT_URL_PATH));
            
            // 1. We are using the 'response.body' property because, at Dec/2019, it has 73.94% of global usage¹, while the
            //    'response.text()' method has only 36.71%².
            // 
            //      [1]: https://caniuse.com/#feat=mdn-api_body_body
            //      [2]: https://caniuse.com/#feat=mdn-api_body_text
            // 
            // 2. According to the MDN website, you should never use 'eval()', but 'window.Function()' instead.
            //    See https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/eval for details.
            // 
            // 3. We have to observe two special cases when dealing with event handlers and nested controls:
            // 
            //    3.1. If there is one ancestor control with an event handler and one descendant without it,
            //         the event will be fired on the ancestor control if the descendant control is stimulated.
            //    3.2. If both the ancestor and descendant controls have event handlers, the event will be fired
            //         on both controls when the descendant control is stimulated.
            //    
            //    We deal with these two special cases checking if 'target' and 'currentTarget' are the same.
            //    We also use 'stopImmediatePropagation' just to certify that no other events will run for the same action.
            //    See the following link for more detail about event order:
            //    
            //      [1]: https://www.quirksmode.org/js/events_order.html
            sbInitialHtml.append(
                    "        let responseText = await getTextFromStream(response.body);\n" + 
                    "        \n" + 
                    "        executeJavaScriptCode(responseText);\n" + 
                    "    }\n" + 
                    "    \n" + 
                    "    async function getTextFromStream(readableStream) {\n" + 
                    "        let reader = readableStream.getReader();\n" + 
                    "        let utf8Decoder = new TextDecoder();\n" + 
                    "        let nextChunk;\n" + 
                    "        \n" + 
                    "        let resultStr = '';\n" + 
                    "        \n" + 
                    "        while (!(nextChunk = await reader.read()).done) {\n" + 
                    "            let partialData = nextChunk.value;\n" + 
                    "            resultStr += utf8Decoder.decode(partialData);\n" + 
                    "        }\n" + 
                    "        \n" + 
                    "        return resultStr;\n" + 
                    "    }\n" + 
                    "    \n" + 
                    "    function executeJavaScriptCode(code) {\n" + 
                    "        return Function(code)();\n" + 
                    "    }\n" + 
                    "    \n" + 
                    "    async function onClickHandler(mouseEvent) {\n" + 
                    "        if (mouseEvent.target == mouseEvent.currentTarget) {\n" + 
                    "            mouseEvent.stopImmediatePropagation();\n" + 
                    "            await processServerUIEvent(mouseEvent.target.id, 'Click');\n" + 
                    "        }\n" + 
                    "    }\n" + 
                    "  </script>\n" + 
                    "</head>\n" + 
                    "<body>\n" + 
                    "</body>\n" + 
                    "</html>");
            
            this.initialHtml = sbInitialHtml.toString();
        }
        
        return this.initialHtml;
    }
    
    @GetMapping(
            path = "/" + GazeUIController.CREATE_INITIAL_UI_URL_PATH,
            produces = MediaTypeExtensions.APPLICATION_JAVASCRIPT_VALUE)
    public String getInitialUICreationScript() {
        final int extraTextLength = 34;
        String renderScript = this.viewStateWindow.getRenderScript(null);
        StringBuilder sbScript = new StringBuilder(renderScript.length() + extraTextLength);
        
        // Here we have to use a closure to limit the scope of the render script to be executed, once the
        // overall code will be executed as the content of a JavaScript file.
        sbScript.append("'use strict';\n");
        sbScript.append("\n");
        sbScript.append("(function() {\n");
        sbScript.append(renderScript);
        sbScript.append("})();");
        
        return sbScript.toString();
    }
    
    @PostMapping(
            path = "/" + GazeUIController.PROCESS_SERVER_UI_EVENT_URL_PATH,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaTypeExtensions.APPLICATION_JAVASCRIPT_VALUE)
    public String processServerUIEvent(@RequestBody ServerUIEventInfo serverUIEventInfo) {
        Window previousViewStateWindow = this.viewStateWindow.clone();
        
        this.viewStateWindow.processUIEvent(serverUIEventInfo.getControlId(), serverUIEventInfo.getEventName());
        
        String renderScript = this.viewStateWindow.getRenderScript(previousViewStateWindow);
        
        if (!renderScript.isEmpty()) {
            final int extraTextLength = 15;
            StringBuilder sbScript = new StringBuilder(renderScript.length() + extraTextLength);
            
            // Here is not necessary to use a closure because this code will be already executed in a limited scope.
            sbScript.append("'use strict';\n");
            sbScript.append("\n");
            sbScript.append(renderScript);
            
            return sbScript.toString();
        } else {
            return "";
        }
    }
}