package view;

import model.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import view.component.*;
import view.component.Frame;
import view.component.Label;
import view.component.HighlightCellRenderer;
import view.component.Panel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private JLabel titleLabels[];
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
        initializeButtons();
        initializeAlgorithmComboBox();
        initializeTextFields();
        initializeLabels();
        initializeTables();
        disableOutputButtons();
        initializeSlider();
        setListeners();
        pageRefString = new PageReferenceString();
        addComponentsToFrame();
    }

    private void initializeButtons() {
        musicOnButton = createImageButton("buttons/volume-on.png", 945, 25, 47, 47);
        musicOffButton = createImageButton("buttons/volume-off.png", 945, 25, 47, 47);
        homeButton = createImageButton("buttons/home.png", 1010, 25, 47, 47);
        musicOffButton.setVisible(false);
        frameNumPlus = createImageButton("buttons/add.png", 469, 212, 44, 40);
        frameNumMinus = createImageButton("buttons/minus.png", 354, 212, 44, 40);
        importButton = createImageButton("buttons/from-text.png", 597, 202, 58, 58);
        randomizeButton = createImageButton("buttons/randomize.png", 673, 202, 58, 58);
        runButton = createImageButton("buttons/run.png", 785, 202, 58, 58);
        pauseButton = createImageButton("buttons/pause.png", 785, 202, 58, 58);
        saveButton = createImageButton("buttons/save.png", 882, 202, 58, 58);
        pauseButton.setVisible(false);
    }

    private ImageButton createImageButton(String imagePath, int x, int y, int width, int height) {
        ImageButton button = new ImageButton(imagePath);
        button.setBounds(x, y, width, height);
        return button;
    }

    private void initializeAlgorithmComboBox() {
        algorithmChoice = new JComboBox(new String[]{"Simulate all", "FIFO", "LRU", "Optimal", "Second Chance(SC)", "Enhanced SC", "LFU", "MFU"});
        algorithmChoice.setRenderer(new CustomComboBoxRenderer());
        algorithmChoice.setBackground(new Color(77,58,104));
        algorithmChoice.setForeground(Color.white);
        algorithmChoice.setFont(new Font("Montserrat", Font.BOLD, 18));
        algorithmChoice.setBounds(150, 209, 150, 44);
    }

    private void initializeTextFields() {
        pageReferenceField = createTextField("", 2, "pageReferenceField", 333, 107, 426, 40);
        frameNumField = createTextField("4", 2, "frameNumField", 397, 212, 73, 40);
    }

    private JTextField createTextField(String text, int columns, String name, int x, int y, int width, int height) {
        JTextField textField = new JTextField(text, columns);
        textField.setName(name);
        textField.setBorder(null);
        textField.setHorizontalAlignment(SwingConstants.CENTER);
        textField.setFont(new Font("Montserrat", Font.BOLD, 20));
        textField.setBounds(x, y, width, height);
        textField.setToolTipText("Length must be 10-40. Value must be between 0 and 20");
        return textField;
    }

    private void initializeLabels() {
        tableTitles = new String[]{"FIFO", "LRU", "OPT", "Second Chance", "Enhanced Second Chance", "LFU", "MFU"};
        titleLabels = new JLabel[tableTitles.length];

        for (int i = 0; i < tableTitles.length; i++) {
            titleLabels[i] = new JLabel(tableTitles[i]);
            titleLabels[i].setForeground(new Color(222, 120, 94));
            titleLabels[i].setFont(new Font("Montserrat", Font.BOLD, 20));
            titleLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
            titleLabels[i].setBounds(149 + i * 141, 294, 150, 40);
        }
    }

    private void initializeTables() {
        showAllTables();
        showOneTable();

        tableScrollPane.setVisible(false);

        timerLabel = new Label("TIMER: 00:00 ");
        timerLabel.setBounds(47, 288, 145, 29);
        timerLabel.setFont(new Font("Montserrat", Font.BOLD, 18));

        totalPageFault = new Label();
        totalPageFault.setBounds(412, 725, 225, 25);
        totalPageFault.setFont(new Font("Montserrat", Font.BOLD, 24));

    }

    private void showAllTables() {
        tablesPanel = new JPanel();
        tablesPanel.setBackground(bgColor);
        tablesPanel.setLayout(new BoxLayout(tablesPanel, BoxLayout.Y_AXIS));

        // Create arrays to store table models and tables
        tableModels = new CustomTableModel[tableTitles.length];
        tables = new CustomTable[tableTitles.length];
        scrollPanes = new JScrollPane[tableTitles.length];

        // Create multiple tables
        for (int i = 0; i < tableTitles.length; i++) {
            String title = tableTitles[i];

            // Create a label for the table title
            titleLabels[i] = new JLabel(title + " | Page Faults: ");
            titleLabels[i].setFont(new Font("Arial", Font.BOLD, 16));
            titleLabels[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            tablesPanel.add(titleLabels[i]);

            tableModels[i] = new CustomTableModel();
            tables[i] = new CustomTable(tableModels[i]);
            scrollPanes[i] = tables[i].createTablePane();
            tablesPanel.add(scrollPanes[i]);

            // Add some vertical space between tables
            tablesPanel.add(Box.createVerticalStrut(30));

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

    public void resetTables() {
        // Reset the table model and get the steps
        tableModel.resetTable();
        table.clearCellRendererBackground();
        for (int i = 0; i < tableModels.length; i++) {
            tableModels[i].resetTable();
            tables[i].clearCellRendererBackground();
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

    private void setListeners() {
        musicOnButton.addActionListener(e -> {
            musicOnButton.setVisible(false);
            musicOffButton.setVisible(true);
        });

        musicOffButton.addActionListener(e -> {
            musicOffButton.setVisible(false);
            musicOnButton.setVisible(true);
            // Handle music off button click
        });

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

        importButton.addActionListener(e -> {
            FileReader fr = new FileReader();
            try {
                if (fr.readInputFromFile()) {
                    ArrayList<Integer> inputList = fr.getPageRefString();
                    System.out.println("Input: " + inputList);
                    pageRefString.setString(inputList);
                    pageReferenceField.setText(pageRefString.getString()); // sets string to random
                    frameNumField.setText(Integer.toString(fr.getFrameNumber()));
                    resetTables();
                }
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        randomizeButton.addActionListener(e -> {
            resetTables();
            pageReferenceField.setText(pageRefString.random()); // sets string to random
            frameNumField.setText(Integer.toString(new Random().nextInt(6) + 4));
        });

        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                double val = (double)((JSlider)e.getSource()).getValue();
                sliderValue = (int)((val / 100) * 4000); //map positions accordingly to 0, 1000, 2000, 3000
            }
        });

        runButton.addActionListener(e -> {
            resetTables();
            simulate();
            pauseButton.setVisible(true);
            runButton.setVisible(false);
        });

        pauseButton.addActionListener(e -> {
            if (timer != null) {
                timer.stop();
                slider.setEnabled(true);
                saveButton.setEnabled(true);
                pauseButton.setVisible(false);
                runButton.setVisible(true);
            }
        });

        saveButton.addActionListener(e -> {
            saveResults(tablesPanel);
        });

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
                            for (int i = 0; i < tableModels.length; i++) {
                                tableModels[i].setNumRows(value); // for the table that shows all algo
                                scrollPanes[i].setPreferredSize(new Dimension(scrollPanes[i].getPreferredSize().width, tables[i].getRowCount()*30));
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

    private void initializeSlider() {
        // slider and timer
        slider = new JSlider();
        slider.setMajorTickSpacing(25);
        slider.setMinorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        Hashtable position = new Hashtable();
        position.put(0, new JLabel("0s"));
        position.put(25, new JLabel("1s"));
        position.put(50, new JLabel("2s"));
        position.put(75, new JLabel("3s"));
        position.put(100, new JLabel("4s"));
        slider.setLabelTable(position);
        slider.setBackground(bgColor);
        slider.setBounds(190, 288, 246, 45);
    }

    private void addComponentsToFrame() {
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

    private void saveResults(JPanel panel) {
        String[] fileFormats = {"PDF", "JPEG"};
        JComboBox<String> formatComboBox = new JComboBox<>(fileFormats);

        int result = JOptionPane.showOptionDialog(null, formatComboBox, "Save As", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);

        if (result == JOptionPane.OK_OPTION) {
            String selectedFormat = (String) formatComboBox.getSelectedItem();

            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = null;
            String defaultExtension = null;

            if (selectedFormat.equals("PDF")) {
                filter = new FileNameExtensionFilter("PDF Files", "pdf");
                defaultExtension = "pdf";
            } else if (selectedFormat.equals("JPEG")) {
                filter = new FileNameExtensionFilter("JPEG Files", "jpg", "jpeg");
                defaultExtension = "jpg";
            }

            fileChooser.setFileFilter(filter);
            fileChooser.setApproveButtonText("Save");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            // Generate the file name using the desired format
            String formattedDate = new SimpleDateFormat("MMddyy_HHmmss").format(new Date());
            String fileName = String.format("%s_PG.%s", formattedDate, defaultExtension);
            fileChooser.setSelectedFile(new File(fileName));

            int option = fileChooser.showSaveDialog(null);

            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                String extension = getFileExtension(file);

                switch (selectedFormat) {
                    case "PDF":
                        if (!extension.equalsIgnoreCase("pdf")) {
                            file = new File(file.getAbsolutePath() + ".pdf");
                        }
                        saveResultsAsPDF(panel, file);
                        break;
                    case "JPEG":
                        if (!extension.equalsIgnoreCase("jpeg") && !extension.equalsIgnoreCase("jpg")) {
                            file = new File(file.getAbsolutePath() + ".jpg");
                        }
                        saveResultsAsJPEG(panel, file);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private String getFileExtension(File file) {
        String extension = "";
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            extension = fileName.substring(dotIndex + 1).toLowerCase();
        }
        return extension;
    }

    private void saveResultsAsPDF(JPanel panel, File file) {
    }

    private void saveResultsAsJPEG(JPanel panel, File file) {
        try {
            BufferedImage image = new BufferedImage(tablesPanel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = image.createGraphics();
            panel.print(graphics2D);
            graphics2D.dispose();
            ImageIO.write(image, "JPEG", file);
            JOptionPane.showMessageDialog(this, "Image saved successfully.");
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error occurred while saving image.", "Error", JOptionPane.ERROR_MESSAGE);
        }
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
            case "Second Chance(SC)":
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
                    saveButton.setEnabled(false);


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

                    }
                    table.setValueAt(step.getStatus(), table.getRowCount() - 1, stepIndex);
                    table.getColumnModel().getColumn(stepIndex).setCellRenderer(new HighlightCellRenderer(step.getFrame(), stepIndex, table.getRowCount(), step.isHit()));
                    totalPageFault.setText("Page Fault: " + String.valueOf(step.getPageFaults()));
                    stepIndex++; // Move to the next step
                } else {
                    // enable sliders and run function
                    slider.setEnabled(true);
                    pauseButton.setVisible(false);
                    runButton.setVisible(true);
                    saveButton.setEnabled(true);

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
                    saveButton.setEnabled(false);

                    for (int i = 0; i < tableModels.length; i++) {
                        tables[i].setValueAt(pageRefString.getPages().get(stepIndex), 0, stepIndex);
                        Step step = simulators[i].getSteps().get(stepIndex);
                        for (int j = 0; j < step.getPagesProcessed().size(); j++) {
                            int row = tables[i].getRowCount() - j - 2; // Subtract 2 to account for header and footer rows
                            tables[i].setValueAt(step.getPagesProcessed().get(j), row, stepIndex);
                        }
                        tables[i].setValueAt(step.getStatus(), tables[i].getRowCount() - 1, stepIndex);
                        tables[i].getColumnModel().getColumn(stepIndex).setCellRenderer(new HighlightCellRenderer(step.getFrame(), stepIndex, table.getRowCount(), step.isHit()));
                        titleLabels[i].setText(tableTitles[i] + " | Page Faults: " + step.getPageFaults());
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
                    saveButton.setEnabled(true);

                    // All steps have been processed, stop the timer
                    timer.stop();
                }
            }
        });

        // Start the timer
        timer.start();
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
