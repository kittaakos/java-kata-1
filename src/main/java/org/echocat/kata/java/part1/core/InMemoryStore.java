package org.echocat.kata.java.part1.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import org.echocat.kata.java.part1.model.Author;
import org.echocat.kata.java.part1.model.Book;
import org.echocat.kata.java.part1.model.Magazine;
import org.echocat.kata.java.part1.model.Media;

public enum InMemoryStore implements Store {

    INSTANCE;

    @Override
    public CompletableFuture<Stream<Media>> search(String query, FilterType type) {
        return loadAll().thenApply(all -> {
            return all.filter(media -> {
                if (Strings.isNullOrEmpty(query)) {
                    return true;
                }
                if (type == null) {
                    return media.getIsbn().contains(query);
                }
                switch (type) {
                    case EMAIL: return media.getAuthors().stream().anyMatch(a -> a.getEmail().contains(query));
                    case ISBN: return media.getIsbn().contains(query);
                    default: return true;
                }
            });
        });
    }

    protected CompletableFuture<Stream<Media>> loadAll() {
        return loadAuthors()
            .thenApply(authors -> Maps.uniqueIndex(authors.collect(Collectors.toList()), Author::getEmail))
            .thenApplyAsync(authors -> {
                List<CompletableFuture<Stream<Media>>> loads = Arrays.asList(loadBooks(authors), loadMagazines(authors));
                CompletableFuture<Void> all = CompletableFuture .allOf(loads.toArray(new CompletableFuture[loads.size()]));
                return all.thenApply(void_ -> {
                    return loads.stream()
                        .map(load -> load.join())
                        .reduce(Stream.empty(), (prev, curr) -> Stream.concat(prev, curr));
                });
            }).join();
    }
    
    protected CompletableFuture<Stream<Author>> loadAuthors() {
        return getData(getResourceUrl("authors.csv"), segments -> this.createAuthor(segments));
    }

    protected CompletableFuture<Stream<Media>> loadBooks(Map<String, Author> authors) {
        return getData(getResourceUrl("books.csv"), segments -> this.createBook(segments, authors));
    }

    protected CompletableFuture<Stream<Media>> loadMagazines(Map<String, Author> authors) {
        return getData(getResourceUrl("magazines.csv"), segments -> this.createMagazine(segments, authors));
    }
    
    protected CompletableFuture<String> getContent(String url) {
        return CompletableFuture.supplyAsync(() -> {
            try (InputStream is = new URL(url).openStream()) {
                return new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining("\n"));
            } catch (IOException e) {
                throw new RuntimeException("Could not stream content from " + url, e);
            }
        });
    }
    
    protected <T> CompletableFuture<Stream<T>> getData(String url, Function<List<String>, T> dataMapper) {
        return getContent(url)
        .thenApply(content -> Splitter.on("\n").trimResults().omitEmptyStrings().splitToStream(content).skip(1))
        .thenApply(lines -> lines.map(line -> Splitter.on(";").trimResults().splitToList(line)))
        .thenApply(tokens -> tokens.map(segments -> dataMapper.apply(segments)));
    }
    
    protected Author createAuthor(List<String> segments) {
        Preconditions.checkArgument(segments.size() == 3, "segment.size() !== 3, got: " + segments.size());
        return new Author(segments.get(0), segments.get(1), segments.get(2));
    }
    
    protected Media createBook(List<String> segments, Map<String, Author> authors) {
        Preconditions.checkArgument(segments.size() == 4, "segment.size() !== 4, got: " + segments.size());
        return new Book(segments.get(1), segments.get(0), segments.get(3), resolveAuthors(segments.get(2), authors));
    }
    
    protected Media createMagazine(List<String> segments, Map<String, Author> authors) {
        Preconditions.checkArgument(segments.size() == 4, "segment.size() !== 4, got: " + segments.size());
        return new Magazine(segments.get(1), segments.get(0), segments.get(3), resolveAuthors(segments.get(2), authors));
    }
    
    private List<Author> resolveAuthors(String rawEmails, Map<String, Author> authors) {
        return Splitter.on(",").trimResults().omitEmptyStrings().splitToStream(rawEmails)
        .map(email -> authors.get(email)).filter(Objects::nonNull).collect(Collectors.toList());
    }
    
    // TODO: it's a hack. For some reason, I could not get the an `inputStream` for the resource via `classLoader.getResource`.
    private String getResourceUrl(String name) {
        try {
            return new File("").getAbsoluteFile().toPath()
            .resolve("src")
            .resolve("main")
            .resolve("resources")
            .resolve("org")
            .resolve("echocat")
            .resolve("kata")
            .resolve("java")
            .resolve("part1")
            .resolve("data")
            .resolve(name).toUri().toURL().toString();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

}
