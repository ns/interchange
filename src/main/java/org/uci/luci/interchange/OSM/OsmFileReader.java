package org.uci.luci.interchange.OSM;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class OsmFileReader {
	private String xmlfile = null;
	private InputStream xmlInputStream = null;
	public OsmHandler osmHandler;
	public OsmErrorHandler osmErrorHandler;
	public String IOError = "";
	public String SAXError = "";
	public String GeneralError = "";

	public OsmFileReader(String mapFile) {
		xmlfile = "lib/" + mapFile;

		// instantiate our handler
		osmHandler = new OsmHandler();
		// instantiate our error handler
		osmErrorHandler = new OsmErrorHandler();
	}

	public String getXMLString() {
		if (xmlInputStream != null) {
			return xmlfile;
		}
		return null;
	}

	public boolean parseStructure() throws ParserConfigurationException {
		try {
			// create the factory
			SAXParserFactory factory = SAXParserFactory.newInstance();
			// create a parser
			SAXParser parser = factory.newSAXParser();

			// create the reader (scanner)
			XMLReader xmlreader = parser.getXMLReader();

			// assign our handler
			xmlreader.setContentHandler(osmHandler);

			// assign our ErrorHandler
			xmlreader.setErrorHandler(osmErrorHandler);

			xmlInputStream = new FileInputStream(xmlfile);

			xmlreader.parse(new InputSource(xmlInputStream));
			osmHandler.isLoaded = true;

		} catch (SAXException e) {
			SAXError = e.getMessage();
			// read someting more
			return false;
		} catch (IOException e) {
			IOError = e.getMessage();
			return false;
		}
		return true;
	}
}
