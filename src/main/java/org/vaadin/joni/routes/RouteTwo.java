package org.vaadin.joni.routes;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.joni.MainView;
import org.vaadin.joni.shortcututil.ShortcutUtil;

@Route(value = "routetwo", layout = MainView.class)
public class RouteTwo extends VerticalLayout {

    private RouteTwoChild child;

    public RouteTwo() {
        add(new Paragraph("This is page two (2)."));
        add(new Paragraph("Shortcut here is Right Arrow"));

        Button button = new Button("Enable RouteTwoChild");
        button.addClickListener(buttonClickEvent -> {
            if (child.getUI().isPresent()) {
                remove(child);
            }
            else {
                add(child);
            }
        });
        add(button);

        child = new RouteTwoChild();

        ShortcutUtil.addShortcut(this, Key.ARROW_RIGHT, this::handleKeyDown);
    }

    private void handleKeyDown() {
        Notification.show("RouteTwo handled RIGHT");
    }
}
