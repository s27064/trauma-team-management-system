package com.olafnaleszkiewicz.traumateam.view.common;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Supplier;

public final class ThemeToggleButton {
    private static final Supplier<Boolean> isDark = () -> UIManager.getLookAndFeel() instanceof FlatMacDarkLaf;

    public static JButton create() {
        JButton btn = new JButton("Switch Theme");
        btn.setToolTipText("Switch between Dark/Light mode");
        btn.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_TOOLBAR_BUTTON);

        btn.addActionListener(ThemeToggleButton::toggle);
        return btn;
    }

    private static void toggle(ActionEvent e) {
        FlatAnimatedLafChange.showSnapshot();
        try {
            if (isDark.get()) {
                FlatMacLightLaf.setup();
                UIManager.put("Table.alternateRowColor", new Color(240, 240, 240));
            } else {
                FlatMacDarkLaf.setup();
                UIManager.put("Table.alternateRowColor", new Color(65, 65, 65));
            }
            FlatLaf.updateUI();
        } finally {
            FlatAnimatedLafChange.hideSnapshotWithAnimation();
        }
    }

}
