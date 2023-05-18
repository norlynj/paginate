package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class Step {
    private int page;
    private int frame;
    private int pageFaults;
    private boolean hit;
    private String status;
    private ArrayList<Integer> pagesProcessed;

    public Step(int page, int frame, String status, ArrayList<Integer> pagesProcessed, int pageFaults) {
        this.page = page;
        this.frame = frame;
        this.status = status;
        this.pagesProcessed = pagesProcessed;
        this.pageFaults = pageFaults;

        if (status == "hit") {
            hit = true;
        }
    }

    public int getPage() {
        return page;
    }

    public int getFrame() {
        return frame;
    }

    public String getStatus() {
        return status;
    }

    public ArrayList<Integer> getPagesProcessed() {
        return pagesProcessed;
    }

    public int getPageFaults() {
        return pageFaults;
    }

    public boolean isHit() {
        return hit;
    }
}
