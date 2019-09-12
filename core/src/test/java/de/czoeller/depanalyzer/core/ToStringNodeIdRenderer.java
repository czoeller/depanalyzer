package de.czoeller.depanalyzer.core;

import de.czoeller.depanalyzer.core.graph.NodeRenderer;

public enum ToStringNodeIdRenderer implements NodeRenderer<Object> {
    INSTANCE;

    @Override
    public String render(Object node) {
        return node.toString();
    }
}


