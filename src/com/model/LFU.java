package model;

public class LFU extends PageReplacementSimulator{
    public LFU(PageReferenceString prs, int frameNum) {
        super(prs);
        this.frameNumber = frameNum;
    }
}
