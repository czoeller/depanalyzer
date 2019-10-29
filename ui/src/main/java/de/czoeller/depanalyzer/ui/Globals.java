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
