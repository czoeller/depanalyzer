package de.czoeller.depanalyzer.ui.components;

import de.czoeller.depanalyzer.core.dependency.DependencyNode;
import de.czoeller.depanalyzer.ui.core.DependencyGraphScene;
import de.czoeller.depanalyzer.ui.core.ScopesVisitor;
import de.czoeller.depanalyzer.ui.core.SearchVisitor;
import org.openide.util.NbBundle;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static de.czoeller.depanalyzer.ui.components.Bundle.*;
import static org.eclipse.aether.util.artifact.JavaScopes.*;


public class TopComponent extends JComponent {

    private final DependencyGraphScene scene;
    private JComboBox<List<String>> comScopes = new JComboBox<>();
    private JTextField txtFind = new JTextField();
    private Timer timer = new Timer(500, $ -> checkFindValue());

    @NbBundle.Messages({"LBL_Scope_All=All", "LBL_Scope_Compile=Compile", "LBL_Scope_Runtime=Runtime", "LBL_Scope_Test=Test"})
    public TopComponent(DependencyGraphScene scene) {
        super();
        this.scene = scene;

        setPreferredSize(new Dimension(600, 300));

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
        comScopes.addActionListener(e -> {
            if (scene != null) {
                List<String> selected = (List<String>) comScopes.getSelectedItem();
                ScopesVisitor vis = new ScopesVisitor(scene, selected);
                scene.getRootGraphNode().getArtifact().accept(vis);
                scene.validate();
                scene.repaint();
                revalidate();
                repaint();
            }
        });
    }

    public void initComponents() {
        setLayout(new BorderLayout());
        JPanel jPanel1 = new JPanel();
        jPanel1.setLayout(new FlowLayout(FlowLayout.LEFT));

        txtFind.setPreferredSize(new Dimension(200, 20));
        JLabel txtFindLabel = new JLabel("Search:");
        txtFindLabel.setLabelFor(txtFind);

        JLabel comScopesLabel = new JLabel("Scopes: ");
        comScopesLabel.setLabelFor(comScopes);

        jPanel1.add(txtFindLabel);
        jPanel1.add(txtFind);
        jPanel1.add(comScopesLabel);
        jPanel1.add(comScopes);
        add(jPanel1, BorderLayout.NORTH);
    }

    private void checkFindValue() {
        String val = txtFind.getText().trim();
        if ("".equals(val)) { //NOI18N
            val = null;
        }
        SearchVisitor visitor = new SearchVisitor(scene);
        visitor.setSearchString(val);
        DependencyNode node = scene.getRootGraphNode().getArtifact();
        node.accept(visitor);
        scene.validate();
        scene.repaint();
        revalidate();
        repaint();
    }
}
