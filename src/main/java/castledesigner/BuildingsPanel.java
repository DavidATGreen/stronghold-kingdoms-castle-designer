/*
 * Copyright (c) 2012 David Green
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package castledesigner;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author David Green
 */
public class BuildingsPanel extends JPanel implements DesignListener
{
	public static final String		SELECTED_BUILDING	= "SelectedBuilding";
	private static final Integer[]	constructionBonuses	= new Integer[]
															{ 0, 10, 15, 20, 25, 30, 35, 40, 45, 50, 60 };
	private ButtonGroup				buildingButtonGroup	= new ButtonGroup();
	private static final Dimension	buttonDimension		= new Dimension(50, 50);
	private JLabel					moatsLabel;
	private JLabel					ballistaTowersLabel;
	private JLabel					turretsLabel;
	private JLabel					guardHousesLabel;
	private JLabel					bombardsLabel;
	
	private JLabel	stoneLabel;
	private JLabel	woodLabel;
	private JLabel	ironLabel;
	private JLabel	goldLabel;
	private JLabel	timeLabel;
	
	private JSlider	constructionSpeed;
	private JSlider	ageSelection;
	
	private Castle castle;
	
	public BuildingsPanel()
	{
		JToggleButton woodenWallButton = createButton("<html>Wooden Wall</html>", BuildingType.WOODEN_WALL);
		JToggleButton woodenTowerButton = createButton("<html>Wooden Tower</html>", BuildingType.WOODEN_TOWER);
		JToggleButton woodenGatehouseButton = createButton("<html>Wooden Gatehouse</html>", BuildingType.WOODEN_GATEHOUSE);
		
		JToggleButton stoneWallButton = createButton("<html>Stone Wall</html>", BuildingType.STONE_WALL);
		JToggleButton stoneGatehouseButton = createButton("<html>Stone Gatehouse</html>", BuildingType.STONE_GATEHOUSE);
		JToggleButton lookoutTowerButton = createButton("<html>Lookout Tower (2x2)</html>", BuildingType.LOOKOUT_TOWER);
		JToggleButton smallTowerButton = createButton("<html>Small Tower (3x3)</html>", BuildingType.SMALL_TOWER);
		JToggleButton largeTowerButton = createButton("<html>Large Tower (4x4)</html>", BuildingType.LARGE_TOWER);
		JToggleButton greatTowerButton = createButton("<html>Great Tower (5x5)</html>", BuildingType.GREAT_TOWER);
		
		JToggleButton guardHouseButton = createButton("<html>Guard House</html>", BuildingType.GUARD_HOUSE);
		JToggleButton ballistaTowerButton = createButton("<html>Ballista Tower</html>", BuildingType.BALLISTA_TOWER);
		JToggleButton turretButton = createButton("<html>Turret</html>", BuildingType.TURRET);
		JToggleButton bombardButton = createButton("<html>Bombard</html>", BuildingType.BOMBARD);
		JToggleButton smelterButton = createButton("<html>Smelter</html>", BuildingType.SMELTER);
		JToggleButton moatButton = createButton("<html>Moat</html>", BuildingType.MOAT);
		JToggleButton killingPitButton = createButton("<html>Killing Pit</html>", BuildingType.KILLING_PIT);
		
		this.setMaximumSize(new Dimension(300, 750));
		this.setMinimumSize(new Dimension(300, 750));
		this.setPreferredSize(new Dimension(300, 750));
		
		setLayout(new BorderLayout());
		
		JPanel ageSelectionPanel = createAgeSelectionPanel();
		add(ageSelectionPanel, BorderLayout.NORTH);
		
		JPanel buttonsPanel = new JPanel(new GridLayout(8, 2));
		buttonsPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
		buttonsPanel.add(stoneWallButton);
		buttonsPanel.add(woodenWallButton);
		buttonsPanel.add(stoneGatehouseButton);
		buttonsPanel.add(woodenGatehouseButton);
		buttonsPanel.add(guardHouseButton);
		buttonsPanel.add(woodenTowerButton);
		buttonsPanel.add(lookoutTowerButton);
		buttonsPanel.add(smallTowerButton);
		buttonsPanel.add(largeTowerButton);
		buttonsPanel.add(greatTowerButton);
		buttonsPanel.add(smelterButton);
		buttonsPanel.add(ballistaTowerButton);
		buttonsPanel.add(turretButton);
		buttonsPanel.add(bombardButton);
		buttonsPanel.add(moatButton);
		buttonsPanel.add(killingPitButton);
		
		add(buttonsPanel, BorderLayout.CENTER);
		
		JPanel infoPanel = new JPanel(new BorderLayout());
		
		JPanel quantitiesPanel = createQuantitiesPanel();
		infoPanel.add(BorderLayout.NORTH, quantitiesPanel);
		
		JPanel resourcesPanel = createResourcesPanel();
		infoPanel.add(BorderLayout.CENTER, resourcesPanel);
		
		JPanel timePanel = createTimePanel();
		infoPanel.add(BorderLayout.SOUTH, timePanel);
		
		add(infoPanel, BorderLayout.SOUTH);
		
		this.repaint();
	}
	
	private JPanel createQuantitiesPanel()
	{
		JPanel quantitiesPanel = new JPanel(new GridLayout(5, 2));
		quantitiesPanel.setBorder(new TitledBorder("Number of Buildings"));
		JLabel moatsTitleLabel = new JLabel("Moats");
		moatsLabel = new JLabel("-/-");
		JLabel ballistaTowersTitleLabel = new JLabel("Ballista Towers");
		ballistaTowersLabel = new JLabel("-/-");
		JLabel bombardsTitleLabel = new JLabel("Bombards");
		bombardsLabel = new JLabel("-/-");
		JLabel turretsTitleLabel = new JLabel("Turrets");
		turretsLabel = new JLabel("-/-");
		JLabel guardHousesTitleLabel = new JLabel("Guard Houses");
		guardHousesLabel = new JLabel("-/-");
		
		quantitiesPanel.add(guardHousesTitleLabel);
		quantitiesPanel.add(guardHousesLabel);
		quantitiesPanel.add(ballistaTowersTitleLabel);
		quantitiesPanel.add(ballistaTowersLabel);
		quantitiesPanel.add(turretsTitleLabel);
		quantitiesPanel.add(turretsLabel);
		quantitiesPanel.add(bombardsTitleLabel);
		quantitiesPanel.add(bombardsLabel);
		quantitiesPanel.add(moatsTitleLabel);
		quantitiesPanel.add(moatsLabel);
		
		return quantitiesPanel;
	}
	
	private JPanel createResourcesPanel()
	{
		JPanel resourcesPanel = new JPanel(new GridLayout(2, 4));
		resourcesPanel.setBorder(new TitledBorder("Resources Used"));
		JLabel stoneTitleLabel = new JLabel("Stone");
		stoneLabel = new JLabel("0");
		JLabel woodTitleLabel = new JLabel("Wood");
		woodLabel = new JLabel("0");
		JLabel ironTitleLabel = new JLabel("Iron");
		ironLabel = new JLabel("0");
		JLabel goldTitleLabel = new JLabel("Gold");
		goldLabel = new JLabel("0");
		resourcesPanel.add(stoneTitleLabel);
		resourcesPanel.add(stoneLabel);
		resourcesPanel.add(woodTitleLabel);
		resourcesPanel.add(woodLabel);
		resourcesPanel.add(ironTitleLabel);
		resourcesPanel.add(ironLabel);
		resourcesPanel.add(goldTitleLabel);
		resourcesPanel.add(goldLabel);
		
		return resourcesPanel;
	}
	
	private JPanel createTimePanel()
	{
		timeLabel = new JLabel("0s");
		
		constructionSpeed = new JSlider(JSlider.HORIZONTAL, 0, 10, 0);
		constructionSpeed.setSnapToTicks(true);
		constructionSpeed.setMinorTickSpacing(1);
		constructionSpeed.setMajorTickSpacing(1);
		constructionSpeed.setPaintTicks(true);
		constructionSpeed.setPaintLabels(true);
		
		constructionSpeed.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				updateTotalBuildingTime();
			}
		});
		
		JPanel totalTimePanel = new JPanel();
		totalTimePanel.setBorder(new TitledBorder("Construction Time"));
		totalTimePanel.add(timeLabel);
		totalTimePanel.setMinimumSize(new Dimension(104, 0));
		totalTimePanel.setMaximumSize(new Dimension(104, 0));
		totalTimePanel.setPreferredSize(new Dimension(104, 0));
		
		JPanel constructionResearchPanel = new JPanel();
		constructionResearchPanel.setBorder(new TitledBorder("Construction Research Rank"));
		constructionResearchPanel.add(constructionSpeed);
		
		JPanel timePanel = new JPanel(new BorderLayout());
		timePanel.add(BorderLayout.WEST, totalTimePanel);
		timePanel.add(BorderLayout.CENTER, constructionResearchPanel);
		
		return timePanel;
	}
	
	private JPanel createAgeSelectionPanel()
	{
		ageSelection = new JSlider(JSlider.HORIZONTAL, 1, 5, 1);
		ageSelection.setSnapToTicks(true);
		ageSelection.setMinorTickSpacing(1);
		ageSelection.setMajorTickSpacing(1);
		ageSelection.setPaintTicks(false);
		ageSelection.setPaintLabels(true);
		ageSelection.setMinimumSize(new Dimension(200, 38));
		ageSelection.setMaximumSize(new Dimension(200, 38));
		ageSelection.setPreferredSize(new Dimension(200, 38));
		ageSelection.addChangeListener(new ChangeListener()
		{
			
			public void stateChanged(ChangeEvent e)
			{
				if (castle != null)
					castle.setAge(ageSelection.getValue());
					
				updateMaximumBuildingsPerAge();
			}
		});
		
		JPanel ageSelectorPanel = new JPanel();
		ageSelectorPanel.setBorder(new TitledBorder("SHK Age")
		{
			private Insets customInsets = new Insets(1, 1, 1, 1);
			
			public Insets getBorderInsets(Component c)
			{
				return customInsets;
			}
			
		});
		ageSelectorPanel.add(ageSelection);
		ageSelectorPanel.setMinimumSize(new Dimension(250, 50));
		ageSelectorPanel.setMaximumSize(new Dimension(250, 50));
		ageSelectorPanel.setPreferredSize(new Dimension(250, 50));
		// ageSelectorPanel.set
		
		return ageSelectorPanel;
	}
	
	public void updateAgeSelection(int age)
	{
		if (age <= 5 && age >= 1)
		{
			ageSelection.setValue(age);
			updateMaximumBuildingsPerAge();
		}
	}
	
	protected void updateMaximumBuildingsPerAge()
	{
		if (castle != null)
		{
			castle.setMaxBuildings();
			updateBuildingQuantities();
			castle.updateDesignStats();
			Editor.updateErrorPanel();
		}
	}
	
	private JToggleButton createButton(String buttonText, final BuildingType buildingType)
	{
		JToggleButton button;
		if (buildingType.getImage() != null)
			button = new JToggleButton(buttonText, new ImageIcon(buildingType.getImage()));
		else
		{
			button = new JToggleButton(buttonText);
			button.setBackground(buildingType.getColour());
		}
		
		button.setPreferredSize(buttonDimension);
		button.setMinimumSize(buttonDimension);
		button.setSize(buttonDimension);
		button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				BuildingsPanel.this.firePropertyChange(SELECTED_BUILDING, 0, buildingType);
			}
		});
		buildingButtonGroup.add(button);
		return button;
	}
	
	public void setCastle(Castle castle)
	{
		this.castle = castle;
		updateBuildingQuantities();
	}
	
	public void designChanged()
	{
		updateBuildingQuantities();
		updateResources();
		updateTotalBuildingTime();
	}
	
	private void updateBuildingQuantities()
	{
		moatsLabel.setText(getBuildingQuantities(BuildingType.MOAT));
		ballistaTowersLabel.setText(getBuildingQuantities(BuildingType.BALLISTA_TOWER));
		turretsLabel.setText(getBuildingQuantities(BuildingType.TURRET));
		bombardsLabel.setText(getBuildingQuantities(BuildingType.BOMBARD));
		guardHousesLabel.setText(getBuildingQuantities(BuildingType.GUARD_HOUSE));
	}
	
	private String getBuildingQuantities(BuildingType buildingType)
	{
		int numberOfBuildings = 0;
		int maxNumberOfBuildings = 0;
		if (castle != null)
		{
			numberOfBuildings = castle.getNumberOfBuildings(buildingType);
			maxNumberOfBuildings = castle.getMaximumNumberOfBuildings(buildingType);
		}
		
		boolean exceeded = numberOfBuildings > maxNumberOfBuildings;
		boolean maximum = numberOfBuildings == maxNumberOfBuildings;
		
		StringBuilder s = new StringBuilder();
		if (exceeded | maximum)
			s.append("<html>");
			
		if (exceeded)
			s.append("<font color=\"red\"><b>");
		else if (maximum)
			s.append("<font color=\"#008800\"/>");
		s.append(numberOfBuildings);
		if (exceeded)
			s.append("</b></font>");
		else if (maximum)
			s.append("</font>");
		s.append('/');
		s.append(maxNumberOfBuildings);
		if (exceeded | maximum)
			s.append("</html>");
			
		return s.toString();
	}
	
	private void updateResources()
	{
		stoneLabel.setText(getBuildingResources(BuildingResource.STONE));
		woodLabel.setText(getBuildingResources(BuildingResource.WOOD));
		ironLabel.setText(getBuildingResources(BuildingResource.IRON));
		goldLabel.setText(getBuildingResources(BuildingResource.GOLD));
	}
	
	private void updateTotalBuildingTime()
	{
		if (castle != null)
		{
			int researchDiscount = constructionBonuses[constructionSpeed.getValue()];
			int buildingTime = (int) (castle.getTotalBuildingTime() * (1 - (((double) researchDiscount) / 100)));
			
			timeLabel.setText(formatTime(buildingTime));
		} else
			timeLabel.setText("0s");
	}
	
	private String formatTime(int totalBuildingTime)
	{
		int time = totalBuildingTime;
		StringBuffer s = new StringBuffer();
		
		int seconds = time % 60;
		int minutes = (time / 60) % 60;
		int hours = (time / 3600) % 24;
		int days = (time / 86400);
		
		if (days > 0)
		{
			s.append(days);
			s.append("d ");
		}
		if (days > 0 || hours > 0)
		{
			s.append(hours);
			s.append("h ");
		}
		if (days > 0 || hours > 0 || minutes > 0)
		{
			s.append(minutes);
			s.append("m ");
		}
		s.append(seconds);
		s.append('s');
		
		return s.toString();
	}
	
	private String getBuildingResources(BuildingResource buildingResource)
	{
		if (castle != null)
		{
			return String.valueOf(castle.getTotalResource(buildingResource));
		} else
			return "0";
	}
}