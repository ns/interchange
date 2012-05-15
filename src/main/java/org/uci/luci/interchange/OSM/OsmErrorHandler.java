package org.uci.luci.interchange;

import org.xml.sax.ErrorHandler; 
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class OsmErrorHandler implements ErrorHandler{
    
	
	
	public Exceptions exceptions = new Exceptions();
    
	
	public void error(SAXParseException exception) throws SAXException {
		exceptions.Error = exception.getMessage();
		
	}

	
	public void fatalError(SAXParseException exception) throws SAXException {
		exceptions.FatalError = exception.getMessage();
	}

	
	public void warning(SAXParseException exception) throws SAXException {
		exceptions.Warning = exception.getMessage();
	}

	class Exceptions {
	   public String Error = "";
	   public String FatalError = "";
	   public String Warning = "";
	}
}
