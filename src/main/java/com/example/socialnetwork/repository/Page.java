package com.example.socialnetwork.repository;

import java.util.ArrayList;
import java.util.List;

public class Page<E> {
    private Iterable<E> elementsOnPage;
    private int totalElementCount;

    public Page(Iterable<E> elementsOnPage, int totalElementCount) {
        this.elementsOnPage = elementsOnPage;
        this.totalElementCount = totalElementCount;
    }

    public Iterable<E> getElementsOnPage() {
        return elementsOnPage;
    }

    public int getTotalElementCount() {
        return totalElementCount;
    }

    public List<E> getContent() {
        // Convert the iterable to a list
        List<E> contentList = new ArrayList<>();
        for (E element : elementsOnPage) {
            contentList.add(element);
        }
        return contentList;
    }
}
