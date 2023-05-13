package view.component;

import javax.swing.table.DefaultTableModel;
import java.util.HashSet;
import java.util.Set;

public class CustomTableModel extends DefaultTableModel {

    private Set<Integer> priorityNumbers = new HashSet<>();

    public CustomTableModel(int columnCount, int rowCount) {
        super(rowCount, columnCount);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return Integer.class;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column != 0;
    }

    public void resetTable() {
        for (int i = 0; i < getRowCount(); i++) {
            for (int j = 1; j < getColumnCount(); j++) {
                setValueAt(null, i, j);
            }
        }
        priorityNumbers.clear();
    }

}