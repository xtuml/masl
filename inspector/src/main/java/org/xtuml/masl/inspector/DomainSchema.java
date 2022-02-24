// 
// Filename : CreateDomainSchema.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xtuml.masl.inspector.processInterface.DomainMetaData;
import org.xtuml.masl.inspector.processInterface.ProcessConnection;
import org.xtuml.masl.inspector.processInterface.ProcessMetaData;

public class DomainSchema {

    public DomainSchema(final DomainMetaData domainMeta, final boolean checkRefIntegrity,
            final boolean checkUniqueIds) {
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (final ParserConfigurationException e) {
            e.printStackTrace();
        }

        document.appendChild(domainMeta.getXMLSchema(document, checkRefIntegrity, checkUniqueIds));
    }

    public Document getDocument() {
        return document;
    }

    private Document document;

    public void writeToFile(final File file) {
        try {
            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            final FileOutputStream fileStream = new java.io.FileOutputStream(file);

            final Result xsdOutput = new StreamResult(fileStream);
            final Source xsdSource = new DOMSource(document);
            transformer.transform(xsdSource, xsdOutput);
            fileStream.flush();
            fileStream.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(final String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        ProcessConnection.getConnection().setCatchConsole(false);
        final ProcessMetaData procMeta = ProcessConnection.getConnection().getMetaData();

        final java.util.Set<String> commandLine = new java.util.HashSet<String>();
        commandLine.addAll(java.util.Arrays.asList(args));

        if (commandLine.contains("-l")) {
            commandLine.remove("-l");
            System.out.print(procMeta.getName() + ": ");
            for (int i = 0; i < procMeta.getDomains().length; ++i) {
                System.out.print(procMeta.getDomains()[i].getName() + " ");
            }
            System.out.println();
        }

        if (commandLine.contains("-a")) {
            commandLine.remove("-a");
            final java.util.Set<String> ignoredDomains = org.xtuml.masl.inspector.Preferences.getIgnoredDomains();
            for (final DomainMetaData domain : procMeta.getDomains()) {
                if (!ignoredDomains.contains(domain.getName())) {
                    commandLine.add(domain.getName());
                }
            }
        }

        for (final String domainName : commandLine) {
            final DomainMetaData domainMeta = procMeta.getDomain(domainName);
            if (domainMeta == null) {
                System.err.println("Error - Cannot find domain " + domainName);
                System.exit(-1);
            }
            System.out.println("Creating " + domainMeta.getName() + " schemas");

            new DomainSchema(domainMeta, true, true).writeToFile(new File(domainMeta.getName() + "_full.xsd"));
            new DomainSchema(domainMeta, false, false).writeToFile(new File(domainMeta.getName() + "_quick.xsd"));
            new DomainSchema(domainMeta, true, false).writeToFile(new File(domainMeta.getName() + "_ref_integ.xsd"));
            new DomainSchema(domainMeta, false, true).writeToFile(new File(domainMeta.getName() + "_unique_id.xsd"));

        }
    }
}
