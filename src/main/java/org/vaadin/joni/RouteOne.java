package org.vaadin.joni;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.joni.shortcututil.ShortcutUtil;

@Route(value = "routeone", layout = MainView.class)
public class RouteOne extends VerticalLayout {
    public RouteOne() {
        add (new Paragraph("This is page one (1)."));
        add(new Paragraph("Shortcut here is Left Arrow"));

        ShortcutUtil.addShortcut(this, Key.ARROW_LEFT, this::handleKeyDown);
    }

    private void handleKeyDown() {
        Notification.show("RouteOne handled LEFT");
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        add(new Paragraph(UI.getCurrent().toString()));
        add(new Paragraph(UI.getCurrent().getPage().toString()));
    }
}
