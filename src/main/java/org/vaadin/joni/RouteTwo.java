package org.vaadin.joni;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.joni.shortcututil.ShortcutUtil;

@Route(value = "routetwo", layout = MainView.class)
public class RouteTwo extends VerticalLayout {
    public RouteTwo() {
        add(new Paragraph("This is page two (2)."));
        add(new Paragraph("Shortcut here is Right Arrow"));

        ShortcutUtil.addShortcut(this, Key.ARROW_RIGHT, this::handleKeyDown);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        add(new Paragraph(UI.getCurrent().toString()));
        add(new Paragraph(UI.getCurrent().getPage().toString()));
    }

    private void handleKeyDown() {
        Notification.show("Handled RIGHT");
    }
}