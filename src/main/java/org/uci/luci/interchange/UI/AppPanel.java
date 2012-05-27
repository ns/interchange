package org.uci.luci.interchange.UI;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.uci.luci.interchange.Driver.VehicleDriver;
import org.uci.luci.interchange.Graph.Node;
import org.uci.luci.interchange.Graph.Way;
import org.uci.luci.interchange.OSM.OpenStreetMap;
import org.uci.luci.interchange.Registry.*;
import org.uci.luci.interchange.Util.*;
import org.uci.luci.interchange.Vehicles.Vehicle;
import org.uci.luci.interchange.Driver.VehicleDriverBehavior.*;
import org.uci.luci.interchange.Intersections.*;

public class AppPanel extends JPanel {
	private WindowProjector windowProjector;
	OpenStreetMap osm;
	private BufferedImage map;
	private Graphics2D mapG2D;
	private BufferedImage overlay;
	private Graphics2D overlayG2D;
	private BufferedImage hud;
	private Graphics2D hudG2D;
	Point draggingPointOrigin;
	int draggingOffsetX;
	int draggingOffsetY;
	NodePoint highlightPoint;

	public Color backgroundColor = Color.WHITE;
	public boolean showMap = true;
	public boolean showAllNodes = false;
	public boolean showPlaceNames = false;
	public boolean showVehicleInfo = false;
	public boolean showVehicleDebugTraces = false;
	public boolean showDistances = false;

	public AppPanel() {
		this.osm = Global.openStreetMap;

		windowProjector = new WindowProjector(osm.projectedMinY,
				osm.projectedMaxY, osm.projectedMinX, osm.projectedMaxX);

		setBorder(BorderFactory.createLineBorder(Color.black));
		this.requestFocus();
		registerListeners();
		scheduleRepaintTimer();
	}

	private void registerListeners() {
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				map = null;
				overlay = null;
			}
		});

		addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
				draggingPointOrigin = null;
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}
		});

		addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent e) {
				highlightPoint = unscaleXY(e.getX(), e.getY());
			}

			public void mouseDragged(MouseEvent e) {
				if (draggingPointOrigin == null) {
					draggingPointOrigin = e.getPoint();
					draggingOffsetX = (int) (draggingPointOrigin.getX() - windowProjector.offsetX);
					draggingOffsetY = (int) (draggingPointOrigin.getY() - windowProjector.offsetY);
				} else {
					windowProjector.offsetX = e.getX() - draggingOffsetX;
					windowProjector.offsetY = e.getY() - draggingOffsetY;
				}
				repaint();
			}
		});

		addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				int steps = (int) Math.pow(e.getWheelRotation(), 2)
						* (e.getWheelRotation() < 0 ? -1 : 1);
				zoomMap(e.getX(), e.getY(), steps);
			}
		});

		addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case 45:
					if (Global.simulator == null) {
						System.out.println("why is simulator null??");
					}
					Global.simulator.changeSpeed(+1);
					break;
				// = Sign which is same as plus, but without shift
				case 61:
					Global.simulator.changeSpeed(-1);
					break;
				case 91:
					zoomMap(-3);
					break;
				case 93:
					zoomMap(3);
					break;
				case 38: // up
					windowProjector.offsetY += 20;
					break;
				case 40: // down
					windowProjector.offsetY -= 20;
					break;
				case 37: // left
					windowProjector.offsetX += 20;
					break;
				case 39: // right
					windowProjector.offsetX -= 20;
					break;
				default:
					System.out.println(e.getKeyCode());
					break;
				}
			}

			public void keyReleased(KeyEvent e) {
			}

			public void keyTyped(KeyEvent e) {
			}
		});
	}

	private void scheduleRepaintTimer() {
		javax.swing.Timer t = new javax.swing.Timer(16, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		});
		t.start();
	}

	public void centerMap() {
		windowProjector.offsetX = (int) (getSize().getWidth() / 2)
				- windowProjector.scale / 2;
		windowProjector.offsetY = (int) (getSize().getHeight() / 2)
				- windowProjector.scale / 2;
		repaint();
	}

	public void zoomMap(int x, int y, int steps) {
		int newScale = windowProjector.scale + (steps * 5);
		if (newScale < 50)
			newScale = 50;
		if (newScale > 1000000)
			newScale = 1000000;

		NodePoint unscaledXY = unscaleXY(x, y);
		windowProjector.scale = newScale;
		NodePoint whereThePointIsNow = scaledXY(unscaledXY.x, unscaledXY.y);

		windowProjector.offsetX += x - whereThePointIsNow.x;
		windowProjector.offsetY += y - whereThePointIsNow.y;
		repaint();
	}

	public void zoomMap(int steps) {
		zoomMap(windowProjector.offsetX, windowProjector.offsetY, steps);
	}

	public Dimension getPreferredSize() {
		return new Dimension(800, 600);
	}

	public NodePoint scaledXY(double x, double y) {
		return windowProjector.scaledXY(x, y);
	}

	public NodePoint unscaleXY(int x, int y) {
		return windowProjector.unscaleXY(x, y);
	}

	private void paintMap(Graphics g_old) {
		if (map == null) {
			map = new BufferedImage(getWidth(), getHeight(),
					BufferedImage.TYPE_INT_ARGB);
			mapG2D = map.createGraphics();
			mapG2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			AlphaComposite ac = AlphaComposite
					.getInstance(AlphaComposite.SRC_OVER);
			mapG2D.setComposite(ac);
		}

		Graphics2D g2d = mapG2D;

		g2d.setBackground(new Color(255, 255, 255, 0));
		g2d.clearRect(0, 0, getWidth(), getHeight());

		g2d.setStroke(new BasicStroke(1f));
		g2d.setColor(new Color(200, 200, 255));

		// ways
		Node _last_n = null;
		Node _n;
		Way _w;

		for (int i = 0; i < osm.ways.size(); i++) {
			_w = osm.ways.get(i);

			if (_w.lanes > 1)
				g2d.setStroke(new BasicStroke(3f));
			else
				g2d.setStroke(new BasicStroke(1f));

			if (_w.oneway) {
				System.out.println("Warning: Not drawing one-way.");
			} else {
				for (int j = 0; j < _w.nd.size(); j++) {
					_n = osm.getNode(_w.nd.get(j));

					if (j == 0) {
						_last_n = _n;
					} else {
						NodePoint _last_np = scaledXY(_last_n.x, _last_n.y);
						NodePoint _np = scaledXY(_n.x, _n.y);

						g2d.setColor(Color.black);
						g2d.drawLine((int) _last_np.x, (int) _last_np.y,
								(int) _np.x, (int) _np.y);

						if (showDistances) {
							// this will draw distances
							g2d.setColor(Color.black);
							double d = Utils.distance(_last_n.lat, _last_n.lon,
									_n.lat, _n.lon, 'K');
							g2d.setFont(new Font("TimesRoman", Font.BOLD, 8));
							DecimalFormat twoDForm = new DecimalFormat("#.##");
							g2d.drawString(Double.valueOf(twoDForm.format(d))
									+ " km",
									(int) (_last_np.x + _np.x) / 2 + 4,
									(int) (_last_np.y + _np.y) / 2 + 4);
						}

						_last_n = _n;
					}
				}
			}
		}

		if (showAllNodes) {
			// nodes
			g2d.setStroke(new BasicStroke(1f));
			g2d.setColor(Color.BLUE);
			g2d.setFont(new Font("TimesRoman", Font.PLAIN, 10));
			int width = 5;

			for (Map.Entry<String, Node> entry : osm.nodeHash.entrySet()) {
				Node n = entry.getValue();
				NodePoint p = scaledXY(n.x, n.y);

				if (n.flaggedForMerge)
					g2d.setColor(Color.RED);
				else
					g2d.setColor(Color.BLUE);

				g2d.fillOval((int) p.x + width / 2, (int) p.y + width / 2,
						width, width);

				// g2d.setColor(Color.RED);
				// g2d.drawString(n.id, (int) p.x + 5, (int) p.y + 5);

			}
		}

		if (showPlaceNames) {
			g2d.setColor(Color.BLACK);
			g2d.setFont(new Font("TimesRoman", Font.PLAIN, 12));
			for (Map.Entry<String, Node> entry : osm.nodeHash.entrySet()) {
				Node n = entry.getValue();
				String name = n.getName();
				if (name == null)
					continue;
				NodePoint p = scaledXY(n.x, n.y);
				g2d.drawString(name, (int) p.x, (int) p.y - 2);
			}
		}
	}

	private void paintOverlay() throws Exception {
		if (overlay == null) {
			overlay = new BufferedImage(getWidth(), getHeight(),
					BufferedImage.TYPE_INT_ARGB);
			overlayG2D = overlay.createGraphics();
			overlayG2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			AlphaComposite ac = AlphaComposite
					.getInstance(AlphaComposite.SRC_OVER);
			overlayG2D.setComposite(ac);
		}
		Graphics2D g2d = overlayG2D;

		g2d.setBackground(new Color(255, 255, 255, 0));
		g2d.clearRect(0, 0, getWidth(), getHeight());

		g2d.setStroke(new BasicStroke(4f));
		for (Intersection i : IntersectionRegistry.allRegisteredIntersections()) {

			String rootNodeId = i.getRootNodeId();

			Node rootNode = osm.getNode(rootNodeId);

			if (i instanceof HighwayRamp) {
				continue;
			}

			// // || i instanceof ThreeWayBiddingIntersection
			// if (i instanceof FourWayBiddingIntersection) {
			// FourWayBiddingIntersection ii = (FourWayBiddingIntersection)i;
			// NodePoint scaledRootNode = scaledXY(rootNode.lat,rootNode.lon);
			// g2d.setColor(Color.black);
			// g2d.drawString("n/s " + ii.nsBidTotal() + "; e/w " +
			// ii.ewBidTotal(), (int)scaledRootNode.x+20,
			// (int)scaledRootNode.y-20);
			// }

			NodePoint rnP = scaledXY(rootNode.x, rootNode.y);
			
			for (Node connectedNode : rootNode.connectedNodes) {
				Way w = Oracle.wayBetweenNodes(rootNode.id, connectedNode.id);

				if (w.oneway) {
					System.out
							.println("Warning: not drawing one way traffic lights");
				} else {
					LightFSM.LIGHT light = i.getLightForWayOnLane(null, connectedNode.id, null, 0);

					NodePoint ccPUS = distanceFromPointInDirectionOfPoint(
							rootNode.x, rootNode.y, connectedNode.x,
							connectedNode.y, 10);
					NodePoint cnP = scaledXY(ccPUS.x, ccPUS.y);

					if (light == LightFSM.LIGHT.GREEN) {
						g2d.setColor(new Color(0, 255, 0, 100));
					} else if (light == LightFSM.LIGHT.YELLOW) {
						g2d.setColor(new Color(255, 255, 0, 100));
					} else if (light == LightFSM.LIGHT.RED) {
						g2d.setColor(new Color(255, 0, 0, 100));
					}

					g2d.drawLine((int) cnP.x, (int) cnP.y, (int) rnP.x,
							(int) rnP.y);
				}
			}
			
			g2d.setColor(Color.BLACK);
			g2d.setFont(new Font("TimesRoman", Font.BOLD, 8));
			g2d.drawString(i.getState(), (int) rnP.x + 5, (int) rnP.y - 5);
		}

		g2d.setStroke(new BasicStroke(1f));

		for (VehicleDriver d : VehicleDriverRegistry.allLicensedDrivers()) {
			Vehicle v = d.vehicle;

			NodePoint p = null;

			if (v.isGoingForwardOnWay()) {
				p = scaledXY(Global.projection.convertLongToX(v.lon),
						Global.projection.convertLatToY(v.lat));
			} else {
				p = scaledXY(Global.projection.convertLongToX(v.lon),
						Global.projection.convertLatToY(v.lat));
			}
			
			int size = 5;
			
			if (v.paused()) {
				size = 10;
				g2d.setColor(Color.MAGENTA);
				g2d.fillOval((int) p.x - size / 2, (int) p.y - size / 2, size, size);
			}
			else {
				g2d.setColor(Color.BLUE);
				g2d.fillOval((int) p.x - size / 2, (int) p.y - size / 2, size, size);
			}

			if (showVehicleInfo) {
				g2d.setColor(Color.BLACK);
				g2d.setFont(new Font("TimesRoman", Font.BOLD, 8));
				g2d.drawString("vin " + v.vin, (int) p.x + 5, (int) p.y);
				if (v.navigation != null) {
          g2d.drawString("origin " + (v.navigation.getOrigin() == null ? "(none)" : v.navigation.getOrigin()),
             (int) p.x + 5, (int) p.y + 10);
          g2d.drawString("dest " + (v.navigation.getDestination() == null ? "(none)" : v.navigation.getDestination()),
             (int) p.x + 5, (int) p.y + 20);
         }
         else {
           g2d.drawString("origin (none)", (int) p.x + 5, (int) p.y + 10);
           g2d.drawString("dest (none)", (int) p.x + 5, (int) p.y + 20);
         }
				g2d.drawString("km/h " + v.speed(), (int) p.x + 5,
						(int) p.y + 30);
				g2d.drawString("km/s^2 " + v.acceleration, (int) p.x + 5,
						(int) p.y + 40);
				if (d.activeBehavior instanceof FollowingBehavior)
					g2d.drawString("behavior Following", (int) p.x + 5,
							(int) p.y + 50);
				else if (d.activeBehavior instanceof GeneralAccelerationBehavior)
					g2d.drawString("behavior General", (int) p.x + 5,
							(int) p.y + 50);
				else if (d.activeBehavior instanceof IntersectionCrossingBehavior)
					g2d.drawString("behavior Intersection Crossing",
							(int) p.x + 5, (int) p.y + 50);
				else if (d.activeBehavior instanceof ReachedDestinationBehavior)
					g2d.drawString("behavior Reached Destination",
							(int) p.x + 5, (int) p.y + 50);
				g2d.drawString("total delay (sec) " + v.vehicleTotalWaitTime,
						(int) p.x + 5, (int) p.y + 60);
				g2d.drawString("behavior is " + d.activeBehavior.state(),
						(int) p.x + 5, (int) p.y + 70);
			}

			if (showVehicleDebugTraces) {
				Vehicle vehicleInFront = v.getVehicleInFront();
				if (vehicleInFront != null) {
					NodePoint pp = scaledXY(
							Global.projection
									.convertLongToX(vehicleInFront.lon),
							Global.projection.convertLatToY(vehicleInFront.lat));
					g2d.setColor(Color.BLUE);
					g2d.drawLine((int) pp.x, (int) pp.y, (int) p.x, (int) p.y);
				}

				Node nextNode = v.getDestinationNode();
				if (nextNode != null) {
					NodePoint pp = scaledXY(
							Global.projection.convertLongToX(nextNode.lon),
							Global.projection.convertLatToY(nextNode.lat));
					g2d.setColor(Color.RED);
					g2d.fillOval((int) pp.x - size / 2, (int) pp.y - size / 2,
							size, size);
					// System.out.println("nextnode is " + nextNode.id);
				}

				Node prevNode = v.getOriginNode();
				if (prevNode != null) {
					NodePoint pp = scaledXY(
							Global.projection.convertLongToX(prevNode.lon),
							Global.projection.convertLatToY(prevNode.lat));
					g2d.setColor(Color.ORANGE);
					g2d.fillOval((int) pp.x - size / 2, (int) pp.y - size / 2,
							size, size);
				}

				Node destNode = Global.openStreetMap.getNode(v.navigation
						.getDestination());
				NodePoint ppd = scaledXY(
						Global.projection.convertLongToX(destNode.lon),
						Global.projection.convertLatToY(destNode.lat));
				g2d.setColor(Color.RED);
				g2d.drawLine((int) ppd.x, (int) ppd.y, (int) p.x, (int) p.y);
			}
		}
	}

	private void paintHUD() throws Exception {
		if (hud == null) {
			hud = new BufferedImage(getWidth(), getHeight(),
					BufferedImage.TYPE_INT_ARGB);
			hudG2D = hud.createGraphics();
			hudG2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			AlphaComposite ac = AlphaComposite
					.getInstance(AlphaComposite.SRC_OVER);
			hudG2D.setComposite(ac);
		}
		Graphics2D g2d = hudG2D;

		g2d.setBackground(new Color(255, 255, 255, 0));
		g2d.clearRect(0, 0, getWidth(), getHeight());

		g2d.setColor(Color.BLACK);
		g2d.setFont(new Font("TimesRoman", Font.BOLD, 18));
		DecimalFormat df = new DecimalFormat("#.##");
		if (Global.simulator.simulatorTime() > 0)
			g2d.drawString(
					"Simulator Time: "
							+ df.format(Global.simulator.simulatorTime()), 10,
					30);
		else
			g2d.drawString("Simulator Time: 0", 10, 30);

		g2d.drawString("Total Vehicles: "
				+ Global.simulator.lastSimulatorStepTotalVehicles, 10, 50);
		g2d.drawString("Total Vehicles: "
				+ Global.simulator.lastSimulatorStepTotalVehicles, 10, 50);
	}

	private NodePoint distanceFromPointInDirectionOfPoint(double fromLat,
			double fromLon, double toLat, double toLon, double d) {
		double angle = -Math.atan2((toLat - fromLat), (toLon - fromLon));
		angle = Math.toDegrees(angle);
		if (angle < 0)
			angle = 360 + angle;
		angle = Math.toRadians(angle);
		double deltaLat = Math.sin(angle) * d;
		double deltaLon = Math.cos(angle) * d;

		// based on this we can negate deltaLat or deltaLon to the correct sign
		if (Math.toDegrees(angle) < 0) {
			deltaLat *= 1;
			deltaLon *= 1;
		} else if (Math.toDegrees(angle) > 45) {
			deltaLat *= -1;
			deltaLon *= 1;
		} else {
			deltaLat *= -1;
			deltaLon *= 1;
		}

		double newLat = fromLat + deltaLat;
		double newLon = fromLon + deltaLon;

		return new NodePoint(newLat, newLon);
	}

	public void paintComponent(Graphics g) {
		try {
			// if (offsetX == -1)
			// offsetX = (int) (getSize().getWidth() / 2) - scale / 2;
			// if (offsetY == -1)
			// offsetY = (int) (getSize().getHeight() / 2) - scale / 2;

			super.paintComponent(g);

			g.setColor(backgroundColor);
			g.fillRect(0, 0, getWidth(), getHeight());

			if (showMap) {
				paintMap(g);
				g.drawImage(map, 0, 0, null);
			}

			paintOverlay();
			g.drawImage(overlay, 0, 0, null);

			paintHUD();
			g.drawImage(hud, 0, 0, null);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	@Override
	public boolean isFocusTraversable() {
		return true;
	}
}