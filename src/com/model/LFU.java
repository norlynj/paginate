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
        int pf = 0;
        for (int i = 0; i < pages.size(); i++) {
            if (s.size() < frameNumber) {
                if (!s.contains(pages.get(i))) {
                    s.add(pages.get(i));
                    freq.put(pages.get(i), 1);
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
                    ArrayList<Integer> leastFrequentPages = new ArrayList<>();
                    for (Map.Entry<Integer, Integer> entry : freq.entrySet()) {
                        if (entry.getValue() == minFreq) {
                            leastFrequentPages.add(entry.getKey());
                        }
                    }
                    int lfuPage = leastFrequentPages.get(0);
                    for (int j = 1; j < leastFrequentPages.size(); j++) {
                        if (s.indexOf(leastFrequentPages.get(j)) < s.indexOf(lfuPage)) {
                            lfuPage = leastFrequentPages.get(j);
                        }
                    }
                    currentFrame = s.indexOf(lfuPage);
                    s.set(currentFrame, pages.get(i));
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
