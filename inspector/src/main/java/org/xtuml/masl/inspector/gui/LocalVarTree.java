// 
// Filename : LocalVarTree.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.xtuml.masl.inspector.processInterface.InstanceIdData;
import org.xtuml.masl.inspector.processInterface.ObjectMetaData;


public class LocalVarTree extends JTree
{

  private final JPopupMenu popup = new JPopupMenu();

  public LocalVarTree ( final LocalVarTreeModel model )
  {
    super(model);
    setRootVisible(false);
    setShowsRootHandles(true);
    addMouseListener(new MouseHandler());

    final JMenuItem refresh = new JMenuItem("View Instances");
    popup.add(refresh);
    refresh.addActionListener(new ActionListener()
      {

        public void actionPerformed ( final ActionEvent action )
        {
          final Map<ObjectMetaData, List<Integer>> instances = getSelectedInstances();
          final Iterator<ObjectMetaData> it = instances.keySet().iterator();
          while ( it.hasNext() )
          {
            final ObjectMetaData meta = it.next();
            final Integer[] pks = instances.get(meta).toArray(new Integer[0]);
            InstanceViewer.display(meta, pks);
          }
        }
      });


  }

  private List<String[]> expandedNames;

  public void updateStarted ()
  {
    final Enumeration<TreePath> enumVar = getExpandedDescendants(new TreePath(getModel().getRoot()));
    if ( enumVar != null )
    {
      expandedNames = new ArrayList<String[]>();
      while ( enumVar.hasMoreElements() )
      {
        final TreePath path = enumVar.nextElement();

        final Object[] nodes = path.getPath();

        final String[] names = new String[nodes.length];

        for ( int i = 0; i < nodes.length; i++ )
        {
          names[i] = ((LocalVarNode)nodes[i]).getName();
        }

        expandedNames.add(names);
      }
    }
  }

  public void updateComplete ()
  {
    if ( expandedNames != null )
    {
      for ( int i = 0; i < expandedNames.size(); i++ )
      {
        final TreePath path = ((LocalVarTreeModel)getModel()).getPathForNames(expandedNames.get(i));
        expandPath(path);
      }
      expandedNames = null;
    }
  }

  public Map<ObjectMetaData, List<Integer>> getSelectedInstances ()
  {
    final Map<ObjectMetaData, List<Integer>> result = new HashMap<ObjectMetaData, List<Integer>>();

    final TreePath[] paths = getSelectionPaths();
    for ( int i = 0; i < paths.length; ++i )
    {
      final TreePath path = paths[i];

      final LocalVarNode node = (LocalVarNode)path.getLastPathComponent();
      final List<Object> instances = node.getContainedInstances();
      if ( instances.size() > 0 )
      {
        final ObjectMetaData meta = ((InstanceIdData)instances.get(0)).getMetaData();
        List<Integer> pks = result.get(meta);
        if ( pks == null )
        {
          pks = new ArrayList<Integer>();
          result.put(meta, pks);
        }

        final Iterator<Object> it = instances.iterator();
        while ( it.hasNext() )
        {
          pks.add(((InstanceIdData)it.next()).getId());
        }
      }
    }
    return result;
  }

  private class MouseHandler extends MouseAdapter
  {

    @Override
    public void mousePressed ( final MouseEvent e )
    {
      if ( e.isPopupTrigger() )
      {
        final int row = getRowForLocation(e.getX(), e.getY());
        if ( row != -1 )
        {
          if ( !isRowSelected(row) )
          {
            setSelectionRows(new int[]
              { row });
          }
          popup.show(e.getComponent(), e.getX(), e.getY());
        }
      }
    }
  }

}
