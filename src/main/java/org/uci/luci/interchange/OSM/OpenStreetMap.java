package org.uci.luci.interchange.OSM;

import org.uci.luci.interchange.Util.*;
import org.uci.luci.interchange.Graph.*;
import org.uci.luci.interchange.Pathfinding.*;

import java.awt.BasicStroke;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class OpenStreetMap {
	/* basic */
	public String minlat, minlon, maxlat, maxlon;
	String version, generator;
	public int nodesnum = 0;
	public int relations = 0;

	// ArrayList<Node> nodes = new ArrayList<Node>();
	public HashMap<String, Node> nodeHash = new HashMap<String, Node>();

	public ArrayList<Way> ways = new ArrayList<Way>();

	/* A star algorithm */
	public Pathfinder Astar;
	public boolean AstarStatus = false;

	public static String separator = "__";

	public OsmAStarSearch AStar2;
	Projection projection;

	public double projectedMinX = -1, projectedMinY = -1;
	public double projectedMaxX = -1, projectedMaxY = -1;

	public double widthInKm, heightInKm;

	public void mergeUnwantedNodes() {

	}

	public void projectUsingProjection(Projection p) {
		projection = p;

		for (Map.Entry<String, Node> entry : nodeHash.entrySet()) {
			Node n = entry.getValue();
			n.x = projection.convertLongToX(n.lon);
			n.y = projection.convertLatToY(n.lat);

			if (projectedMinX == -1 || n.x < projectedMinX)
				projectedMinX = n.x;
			if (projectedMaxX == -1 || n.x > projectedMaxX)
				projectedMaxX = n.x;
			if (projectedMinY == -1 || n.y < projectedMinY)
				projectedMinY = n.y;
			if (projectedMaxY == -1 || n.y > projectedMaxY)
				projectedMaxY = n.y;
		}

		widthInKm = 1;

		heightInKm = 1;
		// widthInKm = Utils.distance(Double.valueOf(getMinlat()),
		// Double.valueOf(getMinlon()), Double.valueOf(getMinlat()),
		// Double.valueOf(getMaxlon()), 'K');
		//
		// heightInKm = Utils.distance(Double.valueOf(getMinlat()),
		// Double.valueOf(getMinlon()), Double.valueOf(getMaxlat()),
		// Double.valueOf(getMinlon()), 'K');
		System.out.println("widthInKm = " + widthInKm + " heightInKm = "
				+ heightInKm);
	}

	public List<Node> nodes() {
		return new ArrayList<Node>(nodeHash.values());
	}

	public void setPathNodes() {
		AStar2 = new OsmAStarSearch(this);
		// Astar = new Pathfinder(nodes);
		// Astar.setCuboidNodes(999999, 999999, 1);
		AstarStatus = true;
	}

	public Node getNode(String _id) {
		return nodeHash.get(_id);
		// List<Node> nodes = nodes();
		//
		// for(int i = 0; i < nodes.size(); i++){
		// if(nodes.get(i).id.equals(_id)){
		// return nodes.get(i);
		// }
		// }
		// return null;
	}

	public void removeNode(String _id) {
		nodeHash.remove(_id);
	}

	public Way getWayByName(String name) {
		for (Way w : ways) {
			if (name.equals(w.getName()))
				return w;
		}
		return null;
	}

	public String[] searchNode(String pattern) {
		String coordinates[] = new String[2];
		// see origin -w- or -n-

		Way w;
		Node n;
		coordinates[0] = "" + 0.0;
		coordinates[1] = "" + 0.0;
		for (int i = 0; i < ways.size(); i++) {
			w = ways.get(i);

			for (int j = 0; j < w.tags.size(); j++) {

				if (w.tags.get(j).k.equals("name")
						&& w.tags.get(j).v.equals(pattern)) {
					// prendi il primo punto che trovi, non è sengato,
					// ovviamente, dal numero civico
					n = getNode(w.nd.get(0));

					if (n != null) {
						coordinates[0] = "" + n.lat;// Double.parseDouble(n.lat);
						coordinates[1] = "" + n.lon;// Double.parseDouble(n.lon);
					} else {
						coordinates[0] = "Nodo non trovato";
						coordinates[1] = w.nd.get(0);// ""+Double.parseDouble(w.nd.get(0));
					}
					return coordinates;

				}

			}
		}
		for (int ii = 0; ii < nodes().size(); ii++) {
			n = nodes().get(ii);

			for (int jj = 0; jj < n.tags.size(); jj++) {
				// eepContent += w.tags.get(j).k +"\n";
				if (n.tags.get(jj).k.equals("name")
						&& n.tags.get(jj).v.equals(pattern)) {
					coordinates[0] = "" + n.lat;
					coordinates[1] = "" + n.lon;
					return coordinates;
				}
			}
		}
		return coordinates;

	}

	public String generateTagString() {
		String deepContent = "";
		Way w;
		Node n;
		for (int i = 0; i < ways.size(); i++) {
			w = ways.get(i);

			for (int j = 0; j < w.tags.size(); j++) {

				if (w.tags.get(j).k.equals("name")
						|| w.tags.get(j).k.equals("amenity")) {
					deepContent += separator + w.tags.get(j).v + "-w-";
				} else
					continue;
			}
		}
		for (int ii = 0; ii < nodes().size(); ii++) {
			n = nodes().get(ii);

			for (int jj = 0; jj < n.tags.size(); jj++) {
				// eepContent += w.tags.get(j).k +"\n";
				if (n.tags.get(jj).k.equals("name")) {
					deepContent += separator + n.tags.get(jj).v + "-n-";
					// deepContent += separator + w.tags.get(j).v;
				} else
					continue;
			}
		}
		return deepContent;
	}

	public String getXML() {
		return ("version : " + version + "\n" + "generator : " + generator
				+ "\n\n" + "bounds : \n" + " - min Lat : " + minlat + "\n"
				+ " - min Lon : " + minlon + "\n" + " - max Lat : " + maxlat
				+ "\n" + " - max Lon : " + maxlon + "\n\n" + "nodes : "
				+ nodes().size() + "\n" + "relations : " + relations + "\n"
				+ "ways : " + ways.size() + "( way 1 : rel "
				+ ways.get(1).getNdSize() + ", tag "
				+ ways.get(1).getTagsSize() + ")");
	}

	public void addNode(Node n) {
		nodeHash.put(n.id, n);
	}

	public void addRelation() {
		relations++;
	}

	public void purgeUnconnectedNodes() {
		ArrayList<String> nodesToRemove = new ArrayList<String>();
		for (Map.Entry<String, Node> entry : nodeHash.entrySet()) {
			Node n = entry.getValue();
			if (n.way == null) {
				nodesToRemove.add(n.id);
			}
		}
		for (String nid : nodesToRemove) {
			removeNode(nid);
		}
	}

	public void addWay(Way w) {
		if (!w.hasTag("highway", "motorway")
				&& !w.hasTag("highway", "motorway_link")
				&& !w.hasTag("highway", "trunk")
				&& !w.hasTag("highway", "trunk_link")
				&& !w.hasTag("highway", "primary")
				&& !w.hasTag("highway", "primary_link")
				&& !w.hasTag("highway", "secondary")
				&& !w.hasTag("highway", "secondary_link")
				&& !w.hasTag("highway", "tertiary")
				&& !w.hasTag("highway", "tertiary_link")
				&& !w.hasTag("highway", "living_street")
				&& !w.hasTag("highway", "residential")
				&& !w.hasTag("highway", "living_street")) {
			return;
		}

		for (int i = 0; i < w.nd.size(); i++) {
			Node n = getNode(w.nd.get(i));
			if (n == null) {
				System.out.println("Not adding way " + w.tags());
				return;
			}
		}
		
		if (w.hasTag("lanes")) {
			w.lanes = Integer.parseInt(w.getTag("lanes"));
		} else {
			String highway = w.getTag("highway");
			if (highway.equals("primary") || highway.equals("primary_link")) {
				w.lanes = 2;
			} else if (highway.equals("residential")) {
				w.lanes = 1;
			} else if (highway.equals("secondary")
					|| highway.equals("tertiary")) {
				w.lanes = 1;
			} else if (highway.equals("motorway_link")) {
				w.lanes = 1;
			} else if (highway.equals("living_street")) {
				w.lanes = 1;
			} else {
				w.lanes = 1;
        // System.out.println("\thighway=" + w.getTag("highway"));
			}
		}

		if (w.hasTag("oneway")) {
			w.oneway = Boolean.parseBoolean(w.getTag("oneway"));
		} else {
			w.oneway = false;
		}

		// connect all these nodes together
		for (int i = 0; i < w.nd.size(); i++) {
			// System.out.println("\t" + i);
			Node n = getNode(w.nd.get(i));

			if (n == null) {
				System.out.println("way " + w.tags());
				System.out.println("couldnt find node " + w.nd.get(i));
			}

			n.way = w;

			// System.out.println("\t...");

			// connect this node to the one before and after it
			if (i != 0) {
				Node prevN = getNode(w.nd.get(i - 1));
				n.addLink(prevN);

				Oracle.registerWay(prevN.id, n.id, w);
			}

			if (i != w.nd.size() - 1) {
				Node nextN = getNode(w.nd.get(i + 1));
				n.addLink(nextN);
			}
		}

		ways.add(w);
	}

	public String getMinlat() {
		return minlat;
	}

	public void setMinlat(String minlat) {
		this.minlat = minlat;
	}

	public String getMinlon() {
		return minlon;
	}

	public void setMinlon(String minlon) {
		this.minlon = minlon;
	}

	public String getMaxlat() {
		return maxlat;
	}

	public void setMaxlat(String maxlat) {
		this.maxlat = maxlat;
	}

	public String getMaxlon() {
		return maxlon;
	}

	public void setMaxlon(String maxlon) {
		this.maxlon = maxlon;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getGenerator() {
		return generator;
	}

	public void setGenerator(String generator) {
		this.generator = generator;
	}
}
