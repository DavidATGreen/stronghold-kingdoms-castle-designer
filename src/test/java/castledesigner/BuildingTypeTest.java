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

import java.awt.Dimension;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author David Green
 */
public class BuildingTypeTest
{
	@Test
	public void testValues()
	{
		for (BuildingType buildingType : BuildingType.values())
		{
			assertNotNull(buildingType);
		}
	}

	@Test
	public void testGetHotspot()
	{
		for (BuildingType buildingType : BuildingType.values())
		{
			int[] hotspot = buildingType.getHotspot();
			assertNotNull(hotspot);
			assertEquals(2, hotspot.length);
		}
	}

	@Test
	public void testGetDimension()
	{
		for (BuildingType buildingType : BuildingType.values())
		{
			Dimension dim = buildingType.getDimension();
			assertNotNull(buildingType.getDimension());
			assertTrue(dim.width > 0);
			assertTrue(dim.height > 0);
		}
	}

	@Test
	public void testGetImage()
	{
		for (BuildingType buildingType : BuildingType.values())
		{
			assertNotNull(buildingType.getImage());
		}
	}

	@Test
	public void testGetValidOverlay()
	{
		for (BuildingType buildingType : BuildingType.values())
		{
			assertNotNull(buildingType.getValidOverlay());
		}
	}

	@Test
	public void testGetInvalidOverlay()
	{
		for (BuildingType buildingType : BuildingType.values())
		{
			assertNotNull(buildingType.getInvalidOverlay());
		}
	}

	@Test
	public void testToString()
	{
		for (BuildingType buildingType : BuildingType.values())
		{
			assertNotNull(buildingType.toString());
		}
	}

	@Test
	public void testGetColour()
	{
		for (BuildingType buildingType : BuildingType.values())
		{
			assertNotNull(buildingType.getColour());
		}
	}

	@Test
	public void testGetCost()
	{
		for (BuildingType buildingType : BuildingType.values())
		{
			for (BuildingResource buildingResource : BuildingResource.values())
			{
				assertNotNull(buildingType.getCost(buildingResource));
			}
		}
	}

	@Test
	public void testGetBuildTime()
	{
		for (BuildingType buildingType : BuildingType.values())
		{
			assertTrue(buildingType.getBuildTime() >= 0);
		}
	}
}