package com.olafnaleszkiewicz.traumateam.view.team;

import com.olafnaleszkiewicz.traumateam.model.TraumaTeam;
import com.olafnaleszkiewicz.traumateam.model.aerialvehicle.AerialVehicle;
import com.olafnaleszkiewicz.traumateam.model.enums.TeamRank;
import com.olafnaleszkiewicz.traumateam.model.enums.VehicleState;
import com.olafnaleszkiewicz.traumateam.model.person.Person;
import com.olafnaleszkiewicz.traumateam.service.AVService;
import com.olafnaleszkiewicz.traumateam.service.PersonService;
import com.olafnaleszkiewicz.traumateam.service.TraumaTeamService;
import com.olafnaleszkiewicz.traumateam.view.common.ListItem;
import com.olafnaleszkiewicz.traumateam.view.common.Ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class TeamsPanel extends JPanel {
    private final TraumaTeamService teamService;
    private final PersonService personService;
    private final AVService vehicleService;

    private final JTable table = Ui.table(new int[] { 40, 300, 160 }, "ID", "Codename", "Rank");
    private final DefaultTableModel model = (DefaultTableModel) table.getModel();
    private List<TraumaTeam> teams = List.of();

    private final JLabel lblTitle = new JLabel();
    private final JList<ListItem> lstVehicles = new JList<>();
    private final JList<ListItem> lstPilots = new JList<>();
    private final JList<ListItem> lstMedics = new JList<>();
    private final JPanel fields = Ui.verticalPanel();
    private final JButton btnPilot = new JButton("Add");
    private final JButton btnMedic = new JButton("Add");
    private final JButton btnVehicle = new JButton("Assign");
    private final JButton btnRemovePilot = new JButton("Remove");
    private final JButton btnRemoveMedic = new JButton("Remove");
    private final JButton btnRemoveVehicle = new JButton("Remove");

    public TeamsPanel(TraumaTeamService teamService, PersonService personService, AVService vehicleService) {
        this.teamService = teamService;
        this.personService = personService;
        this.vehicleService = vehicleService;

        JButton btnNew = new JButton("New Team");
        btnNew.addActionListener(e -> newTeam());
        btnPilot.addActionListener(e -> addPilot());
        btnMedic.addActionListener(e -> addMedic());
        btnVehicle.addActionListener(e -> assignVehicle());
        btnRemovePilot.addActionListener(e -> removePilot());
        btnRemoveMedic.addActionListener(e -> removeMedic());
        btnRemoveVehicle.addActionListener(e -> removeVehicle());

        Ui.addListField(fields, "Vehicles", lstVehicles, btnVehicle, btnRemoveVehicle);
        Ui.addListField(fields, "Pilots", lstPilots, btnPilot, btnRemovePilot);
        Ui.addListField(fields, "Medics", lstMedics, btnMedic, btnRemoveMedic);
        Ui.layout(this, Ui.toolbar(btnNew), table, Ui.details(lblTitle, fields, new JPanel()));

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                showDetails(selected());
        });

        Ui.refreshWhenShown(this, this::refresh);
        refresh();
        showDetails(null);
    }

    private void newTeam() {
        String[] values = Ui.form(this, "New Team", "Codename");
        if (values == null)
            return;

        TeamRank rank = Ui.choose(this, "Rank", List.of(TeamRank.values()), Enum::name);
        if (rank == null)
            return;

        Ui.run(this, () -> {
            teamService.create(values[0], rank);
            refresh();
        });
    }

    private void addPilot() {
        TraumaTeam team = selected();
        List<Person> free = personService.findAllCrew().stream()
                .filter(p -> p.getPilot() != null && p.getPilot().getTeam() == null)
                .toList();
        Person person = Ui.choose(this, "Add Pilot", free, p -> p.getName() + " " + p.getSurname());
        if (person == null)
            return;

        Ui.run(this, () -> {
            teamService.addPilot(team.getId(), person.getId());
            refresh();
        });
    }

    private void addMedic() {
        TraumaTeam team = selected();
        List<Person> free = personService.findAllCrew().stream()
                .filter(p -> p.getMedic() != null && p.getMedic().getTeam() == null)
                .toList();
        Person person = Ui.choose(this, "Add Medic", free, p -> p.getName() + " " + p.getSurname());
        if (person == null)
            return;

        Ui.run(this, () -> {
            teamService.addMedic(team.getId(), person.getId());
            refresh();
        });
    }

    private void assignVehicle() {
        TraumaTeam team = selected();
        List<AerialVehicle> free = vehicleService.findAll().stream()
                .filter(v -> v.getVehicleState() == VehicleState.AVAILABLE && v.getTraumaTeam() == null)
                .toList();
        AerialVehicle vehicle = Ui.choose(this, "Assign Vehicle", free, AerialVehicle::getSerialNumber);
        if (vehicle == null)
            return;

        Ui.run(this, () -> {
            teamService.assignVehicle(team.getId(), vehicle.getId());
            refresh();
        });
    }

    private void removePilot() {
        Long pilotId = lstPilots.getSelectedValue().id();
        Ui.run(this, () -> {
            teamService.removePilot(selected().getId(), pilotId);
            refresh();
        });
    }

    private void removeMedic() {
        Long medicId = lstMedics.getSelectedValue().id();
        Ui.run(this, () -> {
            teamService.removeMedic(selected().getId(), medicId);
            refresh();
        });
    }

    private void removeVehicle() {
        Long vehicleId = lstVehicles.getSelectedValue().id();
        Ui.run(this, () -> {
            teamService.removeVehicle(selected().getId(), vehicleId);
            refresh();
        });
    }

    private TraumaTeam selected() {
        int row = table.getSelectedRow();
        return row >= 0 ? teams.get(row) : null;
    }

    private void refresh() {
        int row = table.getSelectedRow();
        teams = teamService.findAll();
        model.setRowCount(0);
        for (TraumaTeam t : teams)
            model.addRow(new Object[] { t.getId(), t.getTeamCodename(), t.getRank() });
        if (row >= 0 && row < teams.size())
            table.setRowSelectionInterval(row, row);
    }

    private void showDetails(TraumaTeam team) {
        fields.setVisible(team != null);

        if (team == null) {
            lblTitle.setText("Select a team");
            return;
        }

        TraumaTeam full = teamService.getById(team.getId());
        lblTitle.setText(full.getTeamCodename());
        lstVehicles.setListData(full.getVehicles().stream()
                .map(v -> new ListItem(v.getId(), v.getSerialNumber()))
                .toArray(ListItem[]::new));
        lstPilots.setListData(full.getPilots().stream()
                .map(p -> new ListItem(p.getId(), p.getPerson().getName() + " " + p.getPerson().getSurname()))
                .toArray(ListItem[]::new));
        lstMedics.setListData(full.getMedics().stream()
                .map(m -> new ListItem(m.getId(), m.getPerson().getName() + " " + m.getPerson().getSurname()))
                .toArray(ListItem[]::new));
    }
}
