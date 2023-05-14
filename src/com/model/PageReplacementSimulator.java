package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class PageReplacementSimulator {
    PageReferenceString string;
    ArrayList<Step> steps;
    int frameNumber;
    int pageFaults;

    public PageReplacementSimulator (PageReferenceString prs) {
        this.string = prs;
    }

    public void simulate() {

    }

    public PageReferenceString getString() {
        return string;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public int getPageFaults() {
        return pageFaults;
    }
}
