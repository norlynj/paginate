package model;

import java.util.ArrayList;
import java.util.Random;

public class PageReferenceString {
    private int STRING_LEN_MIN = 10, STRING_LEN_MAX = 40, STRING_VAL_MIN = 0, STRING_VAL_MAX = 20;
    private ArrayList<Integer> pages;

    public void setString(ArrayList<Integer> pageRefString) {
        this.pages = pageRefString;
    }

    public String random() {
        ArrayList<Integer> list = new ArrayList<>();
        Random random = new Random();
        int size = random.nextInt(31) + 10; // Generate random size between 10 and 40, meaning 11-39

        for (int i = 0; i < size; i++) {
            int value = random.nextInt(19) + 1;; // Generate random integer between 0 and 20, meaning 1-19
            list.add(value);
        }
        this.pages = list;
        return String.join(", ", list.stream().map(Object::toString).toArray(String[]::new));
    }

    public ArrayList<Integer> getPages() {
        return pages;
    }

    public String getString() {
        return String.join(", ", this.pages.stream().map(Object::toString).toArray(String[]::new));
    }
}
