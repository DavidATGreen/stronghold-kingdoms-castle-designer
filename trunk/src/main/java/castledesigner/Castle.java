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
	public static final int CASTLE_BOUNDRY_LENGTH = 52;
	public static final int exportVersionId = 1;

	private Map<BuildingType, Integer> buildingQuantities = new HashMap<BuildingType, Integer>();
	private Map<BuildingType, Integer> maxBuildings;
	private List<String> designErrors = new ArrayList<String>();
	private TileBuilding[][] gridData = new TileBuilding[CASTLE_BOUNDRY_LENGTH][CASTLE_BOUNDRY_LENGTH];
	private static int lastIdUsed = 0;

	public Castle()
	{
		maxBuildings = new HashMap<BuildingType, Integer>();
		setMaxBuildings();

		//Call reset here to set the Keep in the centre
		resetGridData();
	}
	
	public TileBuilding getGridData(int x, int y)
	{
		return gridData[x][y];
	}

	private void setMaxBuildings()
	{
		maxBuildings.put(BuildingType.MOAT, 500);
		maxBuildings.put(BuildingType.BALLISTA_TOWER, 10);
		maxBuildings.put(BuildingType.TURRET, 10);
		maxBuildings.put(BuildingType.GUARD_HOUSE, 38);
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

		StringBuilder exportStringBuffer = new StringBuilder();
		exportStringBuffer.append(exportVersionId);
		
		return exportStringBuffer.append(woodenWalls)
					.append(Converter.seperator)
					.append(stoneWalls)
					.append(Converter.seperator)
					.append(structures).toString();
	}

	private int getNewId()
	{
		return ++lastIdUsed;
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
		}

		for (BuildingType buildingType : maxBuildings.keySet())
		{
			String designError = validateNumberOfBuildings(
				buildingType,
				buildingCounts[buildingType.ordinal()],
				maxBuildings.get(buildingType));

			if (designError != null) designErrors.add(designError);
		}
	}

	public int getNumberOfBuildings(BuildingType buildingType)
	{
		return buildingQuantities.get(buildingType);
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
		Integer max = maxBuildings.get(buildingType);
		if (max == null) return 0;
		else return max;
	}
}