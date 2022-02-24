// 
// Filename : SimpleRelationshipUI.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui.modelView;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import org.xtuml.masl.inspector.processInterface.RelationshipMetaData;

public class SimpleRelationshipUI extends ComponentUI {

    private String leftRole;
    private String rightRole;
    private final String number;
    private String leftCard;
    private String rightCard;
    private Shape leftHead;
    private Shape rightHead;
    private Shape assocHead;
    private ObjectButton destObjectButton;
    private final ObjectButton assocObjectButton;

    private FontMetrics metrics = null;
    private int ascent;

    private int leftRoleWidth;
    private int rightRoleWidth;
    private int leftArrowHeadWidth;
    private int rightArrowHeadWidth;
    private int leftCardWidth;
    private int rightCardWidth;
    private int numberWidth;
    private int gapWidth;
    private int destObjectWidth;
    private int assocObjectWidth;
    private int assocArrowHeadWidth;

    private int descent;
    private int textHeight;
    private int topHalfArrowHeight;
    private int bottomHalfArrowHeight;
    private int roleHeight;
    private int arrowTextHeight;
    private int destObjectHeight;
    private int assocObjectHeight;
    private int assocArrowHeadHeight;
    private int assocArrowLineMinLength;
    private int mainArrowHeight;

    private Dimension prefSize;

    private Shape leftArrow;
    private Shape rightArrow;
    private Shape assocArrow;
    private Rectangle2D leftArrowBounds;
    private Rectangle2D rightArrowBounds;
    private Rectangle2D assocArrowBounds;
    private Stroke assocLineStyle;

    private static final int minAssocArrowLengthMultiplier = 3;
    private static final AffineTransform rotate90 = AffineTransform.getRotateInstance(Math.PI / 2);
    private static final AffineTransform reflect = AffineTransform.getScaleInstance(-1, 1);

    public static ComponentUI createUI(final JComponent c) {
        return new SimpleRelationshipUI((RelationshipArrow) c);
    }

    private SimpleRelationshipUI(final RelationshipArrow arrow) {
        final RelationshipMetaData rel = arrow.getRelationship();

        if (arrow.getSource().equals(rel.getLeftObject())) {
            leftRole = rel.getLeftRole();
            rightRole = rel.getRightRole();
            destObjectButton = arrow.getRightObjectButton();
        } else {
            rightRole = rel.getLeftRole();
            leftRole = rel.getRightRole();
            destObjectButton = arrow.getLeftObjectButton();
        }
        number = rel.getNumber();
        assocObjectButton = arrow.getAssocObjectButton();
    }

    @Override
    public void installUI(final JComponent c) {
        final RelationshipArrow arrow = (RelationshipArrow) c;
        final RelationshipMetaData rel = arrow.getRelationship();

        final RelationshipLineHeadUIResource leftLineHead = RelationshipLineHeadUIResource
                .getLineHead(rel.getLeftMany(), rel.getLeftConditional());
        final RelationshipLineHeadUIResource rightLineHead = RelationshipLineHeadUIResource
                .getLineHead(rel.getRightMany(), rel.getRightConditional());

        if (arrow.getSource().equals(rel.getLeftObject())) {
            leftHead = leftLineHead.getArrowHead();
            leftCard = leftLineHead.getCardinality();
            rightHead = rightLineHead.getArrowHead();
            rightCard = rightLineHead.getCardinality();
        } else {
            rightHead = leftLineHead.getArrowHead();
            rightCard = leftLineHead.getCardinality();
            leftHead = rightLineHead.getArrowHead();
            leftCard = rightLineHead.getCardinality();
        }

        if (rel.getAssocObject() != null) {
            assocHead = AssociativeLineUIResource.getLine(rel.getAssocMany()).getArrowHead();
            assocLineStyle = AssociativeLineUIResource.getLine(rel.getAssocMany()).getLineStyle();
        }
    }

    public void calcSizes() {
        ascent = metrics.getAscent();
        descent = metrics.getDescent();

        final AffineTransform scale = AffineTransform.getScaleInstance(ascent, ascent);

        leftArrow = scale.createTransformedShape(leftHead);
        leftArrowBounds = leftArrow.getBounds2D();

        rightArrow = reflect.createTransformedShape(scale.createTransformedShape(rightHead));
        rightArrowBounds = rightArrow.getBounds2D();

        // Widths
        leftRoleWidth = metrics.stringWidth(leftRole);
        rightRoleWidth = metrics.stringWidth(rightRole);
        leftArrowHeadWidth = (int) leftArrowBounds.getWidth();
        rightArrowHeadWidth = (int) rightArrowBounds.getWidth();
        leftCardWidth = metrics.stringWidth(leftCard);
        rightCardWidth = metrics.stringWidth(rightCard);
        numberWidth = metrics.stringWidth(number);
        gapWidth = metrics.stringWidth("  ");
        destObjectWidth = destObjectButton.getPreferredSize().width;

        // Heights
        topHalfArrowHeight = (int) Math.max(-leftArrowBounds.getMinY(), -rightArrowBounds.getMinY());
        bottomHalfArrowHeight = (int) Math.max(leftArrowBounds.getMaxY(), rightArrowBounds.getMaxY());
        textHeight = ascent + descent;
        roleHeight = textHeight;
        arrowTextHeight = textHeight;

        destObjectHeight = destObjectButton.getPreferredSize().height;

        // Associative Size
        if (assocObjectButton != null) {
            assocArrow = rotate90.createTransformedShape(scale.createTransformedShape(assocHead));
            assocArrowBounds = assocArrow.getBounds2D();

            // Widths
            assocObjectWidth = assocObjectButton.getPreferredSize().width;
            assocArrowHeadWidth = (int) assocArrowBounds.getWidth();

            // Heights
            assocObjectHeight = assocObjectButton.getPreferredSize().height;
            assocArrowHeadHeight = (int) assocArrowBounds.getHeight();
            assocArrowLineMinLength = ascent * minAssocArrowLengthMultiplier;

        } else {
            assocObjectWidth = 0;
            assocArrowHeadWidth = 0;
            assocObjectHeight = 0;
            assocArrowHeadHeight = 0;
            assocArrowLineMinLength = 0;
        }
        mainArrowHeight = arrowTextHeight + bottomHalfArrowHeight
                + Math.max(roleHeight, assocArrowLineMinLength + assocObjectHeight);

    }

    @Override
    public Dimension getPreferredSize(final JComponent c) {
        final FontMetrics newMetrics = c.getFontMetrics(c.getFont());
        if (newMetrics != metrics) {
            metrics = newMetrics;
            calcSizes();

            final int prefRoleLineWidth = Math.max(leftRoleWidth, rightRoleWidth) * 2 + gapWidth;
            int prefArrowLineWidth = Math.max(leftArrowHeadWidth + leftCardWidth, rightArrowHeadWidth + rightCardWidth)
                    * 2 + gapWidth + numberWidth + assocArrowHeadWidth + gapWidth;
            // Number is offset when associative, so ensure enough space to centre the
            // associative arrow
            if (assocObjectButton != null) {
                prefArrowLineWidth += numberWidth;
            }
            final int prefAssocObjectWidth = gapWidth * 2 + assocObjectWidth;

            final int prefWholeArrowWidth = Math.max(Math.max(prefRoleLineWidth, prefArrowLineWidth),
                    prefAssocObjectWidth);

            final int prefWidth = prefWholeArrowWidth + destObjectWidth;
            final int prefHeight = Math.max(mainArrowHeight, destObjectHeight);

            prefSize = new Dimension(prefWidth, prefHeight);
        }

        return prefSize;
    }

    @Override
    public Dimension getMaximumSize(final JComponent c) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public void paint(final Graphics graphics, final JComponent c) {
        final FontMetrics newMetrics = c.getFontMetrics(c.getFont());
        if (newMetrics != metrics) {
            metrics = newMetrics;
            calcSizes();
        }

        final int compWidth = c.getSize().width;
        final int compHeight = c.getSize().height;

        // Calc X positions
        final int compLeft = 0;
        final int compRight = compWidth - 1;

        final int arrowLeft = compLeft;
        final int arrowRight = compRight - destObjectWidth;
        final int arrowCentreXPos = (arrowRight + arrowLeft) / 2;

        final int leftHeadEndXPos = arrowLeft + leftArrowHeadWidth;
        final int leftCardXPos = leftHeadEndXPos;
        final int leftRoleXPos = arrowLeft;

        final int rightHeadEndXPos = arrowRight - rightArrowHeadWidth;
        final int rightCardXPos = rightHeadEndXPos - rightCardWidth;
        final int rightRoleXPos = arrowRight - rightRoleWidth;

        int numberXPos;
        final int assocArrowXPos = arrowCentreXPos;
        final int assocObjXPos = assocArrowXPos - assocObjectWidth / 2;

        if (assocObjectButton != null) {
            numberXPos = assocArrowXPos - assocArrowHeadWidth / 2 - numberWidth;
        } else {
            numberXPos = arrowCentreXPos - numberWidth / 2;
        }

        final int destObjXPos = arrowRight;

        // Calc Y positions
        final int arrowTop = (compHeight - mainArrowHeight) / 2;

        final int roleYPos = arrowTop + ascent;
        final int arrowCentreYPos = arrowTop + textHeight + topHalfArrowHeight;
        final int arrowTextYPos = arrowCentreYPos + ascent;
        final int assocObjYPos = arrowCentreYPos + assocArrowLineMinLength;

        final int destObjYPos = 0;

        final Graphics2D g = (Graphics2D) graphics;

        // Draw the text
        g.drawString(leftRole, leftRoleXPos, roleYPos);
        g.drawString(rightRole, rightRoleXPos, roleYPos);
        g.drawString(leftCard, leftCardXPos, arrowTextYPos);
        g.drawString(rightCard, rightCardXPos, arrowTextYPos);
        g.drawString(number, numberXPos, arrowTextYPos);

        // Draw the main arrow
        GraphicsUtilities.drawAt(g, arrowLeft, arrowCentreYPos, leftArrow);
        GraphicsUtilities.drawAt(g, arrowRight, arrowCentreYPos, rightArrow);
        g.drawLine(leftHeadEndXPos, arrowCentreYPos, rightHeadEndXPos, arrowCentreYPos);

        // Position the object button
        destObjectButton.setLocation(destObjXPos, destObjYPos);

        if (assocObjectButton != null) {
            GraphicsUtilities.drawAt(g, arrowCentreXPos, arrowCentreYPos, assocArrow);
            g.setStroke(assocLineStyle);
            g.drawLine(arrowCentreXPos, arrowCentreYPos + assocArrowHeadHeight, arrowCentreXPos, assocObjYPos);
            assocObjectButton.setLocation(assocObjXPos, assocObjYPos);
        }

    }

}
