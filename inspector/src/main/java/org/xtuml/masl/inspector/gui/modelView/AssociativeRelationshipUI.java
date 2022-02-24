// 
// Filename : AssociativeRelationshipUI.java
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

public class AssociativeRelationshipUI extends ComponentUI {

    private final String topRole;
    private final String bottomRole;
    private final String number;
    private String topCard;
    private String bottomCard;
    private Shape topHead;
    private Shape bottomHead;
    private Shape assocHead = null;

    private final ObjectButton topObjectButton;
    private final ObjectButton bottomObjectButton;

    private FontMetrics metrics = null;
    private int ascent;

    private int topRoleWidth;
    private int bottomRoleWidth;
    private int topArrowHeadWidth;
    private int bottomArrowHeadWidth;
    private int topCardWidth;
    private int bottomCardWidth;
    private int numberWidth;
    private int gapWidth;
    private int destObjectWidth;
    private int topObjectWidth;
    private int bottomObjectWidth;
    private int assocArrowHeadWidth;

    private int descent;
    private int textHeight;
    private int topHalfTopArrowHeight;
    private int bottomHalfBottomArrowHeight;
    private int roleHeight;
    private int arrowTextHeight;
    private int topObjectHeight;
    private int bottomObjectHeight;
    private int assocArrowLineMinLength;
    private int mainArrowMinHeight;
    private int destObjectHeight;

    private Dimension prefSize;

    private Shape topArrow;
    private Shape bottomArrow;
    private Shape assocArrow;
    private Rectangle2D topArrowBounds;
    private Rectangle2D bottomArrowBounds;
    private Rectangle2D assocArrowBounds;
    private Stroke assocLineStyle;

    private static final AffineTransform reflect = AffineTransform.getScaleInstance(-1, 1);

    private static final int minAssocArrowLengthMultiplier = 3;

    public static ComponentUI createUI(final JComponent c) {
        return new AssociativeRelationshipUI((RelationshipArrow) c);
    }

    private AssociativeRelationshipUI(final RelationshipArrow arrow) {
        final RelationshipMetaData rel = arrow.getRelationship();

        topRole = rel.getLeftRole();
        bottomRole = rel.getRightRole();
        number = rel.getNumber();

        topObjectButton = arrow.getLeftObjectButton();
        bottomObjectButton = arrow.getRightObjectButton();
    }

    @Override
    public void installUI(final JComponent c) {
        final RelationshipArrow arrow = (RelationshipArrow) c;
        final RelationshipMetaData rel = arrow.getRelationship();

        final RelationshipLineHeadUIResource topLineHead = RelationshipLineHeadUIResource.getLineHead(rel.getLeftMany(),
                rel.getLeftConditional());
        final RelationshipLineHeadUIResource bottomLineHead = RelationshipLineHeadUIResource
                .getLineHead(rel.getRightMany(), rel.getRightConditional());

        topHead = topLineHead.getArrowHead();
        topCard = topLineHead.getCardinality();
        bottomHead = bottomLineHead.getArrowHead();
        bottomCard = bottomLineHead.getCardinality();
        assocHead = AssociativeLineUIResource.getLine(rel.getAssocMany()).getArrowHead();
        assocLineStyle = AssociativeLineUIResource.getLine(rel.getAssocMany()).getLineStyle();
    }

    public void calcSizes() {
        ascent = metrics.getAscent();
        descent = metrics.getDescent();

        final AffineTransform scale = AffineTransform.getScaleInstance(ascent, ascent);

        topArrow = reflect.createTransformedShape(scale.createTransformedShape(topHead));
        topArrowBounds = topArrow.getBounds2D();

        assocArrow = reflect.createTransformedShape(scale.createTransformedShape(assocHead));
        assocArrowBounds = assocArrow.getBounds2D();

        bottomArrow = reflect.createTransformedShape(scale.createTransformedShape(bottomHead));
        bottomArrowBounds = bottomArrow.getBounds2D();

        // Widths
        topRoleWidth = metrics.stringWidth(topRole);
        bottomRoleWidth = metrics.stringWidth(bottomRole);
        topArrowHeadWidth = (int) topArrowBounds.getWidth();
        bottomArrowHeadWidth = (int) bottomArrowBounds.getWidth();
        topCardWidth = metrics.stringWidth(topCard);
        bottomCardWidth = metrics.stringWidth(bottomCard);
        numberWidth = metrics.stringWidth(number);
        gapWidth = metrics.stringWidth("  ");
        topObjectWidth = topObjectButton.getPreferredSize().width;
        assocArrowHeadWidth = (int) assocArrowBounds.getWidth();

        // Heights
        topHalfTopArrowHeight = (int) -topArrowBounds.getMinY();
        bottomHalfBottomArrowHeight = (int) bottomArrowBounds.getMaxY();
        topObjectHeight = topObjectButton.getPreferredSize().height;
        textHeight = ascent + descent;
        roleHeight = textHeight;
        arrowTextHeight = textHeight;
        assocArrowLineMinLength = ascent * minAssocArrowLengthMultiplier;

        mainArrowMinHeight = roleHeight + topHalfTopArrowHeight + arrowTextHeight * 3 + bottomHalfBottomArrowHeight
                + roleHeight;

        if (bottomObjectButton != null) {
            bottomObjectHeight = bottomObjectButton.getPreferredSize().height;
            bottomObjectWidth = bottomObjectButton.getPreferredSize().width;
            destObjectHeight = topObjectHeight + 10 + bottomObjectHeight;
        } else {
            bottomObjectHeight = 0;
            bottomObjectWidth = 0;
            destObjectHeight = topObjectHeight;
        }
        destObjectWidth = Math.max(topObjectWidth, bottomObjectWidth);
    }

    @Override
    public Dimension getPreferredSize(final JComponent c) {
        final FontMetrics newMetrics = c.getFontMetrics(c.getFont());
        if (newMetrics != metrics) {
            metrics = newMetrics;
            calcSizes();

            final int prefRoleWidth = Math.max(topRoleWidth, bottomRoleWidth);

            final int prefArrowLineWidth = Math.max(topArrowHeadWidth + topCardWidth,
                    bottomArrowHeadWidth + bottomCardWidth) + gapWidth;
            final int prefNumberWidth = numberWidth + gapWidth;

            final int prefMainArrowWidth = Math.max(Math.max(prefRoleWidth, prefArrowLineWidth), prefNumberWidth);

            final int prefWidth = prefMainArrowWidth + destObjectWidth + assocArrowLineMinLength;

            final int prefHeight = Math.max(mainArrowMinHeight, destObjectHeight);

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

        final int arrowLeft = compLeft + assocArrowLineMinLength;
        final int arrowRight = compRight - destObjectWidth;

        final int topHeadEndXPos = arrowRight - topArrowHeadWidth;
        final int topCardXPos = topHeadEndXPos - topCardWidth;
        final int topRoleXPos = arrowRight - topRoleWidth;

        final int bottomHeadEndXPos = arrowRight - bottomArrowHeadWidth;
        final int bottomCardXPos = bottomHeadEndXPos - bottomCardWidth;
        final int bottomRoleXPos = arrowRight - bottomRoleWidth;

        final int numberXPos = arrowLeft;
        final int destObjXPos = compRight - destObjectWidth;

        final int assocArrowLineLeft = compLeft;
        final int assocArrowLineRight = arrowLeft - assocArrowHeadWidth;

        // Calc Y positions
        final int compTop = 0;
        final int compBottom = compHeight - 1;
        int arrowTop;
        int arrowBottom;
        int topObjYPos;
        int bottomObjYPos;
        if (bottomObjectButton != null) {
            arrowTop = compTop + topObjectHeight / 2 - textHeight - topHalfTopArrowHeight;
            arrowBottom = compBottom - bottomObjectHeight / 2 + textHeight + bottomHalfBottomArrowHeight;
            topObjYPos = 0;
            bottomObjYPos = compHeight - bottomObjectHeight;
        } else {
            arrowTop = (compHeight - mainArrowMinHeight) / 2;
            arrowBottom = arrowTop + mainArrowMinHeight;
            topObjYPos = (compHeight - topObjectHeight) / 2;
            bottomObjYPos = 0;
        }

        final int arrowCentreYPos = (arrowTop + arrowBottom) / 2;

        final int topRoleYPos = arrowTop + ascent;
        final int topArrowCentreYPos = arrowTop + textHeight + topHalfTopArrowHeight;
        final int topCardYPos = topArrowCentreYPos + ascent;

        final int bottomRoleYPos = arrowBottom - descent;
        final int bottomArrowCentreYPos = arrowBottom - textHeight - bottomHalfBottomArrowHeight;
        final int bottomCardYPos = bottomArrowCentreYPos - descent;

        final int numberYPos = arrowCentreYPos + arrowTextHeight / 2;

        final Graphics2D g = (Graphics2D) graphics;
        g.drawString(topRole, topRoleXPos, topRoleYPos);
        g.drawString(topCard, topCardXPos, topCardYPos);
        g.drawString(number, numberXPos, numberYPos);
        g.drawString(bottomCard, bottomCardXPos, bottomCardYPos);
        g.drawString(bottomRole, bottomRoleXPos, bottomRoleYPos);

        // Draw the arrow
        GraphicsUtilities.drawAt(g, arrowRight, topArrowCentreYPos, topArrow);
        GraphicsUtilities.drawAt(g, arrowRight, bottomArrowCentreYPos, bottomArrow);

        g.drawLine(arrowLeft, topArrowCentreYPos, topHeadEndXPos, topArrowCentreYPos);
        g.drawLine(arrowLeft, topArrowCentreYPos, arrowLeft, bottomArrowCentreYPos);
        g.drawLine(arrowLeft, bottomArrowCentreYPos, bottomHeadEndXPos, bottomArrowCentreYPos);

        GraphicsUtilities.drawAt(g, arrowLeft, arrowCentreYPos, assocArrow);
        g.setStroke(assocLineStyle);
        g.drawLine(assocArrowLineLeft, arrowCentreYPos, assocArrowLineRight, arrowCentreYPos);

        topObjectButton.setLocation(destObjXPos, topObjYPos);

        if (bottomObjectButton != null) {
            bottomObjectButton.setLocation(destObjXPos, bottomObjYPos);
        }
    }
}
