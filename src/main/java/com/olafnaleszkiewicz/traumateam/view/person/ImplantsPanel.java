package com.olafnaleszkiewicz.traumateam.view.person;

import com.olafnaleszkiewicz.traumateam.model.Chrome;
import com.olafnaleszkiewicz.traumateam.model.enums.ImplantTier;
import com.olafnaleszkiewicz.traumateam.model.person.Person;
import com.olafnaleszkiewicz.traumateam.service.ChromeService;
import com.olafnaleszkiewicz.traumateam.service.PersonService;
import com.olafnaleszkiewicz.traumateam.view.common.Ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ImplantsPanel extends JPanel {
    private final ChromeService chromeService;
    private final PersonService personService;

    private final JTable table = Ui.table(new int[] { 40, 400, 120, 300 }, "ID", "Name", "Tier", "Owner");
    private final DefaultTableModel model = (DefaultTableModel) table.getModel();
    private List<Chrome> implants = List.of();

    private final JLabel lblTitle = new JLabel();
    private final JLabel lblTier = new JLabel();
    private final JLabel lblOwner = new JLabel();
    private final JPanel fields = Ui.verticalPanel();
    private final JButton btnDelete = Ui.dangerButton("Delete Implant");
    private final JPanel actions = Ui.actions(btnDelete);

    public ImplantsPanel(ChromeService chromeService, PersonService personService) {
        this.chromeService = chromeService;
        this.personService = personService;

        JButton btnNew = Ui.primaryButton("New Implant");
        btnNew.addActionListener(e -> newImplant());
        btnDelete.addActionListener(e -> deleteImplant());

        Ui.addField(fields, "Tier", lblTier);
        Ui.addField(fields, "Owner", lblOwner);
        Ui.layout(this, Ui.toolbar(btnNew), table, Ui.details(lblTitle, fields, actions));

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                showDetails(selected());
        });

        Ui.refreshWhenShown(this, this::refresh);
        refresh();
        showDetails(null);
    }

    private void newImplant() {
        String[] v = Ui.form(this, "New Implant", "Name");
        if (v == null)
            return;

        ImplantTier tier = Ui.choose(this, "Tier", List.of(ImplantTier.values()), Enum::name);
        if (tier == null)
            return;

        Person owner = Ui.choose(this, "Owner", personService.findAllPeople(),
                p -> p.getName() + " " + p.getSurname());
        if (owner == null)
            return;

        Ui.run(this, () -> {
            chromeService.addChrome(owner.getId(), v[0], tier);
            refresh();
        });
    }

    private void deleteImplant() {
        Chrome implant = selected();
        if (!Ui.confirm(this, "Delete", "Delete " + implant.getName() + "?"))
            return;

        Ui.run(this, () -> {
            chromeService.deleteChrome(implant.getId());
            refresh();
        });
    }

    private Chrome selected() {
        int row = table.getSelectedRow();
        return row >= 0 ? implants.get(row) : null;
    }

    private void refresh() {
        implants = chromeService.findAll();
        model.setRowCount(0);
        for (Chrome c : implants) {
            model.addRow(new Object[] { c.getId(), c.getName(), c.getImplantTier(),
                    c.getPerson().getName() + " " + c.getPerson().getSurname() });
        }
    }

    private void showDetails(Chrome implant) {
        fields.setVisible(implant != null);
        actions.setVisible(implant != null);

        if (implant == null) {
            lblTitle.setText("Select an implant");
            return;
        }

        lblTitle.setText(implant.getName());
        lblTier.setText(implant.getImplantTier().toString());
        lblOwner.setText(implant.getPerson().getName() + " " + implant.getPerson().getSurname());
    }
}
