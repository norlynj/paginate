package model;

import view.InputPanel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class FileReader {

    ArrayList pageRefString;
    int frameNumber;

    public boolean readInputFromFile() throws FileNotFoundException {
        String resourcePath = "/resources/text/";
        URL resourceUrl = InputPanel.class.getResource(resourcePath);
        boolean invalid = false;

        // Convert the URL to a file object
        assert resourceUrl != null;
        File resourceFile = new File(resourceUrl.getPath());
        JFileChooser fileChooser = new JFileChooser(resourceFile);
        fileChooser.setDialogTitle("Select text file");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
        fileChooser.setFileFilter(filter);
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File inputFile = fileChooser.getSelectedFile();

            try {
                ArrayList<Integer> inputList = new ArrayList<>();
                Scanner scanner = new Scanner(inputFile);

                // Read page reference string
                String pageReferenceStringLine = scanner.nextLine();
                String[] pageReferenceStringArray = pageReferenceStringLine.split(": ")[1].split(", ");
                if (pageReferenceStringArray.length < 10 || pageReferenceStringArray.length > 40) {
                    invalid = true;
                }
                for (String pageReferenceString : pageReferenceStringArray) {
                    int value = Integer.parseInt(pageReferenceString);
                    if (value < 1 || value > 19) {
                        invalid = true;
                    }
                    inputList.add(value);
                }


                // Read number of frames
                String numberOfFramesLine = scanner.nextLine();
                int numberOfFrames = Integer.parseInt(numberOfFramesLine.split(": ")[1]);

                scanner.close();

                this.pageRefString = inputList;
                this.frameNumber = numberOfFrames;

            } catch (FileNotFoundException | ArrayIndexOutOfBoundsException ex) {
                System.out.println("Error reading the file");
                return false;
            }
        } else {
            return false;
        }
        if (invalid) {
            JOptionPane.showMessageDialog(null, "Length must be 10 to 40 while values must be between 0 and 20 ");
            return false;
        }
        return true;
    }

    public ArrayList getPageRefString() {
        return pageRefString;
    }

    public int getFrameNumber() {
        return frameNumber;
    }
}