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

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author David Green
 */
public class Castle
{
	public static final int	CASTLE_BOUNDRY_LENGTH	= 52;
	public static final int	exportVersionId			= 3;
	
	private Map<BuildingType, Integer>		buildingQuantities	= new HashMap<BuildingType, Integer>();
	private Map<BuildingType, Integer>		maxBuildings;
	private Map<BuildingResource, Integer>	buildingResources	= new HashMap<BuildingResource, Integer>();
	private int								totalBuildingTime	= 0;
	private List<String>					designErrors		= new ArrayList<String>();
	private TileBuilding[][]				gridData			= new TileBuilding[CASTLE_BOUNDRY_LENGTH][CASTLE_BOUNDRY_LENGTH];
	private int								age					= 1;
	private static int						lastIdUsed			= 0;
	
	private BuildingsPanel buildingsPanelRef = null;
	
	public Castle()
	{
		maxBuildings = new HashMap<BuildingType, Integer>();
		setMaxBuildings();
		
		// Call reset here to set the Keep in the centre
		resetGridData();
	}
	
	public TileBuilding getGridData(int x, int y)
	{
		return gridData[x][y];
	}
	
	public void setMaxBuildings()
	{
		maxBuildings.put(BuildingType.MOAT, 500);
		maxBuildings.put(BuildingType.BALLISTA_TOWER, getMaximumBuildingsPerAge(BuildingType.BALLISTA_TOWER));
		maxBuildings.put(BuildingType.TURRET, getMaximumBuildingsPerAge(BuildingType.TURRET));
		maxBuildings.put(BuildingType.BOMBARD, getMaximumBuildingsPerAge(BuildingType.BOMBARD));
		maxBuildings.put(BuildingType.GUARD_HOUSE, 38);
	}
	
	private int getMaximumBuildingsPerAge(BuildingType b)
	{
		switch (age)
		{
			// age one and two allow the same amount of ballistas / turrets
			case 1:
			case 2:
				switch (b)
				{
					case BOMBARD:
						return 0;
					case BALLISTA_TOWER:
					case TURRET:
						return 10;
					default:
						break;
				}
				
				// from age 3 and on, there are more turrets and ballistas possible
			case 3:
				switch (b)
				{
					case BALLISTA_TOWER:
					case TURRET:
						return 15;
					default:
						break;
				}
			case 4:
				switch (b)
				{
					case BOMBARD:
						return 3;
					case BALLISTA_TOWER:
					case TURRET:
						return 20;
					default:
						break;
				}
			case 5:
				switch (b)
				{
					case BOMBARD:
						return 5;
					case BALLISTA_TOWER:
					case TURRET:
						return 20;
					default:
						break;
				}
		}
		
		// building type with no restriction, should never be reached
		return 0;
	}
	
	public void removeBuilding(TileBuilding building)
	{
		if (building == null)
			throw new IllegalArgumentException();
			
		int id = building.getBuildingId();
		
		for (int i = 0; i < CASTLE_BOUNDRY_LENGTH; i++)
		{
			for (int j = 0; j < CASTLE_BOUNDRY_LENGTH; j++)
			{
				if (gridData[i][j] != null && gridData[i][j].getBuildingId() == id)
				{
					gridData[i][j] = null;
				}
			}
		}
		updateDesignStats();
	}
	
	public void resetGridData()
	{
		for (int i = 0; i < gridData.length; i++)
		{
			for (int j = 0; j < gridData[i].length; j++)
			{
				gridData[i][j] = null;
			}
		}
		
		for (int i = 22; i < 22 + BuildingType.KEEP.getDimension().getWidth(); i++)
		{
			for (int j = 22; j < 22 + BuildingType.KEEP.getDimension().getHeight(); j++)
			{
				gridData[i][j] = new TileBuilding(BuildingType.KEEP, 0);
			}
		}
		updateDesignStats();
	}
	
	/**
	 * Returns a string full of lovely data representing what buildings were placed where.
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
		
		for (int i = 0; i < gridData.length; i++)
		{
			for (int j = 0; j < gridData[i].length; j++)
			{
				TileBuilding building = gridData[i][j];
				
				if (building != null)
				{
					if (building.getBuildingType() == BuildingType.WOODEN_WALL)
					{
						woodenWalls.append(Converter.intToAlphaNumeric(i));
						woodenWalls.append(Converter.intToAlphaNumeric(j));
					} else if (building.getBuildingType() == BuildingType.STONE_WALL)
					{
						stoneWalls.append(Converter.intToAlphaNumeric(i));
						stoneWalls.append(Converter.intToAlphaNumeric(j));
					} else if (building.getBuildingType() == BuildingType.MOAT)
					{
						moats.append(Converter.intToAlphaNumeric(i));
						moats.append(Converter.intToAlphaNumeric(j));
					} else if (building.getBuildingType() == BuildingType.KILLING_PIT)
					{
						killingPits.append(Converter.intToAlphaNumeric(i));
						killingPits.append(Converter.intToAlphaNumeric(j));
					} else
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
		exportStringBuffer.append(age);
		
		return exportStringBuffer.append(woodenWalls).append(Converter.seperator).append(stoneWalls).append(Converter.seperator).append(structures)
				.append(Converter.seperator).append(moats).append(Converter.seperator).append(killingPits).toString();
	}
	
	private int getNewId()
	{
		return ++lastIdUsed;
	}
	
	public void importData(String text) throws UnsupportedVersionException
	{
		int version = Character.getNumericValue(text.charAt(0));
		
		String data;
		// read out age information from newer versions
		if (version > 2)
		{
			age = Character.getNumericValue(text.charAt(1));
			data = text.substring(2);
		} else
		{
			// but don't fail, if we import an old image
			data = text.substring(1);
		}
		
		resetGridData();
		
		if (data == null)
			return;
		String[] dataStrings = data.split(String.valueOf(Converter.seperator));
		
		if (dataStrings.length > 0 && dataStrings[0] != null)
			importSingleTiles(BuildingType.WOODEN_WALL, dataStrings[0]);
		if (dataStrings.length > 1 && dataStrings[1] != null)
			importSingleTiles(BuildingType.STONE_WALL, dataStrings[1]);
			
		if (dataStrings.length > 2 && dataStrings[2] != null)
		{
			int i = 0;
			while (i < dataStrings[2].length())
			{
				int ordinal = Converter.alphaNumericToInt(dataStrings[2].charAt(i));
				int x = Converter.alphaNumericToInt(dataStrings[2].charAt(i + 1));
				int y = Converter.alphaNumericToInt(dataStrings[2].charAt(i + 2));
				
				BuildingType buildingType = BuildingType.values()[ordinal];
				int id = getNewId();
				
				for (int k = x; k < x + buildingType.getDimension().getWidth(); k++)
				{
					for (int l = y; l < y + buildingType.getDimension().getHeight(); l++)
					{
						gridData[k][l] = new TileBuilding(buildingType, id);
					}
				}
				
				i += 3;
			}
		}
		if (dataStrings.length > 3 && dataStrings[3] != null)
			importSingleTiles(BuildingType.MOAT, dataStrings[3]);
		if (dataStrings.length > 4 && dataStrings[4] != null)
			importSingleTiles(BuildingType.KILLING_PIT, dataStrings[4]);
		updateDesignStats();
		
		buildingsPanelRef.updateAgeSelection(age);
		
		if (version > 3)
			throw new UnsupportedVersionException(version);
	}
	
	private void importSingleTiles(BuildingType buildingType, String dataString)
	{
		int i = 0;
		while (i < dataString.length())
		{
			int x = Converter.alphaNumericToInt(dataString.charAt(i));
			int y = Converter.alphaNumericToInt(dataString.charAt(i + 1));
			
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
	
	public void updateDesignStats()
	{
		designErrors.clear();
		totalBuildingTime = 0;
		
		for (BuildingResource buildingResource : BuildingResource.values())
		{
			buildingResources.put(buildingResource, 0);
		}
		
		int[] buildingCounts = new int[BuildingType.values().length];
		
		for (int i = 0; i < gridData.length; i++)
		{
			for (int j = 0; j < gridData[i].length; j++)
			{
				TileBuilding building = gridData[i][j];
				if (building != null)
					buildingCounts[building.getBuildingType().ordinal()]++;
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
		
		for (BuildingType buildingType : maxBuildings.keySet())
		{
			String designError = validateNumberOfBuildings(buildingType, buildingCounts[buildingType.ordinal()], maxBuildings.get(buildingType));
			
			if (designError != null)
				designErrors.add(designError);
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
		} else
			return null;
	}
	
	public int getMaximumNumberOfBuildings(BuildingType buildingType)
	{
		Integer max = maxBuildings.get(buildingType);
		if (max == null)
			return 0;
		else
			return max;
	}
	
	public void setAge(int value)
	{
		this.age = value;
	}
	
	public void setBuildingsPanel(BuildingsPanel b)
	{
		buildingsPanelRef = b;
	}
}