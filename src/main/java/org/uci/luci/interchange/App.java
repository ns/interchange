package org.uci.luci.interchange;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
        OsmFileReader osmFileReader = new OsmFileReader();
        osmFileReader.parseStructure();
        
        OpenStreetMap openStreetMap = osmFileReader.osmHandler.openStreetMap;
        
        AppWindow appWindow = new AppWindow(openStreetMap);
    }
}
