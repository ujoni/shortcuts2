package org.vaadin.joni;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import org.vaadin.joni.shortcututil.ShortcutUtil;

public class RouteTwoChild extends Div {
    public RouteTwoChild() {

        this.add(new Paragraph("Hello! I am a dynamic component on RouteTwo and I override the RouteTwo's shortcut"));
        this.add(new Paragraph("I also have a shortcut for Arrow Down! Try me!"));

        ShortcutUtil.addShortcut(this, Key.ARROW_RIGHT, this::handleShortcut);
        ShortcutUtil.addShortcut(this, Key.ARROW_DOWN, this::handleShortcut);
    }

    private void handleShortcut() {
        Notification.show("RouteTwoChild handled a shortcut");
    }
}
