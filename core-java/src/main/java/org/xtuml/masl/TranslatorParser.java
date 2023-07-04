/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ----------------------------------------------------------------------------
 Classification: UK OFFICIAL
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * The Translators that are run as part of a normal domain or process build can
 * be configured on the command line using the -addTranslator -skipTranslator
 * and -onlyTranslator command line options. To supplement this functionality,
 * so that custom Imakefiles are not required to force a set of specific
 * translators to be executed, a translator.xml file can be placed in the custom
 * directory and configured to run the required set of domain specific
 * translators.
 */
public class TranslatorParser {

    private final static String XMLFILE = "custom/translator.xml";
    private final static String NAME_ATTRIBUTE = "name";
    private final static String OPTION_ATTRIBUTE = "option";
    private final static String VALUE_ATTRIBUTE = "value";

    private final List<String> addList = new ArrayList<String>();
    private final List<String> skipList = new ArrayList<String>();
    private final List<String> cmdlineList = new ArrayList<String>();

    private final Map<String, Properties> translatorProperties = new HashMap<String, Properties>();

    private boolean isOverride = false;
    private InputStream xmlStream = null;

    TranslatorParser(final InputStream xmlStream) {
        this.xmlStream = xmlStream;
        parse();
    }

    TranslatorParser() {
    }

    TranslatorParser(final File xmlFile) {
        try {
            if (xmlFile.canRead()) {
                xmlStream = new FileInputStream(xmlFile);
                parse();
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getCmdLineList() {
        return Collections.unmodifiableList(cmdlineList);
    }

    public String[] getCmdLineArgs() {
        final ArrayList<String> cmdLineArgs = new ArrayList<String>();
        for (final String cmdLine : cmdlineList) {
            final StringTokenizer tokeniser = new StringTokenizer(cmdLine);
            while (tokeniser.hasMoreTokens()) {
                cmdLineArgs.add(tokeniser.nextToken());
            }
        }
        return cmdLineArgs.toArray(new String[cmdLineArgs.size()]);
    }

    public List<String> getTranslatorAddList() {
        return Collections.unmodifiableList(addList);
    }

    public List<String> getTranslatorSkipList() {
        return Collections.unmodifiableList(skipList);
    }

    public Map<String, Properties> getTranslatorProperties() {
        return translatorProperties;
    }

    private void addToSkipList(final String translator) {
        skipList.add(translator);
    }

    private void addToAddList(final String translator) {
        addList.add(translator);
    }

    private void addToCmdLineList(final String cmdLine) {
        cmdlineList.add(cmdLine);
    }

    private void addProperty(final String translatorName, final String propertyName, final String propertyValue) {
        if (!translatorProperties.containsKey(translatorName)) {
            translatorProperties.put(translatorName, new Properties());
        }
        translatorProperties.get(translatorName).setProperty(propertyName, propertyValue);
    }

    /**
     * Define an enum class to handle the detection of the different xml tags
     * allowed in the translator.xml file.
     */
    private enum TranslatorNodeNames {
        skip() {
            @Override
            void decodeAttributes(final TranslatorParser parent, final Element element) {
                final String translator = element.getAttribute(NAME_ATTRIBUTE);
                parent.addToSkipList(translator);
            }
        },

        only() {
            // deprecated - replaced by override and add
            @Override
            void decodeAttributes(final TranslatorParser parent, final Element element) {
                final String translator = element.getAttribute(NAME_ATTRIBUTE);
                parent.addToAddList(translator);
                parent.setOverride();
            }
        },

        add() {
            @Override
            void decodeAttributes(final TranslatorParser parent, final Element element) {
                final String translator = element.getAttribute(NAME_ATTRIBUTE);
                parent.addToAddList(translator);
            }
        },

        configure() {
            @Override
            void decodeAttributes(final TranslatorParser parent, final Element element) {
                // Just a place holder, so that properties can be associated with a
                // specific
                // translator that doesn't require anyother of the skip/add/only
                // translator
                // tags. It can therefore be used to configure one of the default
                // translators
                // or a translator that will be invoked due to a dependency.
            }
        },

        cmdline() {
            @Override
            void decodeAttributes(final TranslatorParser parent, final Element element) {
                final String cmdline = element.getAttribute(OPTION_ATTRIBUTE);
                parent.addToCmdLineList(cmdline);
            }
        },

        override() {
            @Override
            void decodeAttributes(final TranslatorParser parent, final Element element) {
                parent.setOverride();
            }
        },

        property() {
            @Override
            void decodeAttributes(final TranslatorParser parent, final Element element) {
                // Assume that xsd has validated the xml document and so can assume that
                // any property tag is nested under a parent tag that contains a name
                // attribute (this will contain the translator the property(s) should
                // be associated with.
                final String propertyName = element.getAttribute(NAME_ATTRIBUTE);
                final String propertyValue = element.getAttribute(VALUE_ATTRIBUTE);
                final String translatorName = ((Element) element.getParentNode()).getAttribute(NAME_ATTRIBUTE);
                parent.addProperty(translatorName, propertyName, propertyValue);
            }
        },

        ;

        /**
         * Using the specified xml element find the associated name attribute and
         * extract the Translator name text.
         * <p>
         * <p>
         * XML file parser source
         * <p>
         * The XML node to decode
         */
        private static void decodeTag(final TranslatorParser parent, final Element element) {
            valueOf(TranslatorNodeNames.class, element.getNodeName()).decodeAttributes(parent, element);
        }

        abstract void decodeAttributes(TranslatorParser parent, Element element);
    }

    private class SAXErrorHandler implements ErrorHandler {

        @Override
        public void error(final SAXParseException e) throws SAXException {
            System.err.println(formatException("Error", e));
        }

        @Override
        public void fatalError(final SAXParseException e) throws SAXException {
            System.err.println(formatException("Fatal Error", e));
            throw e;
        }

        @Override
        public void warning(final SAXParseException e) throws SAXException {
            System.err.println(formatException("Warning", e));
        }

        private String formatException(final String type, final SAXParseException e) {
            return type +
                   ": " +
                   XMLFILE +
                   ": " +
                   e.getLineNumber() +
                   ":" +
                   e.getColumnNumber() +
                   ": " +
                   e.getLocalizedMessage();
        }
    }

    private void parse() {
        if (xmlStream != null) {
            try {
                final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                final String
                        xsdFile =
                        TranslatorParser.class.getPackage().getName().replaceAll("\\.", "/") + "/translator.xsd";
                final Schema
                        schema =
                        schemaFactory.newSchema(new StreamSource(ClassLoader.getSystemResource(xsdFile).openStream()));

                final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setSchema(schema);
                factory.setIgnoringElementContentWhitespace(true);
                factory.setIgnoringComments(true);
                final DocumentBuilder builder = factory.newDocumentBuilder();
                builder.setErrorHandler(new SAXErrorHandler());

                final Document document = builder.parse(xmlStream);

                for (int i = 0; i < document.getChildNodes().getLength(); ++i) {
                    final Node curNode = document.getChildNodes().item(i);
                    if (curNode instanceof Element curElem) {
                      for (int j = 0; j < curElem.getChildNodes().getLength(); ++j) {
                            final Node childNode = curElem.getChildNodes().item(j);
                            if (childNode instanceof Element element) {
                              TranslatorNodeNames.decodeTag(this, element);
                                for (int k = 0; k < element.getChildNodes().getLength(); ++k) {
                                    final Node minorNode = element.getChildNodes().item(k);
                                    if (minorNode instanceof Element childElement) {
                                      TranslatorNodeNames.decodeTag(this, childElement);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    static final void main(final String[] args) {
        final TranslatorParser parser = new TranslatorParser();
        parser.parse();
    }

    public boolean isOverride() {
        return isOverride;
    }

    private void setOverride() {
        this.isOverride = true;
    }
}
