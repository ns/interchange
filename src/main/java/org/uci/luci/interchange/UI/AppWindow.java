package org.uci.luci.interchange.UI;

import org.uci.luci.interchange.Util.*;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.event.*;

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
		sim.add(makeMenuItem("Speed Up\t -"));
		sim.add(makeMenuItem("Slow Down\t = or +"));

		JMenu view = new JMenu("View");
		view.setMnemonic(KeyEvent.VK_V);
		view.add(makeMenuItem("Center Map"));
		view.add(makeMenuItem("Zoom In\t\t ["));
		view.add(makeMenuItem("Zoom Out\t ]"));
		view.addSeparator();
		view.add(makeMenuItem("Use White Background"));
		view.add(makeMenuItem("Use Black Background"));
		view.addSeparator();
		view.add(makeCheckBoxMenuItem("Toggle Place Names"));
		view.add(makeCheckBoxMenuItem("Toggle Vehicle Info\t v"));

		JMenu debug = new JMenu("Debug");
		debug.add(makeCheckBoxMenuItem("Toggle Vehicle Traces\t t"));
		debug.add(makeCheckBoxMenuItem("Toggle Infrastructure Map\t m"));
		debug.add(makeCheckBoxMenuItem("Toggle Nodes\t n"));
		debug.add(makeCheckBoxMenuItem("Toggle Distances\t d"));

		menubar.add(sim);
		menubar.add(view);
		menubar.add(debug);

		f.setJMenuBar(menubar);
	}

	private JMenuItem makeMenuItem(String action) {
		return makeMenuItem(action, this);
	}

	private JCheckBoxMenuItem makeCheckBoxMenuItem(String action) {
		return makeCheckBoxMenuItem(action, this, false);
	}
	
	private JCheckBoxMenuItem makeCheckBoxMenuItem(String action, Boolean startChecked) {
		return makeCheckBoxMenuItem(action, this, startChecked);
	}
	
	private JCheckBoxMenuItem makeCheckBoxMenuItem(String action, ActionListener listener, Boolean startChecked) {
		JCheckBoxMenuItem eMenuItem = new JCheckBoxMenuItem(action, startChecked);
		eMenuItem.addActionListener(listener);
		return eMenuItem;
	}
	
	private JMenuItem makeMenuItem(String action, ActionListener listener) {
		JMenuItem eMenuItem = new JMenuItem(action);
		eMenuItem.addActionListener(listener);
		return eMenuItem;
	}

	public void actionPerformed(ActionEvent e) {
		performAction(e.getActionCommand());
	}
	
	public void performAction(String command)
	{
		try {
			if (command.equals("Start")) {
				Global.simulator.unpause();
			} else if (command.equals("Stop")) {
				Global.simulator.pause();
			} else if (command.equals("Reset")) {
				// Global.simulator.resetSimulator();
			} else if (command.equals("Toggle Vehicle Traces\t t")) {
				appPanel.showVehicleDebugTraces = !appPanel.showVehicleDebugTraces;
			} else if (command.equals("Speed Up\t = or +")) {
				Global.simulator.changeSpeed(+10);
			} else if (command.equals("Slow Down\t -")) {
				Global.simulator.changeSpeed(-10);
			} else if (command.equals("Zoom In\t\t [")) {
				System.out.println("here");
				appPanel.zoomMap(+10);
				// myPanel.centerMap();
			} else if (command.equals("Zoom Out\t ]")) {
				appPanel.zoomMap(-10);
				// myPanel.centerMap();
			} else if (command.equals("Center Map")) {
				appPanel.centerMap();
			} else if (command.equals("Toggle Infrastructure Map\t m")) {
				appPanel.showMap = !appPanel.showMap;
			} else if (command.equals("Use Black Background")) {
				appPanel.backgroundColor = Color.black;
			} else if (command.equals("Use White Background")) {
				appPanel.backgroundColor = Color.white;
			} else if (command.equals("Toggle Nodes\t n")) {
				appPanel.showAllNodes = !appPanel.showAllNodes;
			} else if (command.equals("Toggle Place Names")) {
				appPanel.showPlaceNames = !appPanel.showPlaceNames;
			} else if (command.equals("Toggle Vehicle Info\t v")) {
				appPanel.showVehicleInfo = !appPanel.showVehicleInfo;
			} else if (command.equals("Toggle Distances\t d")) {
				appPanel.showDistances = !appPanel.showDistances;
			} else {
				System.out.println("Command Not Found: '" + command + "'");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}