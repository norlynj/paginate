package model;

import java.util.*;

public class LRU extends PageReplacementSimulator{
    public LRU(PageReferenceString prs, int frameNum) {
        super(prs);
        this.frameNumber = frameNum;
    }

    public void simulate() {
        steps = new ArrayList<>();
        String status =  "";
        int currentFrame = 0;

        ArrayList<Integer> s = new ArrayList<>(frameNumber);
        HashMap<Integer, Integer> indexes = new HashMap<>();
        ArrayList<Integer> pages = string.getPages();
        int pf = 0;
        for (int i = 0; i < pages.size(); i++) {
            if (s.size() < frameNumber) {
                if (!s.contains(pages.get(i))) {
                    s.add(pages.get(i));
                    pf++;
                    status = "miss";
                    currentFrame = s.size();
                }
                indexes.put(pages.get(i), i);
            } else {
                if (!s.contains(pages.get(i))) {
                    int lru = Integer.MAX_VALUE;
                    int val = Integer.MIN_VALUE;

                    Iterator<Integer> itr = s.iterator();
                    while (itr.hasNext()) {
                        int temp = itr.next();
                        if (indexes.get(temp) < lru) {
                            lru = indexes.get(temp);
                            val = temp;
                        }
                    }
                    currentFrame = s.indexOf(val);
                    s.set(currentFrame, pages.get(i));
                    indexes.remove(val);

                    // Increment page faults
                    pf++;
                    status = "miss";
                } else {
                    status = "hit";
                }
                indexes.put(pages.get(i), i);
            }
            steps.add(new Step(i, currentFrame, status, new ArrayList<>(s), pf));
        }
        this.pageFaults = pf;
    }
}
