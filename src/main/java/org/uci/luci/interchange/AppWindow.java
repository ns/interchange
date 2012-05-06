package org.uci.luci.interchange;

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

public class AppWindow {
  JFrame f;
  MyPanel myPanel;
  
    public AppWindow() throws InterruptedException {
      myPanel = new MyPanel();
      
      f = new JFrame("Interchange");
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.add(myPanel);
      f.pack();
      f.setVisible(true);
      f.setLocationRelativeTo(null);
    }
    
    class MyPanel extends JPanel {
      OpenStreetMap osm;
      double top = -1;
      double bottom = -1;
      double left = -1;
      double right = -1;
      int scale = 100;
      int offsetX = -1;
      int offsetY = -1;
      
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
              
              // System.out.println("offsetX="+offsetX + " offsetY="+offsetY + " scale="+scale);
              repaint();
            }
          });
          
          addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
              int steps = (int)Math.pow(e.getWheelRotation(),2) * (e.getWheelRotation()<0 ? -1 : 1);
              int newScale = scale + (steps * 5);
              
              if (newScale < 50)
                newScale = 50;
              if (newScale > 100000)
                newScale = 100000;
              
              
              int mapWidth = scale;
              int mapHeight = scale;
              
              int newMapWidth = newScale;
              int newMapHeight = newScale;

              NodePoint unscaledXY = unscaleXY(e.getX(), e.getY());
              
              scale = newScale;
              
              NodePoint whereThePointIsNow = scaledXY(unscaledXY.x,unscaledXY.y);
              
              // System.out.println("offsetXY " + offsetX + ", " + offsetY);
              // System.out.println("whereThePointIsNow " + whereThePointIsNow.x + ", " + whereThePointIsNow.y);
              // System.out.println("diff " + (whereThePointIsNow.x-(double)offsetX) + ", " + (whereThePointIsNow.y-(double)offsetY));

              offsetX += e.getX() - whereThePointIsNow.x;
              offsetY += e.getY() - whereThePointIsNow.y;
              
              repaint();
            }
          });
          
          addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {}

            public void keyReleased(KeyEvent e) {}

            public void keyTyped(KeyEvent e) {
              // System.out.println(e.getKeyChar())
              if (e.getKeyChar() == 'p') {
                Way bb = osm.getWayByName("Academy Way");
                Way ee = osm.getWayByName("Port Stirling Place");
                
                Node startNode = osm.getNode(bb.nd.get(0));
                Node endNode = osm.getNode(ee.nd.get(0));
                generateAndDrawPathBetweenNodes(getGraphics(), startNode, endNode);
                // repaint();
                System.out.println("done");
              }
              else if (e.getKeyChar() == 'c') {
                // paintMap(null);
                // scale = 200;
                offsetX = (int)(getSize().getWidth()/2) - scale/2;
                offsetY = (int)(getSize().getHeight()/2) - scale/2;
                repaint();
              }
              else if (e.getKeyChar() == 'n') {
                scale = 46415;
                offsetX = -15532;
                offsetY = -43460;
                repaint();
              }
            }
          });
          
          
          javax.swing.Timer t = new javax.swing.Timer(16, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              repaint();
            }
          });
          t.start();
        }

        public Dimension getPreferredSize() {
            return new Dimension(800,600);
        }
        
        public NodePoint scaledXY(double lat, double lon) {
          if (top == -1)
            top = Double.valueOf(osm.getMinlat());
          if (bottom == -1)
            bottom = Double.valueOf(osm.getMaxlat());
          if (left == -1)
            left = Double.valueOf(osm.getMinlon());
          if (right == -1)
            right = Double.valueOf(osm.getMaxlon());
          
          double latF = lat;
          double lonF = lon;
          
          double pY = ((latF-top) / (double)(bottom-top));
          double pX = ((lonF-left) / (double)(right-left));
          
          double x = (pX * scale) + offsetX;
          double y = (pY * scale) + offsetY;
          
          return new NodePoint(x,y);
        }
        
        public NodePoint unscaleXY(int x, int y) {
          if (top == -1)
            top = Double.valueOf(osm.getMinlat());
          if (bottom == -1)
            bottom = Double.valueOf(osm.getMaxlat());
          if (left == -1)
            left = Double.valueOf(osm.getMinlon());
          if (right == -1)
            right = Double.valueOf(osm.getMaxlon());
          
          double pX = (x - offsetX) / (double)scale;
          double pY = (y - offsetY) / (double)scale;
          
          double lat = (pY * (bottom-top)) + top;
          double lon = (pX * (right-left)) + left;
          
          return new NodePoint((double)lat, (double)lon);
        }
        
        private void paintMap(Graphics g_old) {
          if (map == null) {
            map = new BufferedImage(getWidth(),getHeight(), BufferedImage.TYPE_INT_RGB);
            mapG2D = map.createGraphics();
            mapG2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
          }
          
          Graphics2D g2d = mapG2D;
          
          g2d.setColor(Color.white);
          g2d.fillRect(0,0,getWidth(),getHeight());
          
          
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
        	
          // double _np_old_x = -1;
          // double _np_old_y = -1;
        	
          Color[] colorz = new Color[10];
          colorz[0] = Color.black;
          colorz[1] = Color.red;
          colorz[2] = Color.green;
          colorz[3] = Color.blue;
          colorz[4] = Color.yellow;
          colorz[5] = Color.gray;
          colorz[6] = Color.cyan;
          colorz[7] = Color.orange;
          colorz[8] = Color.magenta;
          colorz[9] = Color.pink;
          
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
                   double laneSpacing =   0.00001;
                   double streetSpacing = laneSpacing;//*(_w.lanes+1);
                   
                   for (int l = 0; l < _w.lanes; l++) {
                     g2d.setColor(colorz[l%10]);
                     
                     if (l != 0) {
                       // System.out.println("_last_n.lat = " + _last_n.lat);
                       // System.out.println("\tl = " + l);
                       // System.out.println("\tshift = " + (streetSpacing+(l*laneSpacing)));
                       // System.out.println("\t_last_n.lat = " + (_last_n.lat+streetSpacing+(l*laneSpacing)));
                     }
                     
                     
                     NodePoint _last_np = scaledXY(
                       _last_n.lat+streetSpacing+(l*laneSpacing),
                       _last_n.lon+streetSpacing+(l*laneSpacing)
                     );
                     NodePoint _np = scaledXY(
                       _n.lat+streetSpacing+(l*laneSpacing),
                       _n.lon+streetSpacing+(l*laneSpacing)
                     );
                     
                     // fwd
                     g2d.setColor(Color.black);
                     g2d.drawLine(
                       (int)_last_np.x,
                       (int)_last_np.y,
                       (int)_np.x,
                       (int)_np.y
                     );
                     
                     
                     
                     if (!_w.oneway) {
                       NodePoint _last_np_reverse = scaledXY(
                         _last_n.lat-streetSpacing-(l*laneSpacing),
                         _last_n.lon-streetSpacing-(l*laneSpacing)
                       );
                       NodePoint _np_reverse = scaledXY(
                         _n.lat-streetSpacing-(l*laneSpacing),
                         _n.lon-streetSpacing-(l*laneSpacing)
                       );

                       // reverse
                       g2d.setColor(Color.blue);
                       g2d.drawLine(
                         (int)_last_np_reverse.x,
                         (int)_last_np_reverse.y,
                         (int)_np_reverse.x,
                         (int)_np_reverse.y
                       );
                     }
                   }
                   
                   
                   
                   
                     // // int streetSpacing = 8;
                     // int laneSpacing = 4;
                     // 
                     // // draw lanes on primary direction
                     // for (int l = 0; l < _n.way.lanes; l++) {
                     //   g2d.setColor(Color.BLACK);
                     //   g2d.drawLine((int)_np_old_x+streetSpacing+(l*laneSpacing),(int)_np_old_y+streetSpacing+(l*laneSpacing),(int)_np.x+streetSpacing+(l*laneSpacing),(int)_np.y+streetSpacing+(l*laneSpacing));
                     // }
                     // 
                     // // draw lanes on reverse direction
                     // for (int l = 0; l < _n.way.lanes; l++) {
                     //   g2d.setColor(Color.BLUE);
                     //   g2d.drawLine((int)_np_old_x-streetSpacing-(l*laneSpacing),(int)_np_old_y-streetSpacing-(l*laneSpacing),(int)_np.x-streetSpacing-(l*laneSpacing),(int)_np.y-streetSpacing-(l*laneSpacing));
                     // }
                   
                   // _np_old_x = _np.x;
                   // _np_old_y = _np.y;
                   _last_n = _n;
                 }
               }
             }
          }
        	
        	// nodes
        	g2d.setStroke(new BasicStroke(1f));
          g2d.setColor(Color.LIGHT_GRAY);
          
          g2d.setFont(new Font("TimesRoman", Font.PLAIN, (int)((scale/10000.0)*40)));
          
          for (Map.Entry<String, Node> entry : osm.nodeHash.entrySet()) {
            Node n = entry.getValue();
            NodePoint p = scaledXY(n.lat,n.lon);
            g2d.fillOval((int)p.x, (int)p.y, 2, 2);
          }
          
          
          
          
          
          // g2d.setStroke(new BasicStroke(1f));
          // g2d.setColor(Color.BLUE);
          //          
          // for (Intersection i : IntersectionRegistry.allRegisteredIntersections()) {
          //   Node n = Global.openStreetMap.getNode(i.getRootNodeId());
          //   
          //   NodePoint p = scaledXY(n.lat,n.lon);
          //   g2d.fillOval((int)p.x, (int)p.y, (int)i.getBounds(), (int)i.getBounds());
          // }
        }
        
        private void paintOverlay() {
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
          
          
          
          
          
          
          
          
          
          
          
          
          g2d.setStroke(new BasicStroke(4f));
          for (Intersection i : IntersectionRegistry.allRegisteredIntersections()) {
            
            String rootNodeId = i.getRootNodeId();
            
            Node rootNode = osm.getNode(rootNodeId);
            
            for (Node connectedNode : rootNode.connectedNodes) {
              double laneSpacing =   0.00001;
              double streetSpacing = laneSpacing;
              
              for (int l = 0; l < connectedNode.way.lanes; l++) {
                NodePoint rnP = scaledXY(
                  rootNode.lat+streetSpacing+(l*laneSpacing),
                  rootNode.lon+streetSpacing+(l*laneSpacing)
                );
                
                int light = i.getLightForWayOnLane(null, connectedNode.id, 0);
              
                NodePoint ccPUS = distanceFromPointInDirectionOfPoint(
                  rootNode.lat+streetSpacing+(l*laneSpacing),
                  rootNode.lon+streetSpacing+(l*laneSpacing),
                  connectedNode.lat+streetSpacing+(l*laneSpacing),
                  connectedNode.lon+streetSpacing+(l*laneSpacing),
                  0.00005
                );
                NodePoint cnP = scaledXY(ccPUS.x,ccPUS.y);
              
                if (light == 0) {
                  g2d.setColor(Color.green);
                }
                else if (light == 2) {
                  g2d.setColor(Color.red);
                }
            
                g2d.drawLine((int)cnP.x,(int)cnP.y,(int)rnP.x,(int)rnP.y);
              }
            }
          }
          
          
          
          
          
          
          
          
          
          
          
          
          
          
          
          
          
          
          // ways
        	Node _n;
        	NodePoint _np;
        	Way _w;
        	
          g2d.setStroke(new BasicStroke(1f));
          
          
          double laneSpacing =   0.00001;
          double streetSpacing = laneSpacing;
          for (Vehicle v : VehicleRegistry.allRegisteredVehicles()) {
             g2d.setColor(Color.red);
              
            // Node lastNode = osm.getNode(lastNodeId);
            // String nextNodeId = _w.nd.get(_w.nd.indexOf(lastNodeId) + 1);
            // Node nextNode = osm.getNode(nextNodeId);
            Node lastNode = v.getOriginNode();//osm.getNode(v.originNodeId);
            Node nextNode = v.getDestinationNode();//osm.getNode(v.destinationNodeId);
            NodePoint lnP = scaledXY(lastNode.lat,lastNode.lon);
            NodePoint nnP = scaledXY(nextNode.lat,nextNode.lon);
            
            g2d.setColor(Color.blue);
            g2d.fillOval((int)lnP.x, (int)lnP.y, 2, 2);
            g2d.setColor(Color.orange);
            g2d.fillOval((int)nnP.x, (int)nnP.y, 2, 2);
            
            g2d.setColor(Color.red);
            NodePoint p = scaledXY(v.lat+streetSpacing+(v.getOnLaneNumber()*laneSpacing),v.lon+streetSpacing+(v.getOnLaneNumber()*laneSpacing));
            g2d.fillOval((int)p.x, (int)p.y, 5, 5);
            
            // System.out.println("p.x = " + p.x + ", " + p.y);
            
            if (v.vehicleInFront != null) {
              g2d.setColor(Color.blue);
              NodePoint p1 = scaledXY(v.vehicleInFront.lat,v.vehicleInFront.lon);
              g2d.drawLine((int)p.x,(int)p.y,(int)p1.x,(int)p1.y);
            }
            if (v.vehicleBehind != null) {
              g2d.setColor(Color.red);
              NodePoint p1 = scaledXY(v.vehicleBehind.lat,v.vehicleBehind.lon);
              g2d.drawLine((int)p.x,(int)p.y,(int)p1.x,(int)p1.y);
            }
          }
          
          
          
          if (highlightPoint != null) {
            NodePoint lnP = scaledXY(highlightPoint.x, highlightPoint.y);
            g2d.setColor(Color.yellow);
            g2d.fillOval((int)lnP.x-10, (int)lnP.y-10, 20, 20);
          }
          
          // for(int i = 0; i < osm.ways.size(); i++) {
          //   g2d.setColor(Color.red);
          //  _w = osm.ways.get(i);
          //  
          //   for (Vehicle v : _w.vehiclesTraversing) {
          //     
          //     String lastNodeId = v.lastPassedNodeId;
          //     if (v.originNodeId == null) {
          //       System.out.println("not drawing line");
          //     }
          //     else {
          //       // Node lastNode = osm.getNode(lastNodeId);
          //       // String nextNodeId = _w.nd.get(_w.nd.indexOf(lastNodeId) + 1);
          //       // Node nextNode = osm.getNode(nextNodeId);
          //       Node lastNode = osm.getNode(v.originNodeId);
          //       Node nextNode = osm.getNode(v.destinationNodeId);
          //       NodePoint lnP = scaledXY(lastNode.lat,lastNode.lon);
          //       NodePoint nnP = scaledXY(nextNode.lat,nextNode.lon);
          //       
          //       g2d.setColor(Color.blue);
          //       g2d.fillOval((int)lnP.x, (int)lnP.y, 2, 2);
          //       g2d.setColor(Color.green);
          //       g2d.fillOval((int)nnP.x, (int)nnP.y, 4, 4);
          //     }
          //     
          //     g2d.setColor(Color.red);
          //     
          //     NodePoint p = scaledXY(v.lat,v.lon);
          //     g2d.fillOval((int)p.x, (int)p.y, 5, 5);
          //     
          //     if (v.vehicleInFront != null) {
          //       g2d.setColor(Color.blue);
          //       NodePoint p1 = scaledXY(v.vehicleInFront.lat,v.vehicleInFront.lon);
          //       g2d.drawLine((int)p.x,(int)p.y,(int)p1.x,(int)p1.y);
          //     }
          //     if (v.vehicleBehind != null) {
          //       g2d.setColor(Color.red);
          //       NodePoint p1 = scaledXY(v.vehicleBehind.lat,v.vehicleBehind.lon);
          //       g2d.drawLine((int)p.x,(int)p.y,(int)p1.x,(int)p1.y);
          //     }
          //   }
          // }
        }
        
        
        private void generateAndDrawPathBetweenNodes(Graphics g, Node startNode, Node endNode) {
          LinkedList<Node> aStarResult = generatePathBetweenNodes(startNode, endNode);
          paintPath(g, aStarResult);
        }
        
        private LinkedList<Node> generatePathBetweenNodes(Node startNode, Node endNode) {
          LinkedList<Node> aStarResult = (LinkedList)osm.AStar2.findPath(startNode, endNode);
          aStarResult.addFirst(startNode);
          System.out.println("Found A* path, len = " + aStarResult.size());
          return aStarResult;
        }
        
        private void paintPath(Graphics g, LinkedList<Node> path) {
          Graphics2D g2d = (Graphics2D)g;
          
          // draw path
          g2d.setStroke(new BasicStroke(4f));
          g2d.setColor(Color.blue);
          double __np_old_x = -1;
          double __np_old_y = -1;
          
          for (int i = 0; i < path.size(); i++) {
            Node __n = path.get(i);
            NodePoint p = scaledXY(__n.lat, __n.lon);
            
            // g.fillOval((int)p.x, (int)p.y, 4, 4);
            
            p = scaledXY(__n.lat, __n.lon);
            
            if(i == 0){
              __np_old_x = p.x;
              __np_old_y = p.y;
            } else {
              g2d.drawLine((int)__np_old_x,(int)__np_old_y,(int)p.x,(int)p.y);
              __np_old_x = p.x;
              __np_old_y = p.y;
            }
          }
          
          Node startNode = path.get(0);
          Node endNode = path.get(path.size()-1);
          
          // draw start and end nodes
          g2d.setStroke(new BasicStroke(1f));
          g2d.setColor(Color.green);
          NodePoint p1 = scaledXY(startNode.lat,startNode.lon);
          g.fillOval((int)p1.x, (int)p1.y, 8, 8);
          NodePoint p2 = scaledXY(endNode.lat,endNode.lon);
          g.fillOval((int)p2.x, (int)p2.y, 8, 8);
        }
        
        public void paintComponent(Graphics g) {
          if (offsetX == -1)
            offsetX = (int)(getSize().getWidth()/2) - scale/2;
          if (offsetY == -1)
            offsetY = (int)(getSize().getHeight()/2) - scale/2;
            
            super.paintComponent(g);
            
            paintMap(g);
            paintOverlay();
            
            g.drawImage(map, 0, 0, null);
            g.drawImage(overlay, 0, 0, null);
        }
        
        @Override
        public boolean isFocusTraversable()
        {
          return true;
        }
        
        private NodePoint distanceFromPointInDirectionOfPoint(double fromLat, double fromLon, double toLat, double toLon, double d) {
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