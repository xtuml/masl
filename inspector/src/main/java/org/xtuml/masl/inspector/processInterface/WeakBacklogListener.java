//
// Filename : WeakProcessStatusListener.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

public class WeakBacklogListener implements BacklogListener {

    private final java.lang.ref.WeakReference<BacklogListener> weakListener;

    public WeakBacklogListener(final BacklogListener listener) {
        weakListener = new java.lang.ref.WeakReference<BacklogListener>(listener);
    }

    private BacklogListener getListener() {
        return weakListener.get();
    }

    @Override
    public void backlogChanged(final BacklogEvent e) {
        final BacklogListener listener = getListener();
        if (listener == null) {
            ((ProcessConnection) e.getSource()).removeBacklogListener(this);
        } else {
            listener.backlogChanged(e);
        }
    }
}
