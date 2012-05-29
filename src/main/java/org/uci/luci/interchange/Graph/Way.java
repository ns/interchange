package org.uci.luci.interchange.Graph;

import java.util.ArrayList;

public class Way {
	public ArrayList<String> nd = new ArrayList<String>();
	public ArrayList<Tag> tags = new ArrayList<Tag>();
	public int lanes;
	public boolean oneway;

	public void addNode(String nodeId) {
		nd.add(nodeId);
	}

	public void addTag(Tag t) {
		tags.add(t);
	}

	public int getNdSize() {
		return nd.size();
	}

	public int getTagsSize() {
		return tags.size();
	}

	public String tags() {
		String sum = "";
		for (Tag t : tags) {
			sum = sum + " " + t.k + "=" + t.v;
		}
		return sum;
	}

	public boolean hasTag(String k, String v) {
		for (Tag t : tags) {
			if (t.k.equals(k) && t.v.equals(v))
				return true;
		}
		return false;
	}

	public String getTag(String k) {
		for (Tag t : tags) {
			if (t.k.equals(k))
				return t.v;
		}
		return null;
	}

	public boolean hasTag(String k) {
		for (Tag t : tags) {
			if (t.k.equals(k))
				return true;
		}
		return false;
	}

	public String getName() {
		for (Tag t : tags) {
			if (t.k.equals("name"))
				return t.v;
		}
		return null;
	}

	// returns speeds in km/h
	public double getSpeedLimit() {
		if (hasTag("highway", "motorway") || hasTag("highway", "motorway_link")) {
			return 60 * 1.609344;
		} else if (hasTag("highway", "primary")) {
			return 55 * 1.609344;
		} else if (hasTag("highway", "secondary")
				|| hasTag("highway", "tertiary")) {
			return 30 * 1.609344;
		} else if (hasTag("highway", "residential")) {
			return 25 * 1.609344;
		} else {
			return 30 * 1.609344;
		}
	}
}
