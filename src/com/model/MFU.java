package model;

import java.util.*;

public class MFU extends PageReplacementSimulator {
    public MFU(PageReferenceString prs, int frameNum) {
        super(prs);
        this.frameNumber = frameNum;
    }

    public void simulate() {
        steps = new ArrayList<>();
        String status = "";
        int currentFrame = 0;

        ArrayList<Integer> s = new ArrayList<>(frameNumber);
        HashMap<Integer, Integer> freq = new HashMap<>();
        ArrayList<Integer> pages = string.getPages();
        Queue<Integer> fifoQueue = new LinkedList<>();  // first to arrive queue for tiebreakers
        int pf = 0;
        for (int i = 0; i < pages.size(); i++) {
            if (s.size() < frameNumber) {
                if (!s.contains(pages.get(i))) {
                    s.add(pages.get(i));
                    freq.put(pages.get(i), 1);
                    fifoQueue.add(pages.get(i));
                    pf++;
                    status = "miss";
                    currentFrame = s.size() - 1 ;
                } else {
                    freq.put(pages.get(i), freq.get(pages.get(i)) + 1);
                    currentFrame = s.indexOf(pages.get(i));
                    status = "hit";
                }
            } else {
                if (!s.contains(pages.get(i))) {
                    int maxFreq = Collections.max(freq.values());
                    int maxFreqCount = 0;

                    for (int value : freq.values()) {
                        if (value == maxFreq) {
                            maxFreqCount++;
                        }
                    }

                    ArrayList<Integer> mostFrequentPages = new ArrayList<>();
                    for (Map.Entry<Integer, Integer> entry : freq.entrySet()) {
                        if (entry.getValue() == maxFreq) {
                            mostFrequentPages.add(entry.getKey());
                        }
                    }

                    // Find the first element among the nearest to the exit element that belongs to mostFrequentPages
                    int nearestToExit = -1;
                    List<Integer> tempList = new ArrayList<>(fifoQueue);

                    for (int page : mostFrequentPages) {
                        int index = tempList.indexOf(page);
                        if (index != -1 && (nearestToExit == -1 || index < tempList.indexOf(nearestToExit))) {
                            nearestToExit = page;
                        }
                    }

                    int mfuPage = mostFrequentPages.get(0);

                    if (maxFreqCount > 1) {
                        mfuPage = nearestToExit;
                        fifoQueue.poll();
                        fifoQueue.add(pages.get(i));
                    } else {
                        for (int j = 1; j < mostFrequentPages.size(); j++) {
                            if (s.indexOf(mostFrequentPages.get(j)) < s.indexOf(mfuPage)) {
                                mfuPage = mostFrequentPages.get(j);
                            }
                        }
                    }

                    currentFrame = s.indexOf(mfuPage);
                    if (currentFrame != -1) {
                        s.set(currentFrame, pages.get(i));
                    }
                    freq.remove(mfuPage);
                    freq.put(pages.get(i), 1);

                    // Increment page faults
                    pf++;
                    status = "miss";
                } else {
                    freq.put(pages.get(i), freq.get(pages.get(i)) + 1);
                    currentFrame = s.indexOf(pages.get(i));
                    status = "hit";
                }
            }
            steps.add(new Step(i, currentFrame + 1, status, new ArrayList<>(s), pf));
        }
        this.pageFaults = pf;
    }
}
