package de.czoeller.depanalyzer.metamodel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class Issue {
    private final Severity severity;
    private final String description;

    public enum Severity {
        LOW,
        MEDIUM,
        HIGH;

        public static Severity map(int priority) {
            if(priority < Severity.values().length) {
                return Severity.values()[priority];
            }
            return HIGH;
        }
    }
}
