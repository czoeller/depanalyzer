package de.czoeller.depanalyzer.core.graph;

import java.util.Objects;

public final class Edge {

    private final String fromNodeId;
    private final String toNodeId;
    private final String name;
    // Not part of equals()/hashCode()
    private final boolean permanent;

    public Edge(String fromNodeId, String toNodeId, String name) {
        this(fromNodeId, toNodeId, name, false);
    }

    public Edge(String fromNodeId, String toNodeId, String name, boolean permanent) {
        this.fromNodeId = fromNodeId;
        this.toNodeId = toNodeId;
        this.name = name;
        this.permanent = permanent;
    }

    public String getFromNodeId() {
        return this.fromNodeId;
    }

    public String getToNodeId() {
        return this.toNodeId;
    }

    public String getName() {
        return this.name;
    }

    public boolean isPermanent() {
        return this.permanent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof Edge)) { return false; }

        Edge edge = (Edge) o;
        return Objects.equals(this.fromNodeId, edge.fromNodeId)
                && Objects.equals(this.toNodeId, edge.toNodeId)
                && Objects.equals(this.name, edge.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.fromNodeId, this.toNodeId, this.name);
    }

    @Override
    public String toString() {
        return this.fromNodeId + " -> " + this.toNodeId + " (" + this.name + ")";
    }
}
