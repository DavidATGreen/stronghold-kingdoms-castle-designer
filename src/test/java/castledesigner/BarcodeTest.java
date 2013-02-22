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
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author David Green
 */
public class BarcodeTest extends TestCase
{
	public BarcodeTest(String testName)
	{
		super(testName);
	}

	public static Test suite()
	{
		TestSuite suite = new TestSuite(BarcodeTest.class);
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
	 * Test of extractBarcode method, of class Barcode.
	 * Each png file should have its twin text file containing the export
	 * string. The result of extractBarcode should match the contents of
	 * this plain text file.
	 */
	public void testExtractBarcode() throws Exception
	{
		System.out.println("extractBarcode");

		for (File file : LayoutUtils.getImageFiles())
		{
			BufferedImage bufferedImage = ImageIO.read(file);
			System.out.println("Testing image " + file.getName());
			String result = Barcode.extractBarcode(bufferedImage);

			String alphanumericTextFile = file.getAbsolutePath().replaceAll(".png$", "");
			
			BufferedReader in = null;
			try
			{
				in = new BufferedReader(new FileReader(alphanumericTextFile));
				String answer = in.readLine();
				
				assertEquals(answer, result);
			}
			finally
			{
				if (in != null) in.close();
			}
		}
	}

	/**
	 * Test of embedBarcode method, of class Barcode.
	 */
	public void testEmbedBarcode()
	{
		System.out.println("embedBarcode");

		BufferedImage bufferedImage = new BufferedImage(650, 17, BufferedImage.TYPE_INT_ARGB);
		embedBarcodeTo(bufferedImage);
		bufferedImage = new BufferedImage(650, 17, BufferedImage.TYPE_INT_BGR);
		embedBarcodeTo(bufferedImage);
		bufferedImage = new BufferedImage(1650, 2000, BufferedImage.TYPE_INT_ARGB);
		embedBarcodeTo(bufferedImage);

		try
		{
			bufferedImage = new BufferedImage(650, 16, BufferedImage.TYPE_INT_ARGB);
			embedBarcodeTo(bufferedImage);
			fail("Expected IllegalArgumentException (16 height)");
		}
		catch (IllegalArgumentException e) { }

		try
		{
			bufferedImage = new BufferedImage(649, 17, BufferedImage.TYPE_INT_ARGB);
			embedBarcodeTo(bufferedImage);
			fail("Expected IllegalArgumentException (649 width)");
		}
		catch (IllegalArgumentException e) { }

		try
		{
			embedBarcodeTo(null);
			fail("Expected IllegalArgumentException (null)");
		}
		catch (IllegalArgumentException e) { }
	}

	private void embedBarcodeTo(BufferedImage bufferedImage)
	{
		for (File file : LayoutUtils.getTextFiles())
		{
			BufferedReader in = null;
			try
			{
				in = new BufferedReader(new FileReader(file));

				String data = in.readLine();
				Barcode.embedBarcode(bufferedImage, data);

				String result = Barcode.extractBarcode(bufferedImage);
			
				assertEquals(data, result);
			}
			catch (IOException e)
			{
				fail("Bad test design: " + e);
			}
			catch (InvalidBarcodeException e)
			{
				fail("Design " + file.getName() + " threw an InvalidBarcodeException when trying to read it back in");
			}
			finally
			{
				try
				{
					if (in != null) in.close();
				}
				catch (IOException ex)
				{
					Logger.getLogger(BarcodeTest.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
	}
}