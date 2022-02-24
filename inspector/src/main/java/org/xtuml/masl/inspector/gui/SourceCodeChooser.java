// 
// Filename : SourceCodeChooser.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.gui;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.xtuml.masl.inspector.processInterface.ExecutableSource;

public class SourceCodeChooser extends JFileChooser {

    private final static FileFilter serviceFilter = new ExtensionFileFilter("svc", "Object Service");
    private final static FileFilter stateActionFilter = new ExtensionFileFilter("al", "State Action");
    private final static FileFilter bridgeFilter = new ExtensionFileFilter(new String[] { "fn", "tr", "svc" },
            "Domain Service/Bridge");
    private final static FileFilter externalFilter = new ExtensionFileFilter("ext", "External");
    private final static FileFilter scenarioFilter = new ExtensionFileFilter("scn", "Scenario");

    public SourceCodeChooser(final ExecutableSource source) {
        addChoosableFileFilter(source.getSourceFileFilter());
        addChoosableFileFilter(serviceFilter);
        addChoosableFileFilter(stateActionFilter);
        addChoosableFileFilter(bridgeFilter);
        addChoosableFileFilter(stateActionFilter);
        addChoosableFileFilter(externalFilter);
        addChoosableFileFilter(scenarioFilter);
        setAcceptAllFileFilterUsed(true);

        setFileFilter(source.getSourceFileFilter());

        setDialogTitle("Open " + source.getFullyQualifiedName() + " Source");
        if (source.getSourceFile() == null) {
            setCurrentDirectory(source.getDirectory());
        } else {
            setCurrentDirectory(source.getSourceFile().getParentFile());
        }
        setApproveButtonText("Open");

    }

    public int showDialog() {
        return showDialog(null, null);
    }

}
