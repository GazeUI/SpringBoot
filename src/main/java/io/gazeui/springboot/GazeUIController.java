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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.gazeui.springboot.configuration.GazeUIConfiguration;
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
        // We are using the 'response.body' property because, at Dec/2019, it has 73.94% of global usage,
        // while the 'response.text()' method has only 36.71%. See these links for details:
        //   [1]: https://caniuse.com/#feat=mdn-api_body_body
        //   [2]: https://caniuse.com/#feat=mdn-api_body_text
        
        String html =
                "<!DOCTYPE html>\n" + 
                "<html>\n" + 
                "<head>\n" + 
                "  <meta charset='UTF-8'>\n" + 
                "  <title></title>\n" + 
                "  <script defer src='create-ui.js'></script>\n" + 
                "  <script>\n" + 
                "    async function btnUpdateUI_OnClick() {\n" + 
                "        let response = await fetch('update-ui.js');\n" + 
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
                "  </script>\n" + 
                "</head>\n" + 
                "<body>\n" + 
                "  <button onclick='btnUpdateUI_OnClick()'>Update UI</button>\n" + 
                "</body>\n" + 
                "</html>";
        
        return html;
    }
    
    @GetMapping(path = "/create-ui.js", produces = "text/javascript")
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
    
    @GetMapping(path = "/update-ui.js", produces = "text/javascript")
    public String getUIUpdateScript(HttpSession session) {
        Window viewStateWindow = (Window)session.getAttribute("viewState");
        Window previousViewStateWindow = viewStateWindow.clone();
        
        // TODO: Temporary
        viewStateWindow.updateUI();
        
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