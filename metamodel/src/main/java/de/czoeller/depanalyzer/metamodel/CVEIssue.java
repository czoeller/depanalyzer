package de.czoeller.depanalyzer.metamodel;

import lombok.Data;

@Data
public class CVEIssue extends Issue {

    public CVEIssue(Severity severity, String description) {
        super(severity, description);
    }
}
