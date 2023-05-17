package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class Step {
    private int page;
    private int frame;
    private String status;
    private ArrayList<Integer> pagesProcessed;

    public Step(int page, int frame, String status, ArrayList<Integer> pagesProcessed) {
        this.page = page;
        this.frame = frame;
        this.status = status;
        this.pagesProcessed = pagesProcessed;
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
}
