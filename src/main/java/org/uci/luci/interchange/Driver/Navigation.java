package org.uci.luci.interchange.Driver;

import org.uci.luci.interchange.Graph.*;
import java.util.List;

public interface Navigation {
	public Node nextNodeOnPath(String curNodeId);

	public String getOrigin();

	public String getDestination();

	public List<Node> getPath();
}