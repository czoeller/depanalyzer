package de.czoeller.depanalyzer.metamodel;

import lombok.Data;

@Data
public class MetricIssue implements Issue {
    private final float instability;
}
