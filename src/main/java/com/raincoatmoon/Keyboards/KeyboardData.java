package com.raincoatmoon.Keyboards;

import java.util.List;

public class KeyboardData {
    private int first;
    private int last;
    private int next;
    private int prev;

    public KeyboardData(int first, int last, int next, int prev) {
        this.first = first;
        this.last = last;
        this.next = next;
        this.prev = prev;
    }

    public KeyboardData(int first, int last) {
        this.first = first;
        this.last = last;
    }

    public KeyboardData(List<String> list) {
        if (list != null && list.size() >= 4) {
            this.first = Integer.valueOf(list.get(0));
            this.last = Integer.valueOf(list.get(1));
            this.next = Integer.valueOf(list.get(2));
            this.prev = Integer.valueOf(list.get(3));
        }
    }

    public int getFirst() {
        return first;
    }

    public int getLast() {
        return last;
    }

    public int getNext() {
        return next;
    }

    public int getPrev() {
        return prev;
    }
}
