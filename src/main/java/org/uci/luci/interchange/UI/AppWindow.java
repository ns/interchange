package org.uci.luci.interchange.UI;

import org.uci.luci.interchange.Intersections.*;
import org.uci.luci.interchange.OSM.*;
import org.uci.luci.interchange.Driver.*;
import org.uci.luci.interchange.Vehicles.*;
import org.uci.luci.interchange.Graph.*;
import org.uci.luci.interchange.Util.*;
import org.uci.luci.interchange.Registry.*;
import org.uci.luci.interchange.Factory.*;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.RenderingHints;
import java.text.DecimalFormat;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;

import java.awt.event.*;
import java.awt.Point;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.awt.AlphaComposite;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;
import java.awt.*;
import java.awt.geom.*;
// import javax.swing.*;

public class AppWindow implements ActionListener{
	JFrame f;
	MyPanel myPanel;
	boolean showMap = true;
	boolean showVehicleDebugTraces = false;
	Color backgroundColor = Color.white;
	boolean showAllNodes = false;
	boolean showMapLabels = false;
	boolean showPlaceNames = false;
	boolean showVehicleInfo = false;

	public AppWindow() throws InterruptedException {
		myPanel = new MyPanel();

		JMenuBar menubar = new JMenuBar();

		JMenu sim = new JMenu("Simulator");
		sim.setMnemonic(KeyEvent.VK_S);
		sim.add(makeMenuItem("Start"));
		sim.add(makeMenuItem("Stop"));
		sim.add(makeMenuItem("Reset"));
		sim.addSeparator();
		sim.add(makeMenuItem("Speed Up"));
		sim.add(makeMenuItem("Slow Down"));


		JMenu view = new JMenu("View");
		view.setMnemonic(KeyEvent.VK_V);
		view.add(makeMenuItem("Center Map"));
		view.add(makeMenuItem("Zoom In"));
		view.add(makeMenuItem("Zoom Out"));
		view.addSeparator();
		view.add(makeMenuItem("Use White Background"));
		view.add(makeMenuItem("Use Black Background"));
		view.addSeparator();
		view.add(makeMenuItem("Toggle Place Names"));
		view.add(makeMenuItem("Toggle Vehicle Info"));

		JMenu debug = new JMenu("Debug");
		debug.add(makeMenuItem("Toggle Vehicle Traces"));
		debug.add(makeMenuItem("Toggle Infrastructure Map"));
		debug.add(makeMenuItem("Toggle Nodes"));

		menubar.add(sim);
		menubar.add(view);
		menubar.add(debug);

		f = new JFrame("Interchange");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(myPanel);
		f.pack();
		f.setVisible(true);
		f.setLocationRelativeTo(null);
		f.setJMenuBar(menubar);
	}

	public JMenuItem makeMenuItem(String action)
	{
		return makeMenuItem(action,this);
	}

	public JMenuItem makeMenuItem(String action, ActionListener listener)
	{
		JMenuItem eMenuItem = new JMenuItem(action);
		eMenuItem.addActionListener(listener);
		return eMenuItem;
	}

	public void actionPerformed(ActionEvent e){

		// Menu item actions
		String command = e.getActionCommand();
		try
		{
			if (command.equals("Start")) {
				Global.simulator.unpause();
			} else if (command.equals("Stop")) {
				Global.simulator.pause();
			} else if (command.equals("Reset")) {
				// Global.simulator.resetSimulator();
			} else if (command.equals("Toggle Vehicle Traces")) {
				showVehicleDebugTraces = !showVehicleDebugTraces;
			} else if (command.equals("Slow Down")) {
				Global.simulator.changeSpeed(+10);
			} else if (command.equals("Slow Down")) {
				Global.simulator.changeSpeed(-10);
			} else if (command.equals("Zoom In")) {
				System.out.println("here");
				myPanel.zoomMap(myPanel.offsetX,myPanel.offsetY,+10);
				//myPanel.centerMap();
			} else if (command.equals("Zoom Out")) {
				myPanel.zoomMap(myPanel.offsetX,myPanel.offsetY,-10);
				//myPanel.centerMap();
			} else if (command.equals("Center Map")) {
				myPanel.centerMap();
			} else if (command.equals("Toggle Infrastructure Map")) {
				showMap = !showMap;
			} else if (command.equals("Use Black Background")) {
				backgroundColor = Color.black;
			} else if (command.equals("Use White Background")) {
				backgroundColor = Color.white;
			} else if (command.equals("Toggle Nodes")) {
				showAllNodes = !showAllNodes;
			} else if (command.equals("Toggle Place Names")) {
				showPlaceNames = !showPlaceNames;
			} else if (command.equals("Toggle Vehicle Info")) {
				showVehicleInfo = !showVehicleInfo;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	class MyPanel extends JPanel {
		OpenStreetMap osm;
		double top = -1;
		double bottom = -1;
		double left = -1;
		double right = -1;
    // int scale = 100;
    // int offsetX = -1;
    // int offsetY = -1;
		int scale = 17555;
		int offsetX = -4890;
		int offsetY = -14649;

		private BufferedImage map;
		private Graphics2D mapG2D;

		private BufferedImage overlay;
		private Graphics2D overlayG2D;

		Point draggingPointOrigin;

		int draggingOffsetX;
		int draggingOffsetY;

		NodePoint highlightPoint;

		public MyPanel() {
			this.osm = Global.openStreetMap;
			setBorder(BorderFactory.createLineBorder(Color.black));

			this.requestFocus();

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

				public void mouseMoved(MouseEvent e) {
				}
			});

			addMouseMotionListener(new MouseMotionListener() {
				public void mouseMoved(MouseEvent e) {
					highlightPoint = unscaleXY(e.getX(), e.getY());
				}

				public void mouseDragged(MouseEvent e) {
					if (draggingPointOrigin == null) {
						draggingPointOrigin = e.getPoint();
						draggingOffsetX = (int)(draggingPointOrigin.getX() - offsetX);
						draggingOffsetY = (int)(draggingPointOrigin.getY() - offsetY);
					}
					else {
						offsetX = e.getX() - draggingOffsetX;
						offsetY = e.getY() - draggingOffsetY;
					}

          System.out.println("offsetX="+offsetX + " offsetY="+offsetY + " scale="+scale);
					repaint();
				}
			});

			addMouseWheelListener(new MouseWheelListener() {
				public void mouseWheelMoved(MouseWheelEvent e) {
					int steps = (int)Math.pow(e.getWheelRotation(),2) * (e.getWheelRotation()<0 ? -1 : 1);
					zoomMap(e.getX(), e.getY(), steps);
				}
			});

			addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent e) {
					// System.out.println("Keycode = " + e.getKeyCode());

					switch (e.getKeyCode()) {
					case 45:
						if (Global.simulator == null) {
							System.out.println("why is simulator null??");
						}
						Global.simulator.changeSpeed(+1);
						break;
						//= Sign which is same as plus, but without shift
					case 61:
						Global.simulator.changeSpeed(-1);
						break;
					case 91: 
						zoomMap(offsetX, offsetY, -3);
						break;
					case 93:
						zoomMap(offsetX, offsetY, 3);
						break;
					default:
						System.out.println(e.getKeyCode());
						break;
					}

					// if (e.getKeyChar() == 'p') {
					//  Way bb = osm.getWayByName("Academy Way");
					//  Way ee = osm.getWayByName("Port Stirling Place");
					// 
					//  Node startNode = osm.getNode(bb.nd.get(0));
					//  Node endNode = osm.getNode(ee.nd.get(0));
					//  generateAndDrawPathBetweenNodes(getGraphics(), startNode, endNode);
					//  // repaint();
					//  System.out.println("done");
					// }
					// else if (e.getKeyChar() == 'c') {
					//  // paintMap(null);
					//  // scale = 200;
					//  
					// }
					// else if (e.getKeyChar() == 'n') {
					//  scale = 46415;
					//  offsetX = -15532;
					//  offsetY = -43460;
					//  repaint();
					// }
					// else if(e.getKeyChar() == 'e')
					//  zoomMap(offsetX, offsetY, 3);
					// else if(e.getKeyChar() == 'i')
					//  zoomMap(offsetX, offsetY, -3);
					// else if(e.getKeyChar() == '+')
					//            Global.simulator.changeSpeed(-1);
					// else if(e.getKeyChar() == '-')
					//            Global.simulator.changeSpeed(+1);
					//          else
					//            System.out.println(e.getKeyChar());
				}

				public void keyReleased(KeyEvent e) {}

				public void keyTyped(KeyEvent e) {}
			});


			javax.swing.Timer t = new javax.swing.Timer(16, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					repaint();
				}
			});
			t.start();
		}

		public void centerMap()
		{
			offsetX = (int)(getSize().getWidth()/2) - scale/2;
			offsetY = (int)(getSize().getHeight()/2) - scale/2;
			repaint();
		}

		public void zoomMap(int x, int y, int steps)
		{
      // System.out.println("zooming");
			int newScale = scale + (steps * 5);
			if (newScale < 50)
				newScale = 50;
			if (newScale > 100000)
				newScale = 100000;

			NodePoint unscaledXY = unscaleXY(x, y);
			scale = newScale;
			NodePoint whereThePointIsNow = scaledXY(unscaledXY.x,unscaledXY.y);

			// System.out.println("offsetXY " + offsetX + ", " + offsetY);
			// System.out.println("whereThePointIsNow " + whereThePointIsNow.x + ", " + whereThePointIsNow.y);
			// System.out.println("diff " + (whereThePointIsNow.x-(double)offsetX) + ", " + (whereThePointIsNow.y-(double)offsetY));

      offsetX += x - whereThePointIsNow.x;
      offsetY += y - whereThePointIsNow.y;
			repaint();
		}

		public Dimension getPreferredSize() {
			return new Dimension(800,600);
		}

    public NodePoint scaledXY(double x, double y) {
      if (top == -1)
        top = osm.projectedMinY;
      if (bottom == -1)
        bottom = osm.projectedMaxY;
      if (left == -1)
        left = osm.projectedMinX;
      if (right == -1)
        right = osm.projectedMaxX;
      
      x = (((double)x-(double)left) / ((double)right-(double)left));
      y = (((double)y-(double)top) / ((double)bottom-(double)top));
      
      x = (x * scale) + offsetX;
      y = (y * scale) + offsetY;
      
      // System.out.println("" + x + ", " + y + " (scale " + scale + ", offsetX = " + offsetX + " offsetY = " + offsetY + ")");
      
      
      // if (top == -1)
      //   top = Double.valueOf(osm.getMinlat());
      // if (bottom == -1)
      //   bottom = Double.valueOf(osm.getMaxlat());
      // if (left == -1)
      //   left = Double.valueOf(osm.getMinlon());
      // if (right == -1)
      //   right = Double.valueOf(osm.getMaxlon());
      
      

      
      
      return new NodePoint(x,y);
      
      
     // if (top == -1)
     //   top = Double.valueOf(osm.getMinlat());
     // if (bottom == -1)
     //   bottom = Double.valueOf(osm.getMaxlat());
     // if (left == -1)
     //   left = Double.valueOf(osm.getMinlon());
     // if (right == -1)
     //   right = Double.valueOf(osm.getMaxlon());
     //     
     // double latF = lat;
     // double lonF = lon;
     //     
     // double pY = ((latF-top) / (double)(bottom-top));
     // double pX = ((lonF-left) / (double)(right-left));
     //     
     // double x = (pX * scale) + offsetX;
     // double y = (pY * scale) + offsetY;
     //     
     // return new NodePoint(x,y);
    }
    
    public NodePoint unscaleXY(int x, int y) {
      if (top == -1)
        top = osm.projectedMinY;
      if (bottom == -1)
        bottom = osm.projectedMaxY;
      if (left == -1)
        left = osm.projectedMinX;
      if (right == -1)
        right = osm.projectedMaxX;
      
      double pX = (x - offsetX) / (double)scale;
      double pY = (y - offsetY) / (double)scale;
          
      double lat = (pY * (bottom-top)) + top;
      double lon = (pX * (right-left)) + left;
          
      return new NodePoint((double)lat, (double)lon);
      
      
     // if (top == -1)
     //   top = Double.valueOf(osm.getMinlat());
     // if (bottom == -1)
     //   bottom = Double.valueOf(osm.getMaxlat());
     // if (left == -1)
     //   left = Double.valueOf(osm.getMinlon());
     // if (right == -1)
     //   right = Double.valueOf(osm.getMaxlon());
     //     
     // double pX = (x - offsetX) / (double)scale;
     // double pY = (y - offsetY) / (double)scale;
     //     
     // double lat = (pY * (bottom-top)) + top;
     // double lon = (pX * (right-left)) + left;
     //     
     // return new NodePoint((double)lat, (double)lon);
    }

    // public NodePoint scaledXY(double lat, double lon) {
    //  if (top == -1)
    //    top = Double.valueOf(osm.getMinlat());
    //  if (bottom == -1)
    //    bottom = Double.valueOf(osm.getMaxlat());
    //  if (left == -1)
    //    left = Double.valueOf(osm.getMinlon());
    //  if (right == -1)
    //    right = Double.valueOf(osm.getMaxlon());
    // 
    //  double latF = lat;
    //  double lonF = lon;
    // 
    //  double pY = ((latF-top) / (double)(bottom-top));
    //  double pX = ((lonF-left) / (double)(right-left));
    // 
    //  double x = (pX * scale) + offsetX;
    //  double y = (pY * scale) + offsetY;
    // 
    //  return new NodePoint(x,y);
    // }
    // 
    // public NodePoint unscaleXY(int x, int y) {
    //  if (top == -1)
    //    top = Double.valueOf(osm.getMinlat());
    //  if (bottom == -1)
    //    bottom = Double.valueOf(osm.getMaxlat());
    //  if (left == -1)
    //    left = Double.valueOf(osm.getMinlon());
    //  if (right == -1)
    //    right = Double.valueOf(osm.getMaxlon());
    // 
    //  double pX = (x - offsetX) / (double)scale;
    //  double pY = (y - offsetY) / (double)scale;
    // 
    //  double lat = (pY * (bottom-top)) + top;
    //  double lon = (pX * (right-left)) + left;
    // 
    //  return new NodePoint((double)lat, (double)lon);
    // }

		private void paintMap(Graphics g_old) {
			if (map == null) {
				map = new BufferedImage(getWidth(),getHeight(), BufferedImage.TYPE_INT_ARGB);
				mapG2D = map.createGraphics();
				mapG2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
				mapG2D.setComposite(ac);
			}

			Graphics2D g2d = mapG2D;

			g2d.setBackground(new Color(255, 255, 255, 0));
			g2d.clearRect(0,0,getWidth(),getHeight());
			
			g2d.setStroke(new BasicStroke(1f));
			g2d.setColor(new Color(200, 200, 255));

			// for (Intersection i : IntersectionRegistry.allRegisteredIntersections()) {
			//   Node n = Global.openStreetMap.getNode(i.getRootNodeId());
			//   
			//   
			//   NodePoint p = scaledXY(n.lat,n.lon);
			//   g2d.fillOval((int)p.x, (int)p.y, (int)i.getBounds(), (int)i.getBounds());
			// }



			// ways
			Node _last_n = null;
			Node _n;
			// NodePoint _np;
			Way _w;

			for(int i = 0; i < osm.ways.size(); i++) {
				// g2d.setColor(colorz[i%10]);
				_w = osm.ways.get(i);

				if (_w.lanes > 1)
					g2d.setStroke(new BasicStroke(3f));
				else
					g2d.setStroke(new BasicStroke(1f));


				// if (_w.oneway) {
				//   g2d.setColor(Color.blue);
				// }
				// else {
				//   g2d.setColor(Color.red);
				// }

				if (_w.oneway) {
					System.out.println("Warning: Not drawing one-way.");
				}
				else {
					for(int j = 0; j < _w.nd.size(); j++){
						_n = osm.getNode(_w.nd.get(j));
						// _np = scaledXY(_n.lat, _n.lon);

						if(j == 0){
							// _np_old_x = _np.x;
							// _np_old_y = _np.y;
							_last_n = _n;
						} else {
							double laneSpacing = 2;
							double streetSpacing = laneSpacing;//*(_w.lanes+1);

							for (int l = 0; l < _w.lanes; l++) {
                NodePoint _last_np = scaledXY(
                   _last_n.x+streetSpacing+(l*laneSpacing),
                   _last_n.y+streetSpacing+(l*laneSpacing)
                );
                NodePoint _np = scaledXY(
                   _n.x+streetSpacing+(l*laneSpacing),
                   _n.y+streetSpacing+(l*laneSpacing)
                );

								// fwd
								g2d.setColor(Color.black);
								g2d.drawLine(
										(int)_last_np.x,
										(int)_last_np.y,
										(int)_np.x,
										(int)_np.y
								);
								
								NodePoint _last_np_reverse = scaledXY(
										_last_n.x-streetSpacing-(l*laneSpacing),
										_last_n.y-streetSpacing-(l*laneSpacing)
								);
								NodePoint _np_reverse = scaledXY(
										_n.x-streetSpacing-(l*laneSpacing),
										_n.y-streetSpacing-(l*laneSpacing)
								);
								
								// reverse
								g2d.setColor(Color.blue);
								g2d.drawLine(
										(int)_last_np_reverse.x,
										(int)_last_np_reverse.y,
										(int)_np_reverse.x,
										(int)_np_reverse.y
								);
								
                //                g2d.setColor(Color.red);
                // double d = Utils.distance(_last_n.lat, _last_n.lon, _n.lat, _n.lon, 'K');
                // g2d.setFont(new Font("TimesRoman", Font.BOLD, 8));
                // DecimalFormat twoDForm = new DecimalFormat("#.##");
                //                g2d.drawString(Double.valueOf(twoDForm.format(d)) + " km", (int)_last_np.x+4, (int)_last_np.y+4);
							}
							
							_last_n = _n;
						}
					}
				}
			}
			
			if (showAllNodes) {
				// nodes
				g2d.setStroke(new BasicStroke(1f));
				g2d.setColor(Color.LIGHT_GRAY);

				for (Map.Entry<String, Node> entry : osm.nodeHash.entrySet()) {
					Node n = entry.getValue();
					NodePoint p = scaledXY(n.x,n.y);
					g2d.fillOval((int)p.x, (int)p.y, 2, 2);
				}
			}

			if (showPlaceNames) {
				g2d.setColor(Color.BLACK);
				g2d.setFont(new Font("TimesRoman", Font.PLAIN, 12));
				for (Map.Entry<String, Node> entry : osm.nodeHash.entrySet()) {
					Node n = entry.getValue();
					String name = n.getName();
					if (name == null) continue;
					NodePoint p = scaledXY(n.x,n.y);
					g2d.drawString(name, (int)p.x, (int)p.y-2);
				}
			}
		}
		
		private void paintOverlay() throws Exception {
			if (overlay == null) {
				overlay = new BufferedImage(getWidth(),getHeight(), BufferedImage.TYPE_INT_ARGB);
				overlayG2D = overlay.createGraphics();
				overlayG2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
				overlayG2D.setComposite(ac);
			}
			Graphics2D g2d = overlayG2D;

			g2d.setBackground(new Color(255, 255, 255, 0));
			g2d.clearRect(0,0,getWidth(),getHeight());
			
			
			
			
			
			
			
      g2d.setStroke(new BasicStroke(1f));

      double laneSpacing = 2;
      double streetSpacing = laneSpacing;
      
      for (VehicleDriver d : VehicleDriverRegistry.allLicensedDrivers()) {
      	Vehicle v = d.vehicle;
      	g2d.setColor(Color.red);

      	NodePoint p = null;
  	    
      	if (v.isGoingForwardOnWay()) {
          p = scaledXY(
            Global.projection.convertLongToX(v.lon)+streetSpacing+(v.getOnLaneNumber()*laneSpacing),
            Global.projection.convertLatToY(v.lat)+streetSpacing+(v.getOnLaneNumber()*laneSpacing)
          );
      	}
      	else {
      		p = scaledXY(
      		  Global.projection.convertLongToX(v.lon)-streetSpacing-(v.getOnLaneNumber()*laneSpacing),
      		  Global.projection.convertLatToY(v.lat)-streetSpacing-(v.getOnLaneNumber()*laneSpacing)
      		);
      	}
        // if (v.isGoingForwardOnWay()) {
        //           p = scaledXY(
        //             v.x+streetSpacing+(v.getOnLaneNumber()*laneSpacing),
        //             v.y+streetSpacing+(v.getOnLaneNumber()*laneSpacing)
        //           );
        // }
        // else {
        //  p = scaledXY(
        //    v.x-streetSpacing-(v.getOnLaneNumber()*laneSpacing),
        //    v.y-streetSpacing-(v.getOnLaneNumber()*laneSpacing)
        //  );
        // }
      	
        // System.out.println("p " + p.x + ", " + p.y);
      	
      	int size = 5;
      	if (v.flagForRemoval)
      		g2d.setColor(Color.BLUE);
      	
      	g2d.fillOval((int)p.x - size/2, (int)p.y - size/2, size, size);
      }
		}

		public void paintComponent(Graphics g) {
			try {
				if (offsetX == -1)
					offsetX = (int)(getSize().getWidth()/2) - scale/2;
				if (offsetY == -1)
					offsetY = (int)(getSize().getHeight()/2) - scale/2;

				super.paintComponent(g);

				g.setColor(backgroundColor);
				g.fillRect(0,0,getWidth(),getHeight());

				if (showMap) {
					paintMap(g);
					g.drawImage(map, 0, 0, null);
				}

				paintOverlay();
				g.drawImage(overlay, 0, 0, null);
			}
			catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}

		@Override
		public boolean isFocusTraversable()
		{
			return true;
		}

		private NodePoint distanceFromPointInDirectionOfPoint(double fromLat, double fromLon,
		                                                      double toLat, double toLon, double d) {
			double angle = -Math.atan2((toLat - fromLat), (toLon - fromLon));
			angle = Math.toDegrees(angle);
			if (angle < 0)
				angle = 360 + angle;
			angle = Math.toRadians(angle);
			double deltaLat = Math.sin(angle)*d;
			double deltaLon = Math.cos(angle)*d;

			// based on this we can negate deltaLat or deltaLon to the correct sign
			if (Math.toDegrees(angle) < 0) {
				deltaLat*=1;
				deltaLon*=1;
			}
			else if (Math.toDegrees(angle) > 45) {
				deltaLat*=-1;
				deltaLon*=1;
			}
			else {
				deltaLat*=-1;
				deltaLon*=1;
			}

			double newLat = fromLat + deltaLat;
			double newLon = fromLon + deltaLon;

			return new NodePoint(newLat, newLon);
		}
	}

	class NodePoint{
		double x;
		double y;
		double xc; // constrained on Canvas
		double yc; // constrained on Canvas
		NodePoint(double _x, double _y){
			x = _x;
			y = _y;
		}
		NodePoint sub(double _x, double _y){
			return new NodePoint(x-_x, y-_y);
		}
		NodePoint sub(NodePoint n){
			return new NodePoint(x-n.x,y-n.y);
		}
	}
}