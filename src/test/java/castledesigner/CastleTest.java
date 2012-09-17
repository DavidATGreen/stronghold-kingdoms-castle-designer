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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author David Green
 */
public class CastleTest extends TestCase
{
	public CastleTest(String testName)
	{
		super(testName);
	}

	public static Test suite()
	{
		TestSuite suite = new TestSuite(CastleTest.class);
		return suite;
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	/**
	 * Test of getGridData method, of class Castle.
	 */
	public void testGetGridData()
	{
		System.out.println("getGridData");

		Castle instance = new Castle();

		for (int i=0; i<Castle.CASTLE_BOUNDRY_LENGTH; i++)
		{
			for (int j=0; j<Castle.CASTLE_BOUNDRY_LENGTH; j++)
			{
				//Tests for no exception
				instance.getGridData(i, j);
			}
		}

		try
		{
			instance.getGridData(-1, -1);
			fail("Exception expected");
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
		}

		try
		{
			instance.getGridData(Castle.CASTLE_BOUNDRY_LENGTH, 1);
			fail("Exception expected");
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
		}
	}

	/**
	 * Test of removeBuilding method, of class Castle.
	 */
	public void testRemoveBuilding()
	{
		System.out.println("removeBuilding");

		Castle instance = new Castle();

		String importString;
		try
		{
			importString = LayoutUtils.getImportString("typical");
			instance.importData(importString);

			Set<Integer> ids = new HashSet<Integer>();
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
		}
		catch (IOException ex)
		{
			Logger.getLogger(CastleTest.class.getName()).log(Level.SEVERE, null, ex);
		}
		catch (InvalidBarcodeException ex)
		{
			Logger.getLogger(CastleTest.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		try
		{
			instance.removeBuilding(null);
			fail("Exception expected");
		}
		catch (IllegalArgumentException e)
		{
		}
	}

	/**
	 * Test of resetGridData method, of class Castle.
	 */
	public void testResetGridData()
	{
		System.out.println("resetGridData");

		Castle instance = new Castle();
		
		String clearExportString = instance.getGridDataExport();
		System.out.println("clear = " + clearExportString);

		try
		{
			for (String importString : LayoutUtils.getImportStrings())
			{
				instance.importData(importString);

				instance.resetGridData();

		System.out.println("a = " + instance.getGridDataExport());
				assertEquals(clearExportString, instance.getGridDataExport());
			}
		}
		catch (InvalidBarcodeException ex)
		{
			Logger.getLogger(CastleTest.class.getName()).log(Level.SEVERE, null, ex);
			fail("Error reading resources layout");
		}
		catch (FileNotFoundException ex)
		{
			Logger.getLogger(CastleTest.class.getName()).log(Level.SEVERE, null, ex);
			fail("Error reading resources layout");
		}
		catch (IOException ex)
		{
			Logger.getLogger(CastleTest.class.getName()).log(Level.SEVERE, null, ex);
			fail("Error reading resources layout");
		}
	}

	/**
	 * Test of getGridDataExport method, of class Castle.
	 */
	public void testGetGridDataExport()
	{
		System.out.println("getGridDataExport");

		Castle instance = new Castle();
		
		try
		{
			for (String importString : LayoutUtils.getImportStrings())
			{
				instance.importData(importString);
				String exportString = instance.getGridDataExport();

				instance.resetGridData();
				instance.importData(exportString);

				/*
				 * This test shows that what was exported can
				 * be imported to give the same export result.
				 */
				assertEquals(exportString, instance.getGridDataExport());
			}
		}
		catch (InvalidBarcodeException ex)
		{
			Logger.getLogger(CastleTest.class.getName()).log(Level.SEVERE, null, ex);
			fail("Error reading resources layout");
		}
		catch (FileNotFoundException ex)
		{
			Logger.getLogger(CastleTest.class.getName()).log(Level.SEVERE, null, ex);
			fail("Error reading resources layout");
		}
		catch (IOException ex)
		{
			Logger.getLogger(CastleTest.class.getName()).log(Level.SEVERE, null, ex);
			fail("Error reading resources layout");
		}

		boolean suitableTestFound = false;
		try
		{
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
		}
		catch (IOException ex)
		{
			Logger.getLogger(CastleTest.class.getName()).log(Level.SEVERE, null, ex);
			fail("Error reading resources layout");
		}
		catch (InvalidBarcodeException ex)
		{
			Logger.getLogger(CastleTest.class.getName()).log(Level.SEVERE, null, ex);
			fail("Error reading resources layout");
		}

		if (suitableTestFound == false) fail("No designs found that use the new export version ID");
	}


	/**
	 * Test of importData method, of class Castle.
	 */
	public void testImportData()
	{
		System.out.println("importData");

		Castle instance = new Castle();
		/**
		 * It's difficult to test this without messing around with
		 * reflection. I'll just accept a limited test for now.
		 */
		try
		{
			for (String importString : LayoutUtils.getImportStrings())
			{
				instance.importData(importString);

				if (Character.getNumericValue(importString.charAt(0)) == Castle.exportVersionId)
				{
					assertEquals(importString, instance.getGridDataExport());
				}
			}
		}
		catch (IOException ex)
		{
			Logger.getLogger(CastleTest.class.getName()).log(Level.SEVERE, null, ex);
			fail("Error reading resources layout");
		}
		catch (InvalidBarcodeException ex)
		{
			Logger.getLogger(CastleTest.class.getName()).log(Level.SEVERE, null, ex);
			fail("Error reading resources layout");
		}
	}

	/**
	 * Test of addBuilding method, of class Castle.
	 */
	public void testAddBuilding()
	{
		System.out.println("addBuilding");

		Castle instance = new Castle();

		for (BuildingType buildingType : BuildingType.values())
		{
			Set<Point> points = new HashSet<Point>();

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

			Set<Point> lonelyPoint = new HashSet<Point>();

			Point q1 = new Point(30, 20);

			lonelyPoint.add(q1);

			instance.addBuilding(lonelyPoint, buildingType);
			TileBuilding tileBuilding = instance.getGridData(q1.x, q1.y);
			assertEquals(buildingType, tileBuilding.getBuildingType());
		}
	}

	/**
	 * Test of getDesignErrors method, of class Castle.
	 */
	public void testGetDesignErrors()
	{
		System.out.println("getDesignErrors");

		Castle instance = new Castle();
		try
		{
			String importString = LayoutUtils.getImportString("waterworld");
			instance.importData(importString);
			assertEquals(1, instance.getDesignErrors().size());

			importString = LayoutUtils.getImportString("typical");
			instance.importData(importString);
			assertTrue(instance.getDesignErrors().isEmpty());
		}
		catch (IOException ex)
		{
			Logger.getLogger(CastleTest.class.getName()).log(Level.SEVERE, null, ex);
			fail("Error reading resources layout");
		}
		catch (InvalidBarcodeException ex)
		{
			Logger.getLogger(CastleTest.class.getName()).log(Level.SEVERE, null, ex);
			fail("Error reading resources layout");
		}
	}

	/**
	 * Test of getNumberOfBuildings method, of class Castle.
	 */
	public void testGetNumberOfBuildings()
	{
		System.out.println("getNumberOfBuildings");

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

	/**
	 * Test of getMaximumNumberOfBuildings method, of class Castle.
	 */
	public void testGetMaximumNumberOfBuildings()
	{
		System.out.println("getMaximumNumberOfBuildings");

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
}
