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
package de.czoeller.depanalyzer.analyzer.util;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class CompletableFutureCollector {

    private CompletableFutureCollector(){
    }

    /**
     * Transforms a <pre>{@code List<CompletableFuture<T>>}</pre> into a <pre>{@code CompletableFuture<List<T>>}</pre>
     * @param <X> the computed result type
     * @param <T> some CompletableFuture
     * @return a CompletableFuture of <pre>{@code CompletableFuture<List<T>>}</pre> that is complete when all collected CompletableFutures are complete.
     */
    public static <X, T extends CompletableFuture<X>> Collector<T, ?, CompletableFuture<List<X>>> collectResult(){
        return Collectors.collectingAndThen(Collectors.toList(), joinResult());
    }

    /**
     * Transforms a <pre>{@code List<CompletableFuture<?>>}</pre> into a <pre>{@code CompletableFuture<Void>}</pre>
     * Use this function if you are not interested in the collected results or the collected CompletableFutures are of
     * type Void.
     * @param <T> some CompletableFuture
     * @return a <pre>{@code CompletableFuture<Void>}</pre> that is complete when all collected CompletableFutures are complete.
     */
    public static <T extends CompletableFuture<?>> Collector<T, ?, CompletableFuture<Void>> allComplete(){
        return Collectors.collectingAndThen(Collectors.toList(), CompletableFutureCollector::allOf);
    }

    private static <X, T extends CompletableFuture<X>> Function<List<T>, CompletableFuture<List<X>>> joinResult() {
        return ls-> allOf(ls)
                .thenApply(v -> ls
                        .stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));
    }

    private static <T extends CompletableFuture<?>> CompletableFuture<Void> allOf(List<T> ls) {
        return CompletableFuture.allOf(ls.toArray(new CompletableFuture[ls.size()]));
    }

}