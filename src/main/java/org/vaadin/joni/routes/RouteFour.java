package org.vaadin.joni.routes;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.vaadin.joni.MainView;
import org.vaadin.joni.shortcutcomponents.ShortcutTextField;

@Route(value = "routefour", layout = MainView.class)
public class RouteFour extends VerticalLayout {
    public RouteFour() {
        add(new Paragraph("Here we test shortcuts within a TextField!"));

        TextField field = new TextField("This does not have a shortcut ", "Write inside of me!");

        ShortcutTextField scField = new ShortcutTextField(
                "This has shortcut: DELETE (as you'll notice, it triggers even if the component does not have focus)",
                "Write here, too!",
                Key.DELETE);

        add(field, scField);
    }
}
