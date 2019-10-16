package de.czoeller.depanalyzer.ui;

import de.czoeller.depanalyzer.metamodel.Analyzers;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Globals {

    private static final ObjectProperty<Analyzers> selectedAnalyzer = new SimpleObjectProperty<>();

    public static Analyzers getSelectedAnalyzer() {
        final Analyzers selected = selectedAnalyzer.get();
        return selected == null ? Analyzers.METRICS : selected;
    }

    public static ObjectProperty<Analyzers> selectedAnalyzerProperty() {
        return selectedAnalyzer;
    }
}
