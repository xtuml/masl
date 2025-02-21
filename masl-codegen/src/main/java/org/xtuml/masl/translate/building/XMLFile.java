/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.building;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.Writer;

public class XMLFile extends ReferencedFile implements WriteableFile {

    private final Document document;

    public XMLFile(final FileGroup parent, final File file) {
        super(parent, file);
        Document doc = null;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (final ParserConfigurationException e) {
            e.printStackTrace();
        }
        this.document = doc;
    }

    public Document getDocument() {
        return document;
    }

    @Override
    public void writeCode(final Writer writer) throws IOException {
        Transformer transformer;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            final Result xsdOutput = new StreamResult(writer);
            final Source xsdSource = new DOMSource(document);
            transformer.transform(xsdSource, xsdOutput);
            writer.flush();
            writer.close();
        } catch (final TransformerException e) {
            throw new IOException(e);
        }

    }

}
