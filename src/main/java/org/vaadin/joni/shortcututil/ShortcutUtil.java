package org.vaadin.joni.shortcututil;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.DomListenerRegistration;

import java.util.*;

import static org.vaadin.marcus.shortcut.Shortcut.*;


public final class ShortcutUtil {
    /*
        This is a map from makeId(Key...) -> list of HandlerHolders
        Handler holders contain the shortcut owning Component and a Listener
     */
    private static HashMap<String, ArrayDeque<HandlerHolder>> scHolders = new HashMap<>();
    /*
        This is a map from makeId(Key...) -> DomListenerRegistrations
        It is used to make sure that double registrations for the same binding do not cause
        double binding in the front-end. Front end only has one handler per binding.
     */
    private static HashMap<String, DomListenerRegistration> registrations = new HashMap<>();
    private static final Object lock = new Object();

    /**
     * Adds a one {@link Key} shortcut for the given  {@link Component}. The provided {@link Listener} is executed
     * when the shortcut is invoked, the component is attached, and no other component has overridden the shortcut.
     * <p>
     *     Previous {@code Component} + {@code Key} combo (if one exists) is overridden by this new one and the given
     *     {@code Component} now has priority over other registrations on the same {@code Key}.
     *
     * @param component     Shortcut owner
     * @param key           Key used to invoke the shortcut
     * @param listener      Handler method for the shortcut invocation
     */
    static public void addShortcut(final Component component, final Key key, final Listener listener) {
        // component is already attached
        if (component.getUI().isPresent()) {
            addHandler(key, component, listener);
        }
        else {
            component.addAttachListener(
                    attachEvent -> addHandler(key, component, listener));
            component.addDetachListener(
                    detachEvent -> removeShortcut(component, key));
        }
    }

    /***
     * Removes the shortcut identified by {@link Key} {@code key} and owned by {@link Component} {@code component}.
     *
     * @param component     Shortcut owner
     * @param keys          Key used to invoke the shortcut
     */
    static public void removeShortcut(Component component, Key keys) {
        String id = makeId(keys);
        synchronized (lock) {
            locked_remove(id, component);
        }
    }

    /**
     * Internals of {@link #addShortcut(Component, Key, Listener)}. Performs the actual shortcut binding.
     * Removes the previous binding if one exists and registers a new one. Also adds the front-end handler
     * if one does not exist.
     *
     * @param keys
     * @param component
     * @param listener
     * @see #addShortcut(Component, Key, Listener)
     */
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

            // remove old shortcut registration for this component
            // this also means, that re-registering a shortcut for a component causes
            // that component's shortcut to have priority over other components with
            // the same shortcut
            locked_remove(id, component);

            // add the shortcut handler to the front-end only once
            if (!registrations.containsKey(id)) {
                DomListenerRegistration registration = add(UI.getCurrent().getElement(), keys, () -> ShortcutUtil.handleShortCut(id));
                registrations.put(id, registration);
            }

            deque.addFirst(new HandlerHolder(component, listener));
        }
    }

    /**
     * Inner implementation for {@link #removeShortcut(Component, Key)}. The caller must own the monitor of {@link #lock},
     * otherwise a runtime exception will be thrown.
     * @param id            Shortcut identifier generated with {@link #makeId(Key...)}
     * @param component     Shortcut owner
     */
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

    /**
     * Invokes the shortcut identified by {@code id}.
     * Selects the latest addition for this particular id for execution.
     * TODO: event-bubbling and only-if-focused
     *
     * @param id    Shortcut identifier generated with {@link #makeId(Key...)}
     */
    private static void handleShortCut(String id) {
        synchronized (lock) {
            ArrayDeque<HandlerHolder> deque = scHolders.get(id);
            if (deque != null && !deque.isEmpty()) {
                deque.getFirst().exec();
            }
        }
    }

    /**
     * Generates a {@link String} id from the given {@link Key} list. The id is
     * order agnostic: ctrl+a == a+ctrl
     *
     * @param keys  Keys to generate id from
     * @return An identifier that maps to all key_count! combinations
     */
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
