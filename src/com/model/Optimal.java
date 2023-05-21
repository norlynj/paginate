package model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Optimal extends PageReplacementSimulator {

    public Optimal(PageReferenceString prs, int frameNum) {
        super(prs);
        this.frameNumber = frameNum;
    }

    public void simulate() {
        steps = new ArrayList<>();
        String status = "";
        int currentFrame = -1;

        ArrayList<Integer> memoryFrames = new ArrayList<>(frameNumber);
        ArrayList<Integer> pages = string.getPages();
        int pageFaults = 0;

        for (int i = 0; i < pages.size(); i++) {
            if (!memoryFrames.contains(pages.get(i))) {
                if (memoryFrames.size() < frameNumber) {
                    memoryFrames.add(pages.get(i));
                    currentFrame = memoryFrames.size() - 1;
                    pageFaults++;
                    status = "miss";
                } else {
                    int farthestIndex = -1;
                    int replaceIndex = -1;

                    for (int j = 0; j < memoryFrames.size(); j++) {
                        int pageIndex = memoryFrames.get(j);
                        int farthestPageIndex = -1;

                        for (int k = i + 1; k < pages.size(); k++) {
                            if (pages.get(k) == pageIndex) {
                                farthestPageIndex = k;
                                break;
                            }
                        }

                        if (farthestPageIndex == -1) {
                            replaceIndex = j;
                            break;
                        }

                        if (farthestPageIndex > farthestIndex) {
                            farthestIndex = farthestPageIndex;
                            replaceIndex = j;
                        }
                    }

                    if (replaceIndex != -1) {
                        memoryFrames.set(replaceIndex, pages.get(i));
                        currentFrame = replaceIndex;
                        pageFaults++;
                        status = "miss";
                    } else {
                        currentFrame = memoryFrames.indexOf(pages.get(i));
                        status = "hit";
                    }
                }
            } else {
                currentFrame = memoryFrames.indexOf(pages.get(i));
                status = "hit";
            }

            steps.add(new Step(i, currentFrame + 1, status, new ArrayList<>(memoryFrames), pageFaults));
        }

        this.pageFaults = pageFaults;
    }
}
