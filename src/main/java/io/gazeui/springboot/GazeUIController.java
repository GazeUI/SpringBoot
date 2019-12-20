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

package io.gazeui.springboot;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.gazeui.springboot.configuration.GazeUIConfiguration;
import io.gazeui.springboot.http.MediaTypeExtensions;
import io.gazeui.ui.Window;

@RestController
// TODO: Allow to configure the base path
@RequestMapping(path = "/some-base-path")
public class GazeUIController {
    
    private final GazeUIConfiguration gazeUIConfiguration;
    
    @Autowired
    public GazeUIController(GazeUIConfiguration gazeUIConfiguration) {
        this.gazeUIConfiguration = gazeUIConfiguration;
    }
    
    @GetMapping(path = "/ui", produces = MediaType.TEXT_HTML_VALUE)
    public String getInitialHtml() {
        // 1. Regarding the title tag, The HTML 5.2 specification says¹:
        // 
        //    1.1. If the document is an iframe srcdoc document or if title information is available from a
        //         higher-level protocol: Zero or more elements of metadata content, of which no more than one is a
        //         title element and no more than one is a base element.
        //         Otherwise: One or more elements of metadata content, of which exactly one is a title element and no
        //         more than one is a base element.
        //    1.2. The title element is a required child in most situations, but when a higher-level protocol provides
        //         title information, e.g., in the Subject line of an e-mail when HTML is used as an e-mail authoring
        //         format, the title element can be omitted.
        //    1.3. If it’s reasonable for the Document to have no title, then the title element is probably not
        //         required. See the head element’s content model for a description of when the element is required.
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
        // 
        // 2. The defer attribute allows the script to be executed after the document has been parsed.
        //    This is necessary because the page contents must be available in order to the script be correctly
        //    executed.
        // 
        // 3. The 'no-store' cache mode bypass the cache completely.
        // 
        // 4. We are using the 'response.body' property because, at Dec/2019, it has 73.94% of global usage¹, while the
        //    'response.text()' method has only 36.71%².
        // 
        //      [1]: https://caniuse.com/#feat=mdn-api_body_body
        //      [2]: https://caniuse.com/#feat=mdn-api_body_text
        // 
        // 5. According to the MDN website, you should never use 'eval()', but 'window.Function()' instead.
        //    See https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/eval for details.
        // 
        // 6. We have to observe two special cases when dealing with event handlers and nested controls:
        // 
        //    6.1. If there is one ancestor control with an event handler and one descendant without it,
        //         the event will be fired on the ancestor control if the descendant control is stimulated.
        //    6.2. If both the ancestor and descendant controls have event handlers, the event will be fired
        //         on both controls when the descendant control is stimulated.
        //    
        //    We deal with these two special cases checking if 'target' and 'currentTarget' are the same.
        //    We also use 'stopImmediatePropagation' just to certify that no other events will run for the same action.
        //    See the following link for more detail about event order:
        //    
        //      [1]: https://www.quirksmode.org/js/events_order.html
        
        String html =
                "<!DOCTYPE html>\n" + 
                "<html>\n" + 
                "<head>\n" + 
                "  <meta charset='UTF-8'>\n" + 
                "  <title></title>\n" + 
                "  <script defer src='create-ui.js'></script>\n" + 
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
                "        \n" + 
                "        let response = await fetch('process-server-ui-event', fetchOptions);\n" + 
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
                "</html>";
        
        return html;
    }
    
    @GetMapping(path = "/create-ui.js", produces = MediaTypeExtensions.APPLICATION_JAVASCRIPT_VALUE)
    public String getUICreationScript(HttpSession session) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        
        // TODO: Create a class that allows strongly typed access to session objects
        Window viewStateWindow = (Window)session.getAttribute("viewState");
        
        if (viewStateWindow == null) {
            Class<? extends Window> mainWindowClass = this.gazeUIConfiguration.getMainWindowClass();
            viewStateWindow = mainWindowClass.getDeclaredConstructor().newInstance();
            
            session.setAttribute("viewState", viewStateWindow);
        }
        
        final int extraTextLength = 34;
        String renderScript = viewStateWindow.getRenderScript(null);
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
            path = "/process-server-ui-event",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaTypeExtensions.APPLICATION_JAVASCRIPT_VALUE)
    public String processServerUIEvent(@RequestBody ServerUIEventInfo serverUIEventInfo, HttpSession session) {
        
        Window viewStateWindow = (Window)session.getAttribute("viewState");
        Window previousViewStateWindow = viewStateWindow.clone();
        
        viewStateWindow.processUIEvent(serverUIEventInfo.getControlId(), serverUIEventInfo.getEventName());
        
        String renderScript = viewStateWindow.getRenderScript(previousViewStateWindow);
        
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