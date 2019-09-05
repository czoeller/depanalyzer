package de.czoeller.depanalyzer.metamodel;

import lombok.Data;

@Data
public class LOCIssue implements Issue {
    private final int loc;

    public LOCIssue(int loc) {
        this.loc = loc;
    }
}
