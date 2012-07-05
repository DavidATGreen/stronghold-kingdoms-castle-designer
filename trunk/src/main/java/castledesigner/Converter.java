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

/**
 * Compresses a numeric string into an alphanumeric string (case sensitive).
 * Our coordinate system is 52x52 tiles, so fits nicely into the alphanumeric
 * range.
 * 
 * Better compression could have been achieved using more ASCII characters,
 * but I wanted to keep it basic to avoid any illegal characters on forums.
 *
 * @author David Green
 */
public class Converter
{
	public static final char seperator = 'Z';

	/**
	 * Encodes an integer between 0 and 60 inclusive into an alphanumeric
	 * character.
	 *
	 * @param integer the integer to be encoded (0-60 inclusive)
	 * @return an alphanumeric character
	 */
	public static char intToAlphaNumeric(int integer)
	{
		if (integer < 0 || integer > 60) throw new RuntimeException("Invalid number");

		if (integer < 10) return (char)(integer + '0');
		else if (integer < 36) return (char)(integer - 10 + 'a');
		else return (char)(integer - 36 + 'A');
	}

	/**
	 * Converts an alphanumeric character into an integer between 0 and 60
	 * inclusive.
	 *
	 * @param alphaNumeric the character to be decoded
	 * @return the decoded integer
	 */
	public static int alphaNumericToInt(char alphaNumeric)
	{
		if (alphaNumeric >= 'A' && alphaNumeric < 'Z') return alphaNumeric - 'A' + 36;
		else if (alphaNumeric >= 'a' && alphaNumeric <= 'z') return alphaNumeric - 'a' + 10;
		else if (alphaNumeric >= '0' && alphaNumeric <= '9') return alphaNumeric - '0';
		else throw new RuntimeException("Unexpected character in import");
	}
}