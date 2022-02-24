// 
// Filename : WeakProcessStatusListener.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

public class WeakProcessStatusListener implements ProcessStatusListener {

    private final java.lang.ref.WeakReference<ProcessStatusListener> weakListener;

    public WeakProcessStatusListener(final ProcessStatusListener listener) {
        weakListener = new java.lang.ref.WeakReference<ProcessStatusListener>(listener);
    }

    private ProcessStatusListener getListener() {
        return weakListener.get();
    }

    @Override
    public void processStatusChanged(final ProcessStatusEvent e) {
        final ProcessStatusListener listener = getListener();
        if (listener == null) {
            ((ProcessConnection) e.getSource()).removeProcessStatusListener(this);
        } else {
            listener.processStatusChanged(e);
        }
    }
}
