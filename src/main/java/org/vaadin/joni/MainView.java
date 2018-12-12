package org.vaadin.joni;

import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;

/**
 * The main view contains a button and a click listener.
 */
@Route("")
public class MainView extends VerticalLayout implements RouterLayout {
    public MainView() {
        Nav nav = new Nav();

        nav.add(new RouterLink("Route One", RouteOne.class));
        nav.add(new RouterLink("Route Two", RouteTwo.class));


        add(nav, new Paragraph("Welcome to the awesome shortcut page!"));
    }
}
