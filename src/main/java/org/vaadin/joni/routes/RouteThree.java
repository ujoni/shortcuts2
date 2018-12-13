package org.vaadin.joni.routes;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.joni.MainView;
import org.vaadin.joni.shortcututil.ShortcutUtil;

@Route(value = "routethree", layout = MainView.class)
public class RouteThree extends HorizontalLayout {
    private static final String NO_SHORTCUT = "Enable Down Arrow shortcut";
    private static final String YES_SHORTCUT = "Disable Down Arrow shortcut";

    private boolean enabled = false;
    public RouteThree() {
        Button button = new Button(NO_SHORTCUT);
        button.addClickListener(event -> {
            enabled = !enabled;
            if (enabled) {
                ShortcutUtil.addShortcut(this, Key.ARROW_DOWN, this::handleShortcut);
                button.setText(YES_SHORTCUT);
            }
            else {
                ShortcutUtil.removeShortcut(this, Key.ARROW_DOWN);
                button.setText(NO_SHORTCUT);
            }
        });

        add(new Paragraph("Click the button to toggle the shortcut!"), button);
    }

    private void handleShortcut() {
        Notification.show("RouteThree handled DOWN");
    }
}
