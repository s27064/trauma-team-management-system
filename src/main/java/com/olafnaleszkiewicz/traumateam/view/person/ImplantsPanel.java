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
    private final JButton btnDelete = new JButton("Delete Implant");

    public ImplantsPanel(ChromeService chromeService, PersonService personService) {
        this.chromeService = chromeService;
        this.personService = personService;

        JButton btnNew = new JButton("New Implant");
        btnNew.addActionListener(e -> newImplant());
        btnDelete.addActionListener(e -> deleteImplant());
        btnDelete.setEnabled(false);
        table.getSelectionModel().addListSelectionListener(e -> btnDelete.setEnabled(table.getSelectedRow() >= 0));

        Ui.layout(this, Ui.toolbar(btnNew, btnDelete), table, null);
        Ui.refreshWhenShown(this, this::refresh);
        refresh();
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
        Chrome implant = implants.get(table.getSelectedRow());
        if (!Ui.confirm(this, "Delete " + implant.getName() + "?"))
            return;

        Ui.run(this, () -> {
            chromeService.deleteChrome(implant.getId());
            refresh();
        });
    }

    private void refresh() {
        implants = chromeService.findAll();
        model.setRowCount(0);
        for (Chrome c : implants) {
            model.addRow(new Object[] { c.getId(), c.getName(), c.getImplantTier(),
                    c.getPerson().getName() + " " + c.getPerson().getSurname() });
        }
    }
}
