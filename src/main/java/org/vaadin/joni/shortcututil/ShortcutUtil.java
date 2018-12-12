package org.vaadin.joni.shortcututil;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.DomListenerRegistration;

import java.util.*;

import static org.vaadin.marcus.shortcut.Shortcut.*;


public final class ShortcutUtil {

    private static HashMap<String, ArrayDeque<HandlerHolder>> scHolders = new HashMap<>();
    private static HashMap<String, DomListenerRegistration> registrations = new HashMap<>();
    private static final Object lock = new Object();

    static public void addShortcut(final Component component, final Key key, final Listener listener) {
        // component is already attached
        if (component.getUI().isPresent()) {
            addHandler(key, component, listener);
        }
        else {
            component.addAttachListener(
                    attachEvent -> addHandler(key, component, listener));
            component.addDetachListener(
                    detachEvent -> removeHandler(key, component));
        }
    }

    static private void addHandler(Key keys, Component component, Listener listener) {
        String id = makeId(keys);
        ArrayDeque<HandlerHolder> deque;

        synchronized (lock) {
            if (!scHolders.containsKey(id)) {
                deque = new ArrayDeque<>();
                scHolders.put(id, deque);
            } else {
                deque = scHolders.get(id);
            }
            locked_remove(id, component);

            // add the shortcut handler to the front-end only once
            if (!registrations.containsKey(id)) {
                DomListenerRegistration registration = add(UI.getCurrent().getElement(), keys, () -> ShortcutUtil.handleShortCut(id));
                registrations.put(id, registration);
            }

            deque.addFirst(new HandlerHolder(component, listener));
        }
    }

    static private void removeHandler(Key keys, Component component) {
        // TODO: remove the actual key binding, too! :D
        String id = makeId(keys);
        synchronized (lock) {
            locked_remove(id, component);
        }
    }

    static private void locked_remove(String id, Component component) {
        if (!Thread.holdsLock(lock)) {
            throw new RuntimeException("The thread " + Thread.currentThread().getName() + " does not own the lock. This method cannot be called without the lock");
        }
        Deque<HandlerHolder> deque = scHolders.get(id);
        if (deque != null && !deque.isEmpty()) {
            HandlerHolder identity = new HandlerHolder(component);
            deque.remove(identity);

            if (deque.isEmpty()) {
                // There are no shortcuts left so we remove both the shortcut holder + the dom registration
                scHolders.remove(id);
                registrations.remove(id).remove();
            }
        }
    }

    private static void handleShortCut(String id) {
        synchronized (lock) {
            ArrayDeque<HandlerHolder> deque = scHolders.get(id);
            if (deque != null && !deque.isEmpty()) {
                deque.getFirst().exec();
            }
        }
    }

    private static String makeId(Key... keys) {
        List<String> keyStrings = new LinkedList<>();
        for (Key k : keys) {
            keyStrings.addAll(k.getKeys());
        }
        keyStrings.sort(String::compareToIgnoreCase);
        return String.join("", keyStrings);
    }

    private static class HandlerHolder {
        private Component component;
        private Listener listener;

        HandlerHolder(Component component) {
            this.component = component;
        }

        HandlerHolder(Component component, Listener listener) {
            this.component = component;
            this.listener = listener;
        }

        void exec() {
            if (listener != null)
                listener.handleAction();
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
