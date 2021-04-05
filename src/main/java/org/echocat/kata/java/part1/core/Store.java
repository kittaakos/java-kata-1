package org.echocat.kata.java.part1.core;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import org.echocat.kata.java.part1.model.Media;

public interface Store {

    CompletableFuture<Stream<Media>> search();
    CompletableFuture<Stream<Media>> search(String query, FilterType type);

    static enum FilterType {
        ISBN,
        EMAIL
    }

}
