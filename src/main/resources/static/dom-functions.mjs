//
// Copyright (c) 2020 Rosberg Linhares (rosberglinhares@gmail.com)
// Licensed under the MIT license. See LICENSE file in the project root for full license information.
//

export function clearCurrentWebPage() {
    clearNodeContents(document.body);
}

function clearNodeContents(node) {
    // The 'remove', 'remove first child' and 'remove last child' methods have similar performance.
    // See the links below for details:
    // 
    //   [1]: https://jsperf.com/innerhtml-vs-removechild/556
    //   [2]: https://stackoverflow.com/questions/3955229/remove-all-child-elements-of-a-dom-node-in-javascript
    while (node.firstChild != null) {
        node.firstChild.remove();
    }
}