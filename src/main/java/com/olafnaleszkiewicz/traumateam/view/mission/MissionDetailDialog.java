package com.olafnaleszkiewicz.traumateam.view.mission;

import com.olafnaleszkiewicz.traumateam.model.Mission;
import com.olafnaleszkiewicz.traumateam.model.person.Client;
import com.olafnaleszkiewicz.traumateam.service.MissionService;
import com.olafnaleszkiewicz.traumateam.view.common.Ui;

import javax.swing.*;
import java.awt.*;

public class MissionDetailDialog extends JDialog {
    private final DefaultListModel<Client> listModel = new DefaultListModel<>();
    private final JList<Client> list = new JList<>(listModel);
    private final JComboBox<Client> combo = new JComboBox<>();
    private final JButton btnAssign = Ui.primaryButton("Assign Client");
    private final JButton btnUnassign = Ui.dangerButton("Unassign Client");
    private final MissionService missionService;
    private final Mission mission;

    public MissionDetailDialog(Window owner, Mission mission, MissionService svc) {
        super(owner, "Clients in mission: " + mission.getName(), ModalityType.APPLICATION_MODAL);
        this.missionService = svc;
        this.mission = mission;

        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Client c) {
                    setText(c.getEmail());
                }
                return this;
            }
        });

        loadAssigned();
        loadAllClients();

        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof Client c) {
                    setText(" <" + c.getEmail() + ">");
                } else {
                    setText("");
                }
                return this;
            }
        });

        btnAssign.addActionListener(e -> {
            Client c = (Client) combo.getSelectedItem();
            if (c == null)
                return;

            try {
                missionService.addClient(mission.getId(), c.getId());
                loadAssigned();
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "Assignment error",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        btnUnassign.setEnabled(false);

        list.addListSelectionListener(ev -> btnUnassign.setEnabled(list.getSelectedIndex() >= 0));

        btnUnassign.addActionListener(e -> {
            int row = list.getSelectedIndex();
            Client c = listModel.getElementAt(row);

            if (Ui.confirm(this, "Unassign", "Unassign “" + c.getEmail() + "” from this mission?")) {
                missionService.removeClient(mission.getId(), c.getId());
                loadAssigned();
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

    private void loadAssigned() {
        listModel.clear();
        Mission full = missionService.getById(mission.getId());
        full.getClientsByEmail().values().forEach(listModel::addElement);
    }

    private void loadAllClients() {
        combo.removeAllItems();
        missionService.findAllClients()
                .forEach(combo::addItem);
    }
}
