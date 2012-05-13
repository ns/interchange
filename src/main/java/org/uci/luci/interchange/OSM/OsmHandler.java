package org.uci.luci.interchange;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class OsmHandler extends DefaultHandler{
    
    public boolean isLoaded = false;
    public OpenStreetMap openStreetMap; 
    
    private Way way;
    private Node node;
    private Tag tag;
    
	public OsmHandler (){
		super();
	}
	
	public void startDocument() {
		openStreetMap = new OpenStreetMap();
	}

    public void endDocument() {
    	openStreetMap.setPathNodes();
    }
    public void startElement  (String uri, String name_, String name, Attributes atts) throws SAXException{
      // System.out.println("name = " + name + " qName = " + qName);
    	if (name.equals ("osm")){
    		openStreetMap.setGenerator(atts.getValue("generator"));
    		openStreetMap.setVersion(atts.getValue("version"));
      	return;
    	}
    	else if (name.equals("bounds")){
    		openStreetMap.setMinlat(atts.getValue("minlat"));
    		openStreetMap.setMaxlat(atts.getValue("maxlat"));
    		openStreetMap.setMinlon(atts.getValue("minlon"));
    		openStreetMap.setMaxlon(atts.getValue("maxlon"));
    		
    	return;
    	} else if(name.equals("node")){
        // System.out.println("...?");
    		//increment node counter
    		node = new Node();
    	    node.lat = Double.parseDouble(atts.getValue("lat"));
    	    node.id =atts.getValue("id");
    	    node.lon = Double.parseDouble(atts.getValue("lon"));
          // node.x = Float.parseFloat(node.lon);
          // node.y = Float.parseFloat(node.lat);
    		return;	
    	} else if(name.equals("relation")){
    		openStreetMap.addRelation();
    		return;
    	} else if(name.equals("way")){
    		way = new Way();
    		return;
    	} else if(name.equals("nd")){
    		way.addNode(atts.getValue("ref"));
    		return;
    	
    	} else if(name.equals("tag")){
    		tag = new Tag();
    		tag.k = atts.getValue("k");
    		tag.v = atts.getValue("v");
    		if(way!=null)way.addTag(tag);
    		else node.addTag(tag);
    		return;
    	}
    }
    
    public void endElement (String uri, String name_, String name){
    	if(name.equals("way")){
    		
    		openStreetMap.addWay(way);
    		return;
    	} else if(name.equals("node")){
    		openStreetMap.addNode(node);
    		
    		return;
    	}
    }
    
    public void characters (char ch[], int start, int length){
    }
}
