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
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(of = {"edge"})
public class GraphDependencyEdge {
    private String edge;
    private int level = 0;
    private DependencyNode source;
    private DependencyNode target;
    private boolean primary;

    /** Creates a new instance of ArtifactGraphEdge */
    public GraphDependencyEdge(DependencyNode source, DependencyNode target) {
        edge = source.getArtifact().getArtifactId() + "--" + target.getArtifact().getArtifactId(); //NOI18N
        this.target = target;
        this.source = source;
    }

    @Override
    public String toString() {
        return edge;
    }

    public void setLevel(int lvl) {
        level = lvl;
    }

    public int getLevel() {
        return level;
    }

    public void setPrimaryPath(boolean primary) {
        this.primary = primary;
    }

    public boolean isPrimary() {
        return primary;
    }

    public DependencyNode getSource() {
        return source;
    }

    public DependencyNode getTarget() {
        return target;
    }
}
