package org.vaadin.joni.shortcutcomponents;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.textfield.TextField;
import org.vaadin.joni.shortcututil.ShortcutUtil;


public class ShortcutTextField extends TextField {
    public ShortcutTextField(String label, Key shortcutKey) {
        super(label);
        ShortcutUtil.addShortcut(this, shortcutKey, this::handleShortcut);
    }

    public ShortcutTextField(String label, String placeHolder, Key shortcutKey) {
        super(label, placeHolder);
        ShortcutUtil.addShortcut(this, shortcutKey, this::handleShortcut);
    }

    private void handleShortcut() {
        this.clear();
    }
}
