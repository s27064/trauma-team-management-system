package com.olafnaleszkiewicz.traumateam.view.team;

import com.olafnaleszkiewicz.traumateam.model.aerialvehicle.AerialVehicle;
import com.olafnaleszkiewicz.traumateam.model.aerialvehicle.Heli;
import com.olafnaleszkiewicz.traumateam.model.aerialvehicle.Ion;
import com.olafnaleszkiewicz.traumateam.model.enums.VehicleState;
import com.olafnaleszkiewicz.traumateam.service.AVService;
import com.olafnaleszkiewicz.traumateam.view.common.Ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class VehiclesPanel extends JPanel {
    private final AVService vehicleService;

    private final JTable table = Ui.table(new int[] { 40, 140, 100, 180, 110, 120 },
            "ID", "Serial", "Type", "Home base", "State", "Team");
    private final DefaultTableModel model = (DefaultTableModel) table.getModel();
    private List<AerialVehicle> vehicles = List.of();

    private final JLabel lblTitle = new JLabel();
    private final JLabel lblSpecs = new JLabel();
    private final JTextArea txtCombat = Ui.text();
    private final JTextArea txtMedical = Ui.text();
    private final JPanel fields = Ui.verticalPanel();
    private final JButton btnState = new JButton("Change State");
    private final JButton btnWeapons = new JButton("Add");
    private final JButton btnMedical = new JButton("Add");
    private final JButton btnRemoveWeapons = Ui.dangerButton("Remove");
    private final JButton btnRemoveMedical = Ui.dangerButton("Remove");
    private final JPanel actions = Ui.actions(btnState);

    public VehiclesPanel(AVService vehicleService) {
        this.vehicleService = vehicleService;

        JButton btnNew = Ui.menuButton("New Vehicle", Ui.menuItem("Helicopter", this::newHeli),
                Ui.menuItem("Ion", this::newIon));
        btnState.addActionListener(e -> changeState());
        btnWeapons.addActionListener(e -> changeWeapons());
        btnMedical.addActionListener(e -> addMedicalModule());
        btnRemoveWeapons.addActionListener(e -> removeWeapons());
        btnRemoveMedical.addActionListener(e -> removeMedicalModule());

        Ui.addField(fields, "Specs", lblSpecs);
        Ui.addField(fields, "Weapons", txtCombat, btnWeapons, btnRemoveWeapons);
        Ui.addField(fields, "Medical module", txtMedical, btnMedical, btnRemoveMedical);
        Ui.layout(this, Ui.toolbar(btnNew), table, Ui.details(lblTitle, fields, actions));

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                showDetails(selected());
        });

        Ui.refreshWhenShown(this, this::refresh);
        refresh();
        showDetails(null);
    }

    private void newHeli() {
        String[] v = Ui.form(this, "New Helicopter", "Serial number", "Home base", "Horse power", "Range (km)");
        if (v == null)
            return;

        Ui.run(this, () -> {
            vehicleService.createHeli(v[0], v[1], Integer.parseInt(v[2]), Double.parseDouble(v[3]));
            refresh();
        });
    }

    private void newIon() {
        String[] v = Ui.form(this, "New Ion", "Serial number", "Home base", "Max altitude (m)", "Max speed (km/h)");
        if (v == null)
            return;

        Ui.run(this, () -> {
            vehicleService.createIon(v[0], v[1], Double.parseDouble(v[2]), Double.parseDouble(v[3]));
            refresh();
        });
    }

    private void changeState() {
        AerialVehicle vehicle = selected();
        VehicleState state = Ui.choose(this, "Change State", List.of(VehicleState.values()), Enum::name);
        if (state == null)
            return;

        Ui.run(this, () -> {
            vehicleService.updateVehicleState(vehicle.getId(), state);
            refresh();
        });
    }

    private void changeWeapons() {
        AerialVehicle vehicle = selected();
        if (vehicle.isCombat())
            editWeapons(vehicle);
        else
            addWeapons(vehicle);
    }

    private void addWeapons(AerialVehicle vehicle) {
        String[] v = Ui.form(this, "Add Weapons", "Weapons (comma separated)");
        if (v == null)
            return;

        Ui.run(this, () -> {
            vehicleService.armVehicle(vehicle.getId(), Ui.commaList(v[0]));
            refresh();
        });
    }

    private void editWeapons(AerialVehicle vehicle) {
        String[] v = Ui.editForm(this, "Edit Weapons", new String[] { "Weapons (comma separated)" },
                new String[] { String.join(", ", vehicle.getCombat().getArmaments()) });
        if (v == null)
            return;

        Ui.run(this, () -> {
            vehicleService.updateWeapons(vehicle.getId(), Ui.commaList(v[0]));
            refresh();
        });
    }

    private void removeWeapons() {
        AerialVehicle vehicle = selected();
        if (!Ui.confirm(this, "Remove", "Remove all weapons from " + vehicle.getSerialNumber() + "?"))
            return;

        Ui.run(this, () -> {
            vehicleService.removeWeapons(vehicle.getId());
            refresh();
        });
    }

    private void removeMedicalModule() {
        AerialVehicle vehicle = selected();
        if (!Ui.confirm(this, "Remove", "Remove the medical module from " + vehicle.getSerialNumber() + "?"))
            return;

        Ui.run(this, () -> {
            vehicleService.removeMedicalModule(vehicle.getId());
            refresh();
        });
    }

    private void addMedicalModule() {
        AerialVehicle vehicle = selected();
        String[] v = Ui.form(this, "Add Medical Module", "Client capacity (1-4)");
        if (v == null)
            return;

        Ui.run(this, () -> {
            vehicleService.addMedicalModule(vehicle.getId(), Integer.parseInt(v[0]));
            refresh();
        });
    }

    private AerialVehicle selected() {
        int row = table.getSelectedRow();
        return row >= 0 ? vehicles.get(row) : null;
    }

    private void refresh() {
        int row = table.getSelectedRow();
        vehicles = vehicleService.findAll();
        model.setRowCount(0);
        for (AerialVehicle v : vehicles) {
            String team = v.getTraumaTeam() == null ? "None" : v.getTraumaTeam().getTeamCodename();
            model.addRow(new Object[] { v.getId(), v.getSerialNumber(), type(v), v.getHomeBase(),
                    v.getVehicleState(), team });
        }
        if (row >= 0 && row < vehicles.size())
            table.setRowSelectionInterval(row, row);
    }

    private static String type(AerialVehicle vehicle) {
        return vehicle instanceof Heli ? "Helicopter" : "Ion";
    }

    private static String specs(AerialVehicle vehicle) {
        if (vehicle instanceof Heli heli)
            return heli.getHorsePower() + " hp, " + heli.getRangeKm() + " km";
        Ion ion = (Ion) vehicle;
        return ion.getMaxAltitudeM() + " m, " + ion.getMaxSpeedKmh() + " km/h";
    }

    private void showDetails(AerialVehicle vehicle) {
        fields.setVisible(vehicle != null);
        actions.setVisible(vehicle != null);

        if (vehicle == null) {
            lblTitle.setText("Select a vehicle");
            return;
        }

        lblTitle.setText(vehicle.getSerialNumber());
        lblSpecs.setText(specs(vehicle));
        txtCombat.setText(vehicle.isCombat() ? String.join(", ", vehicle.getCombat().getArmaments()) : "None");
        txtMedical.setText(vehicle.isMedical() ? vehicle.getMedical().getClientCapacity() + " clients" : "None");

        btnWeapons.setText(vehicle.isCombat() ? "Edit" : "Add");
        btnRemoveWeapons.setEnabled(vehicle.isCombat());
        btnMedical.setEnabled(!vehicle.isMedical());
        btnRemoveMedical.setEnabled(vehicle.isMedical());
    }
}
