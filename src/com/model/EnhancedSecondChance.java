package model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class EnhancedSecondChance extends PageReplacementSimulator {
    public EnhancedSecondChance(PageReferenceString prs, int frameNum) {
        super(prs);
        this.frameNumber = frameNum;
    }

    public void simulate() {
        steps = new ArrayList<>();
        String status="";
        int currentFrame = -1;

        ArrayList<Integer> frames = new ArrayList<>(frameNumber);
        Queue<Integer> referenceQueue = new LinkedList<>();
        ArrayList<Integer> pages = string.getPages();
        ArrayList<Boolean> referenceBit = new ArrayList<>(frameNumber);
        ArrayList<Boolean> modifiedBit = new ArrayList<>(frameNumber);
        int pageFaults = 0;

        for (int i = 0; i < frameNumber; i++) {
            frames.add(null);
            referenceBit.add(false);
            modifiedBit.add(false);
        }

        for (int i = 0; i < pages.size(); i++) {
            int currentPage = pages.get(i);

            // Check if the page is already in memory
            if (frames.contains(currentPage)) {
                currentFrame = frames.indexOf(currentPage);
                referenceBit.set(currentFrame, true);
                status = "hit";
            } else {

                int emptyFrameIndex = frames.indexOf(null);
                if (emptyFrameIndex != -1) {
                    frames.set(emptyFrameIndex, currentPage);
                    currentFrame = emptyFrameIndex;
                    referenceBit.set(emptyFrameIndex, true);
                    modifiedBit.set(emptyFrameIndex, false);
                    referenceQueue.add(emptyFrameIndex);
                    pageFaults++;
                    status = "miss";
                } else {
                    boolean pageFound = false;
                    while (!pageFound) {
                        int frameToReplace = referenceQueue.poll();
                        if (referenceBit.get(frameToReplace) && modifiedBit.get(frameToReplace)) {
                            referenceBit.set(frameToReplace, false);
                            referenceQueue.add(frameToReplace);
                        } else {
                            frames.set(frameToReplace, currentPage);
                            currentFrame = frameToReplace;
                            referenceBit.set(frameToReplace, true);
                            modifiedBit.set(frameToReplace, false);
                            referenceQueue.add(frameToReplace);
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
