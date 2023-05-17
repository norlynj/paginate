package view.component;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;
import java.awt.*;

public class CustomTable extends JTable {
    private final Color TABLE_PANE_COLOR = new Color(247, 245, 245);
    private final Font TABLE_FONT = new Font("Montserrat", Font.PLAIN, 14);

    private Border cellBorder;

    public CustomTable(CustomTableModel tableModel) {
        super(tableModel);
        setRowHeight(30);
        setFont(TABLE_FONT);

        //Set header
        setTableHeader(null);

        //Set text to center
        setCenter();

        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));



        // Add a table model listener to update the cell renderer
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                // Update the cell renderer for all cells in the table
                for (int row = 0; row < getRowCount(); row++) {
                    for (int col = 0; col < getColumnCount(); col++) {
                        updateCellRenderer(row, col);
                    }
                }
            }
        });
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component component = super.prepareRenderer(renderer, row, column);

        int marginSize = 8;
        // Set the cell border
        if (getColumnCount() > 20 && getColumnCount() < 30) {
            marginSize = 3;
        } else if (getColumnCount() >= 30) {
            marginSize = 2;
            setFont(new Font("Montserrat", Font.PLAIN, 10));
        }

        // Set different borders for header row and last column
        if (row == 0 || row == getRowCount() - 1) {
            ((JComponent) component).setBackground(new Color(247, 245, 245));
        } else {
            Border outerBorder = BorderFactory.createMatteBorder(1, marginSize, 1, marginSize, new Color(247, 245, 245)); // Change outer border color here
            Border innerBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK); // Change inner border color here
            ((JComponent) component).setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
        }

        return component;
    }

    public TableCellEditor getCellEditor() {
        JTextField f = new JTextField();
        f.setBorder(BorderFactory.createLineBorder(Color.RED));
        return new DefaultCellEditor(f);
    }

    public void setCenter() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < getColumnModel().getColumnCount(); i++) {
            getColumnModel().getColumn(i).setCellRenderer(renderer);

        }
    }

    public JScrollPane createTablePane(int x, int y, int width, int height) {
        JScrollPane scrollPane = new JScrollPane(this);
        scrollPane.setBounds(x, y, width, height);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(TABLE_PANE_COLOR);
        return scrollPane;
    }

    private void updateCellRenderer(int row, int col) {
        try {
            setIntercellSpacing(new Dimension(0, 0));
            setCenter();
        } catch (Exception ex) {

        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        setCellSelectionEnabled(false);
        return false;
    }
}
