// 
// Filename : ObjectDisplay.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui.modelView;

import java.awt.BasicStroke;
import java.awt.FlowLayout;
import java.awt.ItemSelectable;
import java.awt.Stroke;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.xtuml.masl.inspector.Preferences;
import org.xtuml.masl.inspector.processInterface.ObjectMetaData;

public class ObjectDisplay extends JPanel {

    private final ItemSelectable selector;
    private ObjectMetaData metaObj;

    private ObjectGraph image = null;

    private final Map<ObjectMetaData, ObjectGraph> imageCache = new HashMap<ObjectMetaData, ObjectGraph>();

    public ObjectDisplay(final ItemSelectable selector) {
        super();
        this.selector = selector;
        this.setLayout(new FlowLayout(FlowLayout.LEFT));

        selector.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(final ItemEvent event) {
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    selectObject();
                } else if (event.getStateChange() == ItemEvent.DESELECTED) {
                    deselectObject();
                }
            }
        });

        selectObject();
    }

    public void deselectObject() {
        if (image != null) {
            image.setVisible(false);
        }
    }

    public void selectObject() {
        if (image != null) {
            image.setVisible(false);
        }
        metaObj = (ObjectMetaData) getDependeeValue();
        if (metaObj != null) {
            image = imageCache.get(metaObj);
            if (image == null) {
                image = new ObjectGraph(metaObj, (JList) selector);
                add(image);
                imageCache.put(metaObj, image);
            }

            image.setVisible(true);
        }
    }

    protected Object getDependeeValue() {
        final Object[] selectedObjects = selector.getSelectedObjects();
        if (selectedObjects != null && selectedObjects.length > 0) {
            return selectedObjects[0];
        } else {
            return null;
        }
    }

    public static void installUI() {
        UIManager.put("SimpleRelationshipUI", "org.xtuml.masl.inspector.gui.modelView.SimpleRelationshipUI");
        UIManager.put("ReflexiveRelationshipUI", "org.xtuml.masl.inspector.gui.modelView.ReflexiveRelationshipUI");
        UIManager.put("AssociativeRelationshipUI", "org.xtuml.masl.inspector.gui.modelView.AssociativeRelationshipUI");
        UIManager.put("SuperSubtypeUI", "org.xtuml.masl.inspector.gui.modelView.SuperSubtypeUI");

        if (Preferences.getModellingMode() == Preferences.ModellingMode.SM) {
            UIManager.put("Relationship.1", new RelationshipLineHeadUIResource(ArrowHeads.SINGLE_V, ""));
            UIManager.put("Relationship.1c", new RelationshipLineHeadUIResource(ArrowHeads.SINGLE_V, "c"));
            UIManager.put("Relationship.M", new RelationshipLineHeadUIResource(ArrowHeads.DOUBLE_V, ""));
            UIManager.put("Relationship.Mc", new RelationshipLineHeadUIResource(ArrowHeads.DOUBLE_V, "c"));
            UIManager.put("Relationship.unknown", new RelationshipLineHeadUIResource(ArrowHeads.NO_ARROW, ""));

            final Stroke solidLine = new BasicStroke();
            UIManager.put("Associative.1", new AssociativeLineUIResource(ArrowHeads.SINGLE_V, solidLine));
            UIManager.put("Associative.M", new AssociativeLineUIResource(ArrowHeads.DOUBLE_V, solidLine));

            UIManager.put("SuperSub.super", new SuperSubLineUIResource(ArrowHeads.CROSS_LINE));
            UIManager.put("SuperSub.sub", new SuperSubLineUIResource(ArrowHeads.NO_ARROW));
        } else {
            UIManager.put("Relationship.1", new RelationshipLineHeadUIResource(ArrowHeads.NO_ARROW, "1"));
            UIManager.put("Relationship.1c", new RelationshipLineHeadUIResource(ArrowHeads.NO_ARROW, "0..1"));
            UIManager.put("Relationship.M", new RelationshipLineHeadUIResource(ArrowHeads.NO_ARROW, "1..*"));
            UIManager.put("Relationship.Mc", new RelationshipLineHeadUIResource(ArrowHeads.NO_ARROW, "*"));
            UIManager.put("Relationship.unknown", new RelationshipLineHeadUIResource(ArrowHeads.NO_ARROW, ""));

            final Stroke dottedLine = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
                    new float[] { 3, 3 }, 0);
            UIManager.put("Associative.1", new AssociativeLineUIResource(ArrowHeads.NO_ARROW, dottedLine));
            UIManager.put("Associative.M", new AssociativeLineUIResource(ArrowHeads.NO_ARROW, dottedLine));

            UIManager.put("SuperSub.super", new SuperSubLineUIResource(ArrowHeads.EMPTY_TRIANGLE));
            UIManager.put("SuperSub.sub", new SuperSubLineUIResource(ArrowHeads.NO_ARROW));
        }
    }

}
