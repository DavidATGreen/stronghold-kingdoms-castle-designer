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

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author David Green
 */
public class LandGridTest
{
	private boolean changed = false;

	@Test
	public void testAddDesignListener() throws Exception
	{
		changed = false;
		DesignListener designListener = () -> changed = true;
		LandGrid instance = new LandGrid();
		instance.addDesignListener(designListener);

		//Nothing changed yet
		assertFalse(changed);
		
		File layoutFile = LayoutUtils.getTextFiles()[0];
		try (BufferedReader in = new BufferedReader(new FileReader(layoutFile)))
		{
			String data = in.readLine();
			instance.importData(data);

			//The data changed, so we'd expect designChanged to be called
			assertTrue(changed);

			changed = false;
			
			instance.clearData();

			//The data changed, so we'd expect designChanged to be called again
			assertTrue(changed);
		}
	}

	@Test
	public void testRemoveDesignListener() throws Exception
	{
		changed = false;
		DesignListener designListener = () -> changed = true;
		LandGrid instance = new LandGrid();

		//This should do nothing (i.e. no exception)
		instance.removeDesignListener(designListener);

		instance.addDesignListener(designListener);
		instance.removeDesignListener(designListener);

		//Nothing changed yet
		assertFalse(changed);
		
		File layoutFile = LayoutUtils.getTextFiles()[0];

		try (BufferedReader in = new BufferedReader(new FileReader(layoutFile)))
		{
			String data = in.readLine();
			instance.importData(data);

			//Although the data has changed, we removed the listener
			assertFalse(changed);

			instance.clearData();

			//Although the data has changed, we removed the listener
			assertFalse(changed);
		}
	}

	@Test
	public void testSetSelectedBuilding()
	{
		LandGrid instance = new LandGrid();
		instance.setSelectedBuilding(BuildingType.BALLISTA_TOWER);
		assertEquals(BuildingType.BALLISTA_TOWER, instance.getSelectedBuilding());
		
		instance.setSelectedBuilding(BuildingType.STONE_WALL);
		assertEquals(BuildingType.STONE_WALL, instance.getSelectedBuilding());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetSelectedBuildingNull()
	{
		LandGrid instance = new LandGrid();
		instance.setSelectedBuilding(null);
	}

	@Test
	public void testGetSelectedBuilding()
	{
		LandGrid instance = new LandGrid();
		//Not sure what it will return, but so long as it doesn't throw an exception it's all good
		instance.getSelectedBuilding();

		instance.setSelectedBuilding(BuildingType.BALLISTA_TOWER);
		assertEquals(BuildingType.BALLISTA_TOWER, instance.getSelectedBuilding());
		
		instance.setSelectedBuilding(BuildingType.STONE_WALL);
		assertEquals(BuildingType.STONE_WALL, instance.getSelectedBuilding());
	}

	@Test
	public void testPaintComponent()
	{
		LandGrid instance = new LandGrid();
		BufferedImage bufferedImage = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
		instance.paintComponent(bufferedImage.createGraphics());

		/*
		 * This method is unsuitable for testing, other than checking
		 * there's no exception.
		 */
	}

	@Test
	public void testClearData() throws Exception
	{
		LandGrid instance = new LandGrid();

		DesignListener designListener = () -> changed = true;

		instance.addDesignListener(designListener);
		
		for (String importString : LayoutUtils.getImportStrings())
		{
			instance.importData(importString);
			changed = false;
			instance.clearData();

			/*
			 * We only need to test that a design listener
			 * is triggered. The actual reset of data is
			 * tested in the CastleTest class.
			 */
			assertTrue(changed);
		}
	}

	@Test
	public void testImportData() throws Exception
	{
		LandGrid instance = new LandGrid();

		DesignListener designListener = () -> changed = true;

		instance.addDesignListener(designListener);

		for (String importString : LayoutUtils.getImportStrings())
		{
			changed = false;
			instance.importData(importString);

			/*
			 * We only need to test that a design listener
			 * is triggered. The actual import is tested
			 * in the CastleTest class.
			 */
			assertTrue(changed);
		}
	}

	@Test
	public void testGetCastle()
	{
		LandGrid instance = new LandGrid();
		
		assertNotNull(instance.getCastle());
	}
}