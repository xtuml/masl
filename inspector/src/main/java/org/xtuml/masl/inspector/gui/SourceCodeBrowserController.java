//
// Filename : SourceCodeBrowserController.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.util.HashMap;
import java.util.Map;

import org.xtuml.masl.inspector.Preferences;
import org.xtuml.masl.inspector.processInterface.Capability;
import org.xtuml.masl.inspector.processInterface.ExecutableSource;
import org.xtuml.masl.inspector.processInterface.ProcessConnection;
import org.xtuml.masl.inspector.processInterface.ProcessStatusListener;
import org.xtuml.masl.inspector.processInterface.SourcePosition;
import org.xtuml.masl.inspector.processInterface.WeakProcessStatusListener;

class SourceCodeBrowserController implements ProcessStatusListener {

    private static SourceCodeBrowserController instance = new SourceCodeBrowserController();

    public static SourceCodeBrowserController getInstance() {
        return instance;
    }

    private SourceCodeBrowserController() {
        ProcessConnection.getConnection().addProcessStatusListener(new WeakProcessStatusListener(this));
    }

    private final Map<ExecutableSource, SourceCodeBrowser> browsers = new HashMap<ExecutableSource, SourceCodeBrowser>();

    public void remove(final ExecutableSource file) {
        browsers.remove(file);
    }

    public void display(final ExecutableSource file) {
        final SourcePosition position = ProcessConnection.getConnection().getCurrentPosition();
        if (position != null && position.getSource() == file) {
            display(file, position.getLineNo());
        } else {
            display(file, SourcePosition.NO_LINE);
        }
    }

    public void display(final ExecutableSource file, final int line) {
        SourceCodeBrowser browser = browsers.get(file);
        if (browser == null) {
            browser = new SourceCodeBrowser(file, line);
            browsers.put(file, browser);
        }
        browser.setState(java.awt.Frame.NORMAL);
        browser.toFront();
    }

    @Override
    public void processStatusChanged(final org.xtuml.masl.inspector.processInterface.ProcessStatusEvent e) {
        if (ProcessConnection.getConnection().getCurrentPosition() != null) {
            if (Capability.GET_STACK.isAvailable() && Preferences.getUseStackBrowser()) {
                StackBrowser.display();
            } else {
                display(ProcessConnection.getConnection().getCurrentPosition().getSource(),
                        ProcessConnection.getConnection().getCurrentPosition().getLineNo());
            }
        }
    }
}
