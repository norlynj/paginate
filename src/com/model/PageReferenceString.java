package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class PageReferenceString {
    private int STRING_LEN_MIN = 10, STRING_LEN_MAX = 40, STRING_VAL_MIN = 0, STRING_VAL_MAX = 20;
    ArrayList string;

    public void setString(ArrayList pageRefString) {
        this.string = pageRefString;
    }

    public String random() {
        ArrayList<Integer> list = new ArrayList<>();
        Random random = new Random();
        int size = random.nextInt(31) + 10; // Generate random size between 10 and 40

        for (int i = 0; i < size; i++) {
            int value = random.nextInt(19) + 1;; // Generate random integer between 0 and 20, meaning 1-19
            list.add(value);
        }
        this.string = list;
        return String.join(", ", list.stream().map(Object::toString).toArray(String[]::new));
    }
}
