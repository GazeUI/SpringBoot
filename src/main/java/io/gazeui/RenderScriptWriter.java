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

package io.gazeui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class RenderScriptWriter extends PrintWriter {
    
    public static boolean USE_STATIC_IMPORTS = false;
    public static boolean USE_DYNAMIC_IMPORTS = true;
    
    // Using a LinkedHashMap to have insertion order. Thereby, the import statements will appear like
    // the components were used by the developer.
    private Map<String, String> modulesToImport = new LinkedHashMap<>();
    private boolean useDynamicImports;
    
    public RenderScriptWriter() {
        this(USE_STATIC_IMPORTS);
    }
    
    public RenderScriptWriter(boolean useDynamicImports) {
        // autoFlush does not matter when using StringWriter
        super(new StringWriter());
        
        this.useDynamicImports = useDynamicImports;
    }
    
    public boolean isEmpty() {
        return ((StringWriter)this.out).getBuffer().length() == 0;
    }
    
    public void importModule(String moduleName, String modulePath) {
        if (!this.modulesToImport.containsKey(moduleName)) {
            this.modulesToImport.put(moduleName, modulePath);
        }
    }
    
    public void print(RenderScriptWriter writer) {
        this.print(writer, super::print);
    }
    
    public void println(RenderScriptWriter writer) {
        this.print(writer, super::println);
    }
    
    private void print(RenderScriptWriter writer, Consumer<Object> printOperation) {
        this.modulesToImport.putAll(writer.modulesToImport);
        
        // Print the writer passed as argument without its modules to this writer
        writer.modulesToImport.clear();
        printOperation.accept(writer);
    }
    
    @Override
    public String toString() {
        StringBuilder sbScript = new StringBuilder();
        
        this.modulesToImport.forEach((moduleName, modulePath) -> {
            if (!this.useDynamicImports) {
                sbScript.append(String.format("import * as %s from '%s';\n", moduleName, modulePath));
            } else {
                sbScript.append(String.format("let %s = await import('%s');\n", moduleName, modulePath));
            }
        });
        
        sbScript.append(this.out.toString());
        
        return sbScript.toString();
    }
}