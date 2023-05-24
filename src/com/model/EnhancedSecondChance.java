package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class EnhancedSecondChance extends PageReplacementSimulator {
    private static int[] referenceString;
    private static int framesNum;
    public EnhancedSecondChance(PageReferenceString prs, int framesNum) {
        super(prs);
        this.framesNum = framesNum;
        ArrayList<Integer> referenceList = string.getPages();
        referenceString = new int[referenceList.size()];
        for (int i = 0; i < referenceList.size(); i++) {
           referenceString[i] = referenceList.get(i);
        }
    }

    public void simulate() {
        steps = new ArrayList<>();
        String status="miss";
        int currentFrame = -1;
        int pageFaults = 0;
        int pointer = 0;

        int framesInMemory[][] = new int[framesNum][3]; // 3 bits inside the frame
        int memory[][] = new int[referenceString.length][framesNum];
        int referenceBit[][] = new int[referenceString.length][framesNum];
        int modificationBit[][] = new int[referenceString.length][framesNum];
        int initModificationBit[][] = new int[referenceString.length][2];

        for (int j = 0; j < framesNum; j++) {
            framesInMemory[j][0] = -1;    // initially empty
            framesInMemory[j][1] = 0;     // initially zero
        }

        for (int i = 0; i < initModificationBit.length; i++) {
            initModificationBit[i][1] = 0;
        }

        for (int i = 0; i < referenceString.length; i++) {
            boolean check = false;
            ArrayList<Integer> passedByFrames = new ArrayList<>();

            for (int j = 0; j < framesNum; j++) {
                if (framesInMemory[j][0] == referenceString[i]) {
                    status = "hit";
                    currentFrame = j + 1;
                    check = true;
                    break;
                }
            }

            if (check == false) {
                status = "miss";
                pageFaults++;
                int replacementPointer = esc_00(framesInMemory, pointer, i, initModificationBit, passedByFrames);

                if (replacementPointer == -1) {
                    replacementPointer = esc_01(framesInMemory, pointer, i, initModificationBit, passedByFrames);
                    // Set all by passed r-bits to zero
                    for (int j = 0; j < passedByFrames.size(); j++) {
                        framesInMemory[(int) passedByFrames.get(j)][1] = 0;
                    }
                    if (replacementPointer == -1) {
                        replacementPointer = esc_00(framesInMemory, pointer, i, initModificationBit, passedByFrames);
                        if (replacementPointer == -1) {
                            replacementPointer = esc_01(framesInMemory, pointer, i, initModificationBit, passedByFrames);
                        } else {
                            pointer = replacementPointer;
                        }
                    } else {
                        pointer = replacementPointer;
                    }
                } else {
                    pointer = replacementPointer;
                }
                currentFrame = pointer;
            }

            ArrayList<Integer> column = new ArrayList<>();
            for (int j = 0; j < framesNum; j++) {
                memory[i][j] = framesInMemory[j][0];
                referenceBit[i][j] = framesInMemory[j][1];
                modificationBit[i][j] = framesInMemory[j][2];
                column.add(framesInMemory[j][0] == -1 ? null : framesInMemory[j][0]);
            }
            steps.add(new Step(i, currentFrame, status, new ArrayList<>(column), pageFaults));
        }
        this.pageFaults = pageFaults;
        printThreeColumns(memory, referenceBit, modificationBit, pageFaults, "Enhanced Second Chance Algorithm: ");
    }


    private static int esc_00(int framesInMemory[][], int pointer, int i, int initModificationBit[][], ArrayList passedByFrames){
        for (int j = pointer; j < framesInMemory.length+pointer; j++) {
            if( framesInMemory[pointer][1] == 0 && framesInMemory[pointer][2] == 0 ) {
                framesInMemory[pointer][0] = referenceString[i];
                framesInMemory[pointer][1] = 1;
                framesInMemory[pointer][2] = initModificationBit[i][1];
                pointer++;
                if(pointer == framesNum)
                    pointer = 0;
                return pointer;
            }
            else{
                pointer++;
                if(pointer == framesNum)
                    pointer = 0;
            }
        }
        return -1;
    }

    private static int esc_01(int framesInMemory[][], int pointer, int i, int initModificationBit[][], ArrayList passedByFrames){

        for (int j = pointer; j < framesInMemory.length+pointer; j++) {

            if( framesInMemory[pointer][1] == 0 && framesInMemory[pointer][2] == 1 ) {

                framesInMemory[pointer][0] = referenceString[i];
                framesInMemory[pointer][1] = 1;
                framesInMemory[pointer][2] = initModificationBit[i][1];

                if(passedByFrames.contains(pointer)){
                    passedByFrames.remove((Object)pointer);
                }
                pointer++;

                if(pointer == framesNum)
                    pointer = 0;

                return pointer;
            }
            else{
                if(!passedByFrames.contains(pointer)){
                    passedByFrames.add(pointer);
                }
                pointer++;
                if(pointer == framesNum)
                    pointer = 0;
            }
        }
        return -1;
    }

    private static void printThreeColumns(int[][] memory,int[][] refBit,int[][] modifyBit, int faults,String s){

        System.out.println("");
        for(int i=0; i<25; i++){
            System.out.print("--");
        }
        System.out.println("");
        System.out.println("Reference String:");
        for (int i = 0; i < referenceString.length; i++) {
            System.out.printf("%6d   ",referenceString[i]);
        }
        System.out.println("\n");

        System.out.println(s);
        for(int i = 0; i < framesNum; i++){
            for(int j = 0; j < referenceString.length; j++)
                System.out.printf("%4d %d %d ",memory[j][i],refBit[j][i],modifyBit[j][i]);
            System.out.println("");
        }

        System.out.println("The number of Faults: " + faults);
    }
}
