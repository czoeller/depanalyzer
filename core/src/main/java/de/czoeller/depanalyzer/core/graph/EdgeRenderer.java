package de.czoeller.depanalyzer.core.graph;

public interface EdgeRenderer<T> {
    String render(T from, T to);
}
