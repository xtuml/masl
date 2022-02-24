// 
// Filename : RelationshipArrow.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui.modelView;

import javax.swing.JComponent;
import javax.swing.UIManager;

import org.xtuml.masl.inspector.processInterface.ObjectMetaData;
import org.xtuml.masl.inspector.processInterface.RelationshipMetaData;

class RelationshipArrow extends JComponent {

    private final String uiClassID;

    @Override
    public String getUIClassID() {
        return uiClassID;
    }

    @Override
    public void updateUI() {
        setUI(UIManager.getUI(this));
    }

    private final ObjectMetaData source;
    private final RelationshipMetaData rel;
    private ObjectButton leftObjectButton = null;
    private ObjectButton rightObjectButton = null;
    private ObjectButton assocObjectButton = null;
    private ObjectButton[] objectButtons;

    public RelationshipArrow(final ObjectMetaData source, final RelationshipMetaData rel) {
        this.rel = rel;
        this.source = source;
        final ObjectMetaData leftMeta = rel.getLeftObject();
        final ObjectMetaData rightMeta = rel.getRightObject();
        if (rel.getAssocObject() == null) {
            if (source.equals(rel.getLeftObject()) && source.equals(rel.getRightObject())) {
                // <--+
                // L |
                // <--+
                uiClassID = "ReflexiveRelationshipUI";
                objectButtons = new ObjectButton[] {};
            } else {
                if (source.equals(rel.getLeftObject())) {
                    // L<--->R
                    uiClassID = "SimpleRelationshipUI";
                    rightObjectButton = new ObjectButton(rightMeta);
                    add(rightObjectButton);
                    objectButtons = new ObjectButton[] { rightObjectButton };
                } else {
                    // R<--->L
                    uiClassID = "SimpleRelationshipUI";
                    leftObjectButton = new ObjectButton(leftMeta);
                    add(leftObjectButton);
                    objectButtons = new ObjectButton[] { leftObjectButton };
                }
            }
        } else {
            final ObjectMetaData assocMeta = rel.getAssocObject();
            if (rel.getLeftObject().equals(rel.getRightObject())) {
                if (source.equals(rel.getLeftObject())) {
                    // <--+
                    // L |<--A
                    // <--+
                    //
                    uiClassID = "ReflexiveRelationshipUI";
                    assocObjectButton = new ObjectButton(assocMeta);
                    add(assocObjectButton);
                    objectButtons = new ObjectButton[] { assocObjectButton };
                } else {
                    // +-->
                    // A-->| L
                    // +-->
                    //
                    uiClassID = "AssociativeRelationshipUI";
                    leftObjectButton = new ObjectButton(leftMeta);
                    add(leftObjectButton);
                    objectButtons = new ObjectButton[] { leftObjectButton };
                }
            } else {
                if (source.equals(rel.getLeftObject())) {
                    // A
                    // |
                    // v
                    // L<--->R
                    uiClassID = "SimpleRelationshipUI";
                    rightObjectButton = new ObjectButton(rightMeta);
                    assocObjectButton = new ObjectButton(assocMeta);
                    add(rightObjectButton);
                    add(assocObjectButton);
                    objectButtons = new ObjectButton[] { rightObjectButton, assocObjectButton };
                } else if (source.equals(rel.getRightObject())) {
                    // A
                    // |
                    // v
                    // R<--->L
                    uiClassID = "SimpleRelationshipUI";
                    leftObjectButton = new ObjectButton(leftMeta);
                    assocObjectButton = new ObjectButton(assocMeta);
                    add(leftObjectButton);
                    add(assocObjectButton);
                    objectButtons = new ObjectButton[] { leftObjectButton, assocObjectButton };
                } else {
                    // +-->L
                    // A-->|
                    // +-->R
                    //
                    uiClassID = "AssociativeRelationshipUI";
                    leftObjectButton = new ObjectButton(leftMeta);
                    rightObjectButton = new ObjectButton(rightMeta);
                    add(leftObjectButton);
                    add(rightObjectButton);
                    objectButtons = new ObjectButton[] { leftObjectButton, rightObjectButton };
                }
            }
        }
        setLayout(new PreferredSizeLayout());
        updateUI();
    }

    public RelationshipMetaData getRelationship() {
        return rel;
    }

    public ObjectMetaData getSource() {
        return source;
    }

    public ObjectButton getLeftObjectButton() {
        return leftObjectButton;
    }

    public ObjectButton getRightObjectButton() {
        return rightObjectButton;
    }

    public ObjectButton getAssocObjectButton() {
        return assocObjectButton;
    }

    public ObjectButton[] getObjectButtons() {
        return objectButtons;
    }
}
