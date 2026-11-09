package com.olafnaleszkiewicz.traumateam.view.common;

import com.formdev.flatlaf.FlatClientProperties;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.TransactionSystemException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public final class Ui {
    private static final int BUTTON_GAP = 8;
    private static final int BUTTON_MIN_WIDTH = 150;

    private static final String PRIMARY_STYLE = "font: bold;"
            + "background: #0059C1;"
            + "focusedBackground: #0059C1;"
            + "foreground: #FFFFFF;"
            + "hoverBackground: #004FAB;"
            + "pressedBackground: #00428F;"
            + "borderColor: #0059C1;"
            + "hoverBorderColor: #004FAB;";

    private static final String DANGER_STYLE = "borderWidth: 1;"
            + "hoverBackground: #B91C1C; hoverForeground: #FFFFFF; hoverBorderColor: #B91C1C;"
            + "pressedBackground: #991B1B; pressedForeground: #FFFFFF;"
            + "[light]background: $Button.background; [light]focusedBackground: $Button.background;"
            + "[light]foreground: #B71C1C;"
            + "[dark]background: #3A3A3A; [dark]focusedBackground: #3A3A3A;"
            + "[dark]foreground: #FFB4AB; [dark]borderColor: #5A5A5A;";

    private static final String CONFIRM_DANGER_STYLE = "font: bold;"
            + "background: #B91C1C;"
            + "focusedBackground: #B91C1C;"
            + "foreground: #FFFFFF;"
            + "hoverBackground: #991B1B;"
            + "pressedBackground: #7F1D1D;"
            + "borderColor: #B91C1C;"
            + "hoverBorderColor: #991B1B;";

    /** Keeps a dialog's default button (the one Enter presses) looking like an ordinary button. */
    private static final String NEUTRAL_DEFAULT_STYLE = "default.background: $Button.background;"
            + "default.focusedBackground: $Button.background;"
            + "default.foreground: $Button.foreground;"
            + "default.hoverBackground: $Button.hoverBackground;"
            + "default.pressedBackground: $Button.pressedBackground;"
            + "default.borderColor: $Button.borderColor;"
            + "default.hoverBorderColor: $Button.hoverBorderColor;"
            + "default.boldText: false;";

    /** The main action of a screen or dialog. */
    public static JButton primaryButton(String text) {
        return styledButton(text, PRIMARY_STYLE);
    }

    /** Deletes, detaches or clears something. Stays quiet until hovered. */
    public static JButton dangerButton(String text) {
        return styledButton(text, DANGER_STYLE);
    }

    /** A primary button opening a menu, for one action that comes in several kinds. */
    public static JButton menuButton(String text, JMenuItem... items) {
        JButton button = primaryButton(text + "  \u25BE");
        JPopupMenu menu = new JPopupMenu();
        for (JMenuItem item : items)
            menu.add(item);
        button.addActionListener(e -> {
            SwingUtilities.updateComponentTreeUI(menu);
            Dimension size = menu.getPreferredSize();
            menu.setPopupSize(Math.max(button.getWidth(), size.width), size.height);
            menu.show(button, 0, button.getHeight());
        });
        return button;
    }

    public static JMenuItem menuItem(String text, Runnable action) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(e -> action.run());
        return item;
    }

    private static JButton styledButton(String text, String style) {
        JButton button = new JButton(text);
        button.putClientProperty(FlatClientProperties.STYLE, style);
        return button;
    }

    private static JPanel buttonGrid(int rows, int columns, JButton... buttons) {
        JPanel grid = new JPanel(new GridLayout(rows, columns, BUTTON_GAP, BUTTON_GAP)) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (JButton button : buttons)
            grid.add(button);
        return grid;
    }

    private static void applyMinWidth(JComponent button) {
        button.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, BUTTON_MIN_WIDTH);
    }

    /** Equal-width buttons on the end of a dialog's bottom row, with optional content filling the rest of it. */
    public static JPanel footer(JComponent start, JButton... buttons) {
        for (JButton button : buttons)
            applyMinWidth(button);

        JPanel footer = new JPanel(new BorderLayout(BUTTON_GAP, 0));
        if (start != null)
            footer.add(start, BorderLayout.CENTER);
        footer.add(buttonGrid(1, 0, buttons), BorderLayout.EAST);
        return footer;
    }

    public static JTable table(int[] widths, String... columns) {
        JTable table = new JTable(new DefaultTableModel(columns, 0));
        table.setDefaultEditor(Object.class, null);
        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD, 13f));
        ((JLabel) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        return table;
    }

    public static JPanel toolbar(JComponent... items) {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        for (int i = 0; i < items.length; i++) {
            if (i > 0)
                actions.add(Box.createHorizontalStrut(BUTTON_GAP));
            if (items[i] instanceof JButton)
                applyMinWidth(items[i]);
            actions.add(items[i]);
        }
        return actions;
    }

    public static void layout(JPanel panel, JPanel toolbar, JTable table, JPanel details) {
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        if (details != null)
            panel.add(details, BorderLayout.EAST);
    }

    public static JPanel verticalPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    public static JPanel actions(JButton... buttons) {
        JPanel actions = verticalPanel();
        if (buttons.length == 0)
            return actions;

        JSeparator separator = new JSeparator();
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, separator.getPreferredSize().height));
        actions.add(separator);
        actions.add(Box.createVerticalStrut(12));
        actions.add(buttonGrid(0, 1, buttons));
        return actions;
    }

    public static JPanel details(JLabel title, JPanel fields, JPanel actions) {
        title.putClientProperty(FlatClientProperties.STYLE_CLASS, "h3");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel details = verticalPanel();
        details.setBorder(BorderFactory.createEmptyBorder(4, 10, 0, 4));
        details.setPreferredSize(new Dimension(300, 0));
        details.add(title);
        details.add(Box.createVerticalStrut(16));
        details.add(fields);
        details.add(Box.createVerticalGlue());
        details.add(actions);
        return details;
    }

    public static void addField(JPanel fields, String caption, JComponent value, JButton... buttons) {
        JLabel label = new JLabel(caption);
        label.putClientProperty(FlatClientProperties.STYLE, "foreground: $Label.disabledForeground");
        value.setAlignmentX(Component.LEFT_ALIGNMENT);
        fields.add(label);
        fields.add(value);
        if (buttons.length > 0) {
            fields.add(Box.createVerticalStrut(6));
            fields.add(buttonGrid(1, buttons.length, buttons));
        }
        fields.add(Box.createVerticalStrut(buttons.length > 0 ? 14 : 12));
    }

    public static void addListField(JPanel fields, String caption, JList<ListItem> list, JButton add,
            JButton remove) {
        list.setVisibleRowCount(3);
        JScrollPane scroll = new JScrollPane(list);
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, scroll.getPreferredSize().height));
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        remove.setEnabled(false);
        list.addListSelectionListener(e -> remove.setEnabled(!list.isSelectionEmpty()));

        JPanel buttons = buttonGrid(1, 2, add, remove);

        JLabel label = new JLabel(caption);
        label.putClientProperty(FlatClientProperties.STYLE, "foreground: $Label.disabledForeground");
        fields.add(label);
        fields.add(scroll);
        fields.add(Box.createVerticalStrut(6));
        fields.add(buttons);
        fields.add(Box.createVerticalStrut(14));
    }

    public static JTextArea text() {
        JTextArea area = new JTextArea() {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        area.setEditable(false);
        area.setFocusable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setOpaque(false);
        area.setBorder(BorderFactory.createEmptyBorder());
        return area;
    }

    public static void refreshWhenShown(JPanel panel, Runnable refresh) {
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refresh.run();
            }
        });
    }

    public static String[] form(Component parent, String title, String... labels) {
        return editForm(parent, title, labels, new String[labels.length]);
    }

    public static String[] editForm(Component parent, String title, String[] labels, String[] values) {
        JPanel panel = new JPanel(new GridLayout(labels.length, 2, 8, 6));
        JTextField[] fields = new JTextField[labels.length];
        for (int i = 0; i < labels.length; i++) {
            fields[i] = new JTextField(values[i], 18);
            panel.add(new JLabel(labels[i]));
            panel.add(fields[i]);
        }

        while (true) {
            int ok = JOptionPane.showConfirmDialog(parent, panel, title, JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (ok != JOptionPane.OK_OPTION)
                return null;

            String[] entered = Arrays.stream(fields).map(f -> f.getText().trim()).toArray(String[]::new);
            if (Arrays.stream(entered).noneMatch(String::isEmpty))
                return entered;
            error(parent, "All fields are required");
        }
    }

    public static <T> T choose(Component parent, String title, List<T> items, Function<T, String> label) {
        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "There is nothing to choose from", title,
                    JOptionPane.INFORMATION_MESSAGE);
            return null;
        }

        JComboBox<String> combo = new JComboBox<>(items.stream().map(label).toArray(String[]::new));
        int ok = JOptionPane.showConfirmDialog(parent, combo, title, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        return ok == JOptionPane.OK_OPTION ? items.get(combo.getSelectedIndex()) : null;
    }

    /** Asks before a destructive action. The confirm button is named after the action; Cancel is the default. */
    public static boolean confirm(Component parent, String action, String message) {
        JButton confirm = styledButton(action, CONFIRM_DANGER_STYLE);
        JButton cancel = styledButton("Cancel", NEUTRAL_DEFAULT_STYLE);
        for (JButton button : List.of(confirm, cancel)) {
            button.addActionListener(e -> ((JOptionPane) SwingUtilities.getAncestorOfClass(JOptionPane.class, button))
                    .setValue(button));
        }
        return JOptionPane.showOptionDialog(parent, message, "Confirm " + action, JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE, null, new Object[] { confirm, cancel }, cancel) == 0;
    }

    public static List<String> commaList(String text) {
        return new ArrayList<>(Arrays.stream(text.split(",")).map(String::trim).toList());
    }

    public static void run(Component parent, Runnable action) {
        try {
            action.run();
        } catch (NumberFormatException ex) {
            error(parent, "Enter a valid number");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            error(parent, ex.getMessage());
        } catch (DataIntegrityViolationException ex) {
            error(parent, "That value is already in use");
        } catch (ConstraintViolationException ex) {
            error(parent, describe(ex));
        } catch (TransactionSystemException ex) {
            if (ex.getRootCause() instanceof ConstraintViolationException violation)
                error(parent, describe(violation));
            else
                throw ex;
        }
    }

    private static String describe(ConstraintViolationException ex) {
        ConstraintViolation<?> violation = ex.getConstraintViolations().iterator().next();
        return violation.getPropertyPath() + " " + violation.getMessage();
    }

    private static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
