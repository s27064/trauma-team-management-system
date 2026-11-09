package com.olafnaleszkiewicz.traumateam.view.person;

import com.olafnaleszkiewicz.traumateam.model.person.Client;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class ClientTableModel extends AbstractTableModel {
    private List<Client> clients = List.of();
    private final String[] cols = { "ID", "Email", "Joined", "Name", "Surname" };

    public void setClients(List<Client> data) {
        this.clients = data;
        fireTableDataChanged();
    }

    public Client getClientAt(int r) {
        return clients.get(r);
    }

    @Override
    public int getRowCount() {
        return clients.size();
    }

    @Override
    public int getColumnCount() {
        return cols.length;
    }

    @Override
    public String getColumnName(int c) {
        return cols[c];
    }

    @Override
    public Class<?> getColumnClass(int c) {
        return switch (c) {
            case 0 -> Long.class;
            case 2 -> java.time.LocalDate.class;
            default -> String.class;
        };
    }

    @Override
    public Object getValueAt(int r, int c) {
        Client x = clients.get(r);
        return switch (c) {
            case 0:
                yield x.getId();
            case 1:
                yield x.getEmail();
            case 2:
                yield x.getJoinDate();
            case 3:
                yield x.getPerson().getName();
            case 4:
                yield x.getPerson().getSurname();
            default:
                yield "";
        };
    }
}
