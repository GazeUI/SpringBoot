//
// Copyright (c) 2020 Rosberg Linhares (rosberglinhares@gmail.com)
// Licensed under the MIT license. See LICENSE file in the project root for full license information.
//

export async function processServerUIEvent(controlId, eventName) {
    let eventInfo = {
        controlId: controlId,
        eventName: eventName
    };
    
    let fetchOptions = {
        method: 'POST',
        cache: 'no-store',  // The 'no-store' cache mode bypass the cache completely
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(eventInfo)
    };
    
    let response = await fetch('process-server-ui-event.js', fetchOptions);
    
    // We are using the 'response.body' property because, at Dec/2019, it has 73.94% of global usage¹, while the
    // 'response.text()' method has only 36.71%².
    // 
    //   [1]: https://caniuse.com/#feat=mdn-api_body_body
    //   [2]: https://caniuse.com/#feat=mdn-api_body_text
    let responseText = await getTextFromStream(response.body);
    
    // Once it was not possible to execute this code as a module, we have two consequences:
    // 
    //   1. The code must be executed in async mode to allow using await over dynamic import statements.
    //   2. It is not possible to use top-level awaits¹.
    //   
    //     [1]: https://v8.dev/features/top-level-await
    return executeJavaScriptCodeAsync(responseText);
}

async function getTextFromStream(readableStream) {
    let reader = readableStream.getReader();
    let utf8Decoder = new TextDecoder();
    let nextChunk;
    
    let resultStr = '';
    
    while (!(nextChunk = await reader.read()).done) {
        let partialData = nextChunk.value;
        resultStr += utf8Decoder.decode(partialData);
    }
    
    return resultStr;
}

async function executeJavaScriptCodeAsync(code) {
    // 1. According to the MDN website, you should never use 'eval()', but 'window.Function()' instead¹.
    // 2. AsyncFunction is not a native global object².
    //
    //   [1] https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/eval
    //   [2] https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/AsyncFunction
    
    let AsyncFunction = Object.getPrototypeOf(async function(){}).constructor;
    let asyncFunction = new AsyncFunction(code);
    
    return asyncFunction();
}