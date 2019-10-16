package de.czoeller.depanalyzer.ui.components.detail;

import de.czoeller.depanalyzer.metamodel.Issue;
import de.czoeller.depanalyzer.metamodel.Issue.Severity;
import lombok.Data;

@Data
public class IssueTableViewModel {
    private final Issue issue;

    public Severity getSeverity() {
        return issue.getSeverity();
    }

    public String getDescription() {
        return issue.getDescription();
    }

}
