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
package de.czoeller.depanalyzer.metamodel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IssueTest {

    @DisplayName("testToString")
    @Test
    void testToString() {
        assertThat(new CVEIssue(Issue.Severity.LOW, "Test").toString()).isEqualTo("CVEIssue(super=Issue(severity=LOW, description=Test))");
        assertThat(new MetricIssue(Issue.Severity.LOW, "Test", 0.5f).toString()).isEqualTo("MetricIssue(super=Issue(severity=LOW, description=Test), distance=0.5)");
        assertThat(new LOCIssue(Issue.Severity.LOW, "Test", 12).toString()).isEqualTo("LOCIssue(super=Issue(severity=LOW, description=Test), loc=12)");
        assertThat(new SpotBugsIssue(Issue.Severity.LOW, "Test").toString()).isEqualTo("SpotBugsIssue(super=Issue(severity=LOW, description=Test))");
    }
}