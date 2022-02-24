//
// Filename : WeakBreakpointListener.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector;

public class WeakBreakpointListener implements BreakpointListener {

    private final java.lang.ref.WeakReference<BreakpointListener> weakListener;

    public WeakBreakpointListener(final BreakpointListener listener) {
        weakListener = new java.lang.ref.WeakReference<BreakpointListener>(listener);
    }

    private BreakpointListener getListener() {
        return weakListener.get();
    }

    @Override
    public void breakpointChanged(final BreakpointEvent e) {
        final BreakpointListener listener = getListener();
        if (listener == null) {
            ((BreakpointController) e.getSource()).removeBreakpointListener(this);
        } else {
            listener.breakpointChanged(e);
        }
    }
}
