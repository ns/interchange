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

public class AppWindow implements ActionListener {
	JFrame f;

	private AppPanel appPanel;

	public AppWindow() throws InterruptedException {
		appPanel = new AppPanel();

		f = new JFrame("Interchange");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(appPanel);
		buildMenuBar();
		f.pack();
		f.setVisible(true);
		f.setLocationRelativeTo(null);
	}

	private void buildMenuBar() {
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
		debug.add(makeMenuItem("Toggle Distances"));

		menubar.add(sim);
		menubar.add(view);
		menubar.add(debug);

		f.setJMenuBar(menubar);
	}

	private JMenuItem makeMenuItem(String action) {
		return makeMenuItem(action, this);
	}

	private JMenuItem makeMenuItem(String action, ActionListener listener) {
		JMenuItem eMenuItem = new JMenuItem(action);
		eMenuItem.addActionListener(listener);
		return eMenuItem;
	}

	public void actionPerformed(ActionEvent e) {
		// Menu item actions
		String command = e.getActionCommand();
		try {
			if (command.equals("Start")) {
				Global.simulator.unpause();
			} else if (command.equals("Stop")) {
				Global.simulator.pause();
			} else if (command.equals("Reset")) {
				// Global.simulator.resetSimulator();
			} else if (command.equals("Toggle Vehicle Traces")) {
				appPanel.showVehicleDebugTraces = !appPanel.showVehicleDebugTraces;
			} else if (command.equals("Slow Down")) {
				Global.simulator.changeSpeed(+10);
			} else if (command.equals("Slow Down")) {
				Global.simulator.changeSpeed(-10);
			} else if (command.equals("Zoom In")) {
				System.out.println("here");
				appPanel.zoomMap(+10);
				// myPanel.centerMap();
			} else if (command.equals("Zoom Out")) {
				appPanel.zoomMap(-10);
				// myPanel.centerMap();
			} else if (command.equals("Center Map")) {
				appPanel.centerMap();
			} else if (command.equals("Toggle Infrastructure Map")) {
				appPanel.showMap = !appPanel.showMap;
			} else if (command.equals("Use Black Background")) {
				appPanel.backgroundColor = Color.black;
			} else if (command.equals("Use White Background")) {
				appPanel.backgroundColor = Color.white;
			} else if (command.equals("Toggle Nodes")) {
				appPanel.showAllNodes = !appPanel.showAllNodes;
			} else if (command.equals("Toggle Place Names")) {
				appPanel.showPlaceNames = !appPanel.showPlaceNames;
			} else if (command.equals("Toggle Vehicle Info")) {
				appPanel.showVehicleInfo = !appPanel.showVehicleInfo;
			} else if (command.equals("Toggle Distances")) {
				appPanel.showDistances = !appPanel.showDistances;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}