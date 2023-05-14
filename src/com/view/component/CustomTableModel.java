package view.component;

import javax.swing.table.DefaultTableModel;
import java.util.HashSet;
import java.util.Set;

public class CustomTableModel extends DefaultTableModel {

    public CustomTableModel(int columnCount, int rowCount) {
        super(rowCount, columnCount);
    }

    public void resetTable() {
        for (int i = 0; i < getRowCount(); i++) {
            for (int j = 1; j < getColumnCount(); j++) {
                setValueAt(null, i, j);
            }
        }
    }

}