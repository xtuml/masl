//
// Filename : ProcessMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import java.io.File;

public abstract class ProcessMetaData {

    protected String name;

    public String getName() {
        return name;
    }

    public abstract DomainMetaData getDomain(String domainName);

    public abstract DomainMetaData[] getDomains();

    public abstract Plugin getPlugin(String pluginName);

    public abstract Plugin[] getPlugins();

    private File directory = null;

    public void setDirectory(final File directory) {
        this.directory = directory;
    }

    public File getDirectory() {
        return directory == null ? getDefaultDirectory() : directory;
    }

    protected File defaultDirectory;

    protected File getDefaultDirectory() {
        return defaultDirectory;
    }

}
