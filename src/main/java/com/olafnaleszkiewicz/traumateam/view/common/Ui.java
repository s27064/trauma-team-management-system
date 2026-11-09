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

    public static JPanel toolbar(JButton... buttons) {
        buttons[0].putClientProperty(FlatClientProperties.STYLE,
                "background: $Button.default.background; foreground: $Button.default.foreground;"
                        + "hoverBackground: $Button.default.hoverBackground;");

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        for (int i = 0; i < buttons.length; i++) {
            if (i > 0)
                actions.add(Box.createHorizontalStrut(8));
            actions.add(buttons[i]);
        }

        JPanel top = new JPanel(new BorderLayout());
        top.add(actions, BorderLayout.WEST);
        top.add(ThemeToggleButton.create(), BorderLayout.EAST);
        return top;
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
        JPanel panel = verticalPanel();
        for (int i = 0; i < buttons.length; i++) {
            if (i > 0)
                panel.add(Box.createVerticalStrut(8));
            buttons[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(buttons[i]);
        }
        return panel;
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

    public static void addField(JPanel fields, String caption, JComponent value) {
        JLabel label = new JLabel(caption);
        label.putClientProperty(FlatClientProperties.STYLE, "foreground: $Label.disabledForeground");
        value.setAlignmentX(Component.LEFT_ALIGNMENT);
        fields.add(label);
        fields.add(value);
        fields.add(Box.createVerticalStrut(12));
    }

    public static void addListField(JPanel fields, String caption, JList<ListItem> list, JButton add,
            JButton remove) {
        list.setVisibleRowCount(3);
        JScrollPane scroll = new JScrollPane(list);
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, scroll.getPreferredSize().height));
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        remove.setEnabled(false);
        list.addListSelectionListener(e -> remove.setEnabled(!list.isSelectionEmpty()));

        JPanel buttons = new JPanel(new GridLayout(1, 2, 8, 0));
        buttons.add(add);
        buttons.add(remove);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttons.setMaximumSize(new Dimension(Integer.MAX_VALUE, buttons.getPreferredSize().height));

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

    public static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
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
