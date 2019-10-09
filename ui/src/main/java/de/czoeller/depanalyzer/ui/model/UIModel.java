package de.czoeller.depanalyzer.ui.model;

import com.google.common.graph.Network;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UIModel {


    public enum Layouts {
        KK("Kamada Kawai"),
        DIRECTED_ACYCLIC_GRAPH("Directed Acyclic Graph"),
        CIRCLE("Circle"),
        SELF_ORGANIZING_MAP("Self Organizing Map"),
        FR("Fruchterman Reingold (FR)"),
        FR_BH_VISITOR("FR with Barnes-Hut as Visitor"),
        SPRING("Spring"),
        SPRING_BH_VISITOR("Spring with Barnes-Hut as Visitor");

        Layouts(String name) {
            this.name = name;
        }

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    private final List<Consumer<Layouts>> layoutChangeObservers;
    private final List<Consumer<String>> searchChangeObservers;
    private final Layouts layoutDefault;
    private String search;
    private final DefaultComboBoxModel<Layouts> layoutModel;

    @Getter
    private final Network<GraphDependencyNode, GraphDependencyEdge> graph;

    public UIModel(Network<GraphDependencyNode, GraphDependencyEdge> graph, Layouts layoutDefault) {
        this.layoutChangeObservers = new LinkedList<>();
        this.searchChangeObservers = new LinkedList<>();
        this.layoutDefault = layoutDefault;
        this.graph = graph;
        layoutModel = new DefaultComboBoxModel<>(Layouts.values());
        layoutModel.setSelectedItem(layoutDefault);
    }

    public ComboBoxModel<Layouts> getLayoutModel() {
        return layoutModel;
    }

    public Layouts getSelectedLayout() {
        final Layouts layout = (Layouts) layoutModel.getSelectedItem();
        return layout;
    }

    public void setSelectedLayout(Layouts layout) {
        notifyLayoutChangedObservers();
    }

    public List<String> getAvailableNodeNames() {
        //TODO: replace with renderer from graph
        return graph.nodes().stream().map(GraphDependencyNode::getArtifact).flatMap(v -> Stream.of(v.getGroupId(), v.getArtifactId())).map(Object::toString).distinct().collect(Collectors.toList());
    }

    private void notifyLayoutChangedObservers() {
        for (Consumer<Layouts> observer : layoutChangeObservers) {
            try {
                observer.accept(getSelectedLayout());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addLayoutChangedListener(Consumer<Layouts> layoutChangedAction) {
        layoutChangeObservers.add(layoutChangedAction);
    }

    public void setSearch(String search) {
        this.search = search;
        notifySearchChangedObservers();
    }

    private void notifySearchChangedObservers() {
        for (Consumer<String> observer : searchChangeObservers) {
            try {
                observer.accept(search);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addSearchChangedListener(Consumer<String> searchChangedAction) {
        searchChangeObservers.add(searchChangedAction);
    }

    public ListCellRenderer createRenderer() {
        return new R();
    }

    private static class R extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            return super.getListCellRendererComponent(list, value.getClass().getSimpleName(), index, isSelected, cellHasFocus);
        }
    }
}
