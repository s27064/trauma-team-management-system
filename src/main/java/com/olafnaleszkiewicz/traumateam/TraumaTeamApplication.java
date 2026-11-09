package com.olafnaleszkiewicz.traumateam;

import com.olafnaleszkiewicz.traumateam.view.MainFrame;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import javax.swing.*;
import java.awt.*;

@SpringBootApplication
public class TraumaTeamApplication {
    public static void main(String[] args) {
        FlatAnimatedLafChange.duration = 1000;
        FlatAnimatedLafChange.resolution = 16;

        FlatMacLightLaf.setup();

        UIManager.put("Table.alternateRowColor", new Color(240, 240, 240));
        UIManager.put("Component.focusColor", new Color(0, 0, 0, 0));
        UIManager.put("Table.cellMargins", new Insets(2, 8, 2, 8));
        UIManager.put("TableHeader.cellMargins", new Insets(4, 8, 4, 8));

        var ctx = new SpringApplicationBuilder(TraumaTeamApplication.class)
                .headless(false)
                .run(args);
        SwingUtilities.invokeLater(() -> new MainFrame(ctx).setVisible(true));
    }
}
