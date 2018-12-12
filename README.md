[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/vaadin-flow/Lobby#?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

# Small demo for UI-based shortcuts

Demonstrates how one could implement and use component-specific shortcuts without the need for a
focused html element. Each shortcut has a single handler registered to the page's body. When a component
is attached to the UI, its shortcuts are bound to the body and when the component is detached, those
are removed. If multiple components register for the same shortcut and both are attached, the component 
that registered its shortcut last has priority (a stack).

### Points of interest:
- Implemented on top of [Shortcut addon](https://vaadin.com/directory/component/shortcut/links) - no flow changed
- ShortcutUtil.java has all the "implementation"
- Currently supports only single key bindings (because I was lazy)

### Problems that require further investigation
- Since the shortcuts are all bound to body, normal event bubbling provided by DOM does not work.
  This means, that the event bubbling has to be implemented on the server-side (upside is a less chatty implementation)
- Does not support only-when-focused shortcuts. The component that has priority will receive the callback
  whether it has focus or not.  
  Suggestion: enable event-bubbling for `Focusable` components
- Prevent default functionality is a complete mystery. How should it work? Shortcut should probably enable 
  `preventDefault`. However, in cases where the event bubbles up, default behaviour might be needed (i.e. for 
  `Focusable` components when they are not focused.)