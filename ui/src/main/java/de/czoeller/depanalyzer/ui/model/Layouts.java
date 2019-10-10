package de.czoeller.depanalyzer.ui.model;

public enum Layouts {
    KK("Kamada Kawai"),
    CIRCLE("Circle"),
    SELF_ORGANIZING_MAP("Self Organizing Map"),
    FR("Fruchterman Reingold (FR)"),
    FR_BH_VISITOR("FR with Barnes-Hut"),
    SPRING("Spring"),
    SPRING_BH_VISITOR("Spring with Barnes-Hut");

    Layouts(String name) {
        this.name = name;
    }

    private final String name;

    @Override
    public String toString() {
        return name;
    }
}