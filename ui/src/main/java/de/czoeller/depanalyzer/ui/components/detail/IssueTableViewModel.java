package de.czoeller.depanalyzer.ui.components.detail;

import de.czoeller.depanalyzer.metamodel.Issue;
import lombok.Data;

@Data
public class IssueTableViewModel {
    private final Issue issue;

    public String getSeverity() {
        return issue.getSeverity().toString();
    }

    public String getDescription() {
        return issue.getDescription();
    }

}
