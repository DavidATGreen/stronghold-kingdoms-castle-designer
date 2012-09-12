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

/**
 * Adds or reads a bar code from a lossless image.
 *
 * @author David Green
 */
public class Barcode
{
	/*
	 * Shifted by 4 just to make the bar code stand out a bit more.
	 * Otherwise it'd just look mostly black.
	 */
	private static final int SEPARATOR_CODE = 254 << 4;
	private static final int END_CODE = 255 << 4;
	private static final int OPAQUE_MASK = 0xff000000;

	//Must be > border width
	private static final int startingY = 2;
	//Must be > border width
	private static final int startingX = 50;
	private static final int endingY = 15;

	private static final int MINIMUM_WIDTH = startingX + 600;
	private static final int MINIMUM_HEIGHT = endingY + 1;

	private static final int border = 0xff660066;

	/**
	 * Embeds the barcode string into the image. The barcode appears
	 * near the top of the image as mostly black/blue with a purple border.
	 *
	 * @param bufferedImage the original image
	 * @param barcode the string to be embedded into the image
	 */
	public static void embedBarcode(BufferedImage bufferedImage, String barcode)
	{
		if (bufferedImage == null) throw new IllegalArgumentException("Null image");
		if (bufferedImage.getWidth() < MINIMUM_WIDTH || bufferedImage.getHeight() <= MINIMUM_HEIGHT)
		{
			throw new IllegalArgumentException("Invalid image size");
		}

		int x = startingX;
		int y = startingY;
		for (char c : barcode.toCharArray())
		{
			int code;

			if (c == 'Z') code = SEPARATOR_CODE;
			else
			{
				/*
				 * Yes I know we're going in circles a bit here, converting into alphanumeric
				 * and now back again to a number. It's not ideal, but I didn't forsee
				 * this in the design, oops!
				 */
				code = Converter.alphaNumericToInt(c) << 4;
			}
			bufferedImage.setRGB(x, y, OPAQUE_MASK | code);

			y++;
			if (y > endingY)
			{
				y = startingY;
				x++;
			}
		}
		bufferedImage.setRGB(x, y, OPAQUE_MASK | END_CODE);

		//Fill in the final column to make it pretty
		for (int j=y+1; j<=endingY; j++) bufferedImage.setRGB(x, j, 0xff000000);

		addBorder(bufferedImage, x);
	}

	private static void addBorder(BufferedImage bufferedImage, int endX)
	{
		for (int x=startingX-1; x<=endX+1; x++)
		{
			bufferedImage.setRGB(x, startingY-1, border);
			bufferedImage.setRGB(x, endingY+1, border);
		}
		for (int y=startingY; y<=endingY; y++)
		{
			bufferedImage.setRGB(startingX-1, y, border);
			bufferedImage.setRGB(endX+1, y, border);
		}
	}

	/**
	 * Extracts a barcode string from an image previously barcoded by
	 * the embedBarcode method.
	 *
	 * @param bufferedImage the barcoded image
	 * @return the barcode data string
	 */
	public static String extractBarcode(BufferedImage bufferedImage) throws InvalidBarcodeException
	{
		if (bufferedImage.getWidth() < MINIMUM_WIDTH || bufferedImage.getHeight() < MINIMUM_HEIGHT)
		{
			throw new InvalidBarcodeException("Incorrect image size");
		}

		try
		{
			StringBuffer s = new StringBuffer();

			int x = startingX;
			int y = startingY;
			int rgb = bufferedImage.getRGB(x, y);

			//Some codes spill into the green bits because we're shifting by 4, so we
			//need to mask with 0x00000fff
			while ((rgb & 0xfff) != END_CODE)
			{
				s.append(decodeRGB(rgb));
				y++;
				if (y > endingY)
				{
					y = startingY;
					x++;
				}
				rgb = bufferedImage.getRGB(x, y);
			}
			return s.toString();
		}
		catch (IllegalArgumentException e)
		{
			throw new InvalidBarcodeException("Bad data");
		}
	}

	private static char decodeRGB(int rgb)
	{
		int code = rgb & 0xfff;
		
		if (code == SEPARATOR_CODE) return 'Z';
		else return Converter.intToAlphaNumeric(code >> 4);
	}
}