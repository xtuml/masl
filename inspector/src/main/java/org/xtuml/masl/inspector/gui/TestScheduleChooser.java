//
// Filename : TestScheduleChooser.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.xtuml.masl.inspector.processInterface.ProcessConnection;

public class TestScheduleChooser extends JFileChooser {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final static FileFilter scheduleFilter = new ExtensionFileFilter(new String[] { "scn", "schedule" },
            "Schedule Files");

    private TestScheduleChooser() {
        setDialogTitle("Run Test Schedule");

        addChoosableFileFilter(scheduleFilter);
        setAcceptAllFileFilterUsed(true);
        setFileFilter(scheduleFilter);

        try {
            setCurrentDirectory(ProcessConnection.getConnection().getMetaData().getDirectory());
        } catch (final java.rmi.RemoteException e) {
        }

        setApproveButtonText("Run");
    }

    private static TestScheduleChooser chooser = new TestScheduleChooser();

    public static File getFile() {
        if (chooser.showDialog(null, null) != JFileChooser.APPROVE_OPTION) {
            return null;
        } else {
            return chooser.getSelectedFile();
        }
    }
}
