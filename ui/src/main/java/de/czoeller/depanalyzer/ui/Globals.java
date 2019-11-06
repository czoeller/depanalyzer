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
package de.czoeller.depanalyzer.ui;

import de.czoeller.depanalyzer.metamodel.Analyzers;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Globals {

    private static final ObjectProperty<Analyzers> selectedAnalyzer = new SimpleObjectProperty<>();
    private static final StringProperty analyzedProjectProperty = new SimpleStringProperty();

    public static Analyzers getSelectedAnalyzer() {
        final Analyzers selected = selectedAnalyzer.get();
        return selected == null ? Analyzers.METRICS : selected;
    }

    public static ObjectProperty<Analyzers> selectedAnalyzerProperty() {
        return selectedAnalyzer;
    }

    public static StringProperty analyzedProjectProperty() {
        return analyzedProjectProperty;
    }

}
