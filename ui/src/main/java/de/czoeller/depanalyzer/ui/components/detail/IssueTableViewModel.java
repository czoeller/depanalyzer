/*
 * Copyright (C) 2019 czoeller
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
