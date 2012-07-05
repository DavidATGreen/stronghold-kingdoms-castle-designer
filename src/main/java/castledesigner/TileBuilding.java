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

/**
 * This represents a single 1x1 tile that has been built upon.
 * A TileBuilding may share the same buildingId with other TileBuildings
 * to show that those tiles together make up the same building.
 *
 * @author David Green
 */
public class TileBuilding
{
	private int buildingId;
	private final BuildingType buildingType;
	
	public TileBuilding(BuildingType buildingType, int buildingId)
	{
		this.buildingId = buildingId;
		this.buildingType = buildingType;
	}

	public BuildingType getBuildingType()
	{
		return buildingType;
	}

	public int getBuildingId()
	{
		return buildingId;
	}

	public void setBuildingId(int id)
	{
		this.buildingId = id;
	}
}