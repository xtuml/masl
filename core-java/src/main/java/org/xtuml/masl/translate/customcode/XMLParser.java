//
// File: DomainTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.customcode;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xtuml.masl.translate.Alias;
import org.xtuml.masl.translate.Default;
import org.xtuml.masl.translate.build.BuildSet;
import org.xtuml.masl.translate.build.FileGroup;
import org.xtuml.masl.translate.build.ReferencedFile;



@Alias("CustomCode")
@Default
public class XMLParser
{

  private class SAXErrorHandler
      implements ErrorHandler
  {

    @Override
    public void error ( final SAXParseException e ) throws SAXException
    {
      System.err.println(formatException("Error", e));
    }

    @Override
    public void fatalError ( final SAXParseException e ) throws SAXException
    {
      System.err.println(formatException("Fatal Error", e));
      throw e;
    }

    @Override
    public void warning ( final SAXParseException e ) throws SAXException
    {
      System.err.println(formatException("Warning", e));
    }

    private String formatException ( final String type, final SAXParseException e )
    {
      return type + ": " + XMLFILE + ": " + e.getLineNumber() + ":" + e.getColumnNumber() + ": " + e.getLocalizedMessage();
    }

  }

  private enum NodeName
  {
    library,
    archive,
    group,
    file,
    headers,
    dependency,
    includepath,
    libpath,
    executable,
    publish,
    groupref,
    skipfile,
    skipdependency,
    skipexecutable,
    skiplibrary,
    skiparchive,
    etc,
    top,
    bin,
    lib,
    share,
    include,
    doc

  }

  private static String  XMLFILE  = "custom/custom.xml";

  private static String  NAME     = "name";

  private static String  LITERAL  = "literal";

  private static String  SUBDIR   = "subdir";

  private final BuildSet buildSet;

  public XMLParser ( final BuildSet buildSet )
  {
    this.buildSet = buildSet;
  }

  public boolean parse ()
  {
    final File xmlFile = new File(XMLFILE);
    if ( !xmlFile.canRead() )
    {
      return false;
    }

    try
    {
      final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

      final String xsdFile = XMLParser.class.getPackage().getName().replaceAll("\\.", "/") + "/custom.xsd";

      final Schema schema = schemaFactory.newSchema(new StreamSource(ClassLoader.getSystemResource(xsdFile).openStream()));

      final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setSchema(schema);
      factory.setIgnoringElementContentWhitespace(true);
      factory.setIgnoringComments(true);
      final DocumentBuilder builder = factory.newDocumentBuilder();
      builder.setErrorHandler(new SAXErrorHandler());

      final Document document = builder.parse(xmlFile);

      for ( int i = 0; i < document.getChildNodes().getLength(); ++i )
      {
        final Node curNode = document.getChildNodes().item(i);
        for ( int j = 0; j < curNode.getChildNodes().getLength(); ++j )
        {
          final Node node = curNode.getChildNodes().item(j);
          if ( node instanceof Element )
          {
            final Element element = (Element)node;
            switch ( NodeName.valueOf(element.getNodeName()) )
            {
              case group:
              case executable:
              case library:
              case archive:
                parseGroup(getNamedGroup(element), element);
                break;
              case publish:
                parsePublish(element);
                break;
              case skipexecutable:
                buildSet.skipExecutable(getName(element));
                break;
              case skiplibrary:
                buildSet.skipLibrary(getName(element));
                break;
              case skiparchive:
                buildSet.skipArchive(getName(element));
                break;
              case headers:
                parseHeaders(element);
                break;
              case includepath:
                parseIncludePath(element);
                break;
              default:
                assert false : "Schema mismatch";
            }
          }
        }
      }
    }
    catch ( final Exception e )
    {
      e.printStackTrace();
    }
    return true;
  }


  private void parseDependency ( final FileGroup group, final Element element )
  {
    group.addDependency(getNamedGroup(element));
  }

  private String getName ( final Element element )
  {
    return element.getAttribute(NAME);
  }

  private FileGroup getNamedGroup ( final Element element )
  {
    final String name = element.getAttribute(NAME);
    return FileGroup.getFileGroup(name);
  }


  private void parseHeaders ( final Element curNode )
  {
    final FileGroup headers = FileGroup.getFileGroup(curNode.getAttribute("name"));

    for ( int j = 0; j < curNode.getChildNodes().getLength(); ++j )
    {
      final Node node = curNode.getChildNodes().item(j);
      if ( node instanceof Element )
      {
        final Element element = (Element)node;
        switch ( NodeName.valueOf(element.getNodeName()) )
        {
          case file:
            new ReferencedFile(headers,getName(element));
            break;
          default:
            assert false : "Schema mismatch";
        }
      }
    }
  }

  private void parseIncludePath ( final Element curNode )
  {
    if ( curNode.getAttribute(NAME).length() > 0 )
    {
      buildSet.addInclude(curNode.getAttribute(NAME));
    }
    else if ( curNode.getAttribute(LITERAL).length() > 0 )
    {
      buildSet.addIncludeDir(new File(curNode.getAttribute(LITERAL)));
    }
  }

  private String getSubdir ( final Element element )
  {
    return element.getAttribute(SUBDIR);
  }

  void parsePublish ( final Element curNode )
  {
    for ( int j = 0; j < curNode.getChildNodes().getLength(); ++j )
    {
      final Node node = curNode.getChildNodes().item(j);
      if ( node instanceof Element )
      {
        final Element element = (Element)node;
        final FileGroup group = getNamedGroup(element);
        final String subdir = getSubdir(element);
        parseGroup(group, element);
        switch ( NodeName.valueOf(element.getNodeName()) )
        {
          case etc:
            buildSet.addPublishedEtc(subdir, group);
            break;
          case share:
            buildSet.addPublishedShare(subdir, group);
            break;
          case doc:
            buildSet.addPublishedDoc(subdir, group);
            break;
          case include:
            buildSet.addPublishedInclude(subdir, group);
            break;
          case top:
            buildSet.addPublishedTopLevel(subdir, group);
            break;
          case bin:
            buildSet.addPublishedBin(group);
            break;
          case lib:
            buildSet.addPublishedLib(group);
            break;
          default:
            assert false : "Schema mismatch";

        }
      }
    }
  }

  void parseGroup ( final FileGroup group, final Element curNode )
  {
    for ( int j = 0; j < curNode.getChildNodes().getLength(); ++j )
    {
      final Node node = curNode.getChildNodes().item(j);
      if ( node instanceof Element )
      {
        final Element element = (Element)node;
        switch ( NodeName.valueOf(element.getNodeName()) )
        {
          case file:
            new ReferencedFile(group,getName(element));
            break;
          case skipfile:
            group.skipFile(new File(getName(element)));
            break;
          case groupref:
            group.includeGroup(getNamedGroup(element));
            break;
          case dependency:
            parseDependency(group, element);
            break;
          case skipdependency:
            group.skipDependency(getName(element));
            break;
          default:
            assert false : "Schema mismatch";
        }
      }
    }
  }


}
