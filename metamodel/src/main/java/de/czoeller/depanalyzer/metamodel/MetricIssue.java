package de.czoeller.depanalyzer.metamodel;

import lombok.Data;

@Data
public class MetricIssue extends Issue {
    private final float instability;

    public MetricIssue(Severity severity, String description, float instability) {
        super(severity, description);
        this.instability = instability;
    }
}
