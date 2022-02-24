// 
// Filename : ObjectDetailsPane.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

class TerminatorDetailsPane extends JTabbedPane {

    TerminatorDetailsPane(final TerminatorList terminatorList) {
        addTab("Terminator Services", new JScrollPane(new TerminatorServiceList(terminatorList)));
    }
}
