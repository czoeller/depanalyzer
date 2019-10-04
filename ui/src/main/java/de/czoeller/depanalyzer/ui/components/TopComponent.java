package de.czoeller.depanalyzer.ui.components;

import de.czoeller.depanalyzer.ui.model.UIModel;
import de.czoeller.depanalyzer.ui.model.UIModel.Layouts;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.openide.util.NbBundle;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static de.czoeller.depanalyzer.ui.components.Bundle.*;
import static org.eclipse.aether.util.artifact.JavaScopes.*;


public class TopComponent extends JComponent {

    private final UIModel model;
    private JComboBox<List<String>> comScopes = new JComboBox<>();
    private JTextField txtFind = new JTextField();
    private Timer timer = new Timer(500, $ -> checkFindValue());

    @NbBundle.Messages({"LBL_Scope_All=All", "LBL_Scope_Compile=Compile", "LBL_Scope_Runtime=Runtime", "LBL_Scope_Test=Test"})
    public TopComponent(UIModel model) {
        super();
        this.model = model;

        setPreferredSize(new Dimension(600, 100));
        initComponents();

        timer.setDelay(500);
        timer.setRepeats(false);
        txtFind.getDocument().addDocumentListener(new DocumentListener() {
                   @Override
                   public void insertUpdate(DocumentEvent arg0) {
                       timer.restart();
                   }

                   @Override
                   public void removeUpdate(DocumentEvent arg0) {
                       timer.restart();
                   }

                   @Override
                   public void changedUpdate(DocumentEvent arg0) {
                       timer.restart();
                   }
               });
        comScopes.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                int scopesSize = ((List<?>) value).size();
                String msg;
                if (scopesSize == 0) {
                    msg = LBL_Scope_All();
                } else if (scopesSize == 2) {
                    msg = LBL_Scope_Compile();
                } else if (scopesSize == 3) {
                    msg = LBL_Scope_Runtime();
                } else {
                    msg = LBL_Scope_Test();
                }
                return super.getListCellRendererComponent(list, msg, index, isSelected, cellHasFocus);
            }
        });
        DefaultComboBoxModel<List<String>> mdl = new DefaultComboBoxModel<>();
        mdl.addElement(Collections.emptyList());
        mdl.addElement(Arrays.asList(PROVIDED, COMPILE));
        mdl.addElement(Arrays.asList(PROVIDED, COMPILE, RUNTIME));
        mdl.addElement(Arrays.asList(PROVIDED, COMPILE, RUNTIME, TEST));
        comScopes.setModel(mdl);
        //comScopes.addActionListener(e -> {
        //    if (scene != null) {
        //        List<String> selected = (List<String>) comScopes.getSelectedItem();
        //        ScopesVisitor vis = new ScopesVisitor(scene, selected);
        //        scene.getRootGraphNode().getArtifact().accept(vis);
        //        scene.validate();
        //        scene.repaint();
        //        revalidate();
        //        repaint();
        //    }
        //});
    }

    public void initComponents() {
        setLayout(new BorderLayout());
        JPanel jPanel1 = new JPanel();
        jPanel1.setLayout(new FlowLayout(FlowLayout.LEFT));

        txtFind.setPreferredSize(new Dimension(150, 20));
        AutoCompleteDecorator.decorate(txtFind, model.getAvailableNodeNames(), false);
        JLabel txtFindLabel = new JLabel("Search:");
        txtFindLabel.setLabelFor(txtFind);

        JLabel comScopesLabel = new JLabel("Scopes: ");
        comScopesLabel.setLabelFor(comScopes);

        jPanel1.add(txtFindLabel);
        jPanel1.add(txtFind);
        jPanel1.add(comScopesLabel);
        jPanel1.add(comScopes);

        final JComboBox<Layouts> layouts = new JComboBox<>(model.getLayoutModel());
        AutoCompleteDecorator.decorate(layouts);
        layouts.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
        jPanel1.add(new JLabel(" Layout Type"));
        jPanel1.add(layouts);
        layouts.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                Layouts layout = layouts.getItemAt(layouts.getSelectedIndex());
                // These two layouts implement IterativeContext, but they do
                // not evolve toward anything, they just randomly rearrange
                // themselves.  So disable animation for these.
                if (layout == Layouts.SELF_ORGANIZING_MAP || layout == Layouts.DIRECTED_ACYCLIC_GRAPH) {
                    //TODO: add animate checkbox checkbox.setSelected(false);
                }
                model.setSelectedLayout(layout);
            }
        });

        add(jPanel1, BorderLayout.NORTH);
    }

    private void checkFindValue() {
        //String val = txtFind.getText().trim();
        //if ("".equals(val)) { //NOI18N
        //    val = null;
        //}
        //SearchVisitor visitor = new SearchVisitor(scene);
        //visitor.setSearchString(val);
        //DependencyNode node = scene.getRootGraphNode().getArtifact();
        //node.accept(visitor);
        //scene.validate();
        //scene.repaint();
        //revalidate();
        //repaint();
    }
}
