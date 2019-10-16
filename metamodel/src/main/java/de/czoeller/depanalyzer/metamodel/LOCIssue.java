package de.czoeller.depanalyzer.metamodel;

import lombok.Data;

@Data
public class LOCIssue extends Issue {
    private final int loc;

    public LOCIssue(Severity severity, String description, int loc) {
        super(severity, description);
        this.loc = loc;
    }

}
