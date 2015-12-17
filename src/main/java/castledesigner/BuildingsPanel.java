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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
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
	public static final String SELECTED_BUILDING = "SelectedBuilding";
	private static final Integer[] constructionBonuses = new Integer[] {0, 10, 15, 20, 25, 30, 35, 40, 45, 50, 60};
	private final ButtonGroup buildingButtonGroup = new ButtonGroup();
	private final ButtonGroup worldAgeButtonGroup = new ButtonGroup();
	private static final Dimension buttonDimension = new Dimension(50, 50);
	private JLabel moatsLabel;
	private JLabel ballistaTowersLabel;
	private JLabel turretsLabel;
	private JLabel bombardsLabel;
	private JLabel guardHousesLabel;

	private JLabel stoneLabel;
	private JLabel woodLabel;
	private JLabel ironLabel;
	private JLabel goldLabel;
	private JLabel timeLabel;

	private JToggleButton age1Button;
	private JToggleButton age2Button;
	private JToggleButton age3Button;
	private JToggleButton age4Button;
	private JToggleButton age5Button;

	private JSlider constructionSpeed;

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
		JToggleButton smelterButton = createButton("<html>Smelter</html>", BuildingType.SMELTER);
		JToggleButton moatButton = createButton("<html>Moat</html>", BuildingType.MOAT);
		JToggleButton killingPitButton = createButton("<html>Killing Pit</html>", BuildingType.KILLING_PIT);
		JToggleButton bombardButton = createButton("<html>Bombard</html>", BuildingType.BOMBARD);

		this.setMaximumSize(new Dimension(300, 700));
		this.setMinimumSize(new Dimension(300, 700));
		this.setPreferredSize(new Dimension(300, 700));

		setLayout(new BorderLayout());

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

		JPanel quantitiesPanel = createQuantitiesAndAgePanel();
		infoPanel.add(BorderLayout.NORTH, quantitiesPanel);
		
		JPanel resourcesPanel = createResourcesPanel();
		infoPanel.add(BorderLayout.CENTER, resourcesPanel);

		JPanel timePanel = createTimePanel();
		infoPanel.add(BorderLayout.SOUTH, timePanel);

		add(infoPanel, BorderLayout.SOUTH);

		setSelected(stoneWallButton, buildingButtonGroup);

		this.repaint();
	}

	private JPanel createQuantitiesAndAgePanel()
	{
		JPanel quantitiesAndAgePanel = new JPanel(new BorderLayout());

		JPanel quantitiesPanel = createQuantitiesPanel();
		JPanel worldAgePanel = createWorldAgePanel();
		
		quantitiesAndAgePanel.add(BorderLayout.CENTER, quantitiesPanel);
		quantitiesAndAgePanel.add(BorderLayout.EAST, worldAgePanel);

		return quantitiesAndAgePanel;
	}

	private JPanel createQuantitiesPanel()
	{
		JPanel quantitiesPanel = new JPanel(new BorderLayout());
		quantitiesPanel.setBorder(new TitledBorder("Number of Buildings"));
		
		JPanel titlePanel = new JPanel(new GridLayout(5, 1));
		JPanel numberOfBuildingsPanel = new JPanel(new GridLayout(5, 1));

		JLabel moatsTitleLabel = new JLabel("Moats");
		moatsLabel = new JLabel("-/-");
		JLabel ballistaTowersTitleLabel = new JLabel("Ballista Towers");
		ballistaTowersLabel = new JLabel("-/-");
		JLabel turretsTitleLabel = new JLabel("Turrets");
		turretsLabel = new JLabel("-/-");
		JLabel guardHousesTitleLabel = new JLabel("Guard Houses");
		guardHousesLabel = new JLabel("-/-");
		JLabel bombardsTitleLabel = new JLabel("Bombards");
		bombardsLabel = new JLabel("-/-");

		titlePanel.add(guardHousesTitleLabel);
		numberOfBuildingsPanel.add(guardHousesLabel);
		titlePanel.add(ballistaTowersTitleLabel);
		numberOfBuildingsPanel.add(ballistaTowersLabel);
		titlePanel.add(turretsTitleLabel);
		numberOfBuildingsPanel.add(turretsLabel);
		titlePanel.add(bombardsTitleLabel);
		numberOfBuildingsPanel.add(bombardsLabel);
		titlePanel.add(moatsTitleLabel);
		numberOfBuildingsPanel.add(moatsLabel);

		quantitiesPanel.add(titlePanel, BorderLayout.WEST);
		quantitiesPanel.add(numberOfBuildingsPanel, BorderLayout.EAST);

		return quantitiesPanel;
	}

	private JPanel createWorldAgePanel()
	{
		JPanel worldAgePanel = new JPanel(new GridLayout(2, 4));
		worldAgePanel.setBorder(new TitledBorder("World Age"));

		age1Button = createWorldAgeButton(1);
		age2Button = createWorldAgeButton(2);
		age3Button = createWorldAgeButton(3);
		age4Button = createWorldAgeButton(4);
		age5Button = createWorldAgeButton(5);

		worldAgePanel.add(age1Button);
		worldAgePanel.add(age2Button);
		worldAgePanel.add(age3Button);
		worldAgePanel.add(age4Button);
		worldAgePanel.add(age5Button);

		setSelected(age1Button, worldAgeButtonGroup);
		
		return worldAgePanel;
	}
	
	private JToggleButton getWorldAgeButton(int worldAge)
	{
		switch (worldAge)
		{
			case 1: return age1Button;
			case 2: return age2Button;
			case 3: return age3Button;
			case 4: return age4Button;
			case 5: return age5Button;
			default: return null;
		}
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

	private JToggleButton createButton(String buttonText, final BuildingType buildingType)
	{
		final JToggleButton button;
		if (buildingType.getImage() != null) button = new JToggleButton(buttonText, new ImageIcon(buildingType.getImage()));
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
				setSelected(button, buildingButtonGroup);
			}
		});
		buildingButtonGroup.add(button);
		return button;
	}

	private JToggleButton createWorldAgeButton(final int worldAge)
	{
		String urlPath = "/world_ages/age" + String.valueOf(worldAge) + ".png";
		URL url = getClass().getResource(urlPath.toLowerCase());

		Image image = null;
		if (url != null)
		{
			try
			{
				image = ImageIO.read(url);
			} catch (IOException ex)
			{
				Logger.getLogger(BuildingsPanel.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		final JToggleButton button;

		if (image != null) button = new JToggleButton(new ImageIcon(image));
		else button = new JToggleButton(String.valueOf(worldAge));

		Dimension dim = new Dimension(42, 38);

		button.setPreferredSize(dim);
		button.setMinimumSize(dim);
		button.setSize(dim);

		button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				castle.setWorldAge(worldAge);//Recalculates number of allowed buildings and sets up any error strings
				designChanged();//Updates the numbers visible in the panel, and adds a border around the world age button
			}
		});
		worldAgeButtonGroup.add(button);

		return button;
	}

	private void setSelected(JToggleButton button, ButtonGroup group)
	{
		Enumeration<AbstractButton> elements = group.getElements();
		while (elements.hasMoreElements())
		{
			AbstractButton b = elements.nextElement();
			b.setBackground(Color.WHITE);
			b.setOpaque(false);
		}
		button.setBackground(Color.BLUE);
		button.setOpaque(true);
	}

	public void setCastle(Castle castle)
	{
		this.castle = castle;
		updateBuildingQuantities();
	}

	public void designChanged()
	{
		if (castle != null)
		{
			JToggleButton worldAgeButton = getWorldAgeButton(castle.getWorldAge());
			if (worldAgeButton != null)
			{
				setSelected(worldAgeButton, worldAgeButtonGroup);
			}
		}
				
		updateBuildingQuantities();
		updateResources();
		updateTotalBuildingTime();
	}

	private void updateBuildingQuantities()
	{
		moatsLabel.setText(getBuildingQuantities(BuildingType.MOAT));
		ballistaTowersLabel.setText(getBuildingQuantities(BuildingType.BALLISTA_TOWER));
		turretsLabel.setText(getBuildingQuantities(BuildingType.TURRET));
		guardHousesLabel.setText(getBuildingQuantities(BuildingType.GUARD_HOUSE));
		bombardsLabel.setText(getBuildingQuantities(BuildingType.BOMBARD));
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
		if (exceeded | maximum) s.append("<html>");

		if (exceeded) s.append("<font color=\"red\"><b>");
		else if (maximum) s.append("<font color=\"#008800\"/>");
		s.append(numberOfBuildings);
		if (exceeded) s.append("</b></font>");
		else if (maximum) s.append("</font>");
		s.append('/');
		s.append(maxNumberOfBuildings);
		if (exceeded | maximum) s.append("</html>");

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
			int buildingTime = (int)(castle.getTotalBuildingTime() * (1 - (((double)researchDiscount) / 100)));
			
			timeLabel.setText(formatTime(buildingTime));
		}
		else timeLabel.setText("0s");
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
		}
		else return "0";
	}
}