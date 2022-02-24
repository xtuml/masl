// 
// Filename : ObjectGraph.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui.modelView;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.xtuml.masl.inspector.processInterface.DomainMetaData;
import org.xtuml.masl.inspector.processInterface.ObjectMetaData;
import org.xtuml.masl.inspector.processInterface.RelationshipMetaData;
import org.xtuml.masl.inspector.processInterface.SuperSubtypeMetaData;

public class ObjectGraph extends JPanel {

    public ObjectGraph(final ObjectMetaData metaObj, final JList selector) {
        super();

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        final ObjectButton sourceButton = new ObjectButton(metaObj);
        sourceButton.setEnabled(false);
        add(sourceButton);

        final JPanel relatedPanel = new JPanel();
        relatedPanel.setLayout(new BoxLayout(relatedPanel, BoxLayout.Y_AXIS));
        add(relatedPanel);

        final DomainMetaData metaDomain = metaObj.getDomain();

        for (final RelationshipMetaData rel : metaDomain.getRelationships()) {
            if (metaObj.equals(rel.getLeftObject()) || metaObj.equals(rel.getRightObject())
                    || metaObj.equals(rel.getAssocObject())) {
                final FlowLayout l = new FlowLayout(FlowLayout.LEFT);
                l.setHgap(0);
                l.setVgap(10);
                final JPanel p = new JPanel(l);
                relatedPanel.add(p);
                final RelationshipArrow arrow = new RelationshipArrow(metaObj, rel);
                p.add(arrow);
                final ObjectButton[] objectButtons = arrow.getObjectButtons();
                for (final ObjectButton objectButton : objectButtons) {
                    objectButton.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            selector.setSelectedValue(((ObjectButton) e.getSource()).getMetaObject(), true);
                        }
                    });
                }
            }
        }
        for (final SuperSubtypeMetaData ss : metaDomain.getSuperSubtypes()) {
            if (metaObj.equals(ss.getSupertype()) || Arrays.asList(ss.getSubtypes()).contains(metaObj)) {
                final FlowLayout l = new FlowLayout(FlowLayout.LEFT);
                l.setHgap(0);
                l.setVgap(10);
                final JPanel p = new JPanel(l);
                relatedPanel.add(p);
                final SuperSubtypeArrow arrow = new SuperSubtypeArrow(metaObj, ss);
                p.add(arrow);
                final ObjectButton[] objectButtons = arrow.getObjectButtons();
                for (final ObjectButton objectButton : objectButtons) {
                    objectButton.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            selector.setSelectedValue(((ObjectButton) e.getSource()).getMetaObject(), true);
                        }
                    });
                }
            }
        }

    }

}
