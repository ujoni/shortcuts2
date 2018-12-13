package org.vaadin.joni;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import org.vaadin.joni.routes.RouteFour;
import org.vaadin.joni.routes.RouteOne;
import org.vaadin.joni.routes.RouteThree;
import org.vaadin.joni.routes.RouteTwo;
import org.vaadin.joni.shortcututil.ShortcutUtil;


/**
 * The main view contains a button and a click listener.
 */
@Route("")
public class MainView extends VerticalLayout implements RouterLayout {
    public MainView() {
        Nav nav = new Nav();
        nav.setSizeFull();
        nav.add(new RouterLink("Route One", RouteOne.class));
        nav.add(new Span(" | "));
        nav.add(new RouterLink("Route Two", RouteTwo.class));
        nav.add(new Span(" | "));
        nav.add(new RouterLink("Route Three", RouteThree.class));
        nav.add(new Span(" | "));
        nav.add(new RouterLink("Route Four", RouteFour.class));

        add(nav, new Paragraph("Welcome to the awesome shortcut page!"));
        add(new Paragraph("The whole page has Arrow Up key binding. Try it!"));

        ShortcutUtil.addShortcut(this, Key.ARROW_UP, this::handleShortcut);
    }

    private void handleShortcut() {
        Notification.show("MainView handled UP");
    }
}
