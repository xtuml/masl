//
// UK Crown Copyright (c) 2013. All Rights Reserved.
//
package org.xtuml.masl.translate.build;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;


public class XMLFile extends ReferencedFile
    implements WriteableFile
{

  private final Document document;

  public XMLFile ( final FileGroup parent, final File file )
  {
    super(parent,file);
    Document doc = null;
    try
    {
      doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    }
    catch ( final ParserConfigurationException e )
    {
      e.printStackTrace();
    }
    this.document = doc;
  }

  public Document getDocument ()
  {
    return document;
  }

  @Override
  public void writeCode ( final Writer writer ) throws IOException
  {
    Transformer transformer;
    try
    {
      transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

      final Result xsdOutput = new StreamResult(writer);
      final Source xsdSource = new DOMSource(document);
      transformer.transform(xsdSource, xsdOutput);
      writer.flush();
      writer.close();
    }
    catch ( final TransformerException e )
    {
      throw new IOException(e);
    }

  }

}
