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
package de.czoeller.depanalyzer.ui.model;

import de.czoeller.depanalyzer.metamodel.DependencyNode;
import de.czoeller.depanalyzer.metamodel.Issue;
import de.czoeller.depanalyzer.ui.Globals;
import lombok.*;
import org.apache.maven.artifact.Artifact;

import java.util.List;

@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
public class GraphDependencyNode implements HasHeat, HasArtifact {
    private static long idCounter = 0;
    private final DependencyNode dependencyNode;
    @Setter
    @Getter
    private Integer depth = 0;
    @Getter
    private final Long id;

    public GraphDependencyNode(DependencyNode dependencyNode) {
        this.dependencyNode = dependencyNode;
        this.id = idCounter++;
    }

    @Override
    public double getHeat() {
        return this.getIssues().stream().mapToDouble(issue -> issue.getSeverity().ordinal() + 1).sum();
    }

    @Override
    public Artifact getArtifact() {
        return dependencyNode.getArtifact();
    }

    public List<Issue> getIssues() {
        return dependencyNode.getIssues().get(Globals.getSelectedAnalyzer());
    }

    public boolean isProjectNode() {
        return getArtifact().getGroupId().equals(Globals.analyzedProjectProperty().get().split(":")[0]);
    }

    public String getIdentifier() {
        return dependencyNode.getIdentifier();
    }
}
