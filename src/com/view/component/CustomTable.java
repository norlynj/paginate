package view.component;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class CustomTable extends JTable {
    private final Color TABLE_PANE_COLOR = new Color(247, 245, 245);
    private final Font TABLE_FONT = new Font("Montserrat", Font.PLAIN, 18);
    private boolean editable = true;

    public CustomTable(CustomTableModel tableModel) {
        this(tableModel, true);
    }

    public CustomTable(CustomTableModel tableModel, boolean editable) {
        super(tableModel);
        this.editable = editable;
        setRowHeight(30);
        setFont(TABLE_FONT);

        //Set header
        setTableHeader(null);


        //Set text to center
        setCenter();
        setBorder(BorderFactory.createLineBorder(new Color(247, 245, 245), 5));
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

    @Override
    public boolean isCellEditable(int row, int column) {
        setCellSelectionEnabled(!editable); // disable selection
        return editable;
    }



}