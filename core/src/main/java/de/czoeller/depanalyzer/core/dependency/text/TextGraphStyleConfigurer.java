/*
 * Copyright (c) 2014 - 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.czoeller.depanalyzer.core.dependency.text;

import de.czoeller.depanalyzer.core.dependency.AbstractGraphStyleConfigurer;
import de.czoeller.depanalyzer.metamodel.DependencyNode;
import de.czoeller.depanalyzer.core.dependency.GraphStyleConfigurer;
import de.czoeller.depanalyzer.core.graph.GraphBuilder;
import de.czoeller.depanalyzer.core.graph.text.TextGraphFormatter;

public class TextGraphStyleConfigurer extends AbstractGraphStyleConfigurer {

    boolean repeatTransitiveDependencies;

    @Override
    public GraphStyleConfigurer repeatTransitiveDependencies(boolean repeatTransitiveDependencies) {
        this.repeatTransitiveDependencies = repeatTransitiveDependencies;
        return this;
    }

    @Override
    public GraphBuilder<DependencyNode> configure(GraphBuilder<DependencyNode> graphBuilder) {
        return graphBuilder
                .useNodeNameRenderer(new TextDependencyNodeNameRenderer(this.showGroupId, this.showArtifactId, this.showTypes, this.showClassifiers, this.showVersionsOnNodes, this.showOptional))
                .useEdgeRenderer(new TextDependencyEdgeRenderer(this.showVersionOnEdges))
                .graphFormatter(new TextGraphFormatter(this.repeatTransitiveDependencies));
    }
}
