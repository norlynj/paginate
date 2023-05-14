package view;

import model.*;
import view.component.Frame;
import view.component.ImageButton;
import view.component.Label;
import view.component.Panel;
import view.component.CustomTable;
import view.component.CustomTableModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
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
    PageReplacementSimulator simulator;
    private boolean validStringInputs = false, validFrameNum = false;
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
        algorithmChoice.setBounds(150, 264, 150, 44);

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

        tableModel = new CustomTableModel(10, 4);
        table = new CustomTable(tableModel);
        scrollPane = table.createTablePane(51, 360, 993, 355);

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
        listenToOutputFunctions();
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
                    boolean valid = false ;
                    if (input.getName().equals("frameNumField")) {
                        int value = Integer.parseInt(str);
                        if (value < FRAME_SIZE_MIN || value > FRAME_SIZE_MAX) {
                            // If the value is out of range, highlight the text field
                            input.setBackground(new Color(255, 202, 202));
                            disableOutputButtons();
                            validFrameNum = false;

                        } else {
                            input.setBackground(UIManager.getColor("TextField.background"));
                            tableModel.setNumRows(value);
                            if (validStringInputs) {
                                enableOutputButtons();
                            }
                            validFrameNum = true;

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
                                ArrayList<Integer> numList = new ArrayList<>();
                                for (String num : nums) {
                                    int value = Integer.parseInt(num);
                                    numList.add(value);
                                    // Check that each integer value in the input is between 0 and 20
                                    if (value < STRING_VAL_MIN || value > STRING_VAL_MAX) {
                                        input.setBackground(new Color(255, 202, 202));
                                        disableOutputButtons();
                                        validStringInputs = false;
                                    } else {
                                        input.setBackground(UIManager.getColor("TextField.background"));
                                        pageRefString.setString(numList);
                                        tableModel.setColumnCount(parts.length);
                                        if (validFrameNum) {
                                            enableOutputButtons();
                                        }
                                        validStringInputs = true;
                                    }
                                }
                            } else {
                                input.setBackground(new Color(255, 202, 202));
                                disableOutputButtons();
                                validStringInputs = false;
                            }
                        } else {
                            input.setBackground(new Color(255, 202, 202));
                            disableOutputButtons();
                            validStringInputs = false;
                        }
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
            tableModel.resetTable();
            pageReferenceField.setText(pageRefString.random()); // sets string to random
            frameNumField.setText(Integer.toString(new Random().nextInt(6) + 4));
        });

        importButton.addActionListener( e -> {
            FileReader fr = new FileReader();
            try {
                if (fr.readInputFromFile()) {
                    ArrayList<Integer> inputList = fr.getPageRefString();
                    System.out.println("Input: " + inputList);
                    pageRefString.setString(inputList);
                    pageReferenceField.setText(pageRefString.getString()); // sets string to random
                    frameNumField.setText(Integer.toString(fr.getFrameNumber()));
                }
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }

        });

    }

    private void listenToOutputFunctions() {
        runButton.addActionListener( e -> {
            // create a new instance of PageReplacementSimulator class
            simulate();
            populateResults();
        });

        saveButton.addActionListener( e -> {
            // allow pdf as output file here
        });

    }

    private void simulate() {
        String selected = (String) algorithmChoice.getSelectedItem();
        int frameNum = Integer.parseInt(frameNumField.getText());
        switch (selected) {
            case "FIFO":
                simulator = new FIFO(pageRefString, frameNum);
                break;
            case "LRU":
                simulator = new LRU(pageRefString, frameNum);
                break;
            case "Second Chance (SC)":
                simulator = new SecondChance(pageRefString);
                break;
            case "Enhanced SC":
                simulator = new EnhancedSecondChance(pageRefString);
                break;
            case "LFU":
                simulator = new LFU(pageRefString);
                break;
            case "MFU":
                simulator = new MFU(pageRefString);
                break;
        }
        simulator.simulate();
    }

    private void populateResults() {
        tableModel.resetTable();
        ArrayList<Step> steps = simulator.getSteps();
        int count = 0;
        for (int i = steps.size() - 1; i >= 0; i--) {
            Step step = steps.get(i);
            table.setValueAt(pageRefString.getPages().get(count), 0, count);
            count++;
            for (int j = 0; j < step.getPagesProcessed().size(); j++) {
                int row = table.getRowCount() - j - 2; // Subtract 2 to account for header and footer rows
                table.setValueAt(step.getPagesProcessed().get(j), row, i);
                table.setValueAt(step.getStatus(), table.getRowCount() - 1, i);
            }
        }



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
}

