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
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import javax.imageio.ImageIO;

/**
 * Utility class to load the layouts in the resource directory.
 *
 * @author David Green
 */
public class LayoutUtils
{
	private static final FileFilter pngFileFilter = new FileFilter()
	{
		public boolean accept(File pathname)
		{
			//Return only the png files
			return pathname.getName().endsWith(".png");
		}
	};

	private static final FileFilter textFileFilter = new FileFilter()
	{
		public boolean accept(File pathname)
		{
			//Return only the plain text files
			return !pathname.getName().endsWith(".png");
		}
	};
	
	public static File[] getImageFiles()
	{
		return getDesignDir().listFiles(pngFileFilter);
	}

	public static File[] getTextFiles()
	{
		return getDesignDir().listFiles(textFileFilter);
	}

	public static File[] getFiles()
	{
		return getDesignDir().listFiles();
	}

	public static Set<String> getImportStrings() throws IOException, InvalidBarcodeException
	{
		Set<String> importStrings = new HashSet<String>();

		for (File file : getFiles())
		{
			importStrings.add(getImportString(file));
		}
		return importStrings;
	}

	private static String getImportString(File file) throws IOException, InvalidBarcodeException
	{
		if (file.getName().endsWith(".png"))
		{
			BufferedImage bufferedImage = ImageIO.read(file);
			return Barcode.extractBarcode(bufferedImage);
		}
		else
		{
			BufferedReader in = null;
			try
			{
				in = new BufferedReader(new FileReader(file));
				return in.readLine();
			}
			finally
			{
				if (in != null) in.close();
			}
		}
	}

	public static String getImportString(String layoutName) throws IOException, InvalidBarcodeException
	{
		File file = new File(getDesignDir().getAbsolutePath().concat('/' + layoutName));
		if (file == null) return null;
		else return getImportString(file);
	}

	private static File getDesignDir()
	{
		String urlPath = "/designs";
		URL url = LayoutUtils.class.getResource(urlPath.toLowerCase());
		return new File(url.getPath());
	}
}