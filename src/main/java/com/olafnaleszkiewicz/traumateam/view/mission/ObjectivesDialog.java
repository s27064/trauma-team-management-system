package com.olafnaleszkiewicz.traumateam.view.mission;

import com.olafnaleszkiewicz.traumateam.model.Mission;
import com.olafnaleszkiewicz.traumateam.model.Objective;
import com.olafnaleszkiewicz.traumateam.model.TraumaTeam;
import com.olafnaleszkiewicz.traumateam.model.enums.ObjectiveStatus;
import com.olafnaleszkiewicz.traumateam.service.MissionService;
import com.olafnaleszkiewicz.traumateam.service.TraumaTeamService;
import com.olafnaleszkiewicz.traumateam.view.common.Ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ObjectivesDialog extends JDialog {
    private final MissionService missionService;
    private final TraumaTeamService teamService;
    private final Long missionId;

    private final JTable table = Ui.table(new int[] { 400, 130, 160 }, "Objective", "Status", "Team");
    private final DefaultTableModel model = (DefaultTableModel) table.getModel();
    private final JLabel lblStatus = new JLabel();
    private List<Objective> objectives = List.of();

    public ObjectivesDialog(Window owner, Mission mission, MissionService missionService,
            TraumaTeamService teamService) {
        super(owner, "Objectives: " + mission.getName(), ModalityType.APPLICATION_MODAL);
        this.missionService = missionService;
        this.teamService = teamService;
        this.missionId = mission.getId();

        JButton btnAdd = new JButton("Add Objective");
        JButton btnTeam = new JButton("Assign Team");
        JButton btnStatus = new JButton("Set Status");
        JButton btnLaunch = Ui.primaryButton("Launch Mission");

        btnTeam.setEnabled(false);
        btnStatus.setEnabled(false);
        table.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = table.getSelectedRow() >= 0;
            btnTeam.setEnabled(hasSelection);
            btnStatus.setEnabled(hasSelection);
        });

        btnAdd.addActionListener(e -> addObjective());
        btnTeam.addActionListener(e -> assignTeam());
        btnStatus.addActionListener(e -> setStatus());
        btnLaunch.addActionListener(e -> Ui.run(this, () -> {
            missionService.launchMission(missionId);
            refresh();
        }));

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        content.add(lblStatus, BorderLayout.NORTH);
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        content.add(Ui.footer(null, btnAdd, btnTeam, btnStatus, btnLaunch), BorderLayout.SOUTH);
        setContentPane(content);

        refresh();
        setSize(760, 420);
        setLocationRelativeTo(owner);
    }

    private void addObjective() {
        String[] values = Ui.form(this, "Add Objective", "Description");
        if (values == null)
            return;

        Ui.run(this, () -> {
            missionService.addObjective(missionId, values[0]);
            refresh();
        });
    }

    private void assignTeam() {
        Objective objective = selected();
        if (objective == null)
            return;

        TraumaTeam team = Ui.choose(this, "Assign Team", teamService.findAll(), TraumaTeam::getTeamCodename);
        if (team == null)
            return;

        Ui.run(this, () -> {
            missionService.assignTeam(missionId, objective.getId(), team.getId());
            refresh();
        });
    }

    private void setStatus() {
        Objective objective = selected();
        if (objective == null)
            return;

        ObjectiveStatus status = Ui.choose(this, "Set Status", List.of(ObjectiveStatus.values()), Enum::name);
        if (status == null)
            return;

        Ui.run(this, () -> {
            missionService.updateObjectiveStatus(objective.getId(), status);
            refresh();
        });
    }

    private Objective selected() {
        int row = table.getSelectedRow();
        return row >= 0 ? objectives.get(row) : null;
    }

    private void refresh() {
        int row = table.getSelectedRow();
        objectives = missionService.findObjectives(missionId);
        model.setRowCount(0);
        for (Objective o : objectives) {
            String team = o.getAssignedTeam() == null ? "None" : o.getAssignedTeam().getTeamCodename();
            model.addRow(new Object[] { o.getDescription(), o.getStatus(), team });
        }
        if (row >= 0 && row < objectives.size())
            table.setRowSelectionInterval(row, row);

        lblStatus.setText("Mission status: " + missionService.getById(missionId).getMissionStatus());
    }
}
