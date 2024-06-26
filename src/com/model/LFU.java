package model;

import java.util.*;

public class LFU extends PageReplacementSimulator {
    public LFU(PageReferenceString prs, int frameNum) {
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
        LinkedList<Integer> fifoQueue = new LinkedList<>(); // FIFO queue for tiebreakers
        int pf = 0;
        for (int i = 0; i < pages.size(); i++) {
            if (s.size() < frameNumber) {
                if (!s.contains(pages.get(i))) {
                    s.add(pages.get(i));
                    freq.put(pages.get(i), 1);
                    fifoQueue.add(pages.get(i));
                    pf++;
                    status = "miss";
                    currentFrame = s.size() - 1;
                } else {
                    freq.put(pages.get(i), freq.get(pages.get(i)) + 1);
                    currentFrame = s.indexOf(pages.get(i));
                    status = "hit";
                }
            } else {
                if (!s.contains(pages.get(i))) {
                    int minFreq = Collections.min(freq.values());
                    int minFreqCount = 0;

                    for (int value : freq.values()) {
                        if (value == minFreq) {
                            minFreqCount++;
                        }
                    }
                    ArrayList<Integer> leastFrequentPages = new ArrayList<>();
                    for (Map.Entry<Integer, Integer> entry : freq.entrySet()) {
                        if (entry.getValue() == minFreq) {
                            leastFrequentPages.add(entry.getKey());
                        }
                    }

                    // Find the first element among the nearest to the exit element that belongs to mostFrequentPages
                    int nearestToExit = -1;
                    List<Integer> tempList = new ArrayList<>(fifoQueue);

                    for (int page : leastFrequentPages) {
                        int index = tempList.indexOf(page);
                        if (index != -1 && (nearestToExit == -1 || index < tempList.indexOf(nearestToExit))) {
                            nearestToExit = page;
                        }
                    }

                    int lfuPage = leastFrequentPages.get(0);

                    if (minFreqCount > 1) {
                        lfuPage = nearestToExit;
                        fifoQueue.poll();
                        fifoQueue.add(pages.get(i));
                    } else {
                        for (int j = 1; j < leastFrequentPages.size(); j++) {
                            if (s.indexOf(leastFrequentPages.get(j)) < s.indexOf(lfuPage)) {
                                lfuPage = leastFrequentPages.get(j);
                            }
                        }
                    }

                    currentFrame = s.indexOf(lfuPage);
                    if (currentFrame != -1) {
                        s.set(currentFrame, pages.get(i));
                    }
                    freq.remove(lfuPage);
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
