package org.uci.luci.interchange.UI;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.uci.luci.interchange.Registry.IntersectionRegistry;
import org.uci.luci.interchange.Util.Global;
import org.uci.luci.interchange.Factory.*;

public class SimulatorOptionsWindow {
	public boolean waiting = true;
	public boolean TypeOfIntersection;
	protected JFrame f;
	
	public SimulatorOptionsWindow() throws Exception
	{
		f = new JFrame("Interchange");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(new OptionsWindow());
		f.pack();
		f.setVisible(true);
		f.setLocationRelativeTo(null);	
	}
	
	private class OptionsWindow extends JPanel implements ActionListener
	{
		JComboBox mapChoice;
		JTextField simSpeed;
		ButtonGroup intersectionType;
		
		public OptionsWindow() throws Exception
		{
			//Parses Lib Directory for all Files to list as OSM
			File dir = new File("lib");
			String[] maps = dir.list(new FilenameFilter(){public boolean accept(File arg0, String arg1){if(arg1.contains(".osm.xml"))return true;return false;}});
			if (maps == null) {
				throw new Exception();
			}
			this.add(new JLabel("Map to Simulate"));
			mapChoice = new JComboBox(maps);
			this.add(mapChoice);
			
			
			//Speed for Simulator Default is 10
			this.add(new JLabel("Simulator Speed"));
			simSpeed = new JTextField("10",2);
			this.add(simSpeed);
			
			//Traditional vs Interchange
			intersectionType = new ButtonGroup();
			JRadioButton t = new JRadioButton("Traditional");
			t.setActionCommand("Traditional");
			this.add(t);
			intersectionType.add(t);
			t = new JRadioButton("Bidding");
			t.setActionCommand("Bidding");
			this.add(t);
			t.setSelected(true);
			intersectionType.add(t);
			
			
			
			
			JButton subButton = new JButton("Start Simulation");
			subButton.addActionListener(this);
			this.add(subButton);
		}

		public void actionPerformed(ActionEvent arg0) {
		
			if(arg0.getActionCommand().equals("Start Simulation"))
			{
				if(submitValues())
					f.setVisible(false);
			}
		}
		
		public boolean submitValues()
		{
      String map = (String) mapChoice.getSelectedItem();
      if(map.equals(""))
      {
       JOptionPane.showMessageDialog(this, "Please Select a valid map file");
       return false;
      }
      
		  try {
		    int simulationSpeed = Integer.parseInt(simSpeed.getText());
		    String intersectionsType = intersectionType.getSelection().getActionCommand();
  		  SimulationFactory.runSimulation(map, simulationSpeed, intersectionsType);
		  }
		  catch (Exception e) {
		    e.printStackTrace();
		  }
      
			return true;
		}
	}
}
