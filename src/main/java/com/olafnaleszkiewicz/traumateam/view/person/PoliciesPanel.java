package com.olafnaleszkiewicz.traumateam.view.person;

import com.olafnaleszkiewicz.traumateam.model.Policy;
import com.olafnaleszkiewicz.traumateam.model.enums.PolicyTier;
import com.olafnaleszkiewicz.traumateam.model.person.Person;
import com.olafnaleszkiewicz.traumateam.service.PersonService;
import com.olafnaleszkiewicz.traumateam.service.PolicyService;
import com.olafnaleszkiewicz.traumateam.view.common.ListItem;
import com.olafnaleszkiewicz.traumateam.view.common.Ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class PoliciesPanel extends JPanel {
    private final PolicyService policyService;
    private final PersonService personService;

    private final JTable table = Ui.table(new int[] { 40, 300, 160 }, "ID", "Tier", "Monthly price");
    private final DefaultTableModel model = (DefaultTableModel) table.getModel();
    private List<Policy> policies = List.of();

    private final JLabel lblTitle = new JLabel();
    private final JList<ListItem> lstPeople = new JList<>();
    private final JPanel fields = Ui.verticalPanel();
    private final JButton btnAssign = new JButton("Assign");
    private final JButton btnRemove = new JButton("Remove");
    private final JButton btnDelete = new JButton("Delete Policy");
    private final JPanel actions = Ui.actions(btnDelete);

    public PoliciesPanel(PolicyService policyService, PersonService personService) {
        this.policyService = policyService;
        this.personService = personService;

        JButton btnNew = new JButton("New Policy");
        btnNew.addActionListener(e -> newPolicy());
        btnAssign.addActionListener(e -> assignPerson());
        btnRemove.addActionListener(e -> removePerson());
        btnDelete.addActionListener(e -> deletePolicy());

        Ui.addListField(fields, "People", lstPeople, btnAssign, btnRemove);
        Ui.layout(this, Ui.toolbar(btnNew), table, Ui.details(lblTitle, fields, actions));

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                showDetails(selected());
        });

        Ui.refreshWhenShown(this, this::refresh);
        refresh();
        showDetails(null);
    }

    private void newPolicy() {
        PolicyTier tier = Ui.choose(this, "New Policy", List.of(PolicyTier.values()), Enum::name);
        if (tier == null)
            return;

        Ui.run(this, () -> {
            policyService.create(tier);
            refresh();
        });
    }

    private void assignPerson() {
        Policy policy = selected();
        Person person = Ui.choose(this, "Assign Person", personService.findAllPeople(),
                p -> p.getName() + " " + p.getSurname());
        if (person == null)
            return;

        Ui.run(this, () -> {
            policyService.assignPolicy(person.getId(), policy.getId());
            refresh();
        });
    }

    private void removePerson() {
        Long personId = lstPeople.getSelectedValue().id();
        Ui.run(this, () -> {
            policyService.removeFromPolicy(personId);
            refresh();
        });
    }

    private void deletePolicy() {
        Policy policy = selected();
        if (!Ui.confirm(this, "Delete this " + policy.getPolicyTier() + " policy? People on it will have no policy."))
            return;

        Ui.run(this, () -> {
            policyService.deletePolicy(policy.getId());
            refresh();
        });
    }

    private Policy selected() {
        int row = table.getSelectedRow();
        return row >= 0 ? policies.get(row) : null;
    }

    private void refresh() {
        int row = table.getSelectedRow();
        policies = policyService.findAll();
        model.setRowCount(0);
        for (Policy p : policies)
            model.addRow(new Object[] { p.getId(), p.getPolicyTier(), p.getCurrentPrice() });
        if (row >= 0 && row < policies.size())
            table.setRowSelectionInterval(row, row);
    }

    private void showDetails(Policy policy) {
        fields.setVisible(policy != null);
        actions.setVisible(policy != null);

        if (policy == null) {
            lblTitle.setText("Select a policy");
            return;
        }

        Policy full = policyService.getById(policy.getId());
        lblTitle.setText(full.getPolicyTier().toString());
        lstPeople.setListData(full.getPeople().stream()
                .map(p -> new ListItem(p.getId(), p.getName() + " " + p.getSurname()))
                .toArray(ListItem[]::new));
    }
}
