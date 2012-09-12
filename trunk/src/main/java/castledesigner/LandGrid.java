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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JPanel;

/**
 *
 * @author David Green
 */
public class LandGrid extends JPanel
{
	public static final int exportVersionId = 1;
	private static final int tileWidth = 14;
	private static final int numRows = 52;
	private int gridOffsetX = 20;
	private int gridOffsetY = 20;
	private TileBuilding[][] gridData = new TileBuilding[numRows][numRows];
	
	private String coordinates = "[0, 0]";
	private BuildingType selectedBuilding = BuildingType.STONE_WALL;

	private static final Color GRASS = new Color(0, 125, 0);
	private static int lastIdUsed = 0;

	private Set<DesignListener> designListeners = new HashSet<DesignListener>();
	private List<String> designErrors = new ArrayList<String>();

	private int button = MouseEvent.NOBUTTON;
	private int[] mouseCoords = {0, 0};

	public LandGrid()
	{
		resetGridData();

		this.addMouseMotionListener(new MouseMotionAdapter()
		{
			@Override
			public void mouseMoved(MouseEvent e) {
				mouseCoords = getCoords(e.getX(), e.getY());
				
				coordinates = "[" + mouseCoords[0] + ", " + mouseCoords[1] + "]";
				LandGrid.this.repaint();
			}
			
			@Override
			public void mouseDragged(MouseEvent e)
			{
				mouseCoords = getCoords(e.getX(), e.getY());
				coordinates = "[" + mouseCoords[0] + ", " + mouseCoords[1] + "]";
				changeGridData(e);
				LandGrid.this.repaint();
			}
		});
		
		this.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				button = e.getButton();
				changeGridData(e);
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				//We need to set this here for mouseDragged. Oddly e.getButton() returns nothing for the mouseDragged's event
				button = e.getButton();
				changeGridData(e);
			}
		});

		this.setMinimumSize(new Dimension(tileWidth*numRows + gridOffsetX, tileWidth*numRows + gridOffsetY));
	}

	public void addDesignListener(DesignListener designListener)
	{
		designListeners.add(designListener);
	}

	public void removeDesignListener(DesignListener designListener)
	{
		designListeners.remove(designListener);
	}
	
	public void setSelectedBuilding(BuildingType building)
	{
		if (building == null) throw new IllegalArgumentException("Invalid argument (null) to setSelectedBuilding");
		this.selectedBuilding = building;
	}

	public BuildingType getSelectedBuilding()
	{
		return selectedBuilding;
	}
	
	private void changeGridData(MouseEvent e)
	{
		int[] coords = getCoords(e.getX(), e.getY());

		//If left click
		if (button == MouseEvent.BUTTON1)
		{
			if (isValidCoords(coords))
			{
				Dimension dimension = selectedBuilding.getDimension();
				int[] hotspot = selectedBuilding.getHotspot();
				int id = getNewId();

				for (int i=coords[0] - hotspot[0]; i<coords[0] - hotspot[0] + dimension.getWidth(); i++)
				{
					for (int j=coords[1] - hotspot[1]; j<coords[1] - hotspot[1] + dimension.getHeight(); j++)
					{
						gridData[i][j] = new TileBuilding(selectedBuilding, id);
					}
				}
				LandGrid.this.repaint();
				updateDesignStats();
			}
		}
		else if (button == MouseEvent.BUTTON3) //else if right click
		{
			if (fitsInGrid(coords, new Dimension(1, 1), new int[] {0, 0}))
			{
				TileBuilding building = gridData[coords[0]][coords[1]];
				if (building != null && building.getBuildingType() != BuildingType.KEEP)
				{
					int id = building.getBuildingId();

					for (int i=0; i<numRows; i++)
					{
						for (int j=0; j<numRows; j++)
						{
							if (gridData[i][j] != null &&
								gridData[i][j].getBuildingId() == id)
							{
								gridData[i][j] = null;
							}
						}
					}
				}
				LandGrid.this.repaint();
				updateDesignStats();
			}
		}
	}

	private int getNewId()
	{
		return ++lastIdUsed;
	}
	
	/**
	 * Checks that these coords are valid for the selected building. Note
	 * that these will typically be the center of the building.
	 *
	 * @param coords
	 * @return 
	 */
	private boolean isValidCoords(int[] coords)
	{
		Dimension dimension = selectedBuilding.getDimension();
		int[] hotspot = selectedBuilding.getHotspot();
		
		if (fitsInGrid(coords, dimension, hotspot) &&
			isBuildable(coords, dimension, hotspot))
		{
			if (selectedBuilding.isGapRequired())
			{
				return isGapSatisfied(coords, dimension, hotspot);
			}
			else return true;
		}
		else return false;
	}
	
	private boolean fitsInGrid(int[] coords, Dimension dimension, int[] hotspot)
	{
		return (coords[0] - hotspot[0] >= 0 &&
			coords[0] - hotspot[0] <= numRows - dimension.getWidth() &&
			coords[1] - hotspot[1] >= 0 &&
			coords[1] - hotspot[1] <= numRows - dimension.getHeight());
	}
	
	private boolean isBuildable(int[] coords, Dimension dimension, int[] hotspot)
	{
		for (int i=coords[0] - hotspot[0]; i<coords[0] - hotspot[0] + dimension.getWidth(); i++)
		{
			for (int j=coords[1] - hotspot[1]; j<coords[1] - hotspot[1] +dimension.getHeight(); j++)
			{
				if (gridData[i][j] != null &&
					gridData[i][j].getBuildingType() != BuildingType.WOODEN_WALL &&
					gridData[i][j].getBuildingType() != BuildingType.STONE_WALL)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean isGapSatisfied(int[] coords, Dimension dimension, int[] hotspot)
	{
		for (int i=coords[0] - hotspot[0] -1; i<coords[0] - hotspot[0] +dimension.getWidth()+1; i++)
		{
			for (int j=coords[1] - hotspot[1] - 1; j<coords[1] - hotspot[1] +dimension.getHeight()+1; j++)
			{
				if (i >= 0 && i < numRows && j >= 0 && j < numRows)
				{
					if (gridData[i][j] != null && gridData[i][j].getBuildingType().isGapRequired()) return false;
				}
			}
		}
		return true;
	}
	
	private int[] getCoords(int ex, int ey)
	{
		return new int[] {(int)Math.floor(((double)(ex - gridOffsetX)) / tileWidth),
			(int)Math.floor(((double)(ey - gridOffsetY)) / tileWidth)};
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		g.drawString(coordinates, 10, 15);
		
		g.translate(gridOffsetX, gridOffsetY);
		
		//Draw any plain colours, e.g. grass & keep 
		for (int i=0; i<numRows; i++)
		{
			for (int j=0; j<numRows; j++)
			{
				if (gridData[i][j] != null && gridData[i][j].getBuildingType().getImage() == null)
				{
					g.setColor(gridData[i][j].getBuildingType().getColour());
				}
				else
				{
					//Fill with grass anyway since we might have transparent images (i.e. turret)
					g.setColor(GRASS);
				}
				
				g.fillRect(i*tileWidth, j*tileWidth, tileWidth, tileWidth);
			}
		}
		
		//Draw the grid
		g.setColor(Color.black);
		for (int i=0; i<numRows+1; i++)
		{
			g.drawLine(i*tileWidth, 0, i*tileWidth, tileWidth*numRows);
			g.drawLine(0, i*tileWidth, tileWidth*numRows, i*tileWidth);
		}

		//Draw the buildings
		Set<Integer> ids = new HashSet<Integer>();
		for (int i=0; i<numRows; i++)
		{
			for (int j=0; j<numRows; j++)
			{
				if (gridData[i][j] != null)
				{
					if ((gridData[i][j].getBuildingType().getImage() != null) &&
						!ids.contains(gridData[i][j].getBuildingId()))
					{
						ids.add(gridData[i][j].getBuildingId());
						g.drawImage(gridData[i][j].getBuildingType().getImage(), i*tileWidth+1, j*tileWidth+1, null);
					}
				}
			}
		}

		//Draw the mouse-overlay
		Image overlay;
		if (isValidCoords(mouseCoords)) overlay = selectedBuilding.getValidOverlay();
		else overlay = selectedBuilding.getInvalidOverlay();

		int[] hotspot = selectedBuilding.getHotspot();

		g.drawImage(overlay, (mouseCoords[0] - hotspot[0])*tileWidth+1, (mouseCoords[1] - hotspot[1])*tileWidth+1, null);
	}

	/**
	 * Clears the data to leave just the Keep and empty land.
	 *
	 */
	public void clearData()
	{
		resetGridData();
		repaint();
	}

	private void resetGridData()
	{
		for (int i=0; i<gridData.length; i++)
		{
			for (int j=0; j<gridData[i].length; j++)
			{
				gridData[i][j] = null;
			}
		}
			
		for (int i=22; i<22 + BuildingType.KEEP.getDimension().getWidth(); i++)
		{
			for (int j=22; j<22 + BuildingType.KEEP.getDimension().getHeight(); j++)
			{
				gridData[i][j] = new TileBuilding(BuildingType.KEEP, 0);
			}
		}

		lastIdUsed = 0;
		updateDesignStats();
	}

	/**
	 * Returns a string full of lovely data representing what buildings
	 * were placed where.
	 * 
	 * @return 
	 */
	public String getGridDataExport()
	{
		StringBuffer woodenWalls = new StringBuffer();
		StringBuffer stoneWalls = new StringBuffer();
		StringBuffer structures = new StringBuffer();

		Set<Integer> ids = new HashSet<Integer>();

		for (int i=0; i<gridData.length; i++)
		{
			for (int j=0; j<gridData[i].length; j++)
			{
				TileBuilding building = gridData[i][j];

				if (building != null)
				{
					if (building.getBuildingType() == BuildingType.WOODEN_WALL)
					{
						woodenWalls.append(Converter.intToAlphaNumeric(i));
						woodenWalls.append(Converter.intToAlphaNumeric(j));
					}
					else if (building.getBuildingType() == BuildingType.STONE_WALL)
					{
						stoneWalls.append(Converter.intToAlphaNumeric(i));
						stoneWalls.append(Converter.intToAlphaNumeric(j));
					}
					else
					{
						if (!ids.contains(building.getBuildingId()))
						{
							ids.add(building.getBuildingId());
							structures.append(Converter.intToAlphaNumeric(building.getBuildingType().ordinal()));
							structures.append(Converter.intToAlphaNumeric(i));
							structures.append(Converter.intToAlphaNumeric(j));
						}
					}
				}
			}
		}

		StringBuffer exportStringBuffer = new StringBuffer();
		exportStringBuffer.append(exportVersionId);
		
		return exportStringBuffer.append(woodenWalls)
					.append(Converter.seperator)
					.append(stoneWalls)
					.append(Converter.seperator)
					.append(structures).toString();
	}

	public void importData(String text)
	{
		int version = text.charAt(0);

		resetGridData();

		String data = text.substring(1);

		if (data == null) return;
		String[] dataStrings = data.split(String.valueOf(Converter.seperator));

		if (dataStrings[0] != null)
		{
			int i=0;
			while (i < dataStrings[0].length())
			{
				int x = Converter.alphaNumericToInt(dataStrings[0].charAt(i));
				int y = Converter.alphaNumericToInt(dataStrings[0].charAt(i+1));
				
				gridData[x][y] = new TileBuilding(BuildingType.WOODEN_WALL, getNewId());

				i += 2;
			}
		}
		
		if (dataStrings[1] != null)
		{
			int i=0;
			while (i < dataStrings[1].length())
			{
				int x = Converter.alphaNumericToInt(dataStrings[1].charAt(i));
				int y = Converter.alphaNumericToInt(dataStrings[1].charAt(i+1));
				
				gridData[x][y] = new TileBuilding(BuildingType.STONE_WALL, getNewId());

				i += 2;
			}
		}
		
		if (dataStrings[2] != null)
		{
			int i=0;
			while (i < dataStrings[2].length())
			{
				int ordinal = Converter.alphaNumericToInt(dataStrings[2].charAt(i));
				int x = Converter.alphaNumericToInt(dataStrings[2].charAt(i+1));
				int y = Converter.alphaNumericToInt(dataStrings[2].charAt(i+2));
				
				BuildingType buildingType = BuildingType.values()[ordinal];
				int id = getNewId();

				for (int k=x; k<x+buildingType.getDimension().getWidth(); k++)
				{
					for (int l=y; l<y+buildingType.getDimension().getHeight(); l++)
					{
						gridData[k][l] = new TileBuilding(buildingType, id);
					}
				}

				i += 3;
			}
		}
		updateDesignStats();
		
		this.repaint();
	}

	public List<String> getDesignErrors()
	{
		return designErrors;
	}

	private void updateDesignStats()
	{
		designErrors.clear();

		int[] buildingCounts = new int[BuildingType.values().length];

		for (int i=0; i<gridData.length; i++)
		{
			for (int j=0; j<gridData[i].length; j++)
			{
				TileBuilding building = gridData[i][j];
				if (building != null) buildingCounts[building.getBuildingType().ordinal()]++;
			}
		}

		String designError = validateNumberOfBuildings(BuildingType.MOAT, buildingCounts[BuildingType.MOAT.ordinal()], 500);
		if (designError != null) designErrors.add(designError);

		designError = validateNumberOfBuildings(BuildingType.BALLISTA_TOWER, buildingCounts[BuildingType.BALLISTA_TOWER.ordinal()], 10);
		if (designError != null) designErrors.add(designError);
		
		designError = validateNumberOfBuildings(BuildingType.TURRET, buildingCounts[BuildingType.TURRET.ordinal()], 10);
		if (designError != null) designErrors.add(designError);
		
		designError = validateNumberOfBuildings(BuildingType.GUARD_HOUSE, buildingCounts[BuildingType.GUARD_HOUSE.ordinal()], 38);
		if (designError != null) designErrors.add(designError);

		for (DesignListener designListener : designListeners)
		{
			designListener.designChanged();
		}
	}

	private String validateNumberOfBuildings(BuildingType buildingType, int numberOfTiles, int maxNumberOfBuildings)
	{
		int numberOfBuildings = numberOfTiles / (buildingType.getDimension().width * buildingType.getDimension().height);

		if (numberOfBuildings > maxNumberOfBuildings)
		{
			return "Error: " + numberOfBuildings + " " + buildingType + "s (" + maxNumberOfBuildings + " max)";
		}
		else return null;
	}
}