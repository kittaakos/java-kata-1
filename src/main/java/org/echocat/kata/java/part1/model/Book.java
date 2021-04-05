package org.echocat.kata.java.part1.model;

public class Book extends Media {

    public Book(String isbn, String title, String description, Iterable<? extends Author> authors) {
        super(isbn, title, description, authors);
    }

    public String getDescription() {
        return this.getDetail();
    }

}
