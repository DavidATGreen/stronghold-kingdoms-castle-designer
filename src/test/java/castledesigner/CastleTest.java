/*
 * Copyright (c) 2012-2016 David Green
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
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author David Green
 */
public class CastleTest
{
	@Test
	public void testGetGridDataWithinBoundries()
	{
		Castle instance = new Castle();

		for (int i=0; i<Castle.CASTLE_BOUNDRY_LENGTH; i++)
		{
			for (int j=0; j<Castle.CASTLE_BOUNDRY_LENGTH; j++)
			{
				//Tests for no exception
				instance.getGridData(i, j);
			}
		}
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testGetGridDataWithNegativeCoordinates()
	{
		Castle instance = new Castle();
		instance.getGridData(-1, -1);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testGetGridDataWithInvalidCoordinates()
	{
		Castle instance = new Castle();
		instance.getGridData(Castle.CASTLE_BOUNDRY_LENGTH, 1);
	}

	@Test
	public void testRemoveBuilding() throws Exception
	{
		Castle instance = new Castle();

		String importString = LayoutUtils.getImportString("typical");
		instance.importData(importString);

		Set<Integer> ids = new HashSet<>();
		for (int i=0; i<Castle.CASTLE_BOUNDRY_LENGTH; i++)
		{
			for (int j=0; j<Castle.CASTLE_BOUNDRY_LENGTH; j++)
			{
				TileBuilding tileBuilding = instance.getGridData(i, j);
				if (tileBuilding != null)
				{
					int id = tileBuilding.getBuildingId();

					assertFalse(ids.contains(id));

					instance.removeBuilding(tileBuilding);
					ids.add(id);
				}
			}
		}
		
		for (int i=0; i<Castle.CASTLE_BOUNDRY_LENGTH; i++)
		{
			for (int j=0; j<Castle.CASTLE_BOUNDRY_LENGTH; j++)
			{
				TileBuilding tileBuilding = instance.getGridData(i, j);
				assertNull(tileBuilding); //All buildings should have been removed
			}
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveBuildingNull() throws Exception
	{
		Castle instance = new Castle();

		instance.removeBuilding(null);
	}

	@Test
	public void testResetGridData() throws Exception
	{
		Castle instance = new Castle();
		
		String clearExportString = instance.getGridDataExport();

		for (String importString : LayoutUtils.getImportStrings())
		{
			instance.importData(importString);

			instance.resetGridData();

			assertEquals(clearExportString.substring(1), instance.getGridDataExport().substring(1));
		}
	}

	@Test
	public void testGetGridDataExport() throws Exception
	{
		Castle instance = new Castle();
		
		for (String importString : LayoutUtils.getImportStrings())
		{
			instance.importData(importString);
			String exportString = instance.getGridDataExport();

			instance.resetGridData();
			instance.importData(exportString);

			System.out.println("importString = " + importString);
			System.out.println("exportString = " + exportString);
			System.out.println("newExportString = " + instance.getGridDataExport());
			/*
			 * This test shows that what was exported can
			 * be imported to give the same export result.
			 */
			assertEquals(exportString.substring(1), instance.getGridDataExport().substring(1));
		}
	}

	@Test
	public void testGetGridDataExportTestUpToDate() throws Exception
	{
		Castle instance = new Castle();

		boolean suitableTestFound = false;
		for (String importString : LayoutUtils.getImportStrings())
		{
			if (Character.getNumericValue(importString.charAt(0)) == Castle.exportVersionId)
			{
				suitableTestFound = true;
				
				instance.resetGridData();
				instance.importData(importString);

				assertEquals(importString, instance.getGridDataExport());
			}
		}

		if (suitableTestFound == false) fail("No designs found that use the new export version ID");
	}

	@Test
	public void testImportData() throws Exception
	{
		Castle instance = new Castle();
		/**
		 * It's difficult to test this without messing around with
		 * reflection. I'll just accept a limited test for now.
		 */
		for (String importString : LayoutUtils.getImportStrings())
		{
			instance.importData(importString);

			if (Character.getNumericValue(importString.charAt(0)) == Castle.exportVersionId)
			{
				assertEquals(importString.substring(1), instance.getGridDataExport().substring(1));
			}
		}
	}

	@Test
	public void testAddBuilding2x2()
	{
		Castle instance = new Castle();

		for (BuildingType buildingType : BuildingType.values())
		{
			Set<Point> points = new HashSet<>();

			Point p1 = new Point(0, 0);
			Point p2 = new Point(1, 0);
			Point p3 = new Point(1, 1);
			Point p4 = new Point(0, 1);

			points.add(p1);
			points.add(p2);
			points.add(p3);
			points.add(p4);

			instance.addBuilding(points, buildingType);

			for (Point point : points)
			{
				TileBuilding tileBuilding = instance.getGridData(point.x, point.y);
				assertEquals(buildingType, tileBuilding.getBuildingType());
			}
		}
	}
	
	@Test
	public void testAddBuilding1x1()
	{
		Castle instance = new Castle();

		for (BuildingType buildingType : BuildingType.values())
		{
			Set<Point> lonelyPoint = new HashSet<>();

			Point q1 = new Point(30, 20);

			lonelyPoint.add(q1);

			instance.addBuilding(lonelyPoint, buildingType);
			TileBuilding tileBuilding = instance.getGridData(q1.x, q1.y);
			assertEquals(buildingType, tileBuilding.getBuildingType());
		}
	}

	@Test
	public void testGetDesignErrorsWaterworld() throws Exception
	{
		Castle instance = new Castle();

		String importString = LayoutUtils.getImportString("waterworld");
		instance.importData(importString);
		assertEquals(1, instance.getDesignErrors().size());
	}

	@Test
	public void testGetDesignErrorsTypical() throws Exception
	{
		Castle instance = new Castle();

		String importString = LayoutUtils.getImportString("typical");
		instance.importData(importString);
		assertTrue(instance.getDesignErrors().isEmpty());
	}

	@Test
	public void testGetNumberOfBuildings()
	{
		Castle instance = new Castle();

		for (BuildingType buildingType : BuildingType.values())
		{
			/*
			 * Hard to test when there's no direct setter.
			 */
			int numberOfBuildings = instance.getNumberOfBuildings(buildingType);

			assertTrue(numberOfBuildings >= 0);
		}
	}

	@Test
	public void testGetMaximumNumberOfBuildings()
	{
		Castle instance = new Castle();

		for (BuildingType buildingType : BuildingType.values())
		{
			/*
			 * Hard to test when there's no setter.
			 * Let's just confirm there's no exceptions.
			 */
			instance.getMaximumNumberOfBuildings(buildingType);
		}
	}

	@Test
	public void testGetTotalResource()
	{
		Castle instance = new Castle();

		for (BuildingResource buildingResource : BuildingResource.values())
		{
			/*
			 * Hard to test when there's no direct setter.
			 */
			int amount = instance.getTotalResource(buildingResource);

			assertTrue(amount >= 0);
		}
	}

	@Test
	public void testGetTotalBuildingTime()
	{
		Castle instance = new Castle();

		/*
		 * Hard to test when there's no direct setter.
		 */
		assertEquals(0, instance.getTotalBuildingTime());
	}
}