package com.olafnaleszkiewicz.traumateam.view.person;

import com.olafnaleszkiewicz.traumateam.model.TraumaTeam;
import com.olafnaleszkiewicz.traumateam.model.enums.RoleType;
import com.olafnaleszkiewicz.traumateam.model.person.Address;
import com.olafnaleszkiewicz.traumateam.model.person.Person;
import com.olafnaleszkiewicz.traumateam.service.PersonService;
import com.olafnaleszkiewicz.traumateam.service.RoleService;
import com.olafnaleszkiewicz.traumateam.view.common.Ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class CrewPanel extends JPanel {
    private final PersonService personService;
    private final RoleService roleService;

    private final JTable table = Ui.table(new int[] { 40, 130, 130, 80, 170, 130 },
            "ID", "Name", "Surname", "Role", "Nickname / Specialisation", "Team");
    private final DefaultTableModel model = (DefaultTableModel) table.getModel();
    private List<Person> crew = List.of();

    private final JLabel lblTitle = new JLabel();
    private final JLabel lblAge = new JLabel();
    private final JTextArea txtAddress = Ui.text();
    private final JTextArea txtCerts = Ui.text();
    private final JPanel fields = Ui.verticalPanel();
    private final JButton btnRole = new JButton("Change Role");
    private final JButton btnDelete = Ui.dangerButton("Delete Crew Member");
    private final JPanel actions = Ui.actions(btnRole, btnDelete);

    public CrewPanel(PersonService personService, RoleService roleService) {
        this.personService = personService;
        this.roleService = roleService;

        JButton btnNew = Ui.menuButton("New Crew Member", Ui.menuItem("Pilot", this::newPilot),
                Ui.menuItem("Medic", this::newMedic));
        btnRole.addActionListener(e -> ChangeRole.show(this, roleService, selected(), this::refresh));
        btnDelete.addActionListener(e -> delete());

        Ui.addField(fields, "Age", lblAge);
        Ui.addField(fields, "Address", txtAddress);
        Ui.addField(fields, "Weapon certifications", txtCerts);
        Ui.layout(this, Ui.toolbar(btnNew), table, Ui.details(lblTitle, fields, actions));

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                showDetails(selected());
        });

        Ui.refreshWhenShown(this, this::refresh);
        refresh();
        showDetails(null);
    }

    private void newPilot() {
        String[] v = Ui.form(this, "New Pilot", "Name", "Surname", "Age", "Country", "City", "Street", "Nickname");
        if (v == null)
            return;

        Ui.run(this, () -> {
            personService.addPilot(v[0], v[1], Integer.parseInt(v[2]), new Address(v[3], v[4], v[5]), v[6]);
            refresh();
        });
    }

    private void newMedic() {
        String[] v = Ui.form(this, "New Medic", "Name", "Surname", "Age", "Country", "City", "Street",
                "Specialisation", "Weapon certs (comma separated)");
        if (v == null)
            return;

        Ui.run(this, () -> {
            personService.addMedic(v[0], v[1], Integer.parseInt(v[2]), new Address(v[3], v[4], v[5]), v[6],
                    Ui.commaList(v[7]));
            refresh();
        });
    }

    private void delete() {
        Person person = selected();
        if (!Ui.confirm(this, "Delete", "Delete " + person.getName() + " " + person.getSurname() + "?"))
            return;

        Ui.run(this, () -> {
            if (person.getRole() == RoleType.PILOT)
                personService.deletePilot(person.getId());
            else
                personService.deleteMedic(person.getId());
            refresh();
        });
    }

    private Person selected() {
        int row = table.getSelectedRow();
        return row >= 0 ? crew.get(row) : null;
    }

    private void refresh() {
        int row = table.getSelectedRow();
        crew = personService.findAllCrew();
        model.setRowCount(0);
        for (Person p : crew)
            model.addRow(new Object[] { p.getId(), p.getName(), p.getSurname(), p.getRole(), detail(p), team(p) });
        if (row >= 0 && row < crew.size())
            table.setRowSelectionInterval(row, row);
    }

    private static String detail(Person person) {
        return person.getPilot() != null ? person.getPilot().getNickname() : person.getMedic().getSpecialisation();
    }

    private static String team(Person person) {
        TraumaTeam team = person.getPilot() != null ? person.getPilot().getTeam() : person.getMedic().getTeam();
        return team == null ? "None" : team.getTeamCodename();
    }

    private void showDetails(Person person) {
        fields.setVisible(person != null);
        actions.setVisible(person != null);

        if (person == null) {
            lblTitle.setText("Select a crew member");
            return;
        }

        Address address = person.getAddress();
        lblTitle.setText(person.getName() + " " + person.getSurname());
        lblAge.setText(String.valueOf(person.getAge()));
        txtAddress.setText(address.getStreet() + "\n" + address.getCity() + ", " + address.getCountry());
        txtCerts.setText(person.getMedic() == null ? "None"
                : String.join(", ", person.getMedic().getWeaponCertifications()));
    }
}
