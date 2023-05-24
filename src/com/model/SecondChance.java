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

            // Check if the page is already in memory
            if (frames.contains(currentPage)) {
                currentFrame = frames.indexOf(currentPage);
                secondChance.set(currentFrame, true);
                status = "hit";
            } else {
                // Check if there is an empty frame available
                int emptyFrameIndex = frames.indexOf(null);
                if (emptyFrameIndex != -1) {
                    frames.set(emptyFrameIndex, currentPage);
                    currentFrame = emptyFrameIndex;
                    referenceQueue.add(emptyFrameIndex);
                    secondChance.set(emptyFrameIndex, true);
                    pageFaults++;
                    status = "miss";
                } else {
                    boolean pageFound = false;
                    while (!pageFound) {
                        int frameToReplace = referenceQueue.poll();
                        if (secondChance.get(frameToReplace)) {
                            secondChance.set(frameToReplace, false);
                            referenceQueue.add(frameToReplace);
                        } else {
                            frames.set(frameToReplace, currentPage);
                            currentFrame = frameToReplace;
                            referenceQueue.add(frameToReplace);
                            secondChance.set(frameToReplace, true);
                            pageFaults++;
                            status = "miss";
                            pageFound = true;
                        }
                    }
                }
            }

            steps.add(new Step(i, currentFrame + 1, status, new ArrayList<>(frames), pageFaults));
        }

        this.pageFaults = pageFaults;
    }
}
