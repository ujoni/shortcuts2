package org.vaadin.joni.shortcututil;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.DomListenerRegistration;

import java.util.*;

import static org.vaadin.marcus.shortcut.Shortcut.*;


public final class ShortcutUtil {

    private static HashMap<String, ArrayDeque<HandlerHolder>> scHandlers = new HashMap<>();
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

        System.out.println("addHandler: " + id + ", " + component);

        DomListenerRegistration registration = add(UI.getCurrent().getElement(), keys, () -> ShortcutUtil.handleShortCut(keys));
        synchronized (lock) {
            if (!scHandlers.containsKey(id)) {
                deque = new ArrayDeque<>();
                scHandlers.put(id, deque);
            } else {
                deque = scHandlers.get(id);
            }
            System.out.print(" -> ");
            locked_remove(id, component);

            deque.addFirst(new HandlerHolder(component, listener, registration));
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
        System.out.println("locked_remove: " + id + ", " + component);
        if (!Thread.holdsLock(lock)) {
            throw new RuntimeException("The thread " + Thread.currentThread().getName() + " does not own the lock. This method cannot be called without the lock");
        }
        Deque<HandlerHolder> deque = scHandlers.get(id);
        if (deque != null && !deque.isEmpty()) {
            HandlerHolder identity = new HandlerHolder(component);
            Iterator<HandlerHolder> iterator = deque.iterator();
            HandlerHolder holder;
            while (iterator.hasNext()) {
                holder = iterator.next();

                if (holder.equals(identity)) {
                    System.out.println("locked_remove: found " + holder);
                    holder.clear();
                    iterator.remove();
                    break;
                }
            }

            if (deque.isEmpty()) {
                scHandlers.remove(id);
            }
        }
        else {
            System.out.println("locked_remove: deque was " + (deque == null ? "null" : "empty"));
        }
    }

    private static void handleShortCut(Key keys) {
        String id = makeId(keys);
        synchronized (lock) {
            ArrayDeque<HandlerHolder> deque = scHandlers.get(id);
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
        private DomListenerRegistration registration;

        HandlerHolder(Component component) {
            this.component = component;
        }

        HandlerHolder(Component component, Listener listener, DomListenerRegistration registration) {
            this.component = component;
            this.listener = listener;
            this.registration = registration;
        }

        boolean ownedBy(Component component) {
            return this.component.equals(component);
        }

        void exec() {
            if (listener != null)
                listener.handleAction();
        }

        void clear() {
            if (this.registration != null)
                this.registration.remove();
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
