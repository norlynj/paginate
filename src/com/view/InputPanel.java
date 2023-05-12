package view;

import model.CustomTableModel;
import view.component.Frame;
import view.component.ImageButton;
import view.component.Label;
import view.component.Panel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class InputPanel extends Panel {
    private int STRING_LEN_MIN = 10, STRING_LEN_MAX = 40, STRING_VAL_MIN = 0, STRING_VAL_MAX = 20, FRAME_SIZE_MIN = 3, FRAME_SIZE_MAX = 10;
    private ImageButton musicOnButton, musicOffButton, homeButton;
    private JComboBox algorithmChoice;
    private ImageButton frameNumPlus, frameNumMinus;
    private JTextField pageReferenceField, frameNumField;
    private ImageButton importButton, randomizeButton, runButton, saveButton;
    private JLabel totalPageFault;

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

        frameNumField = new JTextField("3", 2);
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

        totalPageFault = new Label("Total Page Fault: ");
        totalPageFault.setBounds(412, 710, 225, 25);
        totalPageFault.setFont(new Font("Montserrat", Font.BOLD, 24));

        setListeners();

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

    }


    // UI
    private static class CustomComboBoxRenderer extends BasicComboBoxRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (isSelected) {
                setBackground(new Color(223, 235, 246)); // set selected item background color
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
                frameNumField.setText("3");
            }
        });
        frameNumMinus.addActionListener(e -> {
            try {
                frameNumField.setText(String.valueOf(Integer.parseInt(frameNumField.getText()) - 1));
            } catch (NumberFormatException ex) {
                frameNumField.setText("3");
            }
        });

        listenToUserInput();
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
                    if (input.getName().equals("frameNumField")) {
                        String text = input.getText();
                        int value = Integer.parseInt(text);
                        if (value < FRAME_SIZE_MIN || value > FRAME_SIZE_MAX) {
                            // If the value is out of range, highlight the text field
                            input.setBackground(new Color(255, 202, 202));
                            disableOutputButtons();

                        } else {
                            input.setBackground(UIManager.getColor("TextField.background"));
                            enableOutputButtons();
                        }
                    } else if(input.getName().equals("pageReferenceField")) {
                        // check 2 things here: page reference length must be bet 10-40, string value must be 0-20
                    }
                } catch (NumberFormatException ex) {
                    // If the input cannot be parsed as an integer, highlight the text field
                    input.setBackground(new Color(255, 202, 202));
                }
            }
        });
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

