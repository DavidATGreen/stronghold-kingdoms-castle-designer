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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * The tests in this class are very simplistic. This is because the subject
 * class is acting as a container class (which has more rigorous testing).
 *
 * @author David Green
 */
public class LandPanelTest extends TestCase
{
	public LandPanelTest(String testName)
	{
		super(testName);
	}

	public static Test suite()
	{
		TestSuite suite = new TestSuite(LandPanelTest.class);
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
	 * Test of setSelectedBuilding method, of class LandPanel.
	 */
	public void testSetSelectedBuilding()
	{
		System.out.println("setSelectedBuilding");

		LandPanel instance = new LandPanel();
		for (BuildingType buildingType : BuildingType.values())
		{
			instance.setSelectedBuilding(buildingType);
		}
	}

	/**
	 * Test of getGridDataExport method, of class LandPanel.
	 */
	public void testGetGridDataExport()
	{
		System.out.println("getGridDataExport");

		LandPanel instance = new LandPanel();
		instance.getGridDataExport();
	}

	/**
	 * Test of importData method, of class LandPanel.
	 */
	public void testImportData()
	{
		System.out.println("importData");

		LandPanel instance = new LandPanel();
		try
		{
			for (String importString : LayoutUtils.getImportStrings())
			{
				instance.importData(importString);
			}
		}
		catch (IOException ex)
		{
			Logger.getLogger(LandPanelTest.class.getName()).log(Level.SEVERE, null, ex);
		}
		catch (InvalidBarcodeException ex)
		{
			Logger.getLogger(LandPanelTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Test of clearData method, of class LandPanel.
	 */
	public void testClearData()
	{
		System.out.println("clearData");

		LandPanel instance = new LandPanel();
		instance.clearData();
	}

	/**
	 * Test of addDesignListener method, of class LandPanel.
	 */
	public void testAddDesignListener()
	{
		System.out.println("addDesignListener");

		LandPanel instance = new LandPanel();
		instance.addDesignListener(new DesignListener()
		{
			public void designChanged()
			{
			}
		});
	}

	/**
	 * Test of getDesignErrors method, of class LandPanel.
	 */
	public void testGetDesignErrors()
	{
		System.out.println("getDesignErrors");

		LandPanel instance = new LandPanel();
		instance.getDesignErrors();
	}

	/**
	 * Test of getDesignImage method, of class LandPanel.
	 */
	public void testGetDesignImage()
	{
		System.out.println("getDesignImage");
		//Hard to test
	}
}