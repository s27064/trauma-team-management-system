package com.olafnaleszkiewicz.traumateam.view.person;

import com.olafnaleszkiewicz.traumateam.model.Mission;
import com.olafnaleszkiewicz.traumateam.model.person.Client;
import com.olafnaleszkiewicz.traumateam.service.MissionService;
import com.olafnaleszkiewicz.traumateam.view.common.Ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ClientDetailDialog extends JDialog {
    private final DefaultListModel<Mission> listModel = new DefaultListModel<>();
    private final JList<Mission> list = new JList<>(listModel);
    private final JComboBox<Mission> combo = new JComboBox<>();
    private final JButton btnAssign = Ui.primaryButton("Assign Mission");
    private final JButton btnUnassign = Ui.dangerButton("Unassign Mission");
    private final MissionService missionService;

    public ClientDetailDialog(Window owner, Client client, MissionService svc) {
        super(owner, "Missions for " + client.getPerson().getSurname(), ModalityType.APPLICATION_MODAL);
        this.missionService = svc;

        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(l, value, index, isSelected, cellHasFocus);
                if (value instanceof Mission m) {
                    setText("NAME: " + m.getName() + " | DESCRIPTION: " + m.getDescription());
                }
                return this;
            }
        });

        loadAssigned(client);

        List<Mission> all = svc.findAll();
        DefaultComboBoxModel<Mission> cbm = new DefaultComboBoxModel<>(all.toArray(new Mission[0]));
        combo.setModel(cbm);

        btnAssign.addActionListener(e -> {
            Mission m = (Mission) combo.getSelectedItem();
            if (m == null)
                return;

            try {
                svc.addClient(m.getId(), client.getId());
                loadAssigned(client);
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "Assignment Error",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        btnUnassign.setEnabled(false);

        list.addListSelectionListener(e -> btnUnassign.setEnabled(!list.isSelectionEmpty()));

        btnUnassign.addActionListener(e -> {
            int row = list.getSelectedIndex();
            Mission m = listModel.getElementAt(row);

            if (Ui.confirm(this, "Unassign", "Unassign client from mission \"" + m.getName() + "\"?")) {
                missionService.removeClient(m.getId(), client.getId());
                loadAssigned(client);
            }
        });

        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(l, value, index, isSelected, cellHasFocus);
                if (value instanceof Mission m) {
                    setText(m.getName());
                }
                return this;
            }
        });

        JPanel top = new JPanel(new BorderLayout());
        top.add(new JScrollPane(list), BorderLayout.CENTER);

        setLayout(new BorderLayout(5, 5));
        add(top, BorderLayout.CENTER);
        add(Ui.footer(combo, btnAssign, btnUnassign), BorderLayout.SOUTH);

        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setSize(700, 400);
        setLocationRelativeTo(owner);
    }

    private void loadAssigned(Client client) {
        listModel.clear();
        List<Mission> assigned = missionService.findAllReferencingClient(client.getId());
        assigned.forEach(listModel::addElement);
    }
}
