package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

/* Algorithm FIFO from GFG
* 1. Traverse pages
*   - if hashsets holds less than page capacity do the following
*       a. Insert page into the set until all page requests are processed
*       b. Maintain the pages in the queue to perform FIFO
*       c. Increment page fault
*   - else
*       hit when the current page is present in set.
*       else
*           a. remove first page from the queue as it was the first to be entered in memory
*           b. replace the first page with the current page
*           c. store current page in the queue
*           d. increment page faults
*/

public class FIFO extends PageReplacementSimulator {

    public FIFO(PageReferenceString prs, int frameNum) {
        super(prs);
        this.frameNumber = frameNum;
    }

    public void simulate() {
        steps = new ArrayList<>();
        String status =  "";
        int currentFrame = 0;

        ArrayList<Integer> s = new ArrayList<>(frameNumber);
        Queue<Integer> indexes = new LinkedList<>();
        ArrayList<Integer> pages = string.getPages();
        int pf = 0;
        for (int i = 0; i < pages.size(); i++) {
            if (s.size() < frameNumber) {
                if (!s.contains(pages.get(i))) {
                    s.add(pages.get(i));
                    pf++;
                    indexes.add(pages.get(i));
                    status = "miss";
                    currentFrame = s.size();
                }
            } else {
                if (!s.contains(pages.get(i))) {
                    int val = indexes.peek();
                    indexes.poll();
                    currentFrame = s.indexOf(val);
                    s.set(currentFrame, pages.get(i));
                    indexes.add(pages.get(i));
                    pf++;
                    status = "miss";
                } else {
                    status = "hit";
                }
            }
            steps.add(new Step(i, currentFrame, status, new ArrayList<>(s)));
        }
        this.pageFaults = pf;
    }
}
