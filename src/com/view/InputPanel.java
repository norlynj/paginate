package view;

import model.*;
import view.component.*;
import view.component.Frame;
import view.component.Label;
import view.component.HighlightCellRenderer;
import view.component.Panel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

public class InputPanel extends Panel {
    Color bgColor = new Color(247, 245, 245);
    private int STRING_LEN_MIN = 10, STRING_LEN_MAX = 40, STRING_VAL_MIN = 0, STRING_VAL_MAX = 20, FRAME_SIZE_MIN = 3, FRAME_SIZE_MAX = 10;
    private ImageButton musicOnButton, musicOffButton, homeButton;
    private JComboBox algorithmChoice;
    private ImageButton frameNumPlus, frameNumMinus;
    private JTextField pageReferenceField, frameNumField;
    private ImageButton importButton, randomizeButton, runButton, pauseButton, saveButton;
    private JLabel timerLabel, totalPageFault;

    // for one algo simulation
    private CustomTableModel tableModel;
    private CustomTable table;
    private JScrollPane tableScrollPane;

    // for all algo simulation
    private JPanel tablesPanel;
    private CustomTableModel[] tableModels;
    private CustomTable tables[];
    private JScrollPane tablesScrollPane;
    private JLabel totalPageFaults[];
    private String[] tableTitles;

    private JScrollPane[] scrollPanes;
    private PageReferenceString pageRefString;
    PageReplacementSimulator[] simulators;
    PageReplacementSimulator simulator;
    private boolean validStringInputs = false, validFrameNum = false;
    private JSlider slider;
    private int sliderValue = 2000;
    private Timer timer;
    public InputPanel() {

        super("bg/input-panel.png");

        musicOnButton = new ImageButton("buttons/volume-on.png");
        musicOffButton = new ImageButton("buttons/volume-off.png");
        homeButton = new ImageButton("buttons/home.png");

        musicOnButton.setBounds(945, 25, 47, 47);
        musicOffButton.setBounds(945, 25, 47, 47);
        homeButton.setBounds(1010, 25, 47, 47);
        musicOffButton.setVisible(false);

        algorithmChoice = new JComboBox(new String[]{"Simulate all", "FIFO", "LRU", "Optimal", "Second Chance(SC)", "Enhanced SC", "LFU", "MFU"});
        algorithmChoice.setRenderer(new CustomComboBoxRenderer());
        algorithmChoice.setBackground(new Color(77,58,104));
        algorithmChoice.setForeground(Color.white);
        algorithmChoice.setFont(new Font("Montserrat", Font.BOLD, 18));
        algorithmChoice.setBounds(150, 209, 150, 44);

        pageReferenceField = new JTextField("", 2);
        pageReferenceField.setName("pageReferenceField");
        pageReferenceField.setBorder(null);
        pageReferenceField.setHorizontalAlignment(SwingConstants.CENTER);
        pageReferenceField.setFont(new Font("Montserrat", Font.BOLD, 20));
        pageReferenceField.setBounds(333, 107, 426, 40);
        pageReferenceField.setToolTipText("Length must be 10-40. Value must be between 0 and 20");

        frameNumField = new JTextField("4", 2);
        frameNumField.setName("frameNumField");
        frameNumField.setBorder(null);
        frameNumField.setHorizontalAlignment(SwingConstants.CENTER);
        frameNumField.setFont(new Font("Montserrat", Font.BOLD, 20));
        frameNumField.setBounds(397, 212, 73, 40);

        frameNumPlus = new ImageButton("buttons/add.png");
        frameNumMinus = new ImageButton("buttons/minus.png");
        frameNumMinus.setBounds(354, 212, 44, 40);
        frameNumPlus.setBounds(469, 212, 44, 40);


        importButton = new ImageButton("buttons/from-text.png");
        randomizeButton = new ImageButton("buttons/randomize.png");
        runButton = new ImageButton("buttons/run.png");
        pauseButton = new ImageButton("buttons/pause.png");
        saveButton = new ImageButton("buttons/save.png");

        importButton.setBounds(597, 202, 58, 58);
        randomizeButton.setBounds(673, 202, 58, 58);
        runButton.setBounds(785, 202, 58, 58);
        pauseButton.setBounds(785, 202, 58, 58);
        pauseButton.setVisible(false);
        saveButton.setBounds(882, 202, 58, 58);

        showAllTables();
        showOneTable();

        tableScrollPane.setVisible(false);


        timerLabel = new Label("TIMER: 00:00 ");
        timerLabel.setBounds(47, 288, 145, 29);
        timerLabel.setFont(new Font("Montserrat", Font.BOLD, 18));

        totalPageFault = new Label();
        totalPageFault.setBounds(412, 725, 225, 25);
        totalPageFault.setFont(new Font("Montserrat", Font.BOLD, 24));

        totalPageFaults = new JLabel[tableTitles.length];
        for (int i = 0; i < tableTitles.length; i++) {
            totalPageFaults[i] = new JLabel(tableTitles[i] + " | Page Faults: ");
        }


        disableOutputButtons();
        setListeners();

        pageRefString = new PageReferenceString();

        // slider and timer
        slider = new JSlider();
        slider.setMajorTickSpacing(25);
        slider.setMinorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        Hashtable position = new Hashtable();
        position.put(0, new JLabel("0"));
        position.put(25, new JLabel("1"));
        position.put(50, new JLabel("2"));
        position.put(75, new JLabel("3"));
        position.put(100, new JLabel("4"));
        slider.setLabelTable(position);
        slider.setBackground(bgColor);
        slider.setBounds(190, 288, 246, 45);

        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                double val = (double)((JSlider)e.getSource()).getValue();
                sliderValue = (int)((val / 100) * 4000); //map positions accordingly to 0, 1000, 2000, 3000
                System.out.println("Value of the slider is: " + sliderValue);
            }
        });

        //Add components to frame
        this.add(slider);
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
        this.add(pauseButton);
        this.add(saveButton);
        this.add(timerLabel);
        this.add(totalPageFault);
        this.add(tableScrollPane);
        this.add(tablesScrollPane);

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

        listenToUserInput();
        listenToInputFunctions();
        listenToOutputFunctions();

        tablesScrollPane.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                // Get the scroll direction
                int scrollAmount = e.getUnitsToScroll();

                // Adjust the vertical scroll position
                JScrollBar verticalScrollBar = tablesScrollPane.getVerticalScrollBar();
                int newPosition = verticalScrollBar.getValue() + verticalScrollBar.getBlockIncrement() * scrollAmount;
                verticalScrollBar.setValue(newPosition);
            }
        });
    }

    private void listenToUserInput() {
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

        inputValidator(pageReferenceField);
        inputValidator(frameNumField);
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

        algorithmChoice.addActionListener(e -> {
            switch((String) algorithmChoice.getSelectedItem()) {
                case "Simulate all":
                    tablesScrollPane.setVisible(true);
                    tableScrollPane.setVisible(false);
                    break;
                default:
                    tablesScrollPane.setVisible(false);
                    tableScrollPane.setVisible(true);
                    break;

            }
            System.out.println((String) algorithmChoice.getSelectedItem());

        });

    }

    private void showAllTables() {
        tablesPanel = new JPanel();
        tablesPanel.setBackground(bgColor);
        tablesPanel.setLayout(new BoxLayout(tablesPanel, BoxLayout.Y_AXIS));

        // Table titles
        tableTitles = new String[]{"FIFO", "LRU", "OPT", "Second Chance", "Enhanced Second Chance", "LFU", "MFU"};

        // Create arrays to store table models and tables
        tableModels = new CustomTableModel[tableTitles.length];
        tables = new CustomTable[tableTitles.length];
        scrollPanes = new JScrollPane[tableTitles.length];

        // Create multiple tables
        for (int i = 0; i < tableTitles.length; i++) {
            String title = tableTitles[i];

            // Create a label for the table title
            JLabel titleLabel = new JLabel(title + " | Page Faults: ");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            tablesPanel.add(titleLabel);

            tableModels[i] = new CustomTableModel();
            tables[i] = new CustomTable(tableModels[i]);
            scrollPanes[i] = tables[i].createTablePane();
            tablesPanel.add(scrollPanes[i]);

            // Add some vertical space between tables
            tablesPanel.add(Box.createVerticalStrut(10));

            // Set the preferred size of the scroll pane to allow table height adjustment
            scrollPanes[i].setPreferredSize(new Dimension(scrollPanes[i].getPreferredSize().width, tables[i].getRowCount()*30));
        }

        // Create a scroll pane for the tables panel
        tablesScrollPane = new JScrollPane(tablesPanel);
        tablesScrollPane.setBorder(BorderFactory.createEmptyBorder());
        tablesScrollPane.getViewport().setBackground(bgColor);
        tablesScrollPane.setBounds(53, 336, 993, 385);
    }


    private void showOneTable(){
        tableModel = new CustomTableModel();
        table = new CustomTable(tableModel);
        tableScrollPane = table.createTablePane(51, 330, 993, 355);
    }

    private void listenToOutputFunctions() {
        runButton.addActionListener( e -> {
            // create a new instance of PageReplacementSimulator class
            simulate();

            pauseButton.setVisible(true);
            runButton.setVisible(false);
        });

        pauseButton.addActionListener( e -> {
            if (timer != null) {
                System.out.println("true");
                timer.stop();
                slider.setEnabled(true);
                pauseButton.setVisible(false);
                runButton.setVisible(true);
            }
        });

        saveButton.addActionListener( e -> {
            // allow pdf as output file here
        });

    }

    private void simulate() {
        String selected = (String) algorithmChoice.getSelectedItem();
        int frameNum = Integer.parseInt(frameNumField.getText());
        switch (selected) {
            case "Simulate all":
                simulators = new PageReplacementSimulator[7];
                simulators[0] = new FIFO(pageRefString, frameNum);
                simulators[1] = new LRU(pageRefString, frameNum);
                simulators[2] = new Optimal(pageRefString, frameNum);
                simulators[3] = new SecondChance(pageRefString, frameNum);
                simulators[4] = new EnhancedSecondChance(pageRefString, frameNum);
                simulators[5] = new LFU(pageRefString, frameNum);
                simulators[6] = new MFU(pageRefString, frameNum);

                for (int i = 0; i < simulators.length; i++) {
                    simulators[i].simulate();
                }
                populateResultTables();
                return;
            case "FIFO":
                simulator = new FIFO(pageRefString, frameNum);
                break;
            case "LRU":
                simulator = new LRU(pageRefString, frameNum);
                break;
            case "Optimal":
                simulator = new Optimal(pageRefString, frameNum);
                break;
            case "Second Chance (SC)":
                simulator = new SecondChance(pageRefString, frameNum);
                break;
            case "Enhanced SC":
                simulator = new EnhancedSecondChance(pageRefString, frameNum);
                break;
            case "LFU":
                simulator = new LFU(pageRefString, frameNum);
                break;
            case "MFU":
                simulator = new MFU(pageRefString, frameNum);
                break;
        }

        simulator.simulate();
        populateResultTable();
    }
    private void populateResultTable() {
        // Reset the table model and get the steps
        tableModel.resetTable();
        totalPageFault.setText("");
        ArrayList<Step> steps = simulator.getSteps();
        int count = 0;

        // Stop any running timer before starting a new one
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        // Create a new timer with an interval of 1000 milliseconds (1 second)
        timer = new Timer(sliderValue, new ActionListener() {
            private int stepIndex = 0; // Start from the first step (0th index)
            long startTime = System.currentTimeMillis();

            @Override
            public void actionPerformed(ActionEvent e) {
                // Check if there are more steps to process
                if (stepIndex < steps.size()) {
                    // disable sliders and run function
                    slider.setEnabled(false);

                    Step step = steps.get(stepIndex);
                    table.setValueAt(pageRefString.getPages().get(stepIndex), 0, stepIndex);

                    long elapsedTime = System.currentTimeMillis() - startTime;
                    long seconds = (elapsedTime / 1000) % 60;
                    String time = String.format("%02d:%02d", seconds / 60, seconds % 60);
                    timerLabel.setText("TIMER: " + time);

                    // Iterate over the pages processed in the current step
                    for (int j = 0; j < step.getPagesProcessed().size(); j++) {
                        int row = table.getRowCount() - j - 2; // Subtract 2 to account for header and footer rows
                        table.setValueAt(step.getPagesProcessed().get(j), row, stepIndex);
                        table.setValueAt(step.getStatus(), table.getRowCount() - 1, stepIndex);
                        table.getColumnModel().getColumn(stepIndex).setCellRenderer(new HighlightCellRenderer(step.getFrame(), stepIndex, table.getRowCount(), step.isHit()));
                    }
                    totalPageFault.setText("Page Fault: " + String.valueOf(step.getPageFaults()));
                    stepIndex++; // Move to the next step
                } else {
                    // enable sliders and run function
                    slider.setEnabled(true);
                    pauseButton.setVisible(false);
                    runButton.setVisible(true);

                    // All steps have been processed, stop the timer
                    totalPageFault.setText("Page Fault: " + String.valueOf(simulator.getPageFaults()));
                    timer.stop();
                }
            }
        });

        // Start the timer
        timer.start();
    }

    public void populateResultTables() {
        totalPageFault.setText("");

        // Reset the table model and get the steps
        for (int i = 0; i < tableModels.length; i++) {
        }

        // Stop any running timer before starting a new one
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        // Create a new timer with an interval of 1000 milliseconds (1 second)
        timer = new Timer(sliderValue, new ActionListener() {
            private int stepIndex = 0; // Start from the first step (0th index)
            long startTime = System.currentTimeMillis();

            @Override
            public void actionPerformed(ActionEvent e) {
                // Check if there are more steps to process
                if (stepIndex < simulators[0].getSteps().size()) {
                    // Disable sliders and run function
                    slider.setEnabled(false);

                    for (int i = 0; i < tableModels.length; i++) {
                        tableModels[i].resetTable();
                        Step step = simulators[0].getSteps().get(stepIndex);
                        for (int j = 0; j < step.getPagesProcessed().size(); j++) {
                            int row = tables[i].getRowCount() - j - 2; // Subtract 2 to account for header and footer rows
                            tables[i].setValueAt(step.getPagesProcessed().get(j), row, stepIndex);
                            tables[i].setValueAt(step.getStatus(), table.getRowCount() - 1, stepIndex);
                            tables[i].getColumnModel().getColumn(stepIndex).setCellRenderer(new HighlightCellRenderer(step.getFrame(), stepIndex, table.getRowCount(), step.isHit()));
                        }
                        System.out.println(step.getPageFaults());
                        totalPageFaults[i].setText(tableTitles[i] + " | Page Faults: " + step.getPageFaults());
                    }

                    long elapsedTime = System.currentTimeMillis() - startTime;
                    long seconds = (elapsedTime / 1000) % 60;
                    String time = String.format("%02d:%02d", seconds / 60, seconds % 60);
                    timerLabel.setText("TIMER: " + time);

                    stepIndex++; // Move to the next step
                } else {
                    // Enable sliders and run function
                    slider.setEnabled(true);
                    pauseButton.setVisible(false);
                    runButton.setVisible(true);

                    // All steps have been processed, stop the timer
                    timer.stop();
                }
            }
        });

        // Start the timer
        timer.start();
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
                            for (int i = 0; i < tableModels.length; i++) {
                                tableModels[i].setNumRows(value); // for the table that shows all algo
                            }

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
                                        for (int i = 0; i < tableModels.length; i++) {
                                            tableModels[i].setColumnCount(parts.length); // for the table that shows all algo
                                        }

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

