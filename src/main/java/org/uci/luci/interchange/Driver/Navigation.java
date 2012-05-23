package org.uci.luci.interchange.Driver;

import org.uci.luci.interchange.Graph.*;
import org.uci.luci.interchange.Util.*;
import org.uci.luci.interchange.Exception.*;

import java.util.Random;
import java.util.List;
import java.util.LinkedList;

public interface Navigation {
	public Node nextNodeOnPath(String curNodeId);

	public String getOrigin();

	public String getDestination();

	public List<Node> getPath();
}