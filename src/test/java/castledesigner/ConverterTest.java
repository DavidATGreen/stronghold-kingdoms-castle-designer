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

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author David Green
 */
public class ConverterTest
{
	@Test
	public void testIntToAlphaNumeric()
	{
		assertEquals('0', Converter.intToAlphaNumeric(0));
		assertEquals('a', Converter.intToAlphaNumeric(10));
		assertEquals('f', Converter.intToAlphaNumeric(15));
		assertEquals('Y', Converter.intToAlphaNumeric(60));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIntToAlphaNumericNegative()
	{
		Converter.intToAlphaNumeric(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIntToAlphaNumericOutOfBounds()
	{
		Converter.intToAlphaNumeric(61);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIntToAlphaNumericMaxInteger()
	{
		Converter.intToAlphaNumeric(Integer.MAX_VALUE);
	}

	@Test
	public void testAlphaNumericToInt()
	{
		assertEquals(0, Converter.alphaNumericToInt('0'));
		assertEquals(10, Converter.alphaNumericToInt('a'));
		assertEquals(15, Converter.alphaNumericToInt('f'));
		assertEquals(60, Converter.alphaNumericToInt('Y'));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAlphaNumericToIntSpace()
	{
		Converter.alphaNumericToInt(' ');
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAlphaNumericToIntZ()
	{
		Converter.alphaNumericToInt('Z');
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAlphaNumericToIntPlus()
	{
		Converter.alphaNumericToInt('+');
	}
}