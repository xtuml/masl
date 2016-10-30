// 
// Filename : ReflexiveRelationshipUI.java
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


public class ReflexiveRelationshipUI extends ComponentUI
{

  private final String       topRole;
  private final String       bottomRole;
  private final String       number;
  private String             topCard;
  private String             bottomCard;
  private Shape              topHead;
  private Shape              bottomHead;
  private Shape              assocHead                     = null;

  private final ObjectButton assocObjectButton;

  private FontMetrics        metrics                       = null;
  private int                ascent;

  private int                topRoleWidth;
  private int                bottomRoleWidth;
  private int                topArrowHeadWidth;
  private int                bottomArrowHeadWidth;
  private int                topCardWidth;
  private int                bottomCardWidth;
  private int                numberWidth;
  private int                gapWidth;
  private int                assocObjectWidth;
  private int                assocArrowHeadWidth;

  private int                descent;
  private int                textHeight;
  private int                topHalfTopArrowHeight;
  private int                bottomHalfBottomArrowHeight;
  private int                roleHeight;
  private int                arrowTextHeight;
  private int                assocObjectHeight;
  private int                assocArrowLineMinLength;
  private int                mainArrowHeight;

  private Dimension          prefSize;

  private Shape              topArrow;
  private Shape              bottomArrow;
  private Shape              assocArrow;
  private Rectangle2D        topArrowBounds;
  private Rectangle2D        bottomArrowBounds;
  private Rectangle2D        assocArrowBounds;
  private Stroke             assocLineStyle;

  private static final int   minAssocArrowLengthMultiplier = 3;

  public static ComponentUI createUI ( final JComponent c )
  {
    return new ReflexiveRelationshipUI((RelationshipArrow)c);
  }

  private ReflexiveRelationshipUI ( final RelationshipArrow arrow )
  {
    final RelationshipMetaData rel = arrow.getRelationship();

    topRole = rel.getLeftRole();
    bottomRole = rel.getRightRole();
    number = rel.getNumber();

    assocObjectButton = arrow.getAssocObjectButton();
  }

  @Override
  public void installUI ( final JComponent c )
  {
    final RelationshipArrow arrow = (RelationshipArrow)c;
    final RelationshipMetaData rel = arrow.getRelationship();

    final RelationshipLineHeadUIResource topLineHead = RelationshipLineHeadUIResource.getLineHead(rel.getLeftMany(),
                                                                                                  rel.getLeftConditional());
    final RelationshipLineHeadUIResource bottomLineHead = RelationshipLineHeadUIResource.getLineHead(rel.getRightMany(),
                                                                                                     rel.getRightConditional());

    topHead = topLineHead.getArrowHead();
    topCard = topLineHead.getCardinality();
    bottomHead = bottomLineHead.getArrowHead();
    bottomCard = bottomLineHead.getCardinality();

    if ( rel.getAssocObject() != null )
    {
      assocHead = AssociativeLineUIResource.getLine(rel.getAssocMany()).getArrowHead();
      assocLineStyle = AssociativeLineUIResource.getLine(rel.getAssocMany()).getLineStyle();

    }
  }

  public void calcSizes ()
  {
    ascent = metrics.getAscent();
    descent = metrics.getDescent();

    final AffineTransform scale = AffineTransform.getScaleInstance(ascent, ascent);

    topArrow = scale.createTransformedShape(topHead);
    topArrowBounds = topArrow.getBounds2D();

    bottomArrow = scale.createTransformedShape(bottomHead);
    bottomArrowBounds = bottomArrow.getBounds2D();

    // Widths
    topRoleWidth = metrics.stringWidth(topRole);
    bottomRoleWidth = metrics.stringWidth(bottomRole);
    topArrowHeadWidth = (int)topArrowBounds.getWidth();
    bottomArrowHeadWidth = (int)bottomArrowBounds.getWidth();
    topCardWidth = metrics.stringWidth(topCard);
    bottomCardWidth = metrics.stringWidth(bottomCard);
    numberWidth = metrics.stringWidth(number);
    gapWidth = metrics.stringWidth("  ");

    // Heights
    topHalfTopArrowHeight = (int)-topArrowBounds.getMinY();
    bottomHalfBottomArrowHeight = (int)bottomArrowBounds.getMaxY();
    textHeight = ascent + descent;
    roleHeight = textHeight;
    arrowTextHeight = textHeight;

    // Associative Size
    if ( assocObjectButton != null )
    {
      assocArrow = scale.createTransformedShape(assocHead);
      assocArrowBounds = assocArrow.getBounds2D();

      // Widths
      assocObjectWidth = assocObjectButton.getPreferredSize().width;
      assocArrowHeadWidth = (int)assocArrowBounds.getWidth();

      // Heights
      assocObjectHeight = assocObjectButton.getPreferredSize().height;

      assocArrowLineMinLength = ascent * minAssocArrowLengthMultiplier;

    }
    else
    {
      assocObjectWidth = 0;
      assocArrowHeadWidth = 0;
      assocObjectHeight = 0;
      assocArrowLineMinLength = 0;
    }
    mainArrowHeight = roleHeight + topHalfTopArrowHeight + arrowTextHeight * 3 + bottomHalfBottomArrowHeight + roleHeight;
  }


  @Override
  public Dimension getPreferredSize ( final JComponent c )
  {
    final FontMetrics newMetrics = c.getFontMetrics(c.getFont());
    if ( newMetrics != metrics )
    {
      metrics = newMetrics;
      calcSizes();

      final int prefRoleWidth = Math.max(topRoleWidth, bottomRoleWidth);

      final int prefArrowLineWidth = Math.max(topArrowHeadWidth + topCardWidth, bottomArrowHeadWidth + bottomCardWidth) + gapWidth;
      final int prefNumberWidth = numberWidth + gapWidth;

      final int prefMainArrowWidth = Math.max(Math.max(prefRoleWidth, prefArrowLineWidth), prefNumberWidth);

      final int prefWidth = prefMainArrowWidth + assocObjectWidth + assocArrowLineMinLength;

      final int prefHeight = Math.max(mainArrowHeight, assocObjectHeight);

      prefSize = new Dimension(prefWidth, prefHeight);
    }

    return prefSize;
  }

  @Override
  public Dimension getMaximumSize ( final JComponent c )
  {
    return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
  }

  @Override
  public void paint ( final Graphics graphics, final JComponent c )
  {

    final FontMetrics newMetrics = c.getFontMetrics(c.getFont());
    if ( newMetrics != metrics )
    {
      metrics = newMetrics;
      calcSizes();
    }

    final int compWidth = c.getSize().width;
    final int compHeight = c.getSize().height;


    // Calc X positions
    final int compLeft = 0;
    final int compRight = compWidth - 1;

    final int arrowLeft = compLeft;
    final int arrowRight = compRight - assocObjectWidth - assocArrowLineMinLength;

    final int topHeadEndXPos = arrowLeft + topArrowHeadWidth;
    final int topCardXPos = topHeadEndXPos;
    final int topRoleXPos = arrowLeft;

    final int bottomHeadEndXPos = arrowLeft + bottomArrowHeadWidth;
    final int bottomCardXPos = bottomHeadEndXPos;
    final int bottomRoleXPos = arrowLeft;

    final int numberXPos = arrowRight - numberWidth;
    final int assocObjXPos = compRight - assocObjectWidth;
    final int assocArrowLineLeft = arrowRight + assocArrowHeadWidth;
    final int assocArrowLineRight = assocObjXPos;

    // Calc Y positions
    final int arrowTop = (compHeight - mainArrowHeight) / 2;
    final int arrowBottom = arrowTop + mainArrowHeight;
    final int arrowCentreYPos = (arrowTop + arrowBottom) / 2;
    final int assocObjYPos = (compHeight - assocObjectHeight) / 2;

    final int topRoleYPos = arrowTop + ascent;
    final int topArrowCentreYPos = arrowTop + textHeight + topHalfTopArrowHeight;
    final int topCardYPos = topArrowCentreYPos + ascent;

    final int bottomRoleYPos = arrowBottom - descent;
    final int bottomArrowCentreYPos = arrowBottom - textHeight - bottomHalfBottomArrowHeight;
    final int bottomCardYPos = bottomArrowCentreYPos - descent;

    final int numberYPos = arrowCentreYPos + arrowTextHeight / 2;

    final Graphics2D g = (Graphics2D)graphics;
    g.drawString(topRole, topRoleXPos, topRoleYPos);
    g.drawString(topCard, topCardXPos, topCardYPos);
    g.drawString(number, numberXPos, numberYPos);
    g.drawString(bottomCard, bottomCardXPos, bottomCardYPos);
    g.drawString(bottomRole, bottomRoleXPos, bottomRoleYPos);

    // Draw the arrow
    GraphicsUtilities.drawAt(g, arrowLeft, topArrowCentreYPos, topArrow);
    GraphicsUtilities.drawAt(g, arrowLeft, bottomArrowCentreYPos, bottomArrow);

    g.drawLine(topHeadEndXPos, topArrowCentreYPos, arrowRight, topArrowCentreYPos);
    g.drawLine(arrowRight, topArrowCentreYPos, arrowRight, bottomArrowCentreYPos);
    g.drawLine(bottomHeadEndXPos, bottomArrowCentreYPos, arrowRight, bottomArrowCentreYPos);

    if ( assocObjectButton != null )
    {
      GraphicsUtilities.drawAt(g, arrowRight, arrowCentreYPos, assocArrow);
      g.setStroke(assocLineStyle);
      g.drawLine(assocArrowLineLeft, arrowCentreYPos, assocArrowLineRight, arrowCentreYPos);

      assocObjectButton.setLocation(assocObjXPos, assocObjYPos);
    }


  }
}
