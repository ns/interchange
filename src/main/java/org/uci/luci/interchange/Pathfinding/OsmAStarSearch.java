package org.uci.luci.interchange.Pathfinding;

import org.uci.luci.interchange.OSM.*;

public class OsmAStarSearch extends AStarSearch {
	OpenStreetMap osm;

	public OsmAStarSearch(OpenStreetMap osm) {
		this.osm = osm;
	}
}
