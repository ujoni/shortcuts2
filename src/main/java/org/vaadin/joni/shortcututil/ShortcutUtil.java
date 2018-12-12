package org.vaadin.joni.shortcututil;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import org.vaadin.marcus.shortcut.Shortcut;

import java.util.*;


public final class ShortcutUtil {

    private static HashMap<String, ArrayDeque<HandlerHolder>> scHandlers = new HashMap<>();
    private static final Object lock = new Object();

    @FunctionalInterface
    public interface Handler {
        void handle();
    }

    static public void addShortcut(final Component component, final Key key, final Handler handler) {
        // component is already attached
        if (component.getUI().isPresent()) {
            addHandler(key, component, handler);
        }
        else {
            component.addAttachListener(
                    attachEvent -> addHandler(key, component, handler));
            component.addDetachListener(
                    detachEvent -> removeHandler(key, component));
        }
    }

    static private void addHandler(Key keys, Component component, Handler handler) {
        // Shortcut.add will probably blow up with re-registrations for the same component
        // using an ugly wrapper hack to get around the fact that Shortcuts addon does not let us know the keys
        Shortcut.add(UI.getCurrent().getElement(), keys, () -> ShortcutUtil.handleShortCut(keys));

        String id = makeId(keys);
        ArrayDeque<HandlerHolder> deque;
        synchronized (lock) {
            if (!scHandlers.containsKey(id)) {
                deque = new ArrayDeque<>();
                scHandlers.put(id, deque);
            } else {
                deque = scHandlers.get(id);
            }

            HandlerHolder holder = new HandlerHolder(component, handler);
            // we can use holder for removal since its identity is supplied by component
            deque.remove(holder);
            deque.addLast(holder);
        }
    }

    static private void removeHandler(Key keys, Component component) {
        // TODO: remove the actual key binding, too! :D
        String id = makeId(keys);
        synchronized (lock) {
            ArrayDeque<HandlerHolder> deque = scHandlers.get(id);
            if (deque != null) {
                // HandlerHolder is constructed for identity purposes
                deque.remove(new HandlerHolder(component, null));
                if (deque.size() == 0) {
                    scHandlers.remove(id);
                }
            }
        }
    }

    private static void handleShortCut(Key keys) {
        String id = makeId(keys);
        synchronized (lock) {
            ArrayDeque<HandlerHolder> deque = scHandlers.get(id);
            if (deque != null && !deque.isEmpty()) {
                deque.getLast().exec();
            }
        }
    }

    private static String makeId(Key... keys) {
        List<String> keyStrings = new LinkedList<>();
        for (Key k : keys) {
            keyStrings.addAll(k.getKeys());
        }
        return String.join("", keyStrings);
    }

    private static class HandlerHolder {
        private Component component;
        private Handler handler;

        HandlerHolder(Component component, Handler handler) {
            this.component = component;
            this.handler = handler;
        }

        boolean ownedBy(Component component) {
            return this.component.equals(component);
        }

        void exec() {
            this.handler.handle();
        }

        @Override
        public int hashCode() {
            return component.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) return false;
            if (o instanceof HandlerHolder) {
                return this.component.equals(((HandlerHolder) o).component);
            }
            return false;
        }
    }

}
