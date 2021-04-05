package org.echocat.kata.java.part1.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;

public class Media {

    private final String isbn;
    private final String title;
    private final String detail;
    private final Set<Author> authors;

    protected Media(String isbn, String title, String detail, Iterable<? extends Author> authors) {
        this.isbn = isbn;
        this.title = title;
        this.detail = detail;
        this.authors = Sets.newHashSet(authors);
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public Collection<Author> getAuthors() {
        return Collections.unmodifiableSet(this.authors);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((isbn == null) ? 0 : isbn.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Media other = (Media) obj;
        if (isbn == null) {
            if (other.isbn != null)
                return false;
        } else if (!isbn.equals(other.isbn))
            return false;
        return true;
    }

}
