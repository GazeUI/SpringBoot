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
@RequestMapping(path = "/some-base-path")
public class GazeUIController {
    
    private final GazeUIConfiguration gazeUIConfiguration;
    
    @Autowired
    public GazeUIController(GazeUIConfiguration gazeUIConfiguration) {
        this.gazeUIConfiguration = gazeUIConfiguration;
    }
    
    @GetMapping(path = "/ui", produces = MediaType.TEXT_HTML_VALUE)
    public String getInitialHtml() {
        String html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <title></title>\n" +
                "  <script defer src=\"create-ui.js\"></script>\n" +
                "</head>\n" +
                "<body>\n" +
                "</body>\n" +
                "</html>";
        
        return html;
    }
    
    @GetMapping(path = "/create-ui.js", produces = "text/javascript")
    public String getUICreationScript(HttpSession session) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        
        Window viewStateWindow = (Window)session.getAttribute("viewState");
        
        if (viewStateWindow == null) {
            Class<? extends Window> mainWindowClass = this.gazeUIConfiguration.getMainWindowClass();
            viewStateWindow = mainWindowClass.getDeclaredConstructor().newInstance();
            
            session.setAttribute("viewState", viewStateWindow);
        }
        
        String script = viewStateWindow.getRenderScript();
        
        return script;
    }
}