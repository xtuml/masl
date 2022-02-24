//
// Filename : BreakpointController.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector;

import java.util.EventListener;
import java.util.HashMap;

import javax.swing.event.EventListenerList;

import org.xtuml.masl.inspector.processInterface.SourcePosition;

public class BreakpointController {

    protected EventListenerList listenerList = new EventListenerList();
    private final HashMap<SourcePosition, Boolean> m_breakpoints = new HashMap<SourcePosition, Boolean>();

    private static BreakpointController s_singleton = new BreakpointController();

    private BreakpointController() {
    }

    public static BreakpointController getInstance() {
        return s_singleton;
    }

    public HashMap<SourcePosition, Boolean> getBreakpoints() {
        return m_breakpoints;
    }

    public void setBreakpoint(final SourcePosition pos) {
        m_breakpoints.put(pos, Boolean.TRUE);
        fireBreakpointSet(pos);
    }

    public void clearBreakpoint(final SourcePosition pos) {
        m_breakpoints.put(pos, Boolean.FALSE);
        fireBreakpointCleared(pos);
    }

    public void toggleBreakpoint(final SourcePosition pos) {
        if (m_breakpoints.get(pos) == Boolean.TRUE) {
            clearBreakpoint(pos);
        } else {
            setBreakpoint(pos);
        }
    }

    public void removeBreakpoint(final SourcePosition pos) {
        m_breakpoints.remove(pos);
        fireBreakpointRemoved(pos);
    }

    public void setAllBreakpoints() {
        for (final SourcePosition pos : m_breakpoints.keySet()) {
            setBreakpoint(pos);
        }
    }

    public void clearAllBreakpoints() {
        for (final SourcePosition pos : m_breakpoints.keySet()) {
            clearBreakpoint(pos);
        }
    }

    public void removeAllBreakpoints() {
        for (final SourcePosition pos : m_breakpoints.keySet()) {
            fireBreakpointRemoved(pos);
        }
        m_breakpoints.clear();
    }

    public boolean breakpointSet(final SourcePosition pos) {
        return m_breakpoints.get(pos) == Boolean.TRUE;
    }

    public boolean breakpointExists(final SourcePosition pos) {
        return m_breakpoints.get(pos) != null;
    }

    public void addBreakpointListener(final BreakpointListener l) {
        listenerList.add(BreakpointListener.class, l);
    }

    public void removeBreakpointListener(final BreakpointListener l) {
        listenerList.remove(BreakpointListener.class, l);
    }

    public EventListener[] getListeners(final Class<BreakpointListener> listenerType) {
        return listenerList.getListeners(listenerType);
    }

    public void fireBreakpointSet(final SourcePosition pos) {
        fireBreakpointChanged(new BreakpointEvent(this, pos, Boolean.TRUE));
    }

    public void fireBreakpointCleared(final SourcePosition pos) {
        fireBreakpointChanged(new BreakpointEvent(this, pos, Boolean.FALSE));
    }

    public void fireBreakpointRemoved(final SourcePosition pos) {
        fireBreakpointChanged(new BreakpointEvent(this, pos, null));
    }

    public void fireBreakpointChanged(final BreakpointEvent e) {
        // Guaranteed to return a non-null array
        final Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == BreakpointListener.class) {
                ((BreakpointListener) listeners[i + 1]).breakpointChanged(e);
            }
        }
    }

}
