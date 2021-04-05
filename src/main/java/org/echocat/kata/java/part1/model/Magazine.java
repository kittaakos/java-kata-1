package org.echocat.kata.java.part1.model;

public class Magazine extends Media {

    public Magazine(String isbn, String title, String publishedAt, Iterable<? extends Author> authors) {
        super(isbn, title, publishedAt, authors);
    }

    public String getPublishedAt() {
        return getDetail();
    }

}
