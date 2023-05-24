package model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class SecondChance extends PageReplacementSimulator {
    public SecondChance(PageReferenceString prs, int frameNum) {
        super(prs);
        this.frameNumber = frameNum;
    }

    public void simulate() {
        steps = new ArrayList<>();
        String status=" ";
        int currentFrame = -1;

        ArrayList<Integer> frames = new ArrayList<>(frameNumber);
        Queue<Integer> referenceQueue = new LinkedList<>();
        ArrayList<Integer> pages = string.getPages();
        ArrayList<Boolean> secondChance = new ArrayList<>(frameNumber);
        int pageFaults = 0;

        for (int i = 0; i < frameNumber; i++) {
            frames.add(null);
            secondChance.add(false);
        }

        for (int i = 0; i < pages.size(); i++) {
            int currentPage = pages.get(i);

            System.out.println(frames);
            // Check if the page is already in memory
            if (frames.contains(currentPage)) {
                System.out.println("The page is already in memory");
                currentFrame = frames.indexOf(currentPage);
                secondChance.set(currentFrame, true);
                status = "hit";
            } else {
                // Check if there is an empty frame available
                int emptyFrameIndex = frames.indexOf(null);
                if (emptyFrameIndex != -1) {
                    System.out.println("There is an empty index in " + emptyFrameIndex);
                    frames.set(emptyFrameIndex, currentPage);
                    currentFrame = emptyFrameIndex;
                    referenceQueue.add(emptyFrameIndex);
                    secondChance.set(emptyFrameIndex, true);
                    pageFaults++;
                    status = "miss";
                } else {
                    System.out.println("There is no empty index");
                    System.out.println("The while loop starts");
                    boolean pageFound = false;
                    while (!pageFound) {
                        System.out.println("Finding where to insert in the reference queue..." + referenceQueue);
                        System.out.println("The second chance are..." + secondChance);
                        int frameToReplace = referenceQueue.poll();
                        if (secondChance.get(frameToReplace)) {
                            System.out.println("Second chance is true in " + frameToReplace + " that's why we need to make it false.");
                            secondChance.set(frameToReplace, false);
                            referenceQueue.add(frameToReplace);
                        } else {
                            System.out.println("Second chance is false in " + frameToReplace + " that's why we need to make it true. By setting the frame " + frameToReplace + " to " + currentPage);
                            frames.set(frameToReplace, currentPage);
                            currentFrame = frameToReplace;
                            referenceQueue.add(frameToReplace);
                            secondChance.set(frameToReplace, true);
                            pageFaults++;
                            status = "miss";
                            pageFound = true;
                        }
                    }
                    System.out.println("The while loop ends");

                }
            }

            steps.add(new Step(i, currentFrame + 1, status, new ArrayList<>(frames), pageFaults));
        }

        this.pageFaults = pageFaults;
    }
}
