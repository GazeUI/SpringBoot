import * as GazeUICore from '../gazeui-core.mjs';

// We have to observe two special cases when dealing with event handlers and nested controls:
// 
//   1. If there is one ancestor control with an event handler and one descendant without it,
//      the event will be fired on the ancestor control if the descendant control is stimulated.
//   2. If both the ancestor and descendant controls have event handlers, the event will be fired
//      on both controls when the descendant control is stimulated.
//   
//   We deal with these two special cases checking if 'target' and 'currentTarget' are the same.
//   We also use 'stopImmediatePropagation' just to certify that no other events will run for the same action.
//   See the following link for more details about event order:
//   
//     [1]: https://www.quirksmode.org/js/events_order.html
export async function onClickHandler(mouseEvent) {
    if (mouseEvent.target == mouseEvent.currentTarget) {
        mouseEvent.stopImmediatePropagation();
        await GazeUICore.processServerUIEvent(mouseEvent.target.id, 'Click');
    }
}