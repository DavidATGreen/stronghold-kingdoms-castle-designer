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

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author David Green
 */
public class LandGridTest extends TestCase
{
	private boolean changed = false;

	public LandGridTest(String testName)
	{
		super(testName);
	}

	public static Test suite()
	{
		TestSuite suite = new TestSuite(LandGridTest.class);
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
	 * Test of addDesignListener method, of class LandGrid.
	 */
	public void testAddDesignListener()
	{
		System.out.println("addDesignListener");

		changed = false;
		DesignListener designListener = new DesignListener()
		{
			public void designChanged()
			{
				changed = true;
			}
		};
		LandGrid instance = new LandGrid();
		instance.addDesignListener(designListener);

		//Nothing changed yet
		assertFalse(changed);
		
		File layoutFile = LayoutUtils.getTextFiles()[0];
		BufferedReader in = null;
		try
		{
			in = new BufferedReader(new FileReader(layoutFile));
			String data = in.readLine();
			instance.importData(data);

			//The data changed, so we'd expect designChanged to be called
			assertTrue(changed);

			changed = false;
			
			instance.clearData();

			//The data changed, so we'd expect designChanged to be called again
			assertTrue(changed);
		}
		catch (FileNotFoundException ex)
		{
			System.err.println("Error reading resources layout");
			Logger.getLogger(LandGridTest.class.getName()).log(Level.SEVERE, null, ex);
			fail("Error reading resources layout");
		}
		catch (IOException ex)
		{
			System.err.println("Error reading resources layout");
			Logger.getLogger(LandGridTest.class.getName()).log(Level.SEVERE, null, ex);
			fail("Error reading resources layout");
		}
		finally
		{
			try
			{
				if (in != null) in.close();
			}
			catch (IOException ex)
			{
				Logger.getLogger(LandGridTest.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	/**
	 * Test of removeDesignListener method, of class LandGrid.
	 */
	public void testRemoveDesignListener()
	{
		System.out.println("removeDesignListener");

		changed = false;
		DesignListener designListener = new DesignListener()
		{
			public void designChanged()
			{
				changed = true;
			}
		};
		LandGrid instance = new LandGrid();

		//This should do nothing (i.e. no exception)
		instance.removeDesignListener(designListener);

		instance.addDesignListener(designListener);
		instance.removeDesignListener(designListener);

		//Nothing changed yet
		assertFalse(changed);
		
		File layoutFile = LayoutUtils.getTextFiles()[0];
		BufferedReader in = null;
		try
		{
			in = new BufferedReader(new FileReader(layoutFile));
			String data = in.readLine();
			instance.importData(data);

			//Although the data has changed, we removed the listener
			assertFalse(changed);

			instance.clearData();

			//Although the data has changed, we removed the listener
			assertFalse(changed);
		}
		catch (FileNotFoundException ex)
		{
			System.err.println("Error reading resources layout");
			Logger.getLogger(LandGridTest.class.getName()).log(Level.SEVERE, null, ex);
			fail("Error reading resources layout");
		}
		catch (IOException ex)
		{
			System.err.println("Error reading resources layout");
			Logger.getLogger(LandGridTest.class.getName()).log(Level.SEVERE, null, ex);
			fail("Error reading resources layout");
		}
		finally
		{
			try
			{
				if (in != null) in.close();
			}
			catch (IOException ex)
			{
				Logger.getLogger(LandGridTest.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	/**
	 * Test of setSelectedBuilding method, of class LandGrid.
	 */
	public void testSetSelectedBuilding()
	{
		System.out.println("setSelectedBuilding");

		LandGrid instance = new LandGrid();
		instance.setSelectedBuilding(BuildingType.BALLISTA_TOWER);
		assertEquals(BuildingType.BALLISTA_TOWER, instance.getSelectedBuilding());
		
		instance.setSelectedBuilding(BuildingType.STONE_WALL);
		assertEquals(BuildingType.STONE_WALL, instance.getSelectedBuilding());

		try
		{
			instance.setSelectedBuilding(null);
			fail("IllegalArgumentException was expected");
		}
		catch (IllegalArgumentException e)
		{
		}
	}

	/**
	 * Test of getSelectedBuilding method, of class LandGrid.
	 */
	public void testGetSelectedBuilding()
	{
		System.out.println("getSelectedBuilding");

		LandGrid instance = new LandGrid();
		//Not sure what it will return, but so long as it doesn't throw an exception it's all good
		instance.getSelectedBuilding();

		instance.setSelectedBuilding(BuildingType.BALLISTA_TOWER);
		assertEquals(BuildingType.BALLISTA_TOWER, instance.getSelectedBuilding());
		
		instance.setSelectedBuilding(BuildingType.STONE_WALL);
		assertEquals(BuildingType.STONE_WALL, instance.getSelectedBuilding());
	}

	/**
	 * Test of paintComponent method, of class LandGrid.
	 */
	public void testPaintComponent()
	{
		System.out.println("paintComponent");

		LandGrid instance = new LandGrid();
		BufferedImage bufferedImage = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
		instance.paintComponent(bufferedImage.createGraphics());

		/*
		 * This method is unsuitable for testing, other than checking
		 * there's no exception.
		 */
	}

	/**
	 * Test of clearData method, of class LandGrid.
	 */
	public void testClearData()
	{
		System.out.println("clearData");

		LandGrid instance = new LandGrid();

		DesignListener designListener = new DesignListener()
		{
			public void designChanged()
			{
				changed = true;
			}
		};
		instance.addDesignListener(designListener);
		
		try
		{
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
		catch (InvalidBarcodeException ex)
		{
			Logger.getLogger(LandGridTest.class.getName()).log(Level.SEVERE, null, ex);
			fail("Error reading resources layout");
		}
		catch (FileNotFoundException ex)
		{
			Logger.getLogger(LandGridTest.class.getName()).log(Level.SEVERE, null, ex);
			fail("Error reading resources layout");
		}
		catch (IOException ex)
		{
			Logger.getLogger(LandGridTest.class.getName()).log(Level.SEVERE, null, ex);
			fail("Error reading resources layout");
		}
	}

	/**
	 * Test of importData method, of class LandGrid.
	 */
	public void testImportData()
	{
		System.out.println("importData");

		LandGrid instance = new LandGrid();

		DesignListener designListener = new DesignListener()
		{
			public void designChanged()
			{
				changed = true;
			}
		};
		instance.addDesignListener(designListener);

		try
		{
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
		catch (IOException ex)
		{
			Logger.getLogger(LandGridTest.class.getName()).log(Level.SEVERE, null, ex);
			fail("Error reading resources layout");
		}
		catch (InvalidBarcodeException ex)
		{
			Logger.getLogger(LandGridTest.class.getName()).log(Level.SEVERE, null, ex);
			fail("Error reading resources layout");
		}
	}

	/**
	 * Test of getCastle method, of class LandGrid.
	 */
	public void testGetCastle()
	{
		System.out.println("getCastle");

		LandGrid instance = new LandGrid();
		
		assertNotNull(instance.getCastle());
	}
}