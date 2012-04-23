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
  OpenStreetMap openStreetMap;
  JFrame f;
  MyPanel myPanel;
	HashMap<String, Vehicle> vehicleHash = new HashMap<String, Vehicle>();
	ArrayList<String> spawnPoints = new ArrayList<String>();
  
    public AppWindow(OpenStreetMap openStreetMap) throws InterruptedException {
      this.openStreetMap = openStreetMap;
      
      
      openStreetMap.purgeUnconnectedNodes();
      
      myPanel = new MyPanel(openStreetMap);
      
      f = new JFrame("Interchange");
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.add(myPanel);
      f.pack();
      f.setVisible(true);
      f.setLocationRelativeTo(null);
      
      simulate();
    }
    
    public void simulate() throws InterruptedException {
      Random generator = new Random();
      Object[] values = openStreetMap.nodeHash.keySet().toArray();
      
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      spawnPoints.add((String)values[generator.nextInt(values.length)]);
      System.out.println(spawnPoints.get(0));
      
      int vin = 0;
      int tick = 0;
      int cars=0;
      while (true) {
        tick++;
        cars=0;
        
        // System.out.println("vehicles: spawn");
        for (String spawnPoint : spawnPoints) {
          if (!(tick%100==1)) {
            break;
          }
          Node node = openStreetMap.getNode(openStreetMap.getNode(spawnPoint).way.nd.get(0));
          // spawn a vehicle if there is room
          Vehicle newV = new Vehicle(
            Float.valueOf(node.lat),
            Float.valueOf(node.lon)
          );
          newV._way = openStreetMap.getNode(spawnPoint).way;
          newV.openStreetMap = openStreetMap;
          node.way.vehiclesTraversing.add(newV);
          vehicleHash.put(vin+"", newV);
          vin++;
        }
        
        long startTime = System.nanoTime();
        long endTime;
        
        // System.out.println("vehicles: tick");
        for (Map.Entry<String, Vehicle> entry : vehicleHash.entrySet()) {
          Vehicle v = entry.getValue();
          v.tick();
          cars++;
        }
        
        
        for(int i = 0; i < openStreetMap.ways.size(); i++) {
          Way w = openStreetMap.ways.get(i);
          for (Vehicle v : w.vehiclesTraversing) {
            // each vehicle's velocity vector has been determined by now
            // we simply calculate exactly where the vehicle should be for
            // the next timestep
            
            String lastNodeId = v.lastPassedNodeId;
            if (lastNodeId == null)
              lastNodeId = v.lastPassedNodeId = w.nd.get(0);
            Node lastNode = openStreetMap.getNode(lastNodeId);
            
            String nextNodeId = w.nd.get(w.nd.indexOf(lastNodeId) + 1);
            Node nextNode = openStreetMap.getNode(nextNodeId);
            
            double newLat = v.lat + v.velocity.x;
            double newLon = v.lon + v.velocity.y;
            
            v.lat = (double)newLat;
            v.lon = (double)newLon;
          }
        }
        
        
        endTime = System.nanoTime();
        long duration = endTime - startTime;
        System.out.println("d = " + duration + " cars = " + cars);
        
        Thread.sleep(2);
        myPanel.repaint();
      }
    }
    
    class MyPanel extends JPanel {
      OpenStreetMap osm;
      float top = -1;
      float bottom = -1;
      float left = -1;
      float right = -1;
      int scale = 10;
      int offsetX = 0;
      int offsetY = 0;
      
      private BufferedImage map;
      private Graphics2D mapG2D;
      
      private BufferedImage overlay;
      private Graphics2D overlayG2D;
      
      Point draggingPointOrigin;
      
      int draggingOffsetX;
      int draggingOffsetY;
      
        public MyPanel(OpenStreetMap openStreetMap) {
          this.osm = openStreetMap;
          setBorder(BorderFactory.createLineBorder(Color.black));
          
          
          this.requestFocus();
          
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
              int steps = e.getWheelRotation();
              int newScale = scale + (steps * 50);
              
              if (newScale < 50)
                newScale = 50;
              if (newScale > 100000)
                newScale = 100000;
              
              // correct center on mouse X,Y
              // if (scale != newScale) {
              //   double pX = ((double)offsetX/(double)scale);
              //   double pY = ((double)offsetY/(double)scale);
              //   offsetX = (int)(getSize().getWidth()/2) - (int)(pX*newScale);
              //   offsetY = (int)(getSize().getHeight()/2) - (int)(pY*newScale);
              // }
              
              scale = newScale;
              
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
                scale = 200;
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
        }

        public Dimension getPreferredSize() {
            return new Dimension(800,600);
        }
        
        public NodePoint scaledXY(String lat, String lon) {
          if (top == -1)
            top = Float.valueOf(osm.getMinlat());
          if (bottom == -1)
            bottom = Float.valueOf(osm.getMaxlat());
          if (left == -1)
            left = Float.valueOf(osm.getMinlon());
          if (right == -1)
            right = Float.valueOf(osm.getMaxlon());
          
          float latF = Float.valueOf(lat);
          float lonF = Float.valueOf(lon);
          
          float pY = ((latF-top) / (bottom-top));
          float pX = ((lonF-left) / (right-left));
          
          float x = (pX * scale) + offsetX;
          float y = (pY * scale) + offsetY;
          
          return new NodePoint(x,y);
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
          
  	    	// ways
        	Node _n;
        	NodePoint _np;
        	Way _w;
        	
          float _np_old_x = -1;
          float _np_old_y = -1;
        	
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
        	
          g2d.setStroke(new BasicStroke(1f));
          for(int i = 0; i < osm.ways.size(); i++) {
            g2d.setColor(colorz[i%10]);
           _w = osm.ways.get(i);
           
            for(int j = 0; j < _w.nd.size(); j++){
              _n = osm.getNode(_w.nd.get(j));
              _np = scaledXY(_n.lat, _n.lon);

              if(j == 0){
                _np_old_x = _np.x;
                _np_old_y = _np.y;
              } else {
                g2d.drawLine((int)_np_old_x,(int)_np_old_y,(int)_np.x,(int)_np.y);
                _np_old_x = _np.x;
                _np_old_y = _np.y;
              }
            }
          }
        	
        	// nodes
        	g2d.setStroke(new BasicStroke(1f));
          g2d.setColor(Color.red);
          
          g2d.setFont(new Font("TimesRoman", Font.PLAIN, (int)((scale/10000.0)*40)));
          
          for (Map.Entry<String, Node> entry : osm.nodeHash.entrySet()) {
            Node n = entry.getValue();
            NodePoint p = scaledXY(n.lat,n.lon);
            g2d.fillOval((int)p.x, (int)p.y, 2, 2);
          }
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
          
          // ways
        	Node _n;
        	NodePoint _np;
        	Way _w;
        	
          g2d.setStroke(new BasicStroke(1f));
          for(int i = 0; i < osm.ways.size(); i++) {
            g2d.setColor(Color.red);
           _w = osm.ways.get(i);
           
            for (Vehicle v : _w.vehiclesTraversing) {
              
              String lastNodeId = v.lastPassedNodeId;
              if (v.originNodeId == null) {
                System.out.println("not drawing line");
              }
              else {
                // Node lastNode = osm.getNode(lastNodeId);
                // String nextNodeId = _w.nd.get(_w.nd.indexOf(lastNodeId) + 1);
                // Node nextNode = osm.getNode(nextNodeId);
                Node lastNode = osm.getNode(v.originNodeId);
                Node nextNode = osm.getNode(v.destinationNodeId);
                NodePoint lnP = scaledXY(lastNode.lat+"",lastNode.lon+"");
                NodePoint nnP = scaledXY(nextNode.lat+"",nextNode.lon+"");
                
                g2d.setColor(Color.blue);
                g2d.fillOval((int)lnP.x, (int)lnP.y, 2, 2);
                g2d.setColor(Color.green);
                g2d.fillOval((int)nnP.x, (int)nnP.y, 4, 4);
              }
              
              g2d.setColor(Color.red);
              
              NodePoint p = scaledXY(v.lat+"",v.lon+"");
              g2d.fillOval((int)p.x, (int)p.y, 5, 5);
            }
          }
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
          float __np_old_x = -1;
          float __np_old_y = -1;
          
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
    }
    
    class NodePoint{
    	float x;
    	float y;
    	float xc; // constrained on Canvas
    	float yc; // constrained on Canvas
    	NodePoint(float _x, float _y){
    		x = _x;
    		y = _y;
    	}
    	NodePoint sub(float _x, float _y){
    		return new NodePoint(x-_x, y-_y);
    	}
    	NodePoint sub(NodePoint n){
    		return new NodePoint(x-n.x,y-n.y);
    	}
    }
}