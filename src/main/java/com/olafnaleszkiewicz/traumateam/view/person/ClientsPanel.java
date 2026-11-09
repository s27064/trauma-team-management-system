package com.olafnaleszkiewicz.traumateam.view.person;

import com.formdev.flatlaf.FlatClientProperties;
import com.olafnaleszkiewicz.traumateam.model.person.Client;
import com.olafnaleszkiewicz.traumateam.service.MissionService;
import com.olafnaleszkiewicz.traumateam.service.PersonService;
import com.olafnaleszkiewicz.traumateam.service.RoleService;
import com.olafnaleszkiewicz.traumateam.view.common.Ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Pattern;

public class ClientsPanel extends JPanel {

    private final ClientTableModel model = new ClientTableModel();
    private final JTable table = new JTable(model);
    private final TableRowSorter<ClientTableModel> sorter = new TableRowSorter<>(model);

    private final PersonService personService;
    private final MissionService missionService;
    private final RoleService roleService;

    private final JButton btnNew = Ui.primaryButton("New Client");
    private final JButton btnDelete = Ui.dangerButton("Delete Client");
    private final JButton btnMissions = new JButton("Manage Missions");
    private final JButton btnRole = new JButton("Change Role");
    private final JPanel actions = Ui.actions(btnMissions, btnRole, btnDelete);
    private final JTextField txtFilter = new JTextField(22);

    private final JLabel lblTitle = new JLabel();
    private final JLabel lblEmail = new JLabel();
    private final JLabel lblAge = new JLabel();
    private final JLabel lblJoined = new JLabel();
    private final JLabel lblStreet = new JLabel();
    private final JLabel lblCity = new JLabel();
    private final JLabel lblCountry = new JLabel();
    private final JPanel detailFields = new JPanel();

    public ClientsPanel(PersonService personService, MissionService missionService, RoleService roleService) {
        this.personService = personService;
        this.missionService = missionService;
        this.roleService = roleService;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        /* ===== toolbar ================================================= */
        txtFilter.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search by email");
        txtFilter.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        add(Ui.toolbar(txtFilter, btnNew), BorderLayout.NORTH);

        /* ==== Filter ==================================================== */
        table.setRowSorter(sorter);
        txtFilter.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                String text = txtFilter.getText().trim();
                if (text.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 1));
                }
            }

            public void insertUpdate(DocumentEvent e) {
                update();
            }

            public void removeUpdate(DocumentEvent e) {
                update();
            }

            public void changedUpdate(DocumentEvent e) {
                update();
            }
        });

        /* ===== buttons ================================================= */
        btnNew.addActionListener(e -> {
            NewClientDialog dlg = new NewClientDialog(SwingUtilities.getWindowAncestor(this), personService);
            dlg.setVisible(true);
            if (dlg.isSucceeded())
                refreshTable();
        });

        btnDelete.addActionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow < 0)
                return;
            int modelRow = table.convertRowIndexToModel(viewRow);
            Client c = model.getClientAt(modelRow);

            if (Ui.confirm(this, "Delete", "Delete client “" + c.getPerson().getSurname() + "”?")) {
                personService.deleteClient(c.getId());
                refreshTable();
            }
        });

        btnMissions.addActionListener(e -> openMissionsDialog());
        btnRole.addActionListener(e -> changeRole());

        /* ===== table =================================================== */
        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD, 13f));
        ((JLabel) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        table.getColumnModel().getColumn(0).setPreferredWidth(40); // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(300); // Email
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Joined
        table.getColumnModel().getColumn(3).setPreferredWidth(120); // Name
        table.getColumnModel().getColumn(4).setPreferredWidth(140); // Surname

        table.getSelectionModel().addListSelectionListener(ev -> {
            int viewRow = table.getSelectedRow();
            showDetails(viewRow >= 0 ? model.getClientAt(table.convertRowIndexToModel(viewRow)) : null);
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2)
                    openMissionsDialog();
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        /* ===== details ================================================= */
        detailFields.setLayout(new BoxLayout(detailFields, BoxLayout.Y_AXIS));
        detailFields.setAlignmentX(LEFT_ALIGNMENT);
        addField("Email", lblEmail);
        addField("Age", lblAge);
        addField("Joined", lblJoined);
        addField("Street", lblStreet);
        addField("City", lblCity);
        addField("Country", lblCountry);

        add(Ui.details(lblTitle, detailFields, actions), BorderLayout.EAST);

        Ui.refreshWhenShown(this, this::refreshTable);
        refreshTable();
    }

    private void addField(String caption, JLabel value) {
        JLabel label = new JLabel(caption);
        label.putClientProperty(FlatClientProperties.STYLE, "foreground: $Label.disabledForeground");
        detailFields.add(label);
        detailFields.add(value);
        detailFields.add(Box.createVerticalStrut(12));
    }

    private void openMissionsDialog() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0)
            return;
        Client c = model.getClientAt(table.convertRowIndexToModel(viewRow));
        new ClientDetailDialog(SwingUtilities.getWindowAncestor(this), c, missionService).setVisible(true);
    }

    private void changeRole() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0)
            return;
        Client c = model.getClientAt(table.convertRowIndexToModel(viewRow));
        ChangeRole.show(this, roleService, c.getPerson(), this::refreshTable);
    }

    private void refreshTable() {
        model.setClients(personService.findAllWithPerson());
        sorter.sort();
        showDetails(null);
    }

    private void showDetails(Client c) {
        detailFields.setVisible(c != null);
        actions.setVisible(c != null);

        if (c == null) {
            lblTitle.setText("Select a client");
        } else {
            var p = c.getPerson();
            lblTitle.setText(p.getName() + " " + p.getSurname());
            lblEmail.setText(c.getEmail());
            lblAge.setText(String.valueOf(p.getAge()));
            lblJoined.setText(c.getJoinDate().toString());
            lblStreet.setText(p.getAddress().getStreet());
            lblCity.setText(p.getAddress().getCity());
            lblCountry.setText(p.getAddress().getCountry());
        }
    }
}
