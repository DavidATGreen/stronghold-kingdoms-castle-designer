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
import java.awt.Font;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author David Green
 */
public class Castle
{
	public static final int CASTLE_BOUNDRY_LENGTH = 52;
	
	/* The export version id should only be increased as a last resort, otherwise a user is forced to
	 * update their client to load a design.
	 * Futureproofing now gracefully ignores any unknown building types, so there is no longer a need
	 * to increment the export version id when new buildings are added.
	 */
	public static final int exportVersionId = 3;

	private Map<BuildingType, Integer> buildingQuantities = new HashMap<BuildingType, Integer>();
	private final EnumSet<BuildingType> limitedBuildings = EnumSet.of(BuildingType.MOAT,
									BuildingType.BALLISTA_TOWER,
									BuildingType.TURRET,
									BuildingType.GUARD_HOUSE,
									BuildingType.BOMBARD);
	private Map<BuildingResource, Integer> buildingResources = new HashMap<BuildingResource, Integer>();
	private int totalBuildingTime = 0;
	private final List<String> designErrors = new ArrayList<String>();
	private TileBuilding[][] gridData = new TileBuilding[CASTLE_BOUNDRY_LENGTH][CASTLE_BOUNDRY_LENGTH];
	private static int lastIdUsed = 0;
	private int worldAge = 1;

	public Castle()
	{
		//Call reset here to set the Keep in the centre
		resetGridData();
	}

	public int getWorldAge()
	{
		return worldAge;
	}

	public void setWorldAge(int worldAge)
	{
		if (worldAge < 1) throw new RuntimeException("Invalid world age: " + worldAge);

		this.worldAge = worldAge;
		updateDesignStats();
	}
	
	public TileBuilding getGridData(int x, int y)
	{
		return gridData[x][y];
	}

	public void removeBuilding(TileBuilding building)
	{
		if (building == null) throw new IllegalArgumentException();
		
		int id = building.getBuildingId();

		for (int i=0; i<CASTLE_BOUNDRY_LENGTH; i++)
		{
			for (int j=0; j<CASTLE_BOUNDRY_LENGTH; j++)
			{
				if (gridData[i][j] != null &&
					gridData[i][j].getBuildingId() == id)
				{
					gridData[i][j] = null;
				}
			}
		}
		updateDesignStats();
	}

	public void resetGridData()
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
		setWorldAge(1); //This calls updateDesignStats()
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
		StringBuffer moats = new StringBuffer();
		StringBuffer killingPits = new StringBuffer();
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
					else if (building.getBuildingType() == BuildingType.MOAT)
					{
						moats.append(Converter.intToAlphaNumeric(i));
						moats.append(Converter.intToAlphaNumeric(j));
					}
					else if (building.getBuildingType() == BuildingType.KILLING_PIT)
					{
						killingPits.append(Converter.intToAlphaNumeric(i));
						killingPits.append(Converter.intToAlphaNumeric(j));
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

		StringBuilder exportStringBuffer = new StringBuilder();
		exportStringBuffer.append(exportVersionId);
		
		return exportStringBuffer.append(woodenWalls)
					.append(Converter.seperator)
					.append(stoneWalls)
					.append(Converter.seperator)
					.append(structures)
					.append(Converter.seperator)
					.append(moats)
					.append(Converter.seperator)
					.append(killingPits)
					.append(Converter.seperator)
					.append(getWorldAge())
					.toString();
	}

	private int getNewId()
	{
		return ++lastIdUsed;
	}
	
	public void importData(String text) throws UnsupportedVersionException
	{
		int version = Character.getNumericValue(text.charAt(0));

		resetGridData();

		String data = text.substring(1);
	
		if (data == null) return;
		String[] dataStrings = data.split(String.valueOf(Converter.seperator));

		if (dataStrings.length > 0 && dataStrings[0] != null) importSingleTiles(BuildingType.WOODEN_WALL, dataStrings[0]);
		if (dataStrings.length > 1 && dataStrings[1] != null) importSingleTiles(BuildingType.STONE_WALL, dataStrings[1]);
		
		if (dataStrings.length > 2 && dataStrings[2] != null)
		{
			int i=0;
			while (i < dataStrings[2].length())
			{
				int ordinal = Converter.alphaNumericToInt(dataStrings[2].charAt(i));
				int x = Converter.alphaNumericToInt(dataStrings[2].charAt(i+1));
				int y = Converter.alphaNumericToInt(dataStrings[2].charAt(i+2));
				
				try
				{
					BuildingType buildingType = BuildingType.values()[ordinal];
					int id = getNewId();

					for (int k=x; k<x+buildingType.getDimension().getWidth(); k++)
					{
						for (int l=y; l<y+buildingType.getDimension().getHeight(); l++)
						{
							gridData[k][l] = new TileBuilding(buildingType, id);
						}
					}
				}
				catch (ArrayIndexOutOfBoundsException e)
				{
					//The client is trying to load a building that it doesn't yet know about
					//Best thing to do here is to gracefully ignore it!
				}

				i += 3;
			}
		}
		if (dataStrings.length > 3 && dataStrings[3] != null) importSingleTiles(BuildingType.MOAT, dataStrings[3]);
		if (dataStrings.length > 4 && dataStrings[4] != null) importSingleTiles(BuildingType.KILLING_PIT, dataStrings[4]);
		if (version >= 3 && dataStrings.length > 5 && dataStrings[5] != null) setWorldAge(Integer.parseInt(dataStrings[5]));
		else updateDesignStats(); //setWorldAge() calls updateDesignStats()

		if (version > 3) throw new UnsupportedVersionException(version);
	}
	
	private void importSingleTiles(BuildingType buildingType, String dataString)
	{
		int i=0;
		while (i < dataString.length())
		{
			int x = Converter.alphaNumericToInt(dataString.charAt(i));
			int y = Converter.alphaNumericToInt(dataString.charAt(i+1));
			
			gridData[x][y] = new TileBuilding(buildingType, getNewId());

			i += 2;
		}
	}
	
	public void addBuilding(Set<Point> buildingCoords, BuildingType buildingType)
	{
		int id = getNewId();

		for (Point p : buildingCoords)
		{
			gridData[p.x][p.y] = new TileBuilding(buildingType, id);
		}
		updateDesignStats();
	}

	public List<String> getDesignErrors()
	{
		return designErrors;
	}

	private void updateDesignStats()
	{
		designErrors.clear();
		totalBuildingTime = 0;

		for (BuildingResource buildingResource : BuildingResource.values())
		{
			buildingResources.put(buildingResource, 0);
		}

		int[] buildingCounts = new int[BuildingType.values().length];

		for (int i=0; i<gridData.length; i++)
		{
			for (int j=0; j<gridData[i].length; j++)
			{
				TileBuilding building = gridData[i][j];
				if (building != null) buildingCounts[building.getBuildingType().ordinal()]++;
			}
		}

		for (BuildingType buildingType : BuildingType.values())
		{
			int numberOfBuildings = calculateNumberOfBuildings(buildingType, buildingCounts[buildingType.ordinal()]);
			buildingQuantities.put(buildingType, numberOfBuildings);

			for (BuildingResource buildingResource : BuildingResource.values())
			{
				int cumulativeCost = buildingResources.get(buildingResource) + buildingType.getCost(buildingResource) * numberOfBuildings;
				
				buildingResources.put(buildingResource, cumulativeCost);
			}
			totalBuildingTime += buildingType.getBuildTime() * numberOfBuildings;
		}

		for (BuildingType buildingType : limitedBuildings)
		{
			String designError = validateNumberOfBuildings(
				buildingType,
				buildingCounts[buildingType.ordinal()],
				getMaximumNumberOfBuildings(buildingType, worldAge));

			if (designError != null) designErrors.add(designError);
		}
		updateErrorPanel();
	}

	private void updateErrorPanel()
	{
		JPanel errorPanel = Editor.getErrorPanel();
		if (errorPanel != null)
		{
			errorPanel.removeAll();
			for (String designError : designErrors)
			{
				JLabel designErrorLabel = new JLabel(designError);
				designErrorLabel.setForeground(Color.red);
				designErrorLabel.setFont(new Font(designErrorLabel.getFont().getName(),
					Font.BOLD, designErrorLabel.getFont().getSize()));
				errorPanel.add(designErrorLabel);
			}
			errorPanel.revalidate();
		}
	}

	public int getNumberOfBuildings(BuildingType buildingType)
	{
		return buildingQuantities.get(buildingType);
	}

	public int getTotalResource(BuildingResource resource)
	{
		return buildingResources.get(resource);
	}

	public int getTotalBuildingTime()
	{
		return totalBuildingTime;
	}

	private int calculateNumberOfBuildings(BuildingType buildingType, int numberOfTiles)
	{
		return numberOfTiles / (buildingType.getDimension().width * buildingType.getDimension().height);
	}
	
	private String validateNumberOfBuildings(BuildingType buildingType, int numberOfTiles, int maxNumberOfBuildings)
	{
		int numberOfBuildings = calculateNumberOfBuildings(buildingType, numberOfTiles);

		if (numberOfBuildings > maxNumberOfBuildings)
		{
			return "Error: " + numberOfBuildings + " " + buildingType + "s (" + maxNumberOfBuildings + " max)";
		}
		else return null;
	}

	public int getMaximumNumberOfBuildings(BuildingType buildingType)
	{
		return getMaximumNumberOfBuildings(buildingType, worldAge);
	}

	private int getMaximumNumberOfBuildings(BuildingType buildingType, int worldAge)
	{
		switch (buildingType)
		{
			case MOAT: return 500;
			case BALLISTA_TOWER:
			case TURRET:
				if (worldAge < 3) return 10;
				else if (worldAge == 3) return 15;
				else return 20;
			case GUARD_HOUSE:
				return 38;
			case BOMBARD:
				if (worldAge < 4) return 0;
				else if (worldAge == 4) return 3;
				else return 5;
			default: return Integer.MAX_VALUE;
		}
	}
}