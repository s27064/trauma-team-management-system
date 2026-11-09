package com.olafnaleszkiewicz.traumateam.view.mission;

import com.olafnaleszkiewicz.traumateam.model.enums.MissionStatus;
import com.olafnaleszkiewicz.traumateam.model.Mission;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class MissionTableModel extends AbstractTableModel {
    private List<Mission> missions = List.of();
    private final String[] cols = { "ID", "Name", "Description", "Status" };

    public void setMissions(List<Mission> data) {
        this.missions = data;
        fireTableDataChanged();
    }

    public Mission getMissionAt(int row) {
        return missions.get(row);
    }

    @Override
    public int getRowCount() {
        return missions.size();
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
            case 3 -> MissionStatus.class;
            default -> String.class;
        };
    }

    @Override
    public Object getValueAt(int r, int c) {
        Mission m = missions.get(r);
        return switch (c) {
            case 0 -> m.getId();
            case 1 -> m.getName();
            case 2 -> m.getDescription();
            case 3 -> m.getMissionStatus();
            default -> "";
        };
    }
}
