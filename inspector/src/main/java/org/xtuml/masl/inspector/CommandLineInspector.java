// Filename : CreateDomainSchema.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved
//
//
package org.xtuml.masl.inspector;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xtuml.masl.inspector.processInterface.DomainMetaData;
import org.xtuml.masl.inspector.processInterface.InstanceData;
import org.xtuml.masl.inspector.processInterface.InstanceDataListener;
import org.xtuml.masl.inspector.processInterface.ObjectMetaData;
import org.xtuml.masl.inspector.processInterface.ProcessConnection;

class CommandLineInspector {

    public static void help() {
        System.out.println("Commands: ");
        System.out.println("  ca [off]  - catch console");
        System.out.println("  tl [off]  - trace lines");
        System.out.println("  tb [off]  - trace blocks");
        System.out.println("  tev [off] - trace events");
        System.out.println("  tex [off] - trace exeptions");
        System.out.println("  p <domain> <obj> - print population");
        System.out.println("  q         - quit");
    }

    public static void printPopulation(final String command) throws Exception {
        final java.util.StringTokenizer parser = new java.util.StringTokenizer(command);

        parser.nextToken(); // Skip 'p' command
        final String domain = parser.hasMoreTokens() ? parser.nextToken() : "";
        final String object = parser.hasMoreTokens() ? parser.nextToken() : "";

        final DomainMetaData domainMeta = ProcessConnection.getConnection().getMetaData().getDomain(domain);
        if (domainMeta == null) {
            System.out.println("Available Domains: ");
            for (int i = 0; i < ProcessConnection.getConnection().getMetaData().getDomains().length; ++i) {
                System.out.println("  " + ProcessConnection.getConnection().getMetaData().getDomains()[i].getName());
            }
            return;
        }

        final ObjectMetaData objectMeta = domainMeta.getObject(object);
        if (objectMeta == null) {
            System.out.println("Available Objects: ");
            for (final ObjectMetaData obj : domainMeta.getObjects()) {
                System.out.println("  " + obj.getName());
            }
            return;
        }

        final Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        final Node domainNode = document.createElement(domain);
        document.appendChild(domainNode);

        ProcessConnection.getConnection().getInstanceData(objectMeta, new InstanceDataListener() {

            @Override
            public void setInstanceCount(final int count) {
                System.out.println(count + " instances.");
            }

            @Override
            public boolean addInstanceData(final InstanceData instance) {
                domainNode.appendChild(instance.toXML(document));
                return true;
            }

            @Override
            public void finished() {
            }
        });

        final Result xmlOutput = new StreamResult(System.out);
        final Source xmlSource = new DOMSource(document);
        transformer.transform(xmlSource, xmlOutput);

    }

    public static void main(final String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");

        // Can't redirect input, as we need it here!
        ConsoleRedirect.inputSource = null;
        ProcessConnection.getConnection().setCatchConsole(false);

        final BufferedReader commandLine = new BufferedReader(new InputStreamReader(System.in));
        help();

        System.out.print("? ");
        String command = commandLine.readLine();

        while (!command.equals("q")) {
            if (command.equals("ca")) {
                System.out.println("Catch Console On");
                ProcessConnection.getConnection().setCatchConsole(true);
            } else if (command.equals("ca off")) {
                System.out.println("Catch Console Off");
                ProcessConnection.getConnection().setCatchConsole(false);
            } else if (command.equals("tl")) {
                System.out.println("Trace Lines On");
                ProcessConnection.getConnection().setTraceLines(true);
            } else if (command.equals("tl off")) {
                System.out.println("Trace Lines Off");
                ProcessConnection.getConnection().setTraceLines(false);
            } else if (command.equals("tb")) {
                System.out.println("Trace Blocks On");
                ProcessConnection.getConnection().setTraceBlocks(true);
            } else if (command.equals("tb off")) {
                System.out.println("Trace Blocks Off");
                ProcessConnection.getConnection().setTraceBlocks(false);
            } else if (command.equals("tev")) {
                System.out.println("Trace Events On");
                ProcessConnection.getConnection().setTraceEvents(true);
            } else if (command.equals("tev off")) {
                System.out.println("Trace Events Off");
                ProcessConnection.getConnection().setTraceEvents(false);
            } else if (command.equals("tex")) {
                System.out.println("Trace Exceptions On");
                ProcessConnection.getConnection().setTraceExceptions(true);
            } else if (command.equals("tex off")) {
                System.out.println("Trace Exceptions Off");
                ProcessConnection.getConnection().setTraceExceptions(false);
            } else if (command.length() > 0 && command.substring(0, 1).equals("p")) {
                System.out.println("Printing Population");
                printPopulation(command);
            } else if (!command.equals("")) {
                help();
            }

            System.out.print("? ");
            command = commandLine.readLine();
        }

    }
}
