package com.olafnaleszkiewicz.traumateam.view.mission;

import com.formdev.flatlaf.FlatClientProperties;
import com.olafnaleszkiewicz.traumateam.model.enums.MissionStatus;
import com.olafnaleszkiewicz.traumateam.service.MissionService;
import com.olafnaleszkiewicz.traumateam.service.TraumaTeamService;
import com.olafnaleszkiewicz.traumateam.model.Mission;
import com.olafnaleszkiewicz.traumateam.view.common.ThemeToggleButton;
import com.olafnaleszkiewicz.traumateam.view.common.Ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MissionsPanel extends JPanel {
    private final MissionService missionService;
    private final TraumaTeamService teamService;
    private final MissionTableModel model = new MissionTableModel();
    private final JTable table = new JTable(model);

    private final JButton btnNew = new JButton("New Mission");
    private final JButton btnDelete = new JButton("Delete Mission");
    private final JButton btnClients = new JButton("Manage Clients");
    private final JButton btnObjectives = new JButton("Manage Objectives");

    private final JLabel lblTitle = new JLabel();
    private final JLabel lblStatus = new JLabel();
    private final JTextArea txtDesc = new JTextArea();
    private final JPanel detailFields = new JPanel();

    public MissionsPanel(MissionService missionService, TraumaTeamService teamService) {
        this.missionService = missionService;
        this.teamService = teamService;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        /* ===== toolbar ================================================= */
        btnNew.putClientProperty(FlatClientProperties.STYLE,
                "background: $Button.default.background; foreground: $Button.default.foreground;"
                        + "hoverBackground: $Button.default.hoverBackground;");

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actions.add(btnNew);
        actions.add(Box.createHorizontalStrut(8));
        actions.add(btnDelete);

        JPanel top = new JPanel(new BorderLayout());
        top.add(actions, BorderLayout.WEST);
        top.add(ThemeToggleButton.create(), BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        btnDelete.setEnabled(false);

        btnNew.addActionListener(e -> {
            NewMissionDialog dlg = new NewMissionDialog(SwingUtilities.getWindowAncestor(this), missionService);
            dlg.setVisible(true);
            if (dlg.isSucceeded())
                refreshTable();
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0)
                return;
            Mission m = model.getMissionAt(row);
            int ok = JOptionPane.showConfirmDialog(this, "Delete mission “" + m.getName() + "”?", "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                missionService.deleteMission(m.getId());
                refreshTable();
            }
        });

        btnClients.addActionListener(e -> openClientsDialog());
        btnObjectives.addActionListener(e -> openObjectivesDialog());

        /* ===== table =================================================== */
        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD, 13f));
        ((JLabel) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        table.getColumnModel().getColumn(0).setPreferredWidth(40); // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(180); // Name
        table.getColumnModel().getColumn(2).setPreferredWidth(460); // Description
        table.getColumnModel().getColumn(3).setPreferredWidth(120); // Status

        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected)
                    setForeground(statusColor((MissionStatus) value));
                return this;
            }
        });

        table.getSelectionModel().addListSelectionListener(ev -> {
            boolean sel = table.getSelectedRow() >= 0;
            btnDelete.setEnabled(sel);
            showDetails(sel ? model.getMissionAt(table.getSelectedRow()) : null);
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2)
                    openClientsDialog();
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        /* ===== details ================================================= */
        lblTitle.putClientProperty(FlatClientProperties.STYLE_CLASS, "h3");
        lblTitle.setAlignmentX(LEFT_ALIGNMENT);
        btnClients.setAlignmentX(LEFT_ALIGNMENT);
        btnObjectives.setAlignmentX(LEFT_ALIGNMENT);

        txtDesc.setEditable(false);
        txtDesc.setFocusable(false);
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setOpaque(false);
        txtDesc.setBorder(BorderFactory.createEmptyBorder());
        txtDesc.setAlignmentX(LEFT_ALIGNMENT);

        detailFields.setLayout(new BoxLayout(detailFields, BoxLayout.Y_AXIS));
        detailFields.setAlignmentX(LEFT_ALIGNMENT);
        addCaption("Status");
        detailFields.add(lblStatus);
        detailFields.add(Box.createVerticalStrut(12));
        addCaption("Description");
        detailFields.add(txtDesc);

        JPanel details = new JPanel();
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.setBorder(BorderFactory.createEmptyBorder(4, 10, 0, 4));
        details.setPreferredSize(new Dimension(300, 0));
        details.add(lblTitle);
        details.add(Box.createVerticalStrut(16));
        details.add(detailFields);
        details.add(Box.createVerticalGlue());
        details.add(btnObjectives);
        details.add(Box.createVerticalStrut(8));
        details.add(btnClients);
        add(details, BorderLayout.EAST);

        Ui.refreshWhenShown(this, this::refreshTable);
        refreshTable();
    }

    private void addCaption(String caption) {
        JLabel label = new JLabel(caption);
        label.putClientProperty(FlatClientProperties.STYLE, "foreground: $Label.disabledForeground");
        detailFields.add(label);
    }

    private static Color statusColor(MissionStatus status) {
        return switch (status) {
            case COMPLETED -> new Color(46, 158, 79);
            case FAILED -> new Color(224, 69, 58);
            case IN_PROGRESS -> new Color(47, 143, 230);
            case READY -> new Color(208, 138, 0);
            default -> new Color(138, 138, 138);
        };
    }

    private void openClientsDialog() {
        int row = table.getSelectedRow();
        if (row < 0)
            return;
        Mission m = model.getMissionAt(row);
        new MissionDetailDialog(SwingUtilities.getWindowAncestor(this), m, missionService).setVisible(true);
    }

    private void openObjectivesDialog() {
        int row = table.getSelectedRow();
        if (row < 0)
            return;
        Mission m = model.getMissionAt(row);
        new ObjectivesDialog(SwingUtilities.getWindowAncestor(this), m, missionService, teamService).setVisible(true);
        refreshTable();
    }

    private void refreshTable() {
        model.setMissions(missionService.findAll());
        showDetails(null);
    }

    private void showDetails(Mission m) {
        detailFields.setVisible(m != null);
        btnClients.setVisible(m != null);
        btnObjectives.setVisible(m != null);

        if (m == null) {
            lblTitle.setText("Select a mission");
        } else {
            lblTitle.setText(m.getName());
            lblStatus.setText(m.getMissionStatus().toString());
            lblStatus.setForeground(statusColor(m.getMissionStatus()));
            txtDesc.setText(m.getDescription());
        }
    }
}
