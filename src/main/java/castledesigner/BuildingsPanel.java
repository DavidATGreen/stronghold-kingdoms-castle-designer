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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

/**
 *
 * @author David Green
 */
public class BuildingsPanel extends JPanel implements DesignListener
{
	public static final String SELECTED_BUILDING = "SelectedBuilding";
	private ButtonGroup buildingButtonGroup = new ButtonGroup();
	private static final Dimension buttonDimension = new Dimension(50, 50);
	private JLabel moatsLabel;
	private JLabel ballistaTowersLabel;
	private JLabel turretsLabel;
	private JLabel guardHousesLabel;
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

		this.setMaximumSize(new Dimension(300, 600));
		this.setMinimumSize(new Dimension(300, 600));
		this.setPreferredSize(new Dimension(300, 600));

		setLayout(new BorderLayout());

		JPanel buttonsPanel = new JPanel(new GridLayout(7, 2));
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
		buttonsPanel.add(moatButton);

		add(buttonsPanel, BorderLayout.CENTER);

		JPanel quantitiesPanel = new JPanel(new GridLayout(4, 2));
		quantitiesPanel.setBorder(new TitledBorder("Number of Buildings"));
		JLabel moatsTitleLabel = new JLabel("Moats");
		moatsLabel = new JLabel("-/-");
		JLabel ballistaTowersTitleLabel = new JLabel("Ballista Towers");
		ballistaTowersLabel = new JLabel("-/-");
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
		quantitiesPanel.add(moatsTitleLabel);
		quantitiesPanel.add(moatsLabel);
		
		add(quantitiesPanel, BorderLayout.SOUTH);

		this.repaint();
	}

	private JToggleButton createButton(String buttonText, final BuildingType buildingType)
	{
		JToggleButton button;
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
	}

	private void updateBuildingQuantities()
	{
		moatsLabel.setText(getBuildingQuantities(BuildingType.MOAT));
		ballistaTowersLabel.setText(getBuildingQuantities(BuildingType.BALLISTA_TOWER));
		turretsLabel.setText(getBuildingQuantities(BuildingType.TURRET));
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
}