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
 *
 * Modifications copyright (C) 2019 czoeller
 * - construct exception based on aether dependency resolver
 */
package de.czoeller.depanalyzer.core.dependency;

import org.apache.maven.project.DependencyResolutionException;
import org.eclipse.aether.RepositoryException;

/**
 * Wrapper for {@link DependencyResolutionException}.
 */
public final class DependencyGraphException extends RuntimeException {

    private static final long serialVersionUID = 3167396359488785529L;

    public DependencyGraphException(RepositoryException cause) {
        super(cause);
    }
}
