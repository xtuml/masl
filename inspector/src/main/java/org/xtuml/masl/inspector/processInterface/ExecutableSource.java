//
// Filename : ExecutableSource.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

public abstract class ExecutableSource {

    static public class SourceFileFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter {

        private final String description;
        private final Pattern[] patterns;

        private String onlyMatch;

        public SourceFileFilter(final String description, final Pattern... patterns) {
            this.description = description;
            this.patterns = patterns;
            this.onlyMatch = null;
        }

        public SourceFileFilter(final String description, final String onlyMatch) {
            this.description = description;
            this.patterns = null;
            this.onlyMatch = onlyMatch;
        }

        public void setOnlyMatch(final String fileName) {
            onlyMatch = fileName;
        }

        @Override
        public boolean accept(final File f) {
            if (f == null) {
                return false;
            } else if (f.isDirectory()) {
                return true;
            } else if (onlyMatch == null) {
                final String filename = f.getName();
                boolean match = false;
                for (int i = 0; !match && i < patterns.length; i++) {
                    match = patterns[i].matcher(filename).matches();
                }
                return match;
            } else {
                return onlyMatch.equals(f.getName());
            }
        }

        @Override
        public String getDescription() {
            return description;
        }
    }

    protected File sourceFile = null;
    protected String defaultSourceFileName = null;
    protected SourceFileFilter sourceFileFilter = null;
    private boolean sourceFileMismatch = false;

    public boolean isSourceFileMismatch() {
        return sourceFileMismatch;
    }

    public abstract SourcePosition getSourcePosition(int lineNo);

    public abstract File getDirectory();

    public void setSourceFile(final File file) {
        sourceFile = file;
    }

    public abstract LocalVariableMetaData[] getLocalVariables();

    public abstract ParameterMetaData[] getParameters();

    private static String getMd5Sum(final File file) throws IOException {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            final InputStream input = new BufferedInputStream(new FileInputStream(file));
            final byte[] bytes = new byte[1024];
            int read = input.read(bytes);
            while (read != -1) {
                md.update(bytes, 0, read);
                read = input.read(bytes);
            }
            // Convert to hex string with leading zeros
            final String hex = new BigInteger(1, md.digest()).toString(16);
            return "00000000000000000000000000000000".substring(hex.length()) + hex;
        } catch (final NoSuchAlgorithmException e) {
            assert false : "MD5 algorithm not found";
            return null;
        }
    }

    public File getSourceFile() {
        if (sourceFile == null) {
            final File[] files = getDirectory().listFiles(getSourceFileFilter());

            for (int i = 0; i < files.length; i++) {
                // Find the first non-directory in the list.
                // If more than one, we can't choose, so return null;
                if (!files[i].isDirectory()) {
                    if (sourceFile == null) {
                        sourceFile = files[i];
                    } else {
                        return null;
                    }
                }
            }
        }

        try {
            if (sourceFile != null && !getFileHash().equals(getMd5Sum(sourceFile))) {
                sourceFileMismatch = true;
            }
        } catch (final IOException e) {
            sourceFile = null;
        }
        return sourceFile;
    }

    public abstract void initSourceFileFilter();

    public SourceFileFilter getSourceFileFilter() {
        if (sourceFileFilter == null) {
            initSourceFileFilter();
        }
        return sourceFileFilter;
    }

    public abstract String getFullyQualifiedName();

    public String getFileName() {
        return fileName;
    }

    public String getFileHash() {
        return fileHash;
    }

    protected String fileName;
    protected String fileHash;

}
