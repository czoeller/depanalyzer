/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package de.czoeller.depanalyzer.ui.core;

import com.timboudreau.vl.jung.JungConnectionWidget;
import com.timboudreau.vl.jung.JungScene;
import de.czoeller.depanalyzer.core.dependency.DependencyNode;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.ObservableGraph;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.visual.action.*;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.layout.SceneLayout;
import org.netbeans.api.visual.model.ObjectState;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.RequestProcessor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.*;

import static org.openide.util.NbBundle.Messages;

public class DependencyGraphScene extends JungScene<ArtifactGraphNode, ArtifactGraphEdge> implements Runnable {

    private static final RequestProcessor RP = new RequestProcessor(DependencyGraphScene.class);
    private LayerWidget mainLayer;
    private LayerWidget connectionLayer;
    private ArtifactGraphNode rootNode;
    private final AllActionsProvider allActionsP = new AllActionsProvider();

    private final WidgetAction moveAction;
    private WidgetAction popupMenuAction = ActionFactory.createPopupMenuAction(allActionsP);
    private WidgetAction zoomAction = ActionFactory.createMouseCenteredZoomAction(1.1);
    private WidgetAction panAction = ActionFactory.createPanAction();
    private WidgetAction editAction = ActionFactory.createEditAction(allActionsP);
    WidgetAction hoverAction = ActionFactory.createHoverAction(new HoverController());

    Action sceneZoomToFitAction = new SceneZoomToFitAction();
    Action highlitedZoomToFitAction = new HighlightedZoomToFitAction();

    private int maxDepth = 0;
    private FitToViewLayout fitViewL;

    private static Set<ArtifactGraphNode> EMPTY_SELECTION = new HashSet<>();
    private Forest<ArtifactGraphNode, ArtifactGraphEdge> forest;

    public DependencyGraphScene(DelegateForest<ArtifactGraphNode, ArtifactGraphEdge> forest) {
        this(new ObservableGraph<>(forest), forest);
    }

    public DependencyGraphScene(ObservableGraph<ArtifactGraphNode, ArtifactGraphEdge> graph, Forest<ArtifactGraphNode, ArtifactGraphEdge> forest) {
        super(graph, new FRLayout<>(forest));
        layoutModel = new DefaultComboBoxModel<>();
        layoutModel.addElement(layout());
        // These rarely work but look nice when they do
//        layoutModel.addElement(new BalloonLayout<>(forest));
//        layoutModel.addElement(new RadialTreeLayout<>(forest));
        layoutModel.addElement(new CircleLayout<>(graph));
        layoutModel.addElement(new FRLayout<>(graph));
        layoutModel.addElement(new FRLayout2<>(graph));
        layoutModel.addElement(new KKLayout<>(graph));
        TreeLayout<ArtifactGraphNode, ArtifactGraphEdge> treeLayout = new TreeLayout<>(forest, 200, 90);
        layoutModel.addElement(treeLayout);
        layoutModel.addElement(new ISOMLayout<>(graph));
        layoutModel.addElement(new SpringLayout<>(graph));
        layoutModel.addElement(new SpringLayout2<>(graph));
        layoutModel.setSelectedItem(layout());

        moveAction = ActionFactory.createMoveAction(null, new MP());

        this.forest = forest;
        mainLayer = new LayerWidget(this);
        addChild(mainLayer);
        connectionLayer = new LayerWidget(this);
        addChild(connectionLayer);
        //getActions().addAction(this.createObjectHoverAction());
        getActions().addAction(hoverAction);
        getActions().addAction(ActionFactory.createSelectAction(allActionsP));
        getActions().addAction(zoomAction);
        getActions().addAction(panAction);
        getActions().addAction(editAction);
        getActions().addAction(popupMenuAction);
        
        getActions().addAction(new ScrollWheelZoomAction());
    }

    /** Saves fix changes to the pom file, posted to RequestProcessor */
    @Override
    public void run() {
        /*try {
            tc.saveChanges(model);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            //TODO error reporting on wrong model save
        }*/
    }

    private class ScrollWheelZoomAction extends WidgetAction.Adapter {
        @Override
        public State mouseWheelMoved(Widget widget, WidgetMouseWheelEvent event) {
            double zoom = getZoomFactor();
            int units = event.getUnitsToScroll();
            double amt = (double) units * 0.025D;
            zoom = Math.max(0.1D, zoom + amt);
            setZoomFactor(zoom);
            repaint();
            return State.CONSUMED;
        }
    }

    private class MP implements MoveProvider {
        private final MoveProvider delegate = createMoveProvider();

        @Override
        public void movementStarted(Widget widget) {
            allActionsP.movementStarted(widget);
            delegate.movementStarted(widget);
        }

        @Override
        public void movementFinished(Widget widget) {
            allActionsP.movementFinished(widget);
            delegate.movementFinished(widget);
        }

        @Override
        public Point getOriginalLocation(Widget widget) {
            allActionsP.getOriginalLocation(widget);
            return delegate.getOriginalLocation(widget);
        }

        @Override
        public void setNewLocation(Widget widget, Point location) {
            allActionsP.setNewLocation(widget, location);
            delegate.setNewLocation(widget, location);
        }
    }

    public void cleanLayout(JScrollPane panel) {
        layout.reset();
        super.performLayout(true);
    }
    
    private final DefaultComboBoxModel<Layout<ArtifactGraphNode, ArtifactGraphEdge>> layoutModel;
    
    public ComboBoxModel<Layout<ArtifactGraphNode, ArtifactGraphEdge>> getLayoutModel() {
        return layoutModel;
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

    public ArtifactGraphNode getRootGraphNode() {
        return rootNode;
    }

    int getMaxNodeDepth() {
        return maxDepth;
    }

    boolean isAnimated () {
        return true;
    }

    @CheckForNull ArtifactGraphNode getGraphNodeRepresentant(DependencyNode node) {
        for (ArtifactGraphNode grnode : getNodes()) {
            if (grnode.represents(node)) {
                return grnode;
            }
        }
        return null;
    }

    @Override
    protected Widget attachNodeWidget(ArtifactGraphNode node) {
        if (rootNode == null) {
            rootNode = node;
        }
        if (node.getPrimaryLevel() > maxDepth) {
            maxDepth = node.getPrimaryLevel();
        }
        ArtifactWidget root = new ArtifactWidget(this, node);
        mainLayer.addChild(root);
        node.setWidget(root);
        root.setOpaque(true);

        root.getActions().addAction(this.createObjectHoverAction());
        root.getActions().addAction(this.createSelectAction());
        root.getActions().addAction(moveAction);
        root.getActions().addAction(editAction);
        root.getActions().addAction(popupMenuAction);

        return root;
    }

    @Override
    protected Widget attachEdgeWidget(ArtifactGraphEdge edge) {
//        EdgeWidget connectionWidget = new EdgeWidget(this, edge);
//        connectionLayer.addChild(connectionWidget);
//        return connectionWidget;
        JungConnectionWidget<ArtifactGraphNode, ArtifactGraphEdge> w = new EdgeWidget(this, edge);
        connectionLayer.addChild(w);
        return w;
    }

    @Override
    protected void attachEdgeSourceAnchor(ArtifactGraphEdge edge,
                                          ArtifactGraphNode oldsource,
                                          ArtifactGraphNode source) {
        Widget w = findWidget(edge);
        if (w instanceof ConnectionWidget) {
            ((ConnectionWidget) w).setSourceAnchor(AnchorFactory.createRectangularAnchor(findWidget(source)));
        }
    }

    @Override
    protected void attachEdgeTargetAnchor(ArtifactGraphEdge edge,
                                          ArtifactGraphNode oldtarget,
                                          ArtifactGraphNode target) {
        ArtifactWidget wid = (ArtifactWidget)findWidget(target);
        Widget w = findWidget(edge);
        if (w instanceof ConnectionWidget) {
            ((ConnectionWidget) w).setTargetAnchor(AnchorFactory.createRectangularAnchor(wid));
        }
    }

    void highlightRelated (ArtifactGraphNode node) {
        List<ArtifactGraphNode> importantNodes = new ArrayList<ArtifactGraphNode>();
        List<ArtifactGraphEdge> otherPathsEdges = new ArrayList<ArtifactGraphEdge>();
        List<ArtifactGraphEdge> primaryPathEdges = new ArrayList<ArtifactGraphEdge>();
        List<ArtifactGraphNode> childrenNodes = new ArrayList<ArtifactGraphNode>();
        List<ArtifactGraphEdge> childrenEdges = new ArrayList<ArtifactGraphEdge>();

        importantNodes.add(node);

        @SuppressWarnings("unchecked") List<DependencyNode> children = (List<DependencyNode>)node.getArtifact().getChildren();
        for (DependencyNode n : children) {
            ArtifactGraphNode child = getGraphNodeRepresentant(n);
            if (child != null) {
                childrenNodes.add(child);
            }
        }

        childrenEdges.addAll(findNodeEdges(node, true, false));

        // primary path
        addPathToRoot(node, primaryPathEdges, importantNodes);

        // other important paths
        //List<DependencyNode> representants = new ArrayList<DependencyNode>(node.getDuplicatesOrConflicts());
        //for (DependencyNode curRep : representants) {
        //    addPathToRoot(curRep, curRep.getParent(), otherPathsEdges, importantNodes);
        //}

        EdgeWidget ew;
        for (ArtifactGraphEdge curE : getEdges()) {
            ew = (EdgeWidget) findWidget(curE);
            if (primaryPathEdges.contains(curE)) {
                ew.setState(EdgeWidget.HIGHLIGHTED_PRIMARY);
            //} else if (otherPathsEdges.contains(curE)) {
            //    ew.setState(EdgeWidget.HIGHLIGHTED);
            } else if (childrenEdges.contains(curE)) {
                ew.setState(EdgeWidget.GRAYED);
            } else {
                ew.setState(EdgeWidget.DISABLED);
            }
        }

        ArtifactWidget aw;
        for (ArtifactGraphNode curN : getNodes()) {
            aw = (ArtifactWidget) findWidget(curN);
            if (importantNodes.contains(curN)) {
                aw.setPaintState(EdgeWidget.REGULAR);
                aw.setReadable(true);
            } else if (childrenNodes.contains(curN)) {
                aw.setPaintState(EdgeWidget.REGULAR);
                aw.setReadable(true);
            } else {
                aw.setPaintState(EdgeWidget.DISABLED);
                aw.setReadable(false);
            }
        }

    }

    private void addPathToRoot(ArtifactGraphNode node, List<ArtifactGraphEdge> edges, List<ArtifactGraphNode> nodes) {
        DependencyNode parentDepN = node.getArtifactParent(graph());
        addPathToRoot(node.getArtifact(), parentDepN, edges, nodes);
    }


    private void addPathToRoot(DependencyNode depN, DependencyNode parentDepN, List<ArtifactGraphEdge> edges, List<ArtifactGraphNode> nodes) {
        ArtifactGraphNode grNode;
        while (parentDepN != null) {
            grNode = getGraphNodeRepresentant(parentDepN);
            if (grNode == null) {
                return;
            }
            ArtifactGraphNode targetNode = getGraphNodeRepresentant(depN);
            if (targetNode == null) {
                return;
            }
            edges.addAll(findEdgesBetween(grNode, targetNode));
            nodes.add(grNode);
            depN = parentDepN;
            parentDepN = grNode.getArtifactParent(graph());
        }
    }

    private class AllActionsProvider implements PopupMenuProvider,
            MoveProvider, EditProvider, SelectProvider {

        private Point moveStart;

/*        public void select(Widget wid, Point arg1, boolean arg2) {
            System.out.println("select called...");
            Widget w = wid;
            while (w != null) {
                ArtifactGraphNode node = (ArtifactGraphNode)findObject(w);
                if (node != null) {
                    setSelectedObjects(Collections.singleton(node));
                    System.out.println("selected object: " + node.getArtifact().getArtifact().getArtifactId());
                    highlightRelated(node);
                    ((ArtifactWidget)w).setSelected(true);
                    return;
                }
                w = w.getParentWidget();
            }
        }*/

        @Messages({
            "ACT_Show_Graph=Show Dependency Graph",
            "ACT_Export_As_Image=Export As Image",
            "ACT_Export_As_Image_Title=Export Dependency Graph As PNG"
        })
        @Override
        public JPopupMenu getPopupMenu(Widget widget, Point localLocation) {
            JPopupMenu popupMenu = new JPopupMenu();
            if (widget == DependencyGraphScene.this) {
                popupMenu.add(sceneZoomToFitAction);

                popupMenu.add(new AbstractAction(Bundle.ACT_Export_As_Image()) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //TODO: Add filechooser
                        /*File file = new FileChooserBuilder("DependencyGraphScene-ExportDir").setTitle(Bundle.ACT_Export_As_Image_Title())
                                                                                            .setAcceptAllFileFilterUsed(false).addFileFilter(new FileNameExtensionFilter("PNG file", "png")).showSaveDialog();
                        if (file != null) {
                            try {
                                DependencyGraphScene theScene = DependencyGraphScene.this;
                                SceneExporter.createImage(theScene, file, SceneExporter.ImageType.PNG, SceneExporter.ZoomType.CURRENT_ZOOM_LEVEL, false, false, -1, -1, -1);
                            } catch (IOException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }*/
                    }
                });
            } else {
                ArtifactGraphNode node = (ArtifactGraphNode) findObject(widget);
                if (isEditable()) {
                    boolean addSeparator = false;
                    if (node.getPrimaryLevel() > 1) {
                        popupMenu.add(new JSeparator());
                        addSeparator = true;
                    }
                    if (addSeparator) {
                        popupMenu.add(new JSeparator());
                    }
                }
                popupMenu.add(highlitedZoomToFitAction);
                    //TODO: create artifacts detail view
                //if (!node.isRoot()) {
                    popupMenu.add(new JMenuItem("TODO: Detail"));
                    //Action a = CommonArtifactActions.createViewArtifactDetails(node.getArtifact().getArtifact(), project.getRemoteArtifactRepositories());
                    //a.putValue("PANEL_HINT", ArtifactViewer.HINT_GRAPH); //NOI18N
                    //a.putValue(Action.NAME, Bundle.ACT_Show_Graph());
                    //popupMenu.add(a);
                //}
            }
            return popupMenu;
        }

        @Override
        public void movementStarted(Widget widget) {
            widget.bringToFront();
            moveStart = widget.getLocation();
        }
        @Override
        public void movementFinished(Widget widget) {
            // little hack to call highlightRelated on mouse click while leaving
            // normal move behaviour on real dragging
            Point moveEnd = widget.getLocation();
            if (moveStart.distance(moveEnd) < 5) {
                Object obj = DependencyGraphScene.this.findObject(widget);
                if (obj instanceof ArtifactGraphNode) {
                    DependencyGraphScene.this.highlightRelated((ArtifactGraphNode)obj);
                }
            }
        }
        @Override
        public Point getOriginalLocation(Widget widget) {
            return widget.getPreferredLocation ();
        }
        @Override
        public void setNewLocation(Widget widget, Point location) {
            widget.setPreferredLocation (location);
        }

        @Override
        public void edit(Widget widget) {
            if (DependencyGraphScene.this == widget) {
                sceneZoomToFitAction.actionPerformed(null);
            } else {
                highlitedZoomToFitAction.actionPerformed(null);
            }
        }

        @Override
        public boolean isAimingAllowed(Widget widget, Point localLocation, boolean invertSelection) {
            return false;
        }

        @Override
        public boolean isSelectionAllowed(Widget widget, Point localLocation, boolean invertSelection) {
            return true;
        }

        @Override
        public void select(Widget widget, Point localLocation, boolean invertSelection) {
            setSelectedObjects(EMPTY_SELECTION);
            //TODO: Update UI: DependencyGraphScene.this.tc.depthHighlight();
        }
    }

    @Override
    protected void notifyStateChanged(ObjectState previousState, ObjectState state) {
        super.notifyStateChanged(previousState, state);

        if (!previousState.isSelected() && state.isSelected()) {
            //TODO: Update UI: tc.depthHighlight();
        }
    }

    private FitToViewLayout getFitToViewLayout () {
        if (fitViewL == null) {
            fitViewL = new FitToViewLayout(this);
        }
        return fitViewL;
    }

    private static class FitToViewLayout extends SceneLayout {

        private List<? extends Widget> widgets = null;
        private DependencyGraphScene depScene;

        FitToViewLayout(DependencyGraphScene scene) {
            super(scene);
            this.depScene = scene;
        }

        /** Sets list of widgets to fit or null for fitting whole scene */
        public void setWidgetsToFit (List<? extends Widget> widgets) {
            this.widgets = widgets;
        }

        @Override
        protected void performLayout() {
            Rectangle rectangle = null;
            List<? extends Widget> toFit = widgets != null ? widgets : depScene.getChildren();
            if (toFit == null) {
                return;
            }

            for (Widget widget : toFit) {
                Rectangle bounds = widget.getBounds();
                if (bounds == null) {
                    continue;
                }
                if (rectangle == null) {
                    rectangle = widget.convertLocalToScene(bounds);
                } else {
                    rectangle = rectangle.union(widget.convertLocalToScene(bounds));
                }
            }
            // margin around
            if (widgets == null) {
                rectangle.grow(5, 5);
            } else {
                rectangle.grow(25, 25);
            }
            Dimension dim = rectangle.getSize();
            Dimension viewDim = depScene.layout().getSize();
            double zf = Math.min ((double) viewDim.width / dim.width, (double) viewDim.height / dim.height);
            if (depScene.isAnimated()) {
                if (widgets == null) {
                    depScene.getSceneAnimator().animateZoomFactor(zf);
                } else {
                    CenteredZoomAnimator cza = new CenteredZoomAnimator(depScene.getSceneAnimator());
                    cza.setZoomFactor(zf, new Point((int) rectangle.getCenterX(), (int)rectangle.getCenterY()));
                }
            } else {
                depScene.setZoomFactor (zf);
            }
        }
    }

    private class SceneZoomToFitAction extends AbstractAction {

        @Messages("ACT_ZoomToFit=Zoom To Fit")
        SceneZoomToFitAction() {
            putValue(NAME, Bundle.ACT_ZoomToFit());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            FitToViewLayout ftvl = DependencyGraphScene.this.getFitToViewLayout();
            ftvl.setWidgetsToFit(null);
            ftvl.invokeLayout();
        }
    };

    private class HighlightedZoomToFitAction extends AbstractAction {

        HighlightedZoomToFitAction() {
            putValue(NAME, Bundle.ACT_ZoomToFit());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Collection<ArtifactGraphNode> grNodes = DependencyGraphScene.this.getNodes();
            List<ArtifactWidget> aws = new ArrayList<ArtifactWidget>();
            ArtifactWidget aw = null;
            int paintState;
            for (ArtifactGraphNode grNode : grNodes) {
                aw = grNode.getWidget();
                paintState = aw.getPaintState();
                if (paintState != EdgeWidget.DISABLED && paintState != EdgeWidget.GRAYED) {
                    aws.add(aw);
                }
            }

            FitToViewLayout ftvl = DependencyGraphScene.this.getFitToViewLayout();
            ftvl.setWidgetsToFit(aws);
            ftvl.invokeLayout();
        }
    };

    boolean isEditable () {
        return false;
    }

    private static class HoverController implements TwoStateHoverProvider {

        @Override
        public void unsetHovering(Widget widget) {
            ArtifactWidget aw = findArtifactW(widget);
            if (widget != null) {
                aw.bulbUnhovered();
            }
        }

        @Override
        public void setHovering(Widget widget) {
            ArtifactWidget aw = findArtifactW(widget);
            if (aw != null) {
                aw.bulbHovered();
            }
        }

        private ArtifactWidget findArtifactW (Widget w) {
            while (w != null && !(w instanceof ArtifactWidget)) {
                w = w.getParentWidget();
            }
            return (ArtifactWidget)w;
        }

    }

}
