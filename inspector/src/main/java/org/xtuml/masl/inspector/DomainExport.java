// 
// Filename : DomainExport.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector;


import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xtuml.masl.inspector.processInterface.DomainData;
import org.xtuml.masl.inspector.processInterface.DomainMetaData;
import org.xtuml.masl.inspector.processInterface.ProcessConnection;
import org.xtuml.masl.inspector.processInterface.ProcessMetaData;


public class DomainExport
{

  public DomainExport ( final DomainMetaData domainMeta, final File file )
  {
    try
    {
      System.out.println("Exporting domain " + domainMeta.getName() + " to file " + file.getPath());

      final Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
      final DomainData data = domainMeta.getDomainData();
      data.readFromProcess();
      document.appendChild(data.toXML(document));
      final FileOutputStream fileStream = new FileOutputStream(file);

      final Result xmlOutput = new StreamResult(fileStream);
      final Source xmlSource = new DOMSource(document);
      transformer.transform(xmlSource, xmlOutput);
      fileStream.flush();
      fileStream.close();
      System.out.println("Domain " + domainMeta.getName() + " exported to file " + file.getPath());
    }
    catch ( final Exception e )
    {
      e.printStackTrace();
    }
  }


  public static void main ( final String[] args ) throws Exception
  {
    System.setProperty("java.awt.headless", "true");
    ProcessConnection.getConnection().setCatchConsole(false);
    final ProcessMetaData procMeta = ProcessConnection.getConnection().getMetaData();

    final java.util.Set<String> commandLine = new java.util.HashSet<String>();
    commandLine.addAll(java.util.Arrays.asList(args));

    if ( commandLine.contains("-l") )
    {
      commandLine.remove("-l");
      System.out.print(procMeta.getName() + ": ");
      for ( int i = 0; i < procMeta.getDomains().length; ++i )
      {
        System.out.print(procMeta.getDomains()[i].getName() + " ");
      }
      System.out.println();
    }

    if ( commandLine.contains("-a") )
    {
      commandLine.remove("-a");
      final java.util.Set<String> ignoredDomains = org.xtuml.masl.inspector.Preferences.getIgnoredDomains();
      for ( final DomainMetaData domain : procMeta.getDomains() )
      {
        if ( !ignoredDomains.contains(domain.getName()) )
        {
          commandLine.add(domain.getName());
        }
      }
    }

    for ( final String domainName : commandLine )
    {
      final DomainMetaData domainMeta = procMeta.getDomain(domainName);
      if ( domainMeta == null )
      {
        System.err.println("Error - Cannot find domain " + domainName);
        System.exit(-1);
      }
      System.out.println("Dumping " + domainMeta.getName());
      new DomainExport(domainMeta, new File(procMeta.getName() + "_" + domainMeta.getName() + ".xml"));

    }


  }
}
