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
