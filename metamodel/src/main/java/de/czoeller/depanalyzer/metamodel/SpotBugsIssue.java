package de.czoeller.depanalyzer.metamodel;

import lombok.Data;

@Data
public class SpotBugsIssue extends Issue {

    public SpotBugsIssue(Severity severity, String description) {
        super(severity, description);
    }
}
