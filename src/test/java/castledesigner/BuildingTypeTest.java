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

import java.awt.Dimension;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author David Green
 */
public class BuildingTypeTest extends TestCase
{
	public BuildingTypeTest(String testName)
	{
		super(testName);
	}

	public static Test suite()
	{
		TestSuite suite = new TestSuite(BuildingTypeTest.class);
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
	 * Test of values method, of class BuildingType.
	 */
	public void testValues()
	{
		System.out.println("values");

		for (BuildingType buildingType : BuildingType.values())
		{
			assertNotNull(buildingType);
		}
	}

	/**
	 * Test of valueOf method, of class BuildingType.
	 */
	public void testValueOf()
	{
		System.out.println("valueOf");

		assertEquals(BuildingType.BALLISTA_TOWER, BuildingType.valueOf("BALLISTA_TOWER"));
		assertEquals(BuildingType.STONE_GATEHOUSE, BuildingType.valueOf("STONE_GATEHOUSE"));
		assertEquals(BuildingType.WOODEN_WALL, BuildingType.valueOf("WOODEN_WALL"));
	}

	/**
	 * Test of getHotspot method, of class BuildingType.
	 */
	public void testGetHotspot()
	{
		System.out.println("getHotspot");

		assertEquals(0, BuildingType.MOAT.getHotspot()[0]);
		assertEquals(0, BuildingType.MOAT.getHotspot()[1]);
		assertEquals(2, BuildingType.MOAT.getHotspot().length);

		assertEquals(0, BuildingType.STONE_WALL.getHotspot()[0]);
		assertEquals(0, BuildingType.STONE_WALL.getHotspot()[0]);

		assertEquals(1, BuildingType.BALLISTA_TOWER.getHotspot()[0]);
		assertEquals(1, BuildingType.BALLISTA_TOWER.getHotspot()[0]);

		assertEquals(1, BuildingType.SMELTER.getHotspot()[0]);
		assertEquals(1, BuildingType.SMELTER.getHotspot()[0]);

		assertEquals(0, BuildingType.LOOKOUT_TOWER.getHotspot()[0]);
		assertEquals(0, BuildingType.LOOKOUT_TOWER.getHotspot()[0]);
		
		assertEquals(1, BuildingType.SMALL_TOWER.getHotspot()[0]);
		assertEquals(1, BuildingType.SMALL_TOWER.getHotspot()[0]);

		assertEquals(1, BuildingType.LARGE_TOWER.getHotspot()[0]);
		assertEquals(1, BuildingType.LARGE_TOWER.getHotspot()[0]);

		assertEquals(2, BuildingType.GREAT_TOWER.getHotspot()[0]);
		assertEquals(2, BuildingType.GREAT_TOWER.getHotspot()[0]);
	}

	/**
	 * Test of getDimension method, of class BuildingType.
	 */
	public void testGetDimension()
	{
		System.out.println("getDimension");

		assertEquals(new Dimension(1, 1), BuildingType.MOAT.getDimension());
		assertEquals(new Dimension(1, 1), BuildingType.STONE_WALL.getDimension());
		assertEquals(new Dimension(3, 3), BuildingType.BALLISTA_TOWER.getDimension());
		assertEquals(new Dimension(4, 4), BuildingType.SMELTER.getDimension());
		assertEquals(new Dimension(2, 2), BuildingType.LOOKOUT_TOWER.getDimension());
		assertEquals(new Dimension(3, 3), BuildingType.SMALL_TOWER.getDimension());
		assertEquals(new Dimension(4, 4), BuildingType.LARGE_TOWER.getDimension());
		assertEquals(new Dimension(5, 5), BuildingType.GREAT_TOWER.getDimension());
	}

	/**
	 * Test of isGapRequired method, of class BuildingType.
	 */
	public void testIsGapRequired()
	{
		System.out.println("isGapRequired");

		assertTrue(BuildingType.LOOKOUT_TOWER.isGapRequired());
		assertTrue(BuildingType.STONE_GATEHOUSE.isGapRequired());

		assertFalse(BuildingType.STONE_WALL.isGapRequired());
		assertFalse(BuildingType.GUARD_HOUSE.isGapRequired());
		assertFalse(BuildingType.BALLISTA_TOWER.isGapRequired());
	}

	/**
	 * Test of getImage method, of class BuildingType.
	 */
	public void testGetImage()
	{
		System.out.println("getImage");

		for (BuildingType buildingType : BuildingType.values())
		{
			assertNotNull(buildingType.getImage());
		}
	}

	/**
	 * Test of getValidOverlay method, of class BuildingType.
	 */
	public void testGetValidOverlay()
	{
		System.out.println("getValidOverlay");

		for (BuildingType buildingType : BuildingType.values())
		{
			assertNotNull(buildingType.getValidOverlay());
		}
	}

	/**
	 * Test of getInvalidOverlay method, of class BuildingType.
	 */
	public void testGetInvalidOverlay()
	{
		System.out.println("getInvalidOverlay");

		for (BuildingType buildingType : BuildingType.values())
		{
			assertNotNull(buildingType.getInvalidOverlay());
		}
	}

	/**
	 * Test of toString method, of class BuildingType.
	 */
	public void testToString()
	{
		System.out.println("toString");
		
		for (BuildingType buildingType : BuildingType.values())
		{
			assertNotNull(buildingType.toString());
		}
	}
}