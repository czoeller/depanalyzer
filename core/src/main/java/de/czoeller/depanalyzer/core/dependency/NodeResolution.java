package de.czoeller.depanalyzer.core.dependency;

public enum NodeResolution {
    INCLUDED,
    OMITTED_FOR_DUPLICATE,
    OMITTED_FOR_CONFLICT,
    PARENT
}
