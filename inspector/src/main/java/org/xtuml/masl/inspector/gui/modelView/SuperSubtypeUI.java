// 
// Filename : SuperSubtypeUI.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui.modelView;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;


public class SuperSubtypeUI extends ComponentUI
{

  private Shape                        supertypeHead;
  private Shape                        subtypeHead;
  private final ObjectButton           superObjectButton;
  private final ObjectButton[]         subObjectButtons;
  private final String                 number;

  private FontMetrics                  metrics                  = null;
  private int                          ascent;
  private int                          descent;

  private Dimension                    prefSize;

  private Shape                        supertypeArrow           = null;
  private Shape                        subtypeArrow             = null;
  private Shape                        sourceSubtypeArrow       = null;
  private Rectangle2D                  supertypeArrowBounds     = null;
  private Rectangle2D                  subtypeArrowBounds       = null;
  private Rectangle2D                  sourceSubtypeArrowBounds = null;

  private int                          supertypeHeight;
  private int                          supertypeWidth;

  private int                          subtypeHeight;
  private int                          subtypeWidth;

  private int[]                        subtypeWidths;

  private int                          supertypeArrowLength;
  private int                          subtypeArrowLength;

  private static final int             minArrowLengthMultiplier = 3;
  private static final AffineTransform rotate90                 = AffineTransform.getRotateInstance(Math.PI / 2);
  private static final AffineTransform rotate270                = AffineTransform.getRotateInstance(-Math.PI / 2);

  public static ComponentUI createUI ( final JComponent c )
  {
    return new SuperSubtypeUI((SuperSubtypeArrow)c);
  }

  private SuperSubtypeUI ( final SuperSubtypeArrow arrow )
  {
    superObjectButton = arrow.getSuperObjectButton();
    subObjectButtons = arrow.getSubObjectButtons();

    number = arrow.getSuperSubtype().getNumber();
  }

  @Override
  public void installUI ( final JComponent c )
  {
    supertypeHead = SuperSubLineUIResource.getSupertypeLineHead().getArrowHead();
    subtypeHead = SuperSubLineUIResource.getSubtypeLineHead().getArrowHead();
  }

  public void calcSizes ()
  {
    ascent = metrics.getAscent();
    descent = metrics.getDescent();

    final AffineTransform scale = AffineTransform.getScaleInstance(ascent, ascent);

    subtypeArrow = rotate270.createTransformedShape(scale.createTransformedShape(subtypeHead));
    subtypeArrowBounds = subtypeArrow.getBounds2D();

    if ( superObjectButton != null )
    {
      supertypeArrow = rotate90.createTransformedShape(scale.createTransformedShape(supertypeHead));
      sourceSubtypeArrow = scale.createTransformedShape(subtypeHead);
      sourceSubtypeArrowBounds = sourceSubtypeArrow.getBounds2D();
      supertypeHeight = superObjectButton.getPreferredSize().height;
      supertypeWidth = superObjectButton.getPreferredSize().width;
    }
    else
    {
      supertypeArrow = scale.createTransformedShape(supertypeHead);
      supertypeHeight = 0;
      supertypeWidth = 0;
    }
    supertypeArrowBounds = supertypeArrow.getBounds2D();
    supertypeArrowLength = ascent * minArrowLengthMultiplier;
    subtypeArrowLength = ascent * minArrowLengthMultiplier;

    subtypeWidth = 0;
    subtypeWidths = new int[subObjectButtons.length];
    subtypeHeight = 0;
    for ( int i = 0; i < subObjectButtons.length; i++ )
    {
      subtypeHeight = Math.max(subtypeHeight, subObjectButtons[i].getPreferredSize().height);
      subtypeWidths[i] = subObjectButtons[i].getPreferredSize().width;
      subtypeWidth += subtypeWidths[i];
    }

  }


  @Override
  public Dimension getPreferredSize ( final JComponent c )
  {
    final FontMetrics newMetrics = c.getFontMetrics(c.getFont());
    if ( newMetrics != metrics )
    {
      metrics = newMetrics;
      calcSizes();

      final int prefGap = 20;
      final int prefSubtypeWidth = subtypeWidth + prefGap * subObjectButtons.length;

      final int prefWidth = Math.max(supertypeWidth + prefGap, prefSubtypeWidth);

      final int prefHeight = supertypeHeight + supertypeArrowLength + subtypeArrowLength + subtypeHeight;

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
    final int compCentreXPos = (compRight - compLeft) / 2;


    final int noSubtypes = subObjectButtons.length;
    final int[] subArrowXPos = new int[noSubtypes];
    final int[] subObjectXPos = new int[noSubtypes];
    int gap = 0;
    int superArrowXPos;
    if ( noSubtypes > 0 )
    {
      gap = (compWidth - subtypeWidth) / noSubtypes;
      superArrowXPos = compCentreXPos;
    }
    else
    {
      gap = (compWidth - supertypeWidth);
      superArrowXPos = gap + supertypeWidth / 2;
    }

    int currentPos = gap;
    for ( int i = 0; i < noSubtypes; i++ )
    {
      subObjectXPos[i] = currentPos;
      subArrowXPos[i] = currentPos + subtypeWidths[i] / 2;

      currentPos += subtypeWidths[i] + gap;
    }

    // Special case for single subtype with no supertype button to make straight
    // line
    if ( noSubtypes == 1 && superObjectButton == null )
    {
      superArrowXPos = subArrowXPos[0];
    }

    int crossLineRight;
    int crossLineLeft;
    int superObjectXPos = 0;

    if ( superObjectButton != null )
    {
      superObjectXPos = superArrowXPos - supertypeWidth / 2;
      crossLineLeft = compLeft + (int)sourceSubtypeArrowBounds.getWidth();
      crossLineRight = noSubtypes == 0 ? superArrowXPos : subArrowXPos[noSubtypes - 1];
    }
    else
    {
      crossLineLeft = noSubtypes == 0 ? superArrowXPos : subArrowXPos[0];
      crossLineRight = noSubtypes == 0 ? superArrowXPos : subArrowXPos[noSubtypes - 1];
    }

    final int numberXPos = superArrowXPos + descent;

    // Calculate Y positions
    final int compTop = 0;
    final int compBottom = compTop + compHeight - 1;

    final int superObjectYPos = compTop;
    final int subObjectYPos = compBottom - subtypeHeight;

    final int superObjectBottom = compTop + supertypeHeight;
    final int crossLineYPos = (superObjectBottom + subObjectYPos) / 2;
    final int subLineBottom = subObjectYPos - (int)subtypeArrowBounds.getHeight();

    int superArrowTop;
    int superArrowHeadXPos = 0;
    int superArrowHeadEndXPos = 0;
    if ( superObjectButton != null )
    {
      superArrowTop = superObjectYPos + supertypeHeight;
    }
    else
    {
      superArrowTop = compTop - (int)supertypeArrowBounds.getMinY();
      superArrowHeadXPos = compLeft;
      superArrowHeadEndXPos = superArrowHeadXPos + (int)supertypeArrowBounds.getWidth();
    }

    final int numberYPos = crossLineYPos - descent;

    final Graphics2D g = (Graphics2D)graphics;

    if ( superObjectButton != null )
    {
      superObjectButton.setLocation(superObjectXPos, superObjectYPos);
      g.drawLine(superArrowXPos, superArrowTop + (int)supertypeArrowBounds.getHeight(), superArrowXPos, crossLineYPos);
      GraphicsUtilities.drawAt(g, superArrowXPos, superArrowTop, supertypeArrow);
      GraphicsUtilities.drawAt(g, compLeft, crossLineYPos, sourceSubtypeArrow);
    }
    else
    {
      g.drawLine(superArrowXPos, superArrowTop, superArrowXPos, crossLineYPos);
      g.drawLine(superArrowHeadEndXPos, superArrowTop, superArrowXPos, superArrowTop);
      GraphicsUtilities.drawAt(g, superArrowHeadXPos, superArrowTop, supertypeArrow);
    }

    for ( int i = 0; i < noSubtypes; i++ )
    {
      g.drawLine(subArrowXPos[i], crossLineYPos, subArrowXPos[i], subLineBottom);
      GraphicsUtilities.drawAt(g, subArrowXPos[i], subObjectYPos, subtypeArrow);
      subObjectButtons[i].setLocation(subObjectXPos[i], subObjectYPos);
    }
    g.drawLine(crossLineLeft, crossLineYPos, crossLineRight, crossLineYPos);
    g.drawString(number, numberXPos, numberYPos);
  }


}
