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
package de.czoeller.depanalyzer.analyzer;

import com.google.common.base.MoreObjects;
import lombok.Getter;
import lombok.Setter;

public abstract class BaseAnalyzer implements Analyzer {
    @Setter
    @Getter
    private AnalyzerContext context;

    /**
     * Required to obtain instance reflective.
     * TODO: remove reflective instantiation
     */
    public BaseAnalyzer() {}

    public BaseAnalyzer(AnalyzerContext context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("type", getType())
                          .toString();
    }
}
