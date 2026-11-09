package com.olafnaleszkiewicz.traumateam.view.mission;

import javax.swing.*;

import com.olafnaleszkiewicz.traumateam.service.MissionService;
import com.olafnaleszkiewicz.traumateam.view.common.Ui;

import java.awt.*;

public class NewMissionDialog extends JDialog {
    private final JTextField nameField = new JTextField(1);
    private final JTextArea descArea = new JTextArea(5, 20);
    private boolean succeeded;

    public NewMissionDialog(Window owner, MissionService svc) {
        super(owner, "Create New Mission", ModalityType.APPLICATION_MODAL);

        JPanel nameRow = new JPanel(new BorderLayout(10, 0));
        nameRow.add(new JLabel("Name:"), BorderLayout.WEST);
        nameRow.add(nameField, BorderLayout.CENTER);

        JPanel descRow = new JPanel(new BorderLayout(0, 4));
        descRow.add(new JLabel("Description:"), BorderLayout.NORTH);
        descRow.add(new JScrollPane(descArea), BorderLayout.CENTER);

        JPanel form = new JPanel(new BorderLayout(0, 10));
        form.add(nameRow, BorderLayout.NORTH);
        form.add(descRow, BorderLayout.CENTER);

        JButton ok = Ui.primaryButton("Create Mission"), cancel = new JButton("Cancel");
        ok.addActionListener(e -> {
            String n = nameField.getText().trim();
            String d = descArea.getText().trim();
            if (n.isEmpty() || d.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and description are required", "Validation",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            svc.create(n, d);
            succeeded = true;
            dispose();
        });
        cancel.addActionListener(e -> dispose());

        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(Ui.footer(null, ok, cancel), BorderLayout.SOUTH);
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        pack();
        setLocationRelativeTo(owner);
    }

    public boolean isSucceeded() {
        return succeeded;
    }
}
