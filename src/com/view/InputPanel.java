package view;

import model.PageReferenceString;
import view.component.Frame;
import view.component.ImageButton;
import view.component.Label;
import view.component.Panel;
import view.component.CustomTable;
import view.component.CustomTableModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class InputPanel extends Panel {
    private int STRING_LEN_MIN = 10, STRING_LEN_MAX = 40, STRING_VAL_MIN = 0, STRING_VAL_MAX = 20, FRAME_SIZE_MIN = 3, FRAME_SIZE_MAX = 10;
    private ImageButton musicOnButton, musicOffButton, homeButton;
    private JComboBox algorithmChoice;
    private ImageButton frameNumPlus, frameNumMinus;
    private JTextField pageReferenceField, frameNumField;
    private ImageButton importButton, randomizeButton, runButton, saveButton;
    private JLabel totalPageFault;
    private CustomTableModel tableModel;
    private CustomTable table;
    private JScrollPane scrollPane;
    private PageReferenceString pageRefString;
    public InputPanel() {

        super("bg/input-panel.png");

        musicOnButton = new ImageButton("buttons/volume-on.png");
        musicOffButton = new ImageButton("buttons/volume-off.png");
        homeButton = new ImageButton("buttons/home.png");

        musicOnButton.setBounds(945, 25, 47, 47);
        musicOffButton.setBounds(945, 25, 47, 47);
        homeButton.setBounds(1010, 25, 47, 47);
        musicOffButton.setVisible(false);

        algorithmChoice = new JComboBox(new String[]{"FIFO", "LRU", "OPT", "Second Chance(SC)", "Enhanced SC", "LFU", "MFU"});
        algorithmChoice.setRenderer(new CustomComboBoxRenderer());
        algorithmChoice.setBackground(new Color(77,58,104));
        algorithmChoice.setForeground(Color.white);
        algorithmChoice.setFont(new Font("Montserrat", Font.BOLD, 18));
        algorithmChoice.setBounds(155, 264, 145, 44);

        pageReferenceField = new JTextField("", 2);
        pageReferenceField.setName("pageReferenceField");
        pageReferenceField.setBorder(null);
        pageReferenceField.setHorizontalAlignment(SwingConstants.CENTER);
        pageReferenceField.setFont(new Font("Montserrat", Font.BOLD, 20));
        pageReferenceField.setBounds(333, 143, 426, 40);
        pageReferenceField.setToolTipText("Length must be 10-40. Value must be between 0 and 20");

        frameNumField = new JTextField("4", 2);
        frameNumField.setName("frameNumField");
        frameNumField.setBorder(null);
        frameNumField.setHorizontalAlignment(SwingConstants.CENTER);
        frameNumField.setFont(new Font("Montserrat", Font.BOLD, 20));
        frameNumField.setBounds(397, 267, 73, 40);

        frameNumPlus = new ImageButton("buttons/add.png");
        frameNumMinus = new ImageButton("buttons/minus.png");
        frameNumMinus.setBounds(354, 267, 44, 40);
        frameNumPlus.setBounds(469, 267, 44, 40);


        importButton = new ImageButton("buttons/from-text.png");
        randomizeButton = new ImageButton("buttons/randomize.png");
        runButton = new ImageButton("buttons/run.png");
        saveButton = new ImageButton("buttons/save.png");

        importButton.setBounds(597, 257, 58, 58);
        randomizeButton.setBounds(673, 257, 58, 58);
        runButton.setBounds(785, 257, 58, 58);
        saveButton.setBounds(882, 258, 58, 58);

//        disableOutputButtons();

        // table

        tableModel = new CustomTableModel(10, 4);
        table = new CustomTable(tableModel);
        scrollPane = table.createTablePane(51, 380, 993, 355);

        // result total page fault
        totalPageFault = new Label("Total Page Fault: ");
        totalPageFault.setBounds(412, 700, 225, 25);
        totalPageFault.setFont(new Font("Montserrat", Font.BOLD, 24));

        disableOutputButtons();
        setListeners();

        pageRefString = new PageReferenceString();

        //Add components to frame
        this.add(musicOnButton);
        this.add(musicOffButton);
        this.add(homeButton);
        this.add(pageReferenceField);
        this.add(algorithmChoice);
        this.add(frameNumPlus);
        this.add(frameNumMinus);
        this.add(frameNumField);
        this.add(importButton);
        this.add(randomizeButton);
        this.add(runButton);
        this.add(saveButton);
        this.add(totalPageFault);
        this.add(scrollPane);

    }


    // UI
    private static class CustomComboBoxRenderer extends BasicComboBoxRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (isSelected) {
                setBackground(new Color(232, 160, 221)); // set selected item background color
                setForeground(new Color(77,58,104)); // set item text color
            } else {
                setBackground(new Color(77,58,104)); // set unselected item background color
                setForeground(Color.WHITE); // set item text color
            }
            setFont(new Font("Montserrat", Font.BOLD, 14));
            return this;
        }
    }

    private void setListeners() {
        musicOnButton.hover("buttons/volume-off-hover.png", "buttons/volume-on.png");
        musicOffButton.hover("buttons/volume-on-hover.png", "buttons/volume-off.png");
        homeButton.hover("buttons/home-hover.png", "buttons/home.png");
        frameNumMinus.hover("buttons/minus-hover.png", "buttons/minus.png");
        frameNumPlus.hover("buttons/add-hover.png", "buttons/add.png");
        importButton.hover("buttons/from-text-hover.png", "buttons/from-text.png");
        randomizeButton.hover("buttons/randomize-hover.png", "buttons/randomize.png");
        runButton.hover("buttons/run-hover.png", "buttons/run.png");
        saveButton.hover("buttons/save-hover.png", "buttons/save.png");

        frameNumPlus.addActionListener(e -> {
            try {
                frameNumField.setText(String.valueOf(Integer.parseInt(frameNumField.getText()) + 1));
            } catch (NumberFormatException ex) {
                frameNumField.setText("4");
            }
        });
        frameNumMinus.addActionListener(e -> {
            try {
                frameNumField.setText(String.valueOf(Integer.parseInt(frameNumField.getText()) - 1));
            } catch (NumberFormatException ex) {
                frameNumField.setText("4");
            }
        });

        listenToUserInput();
        listenToInputFunctions();
    }

    public static void main(String[] args) {
        InputPanel m = new InputPanel();
        Frame frame = new Frame("Input Panel");
        frame.add(m);
        frame.setVisible(true);
    }

    public void musicClick() {
        if (musicOffButton.isVisible()){
            musicOnButton.setVisible(true);
            musicOffButton.setVisible(false);
        } else {
            musicOnButton.setVisible(false);
            musicOffButton.setVisible(true);
        }
    }

    private void listenToUserInput() {
        inputValidator(pageReferenceField);
        inputValidator(frameNumField);
    }

    private void inputValidator(JTextField input) {
        input.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateInput();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateInput();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateInput();
            }

            private void validateInput() {
                try {
                    String str = input.getText();
                    boolean frameSizeValid = false, stringValid = false ;
                    if (input.getName().equals("frameNumField")) {
                        int value = Integer.parseInt(str);
                        if (value <= FRAME_SIZE_MIN || value >= FRAME_SIZE_MAX) {
                            // If the value is out of range, highlight the text field
                            input.setBackground(new Color(255, 202, 202));
                            disableOutputButtons();

                        } else {
                            input.setBackground(UIManager.getColor("TextField.background"));
                            frameSizeValid = true;
                            tableModel.setNumRows(value);

                        }
                    } else if(input.getName().equals("pageReferenceField")) {
                        // check 3 things here: input is a comma-separated list of integers with a space after each comma,  length must be bet 10-40, string value must be between 0-20
                        if (str.matches("\\d+(,\\s\\d+)*")) {
                            String[] parts = str.split(",\\s");
                            if (parts.length >= STRING_LEN_MIN &&
                                    parts.length <= STRING_LEN_MAX &&
                                    parts[0].matches("\\d+") &&
                                    parts[parts.length-1].matches("\\d+")) {
                                // Split the input into an array of integers
                                String[] nums = str.split(",\\s");
                                for (String num : nums) {
                                    int value = Integer.parseInt(num);
                                    // Check that each integer value in the input is between 0 and 20
                                    if (value <= STRING_VAL_MIN || value >= STRING_VAL_MAX) {
                                        input.setBackground(new Color(255, 202, 202));
                                        disableOutputButtons();
                                    } else {
                                        input.setBackground(UIManager.getColor("TextField.background"));
                                        stringValid = true;
                                        pageRefString.setString(new ArrayList<>(Arrays.asList(nums)));
                                        tableModel.setColumnCount(parts.length);
                                    }
                                }
                            } else {
                                input.setBackground(new Color(255, 202, 202));
                                disableOutputButtons();
                            }
                        } else {
                            input.setBackground(new Color(255, 202, 202));
                            disableOutputButtons();
                        }
                    }

                    if (frameSizeValid && stringValid) {
                        enableOutputButtons();
                    }
                } catch (NumberFormatException ex) {
                    // If the input cannot be parsed as an integer, highlight the text field
                    input.setBackground(new Color(255, 202, 202));
                }
            }
        });
    }

    public void listenToInputFunctions() {
        randomizeButton.addActionListener( e -> {
            pageReferenceField.setText(pageRefString.random()); // sets string to random
            frameNumField.setText(Integer.toString(new Random().nextInt(6) + 4));
        });

    }

    private void setTable() {

    }

    private void enableOutputButtons() {
        runButton.setEnabled(true);
        saveButton.setEnabled(true);
    }

    private void disableOutputButtons() {
        runButton.setEnabled(false);
        saveButton.setEnabled(false);
    }


    public ImageButton getMusicOnButton() {
        return musicOnButton;
    }
    public ImageButton getMusicOffButton() {
        return musicOffButton;
    }
    public ImageButton getHomeButton() {
        return homeButton;
    }
}

