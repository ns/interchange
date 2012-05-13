package org.uci.luci.interchange;

public class OsmAStarSearch extends AStarSearch {
  OpenStreetMap osm;
  
  public OsmAStarSearch(OpenStreetMap osm) {
    this.osm = osm;
  }
}

