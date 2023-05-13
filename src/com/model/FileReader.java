package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;

public class FileReader {

    ArrayList pageRefString;
    int frameNumber;

    public void readInputFromFile(File inputFile) throws FileNotFoundException {
        ArrayList<Integer> inputList = new ArrayList<>();
        Scanner scanner = new Scanner(inputFile);

        // Read page reference string
        String pageReferenceStringLine = scanner.nextLine();
        String[] pageReferenceStringArray = pageReferenceStringLine.split(": ")[1].split(", ");
        for (String pageReferenceString : pageReferenceStringArray) {
            inputList.add(Integer.parseInt(pageReferenceString));
        }


        // Read number of frames
        String numberOfFramesLine = scanner.nextLine();
        int numberOfFrames = Integer.parseInt(numberOfFramesLine.split(": ")[1]);

        scanner.close();

        this.pageRefString = inputList;
        this.frameNumber = numberOfFrames;
    }

    public ArrayList getPageRefString() {
        return pageRefString;
    }

    public int getFrameNumber() {
        return frameNumber;
    }
}