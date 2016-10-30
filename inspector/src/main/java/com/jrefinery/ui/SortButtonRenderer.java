/*
 * ================================================================ JCommon : a
 * general purpose, open source, class library for Java
 * ================================================================
 *
 * Project Info: http://www.jrefinery.com/jcommon Project Lead: David Gilbert
 * (david.gilbert@jrefinery.com);
 *
 * (C) Copyright 2000, 2001 Simba Management Limited and Contributors;
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 *
 * ----------------------- SortButtonRenderer.java ----------------------- (C)
 * Copyright 2000, 2001 Nobuo Tamemasa and Contributors;
 *
 * Original Author: Nobuo Tamemasa; Contributor(s): David Gilbert (for Simba
 * Management Limited);
 *
 * Changes (from 26-Oct-2001) -------------------------- 26-Oct-2001 : Changed
 * package to com.jrefinery.ui.*;
 */

package com.jrefinery.ui;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;


/**
 * A table cell renderer for table headings - uses one of three JButton
 * instances to indicate the sort order for the table column.
 * <P>
 * This class (and also BevelArrowIcon) is adapted from original code by Nobuo
 * Tamemasa (version 1.0, 26-Feb-1999) posted on www.codeguru.com.
 */
public class SortButtonRenderer
    implements TableCellRenderer
{

  /** Useful constant indicating NO sorting. */
  public static final int NONE          = 0;

  /**
   * Useful constant indicating ASCENDING (that is, arrow pointing down) sorting
   * in the table.
   */
  public static final int DOWN          = 1;

  /**
   * Useful constant indicating DESCENDING (that is, arrow pointing up) sorting
   * in the table.
   */
  public static final int UP            = 2;

  /** The current pressed column (-1 for no column). */
  protected int           pressedColumn = -1;

  /** The three buttons that are used to render the table header cells. */
  JButton                 normalButton, ascendingButton, descendingButton;

  /**
   * Constructs a SortButtonRenderer.
   */
  public SortButtonRenderer ()
  {

    pressedColumn = -1;

    normalButton = new JButton();
    normalButton.setMargin(new Insets(0, 0, 0, 0));
    normalButton.setHorizontalAlignment(SwingConstants.LEADING);

    ascendingButton = new JButton();
    ascendingButton.setMargin(new Insets(0, 0, 0, 0));
    ascendingButton.setHorizontalAlignment(SwingConstants.LEADING);
    ascendingButton.setHorizontalTextPosition(SwingConstants.LEFT);
    ascendingButton.setIcon(new BevelArrowIcon(BevelArrowIcon.DOWN, false, false));
    ascendingButton.setPressedIcon(new BevelArrowIcon(BevelArrowIcon.DOWN, false, true));

    descendingButton = new JButton();
    descendingButton.setMargin(new Insets(0, 0, 0, 0));
    descendingButton.setHorizontalAlignment(SwingConstants.LEADING);
    descendingButton.setHorizontalTextPosition(SwingConstants.LEFT);
    descendingButton.setIcon(new BevelArrowIcon(BevelArrowIcon.UP, false, false));
    descendingButton.setPressedIcon(new BevelArrowIcon(BevelArrowIcon.UP, false, true));

    final Border border = UIManager.getBorder("TableHeader.cellBorder");
    normalButton.setBorder(border);
    ascendingButton.setBorder(border);
    descendingButton.setBorder(border);

  }

  /**
   * Returns the renderer component.
   */
  @Override
  public Component getTableCellRendererComponent ( final JTable table, final Object value, final boolean isSelected,
                                                   final boolean hasFocus, final int row, final int column )
  {

    JButton button = normalButton;
    final int cc = table.convertColumnIndexToModel(column);
    if ( table != null )
    {
      final SortableTableModel model = (SortableTableModel)table.getModel();
      if ( model.getSortingColumn() == cc )
      {
        if ( model.getAscending() )
        {
          button = ascendingButton;
        }
        else
        {
          button = descendingButton;
        }
      }
    }

    final boolean isPressed = (cc == pressedColumn);

    final JTableHeader header = table.getTableHeader();
    if ( header != null )
    {
      button.setForeground(header.getForeground());
      button.setBackground(header.getBackground());
      button.setFont(header.getFont());
      final Icon icon = isPressed ? button.getPressedIcon() : button.getIcon();
      if ( icon != null )
      {
        ((BevelArrowIcon)icon).setSize(header.getFont().getSize());
      }
    }

    button.setText((value == null) ? "" : value.toString());
    button.getModel().setPressed(isPressed);
    button.getModel().setArmed(isPressed);
    return button;
  }

  /**
   * Sets the pressed column.
   */
  public void setPressedColumn ( final int column )
  {
    this.pressedColumn = column;
  }

}
