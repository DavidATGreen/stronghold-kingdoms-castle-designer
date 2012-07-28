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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Enumerations representing the different types of buildings we use.
 *
 * @author David Green
 */
public enum BuildingType
{
	//Wood
	WOODEN_WALL(new Color(150, 75, 0), new Dimension(1, 1), false),
	WOODEN_GATEHOUSE(new Color(100, 50, 0), new Dimension(3, 3), true),
	WOODEN_TOWER(new Color(125, 58, 0), new Dimension(2, 2), false),
	
	//Stone
	STONE_WALL(new Color(230, 230, 230), new Dimension(1, 1), false),
	STONE_GATEHOUSE(new Color(100, 100, 100), new Dimension(3, 3), true),
	LOOKOUT_TOWER(new Color(200, 200, 200), new Dimension(2, 2), true),
	SMALL_TOWER(new Color(200, 200, 200), new Dimension(3, 3), true),
	LARGE_TOWER(new Color(200, 200, 200), new Dimension(4, 4), true),
	GREAT_TOWER(new Color(200, 200, 200), new Dimension(5, 5), true),

	//Misc
	GUARD_HOUSE(new Color(255, 200, 180), new Dimension(3, 3), false),
	BALLISTA_TOWER(new Color(230, 200, 60), new Dimension(3, 3), false),
	TURRET(new Color(0, 0, 80), new Dimension(2, 2), false),
	SMELTER(new Color(200, 30, 30), new Dimension(4, 4), false),
	MOAT(new Color(0, 200, 255), new Dimension(1, 1), false),

	//Keep
	KEEP(new Color(0, 0, 0), new Dimension(8, 8), false);

	private final Color colour;
	private Dimension dimension;
	private boolean gapRequired;
	private BufferedImage image;
	private BufferedImage validOverlay;
	private BufferedImage invalidOverlay;
	
	/**
	 * Constructor
	 * 
	 * @param colour the colour represented on the grid and button
	 * @param dimension the dimension of the building (in tiles)
	 * @param gapRequired true if this building cannot be placed next to
	 *                    other buildings that also have this set to true
	 */
	private BuildingType(Color colour, Dimension dimension, boolean gapRequired)
	{
		this.colour = colour;
		this.dimension = dimension;
		this.gapRequired = gapRequired;

		String urlPath = "/buildings/" + name() + ".png";
		URL url = getClass().getResource(urlPath.toLowerCase());
		
		if (url != null)
		{
			try
			{
				image = ImageIO.read(url);
				validOverlay = createOverlay(true);
				invalidOverlay = createOverlay(false);
			} catch (IOException ex)
			{
				Logger.getLogger(BuildingType.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	private BufferedImage createOverlay(boolean valid)
	{
		BufferedImage overlay = new BufferedImage(image.getWidth(null),image.getHeight(null), BufferedImage.TRANSLUCENT);
		Graphics2D g = overlay.createGraphics();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		g.drawImage(image, 0, 0, null);
		g.dispose();

		if (valid == false)
		{
			for (int i=0; i<overlay.getWidth(); i++)
			{
				for (int j=0; j<overlay.getHeight(); j++)
				{
					int rgb = overlay.getRGB(i, j) & 0xFFFF4444 | 0xFF0000;
					overlay.setRGB(i, j, rgb);
				}
			}
		}
		return overlay;
	}

	public int[] getHotspot()
	{
		return new int[] { (dimension.width - 1) / 2, (dimension.height - 1) / 2 };
	}

	/**
	 * The colour represented by the building
	 * 
	 * @return the colour represented by the building
	 */
	public Color getColour()
	{
		return colour;
	}

	/**
	 * The dimension of the building (in tiles).
	 *
	 * @return the dimension of the building (in tiles)
	 */
	public Dimension getDimension()
	{
		return dimension;
	}

	/**
	 * Returns true if this building cannot be placed next to other
	 * buildings that also have this value set to true.
	 * 
	 * @return true if required, false otherwise
	 */
	public boolean isGapRequired()
	{
		return gapRequired;
	}

	/**
	 * Returns a visual image of this building type. Note that null may be
	 * returned.
	 *
	 * @return An Image representing this building type
	 */
	public Image getImage()
	{
		return image;
	}

	/**
	 * Returns a visual translucent image of this building type. Note that
	 * null may be returned.
	 * 
	 * @return A translucent BufferedImage representing this building type
	 */
	public BufferedImage getValidOverlay()
	{
		return validOverlay;
	}

	/**
	 * Returns a visual translucent red-tint image of this building type.
	 * Note that null may be returned.
	 * 
	 * @return A translucent red-tinted BufferedImage representing this
	 * building type
	 */
	public BufferedImage getInvalidOverlay()
	{
		return invalidOverlay;
	}

	@Override
	public String toString()
	{
		String[] words = name().toLowerCase().split("_");

		StringBuffer name = new StringBuffer();
		for (String word : words)
		{
			if (name.length() > 0) name.append(' ');

			name.append(Character.toUpperCase(word.charAt(0)));
			if (word.length() > 1) name.append(word.substring(1));
		}

		return name.toString();
	}
}