/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.blackrook.commons.Common;
import com.blackrook.commons.hash.HashMap;
import com.blackrook.ogl.util.OGLSkin;

/**
 * GUI font for GUIText objects. Basically a texture-coordinate and size lookup for
 * rendering character meshes.  
 * @author Matthew Tropiano
 */
public class OGLGUIFont implements OGLGUIFontType
{
	/** Default font size. */
	public static final float DEFAULT_FONT_SIZE = 10f;
	
	/** Name of the font. */
	protected String name;
	/** skin for this font. */
	protected OGLSkin group;
	/** HashMap for the font map. */
	protected HashMap<Character, FontChar> fontMap; 
	/** Default character to return if the character is not in the map. */
	protected FontChar defaultChar;
	/** Type of directionality to use. */
	protected Directionality directionality;
	/** Font size in units. */
	protected float size;
	
	/**
	 * Creates an OGLGUIFont from a skin.
	 * @param group the skin.
	 */
	public OGLGUIFont(OGLSkin group)
	{
		this("UNNAMED", DEFAULT_FONT_SIZE, group);
	}
	
	/**
	 * Creates an OGLGUIFont from a skin.
	 * @param size the size of this font in units. 
	 * @param group the skin.
	 */
	public OGLGUIFont(float size, OGLSkin group)
	{
		this("UNNAMED", size, group);
	}
	
	/**
	 * Creates an OGLGUIFont from a skin.
	 * @param name the name of the font.
	 * @param size the size of this font in units. 
	 * @param group the skin.
	 */
	public OGLGUIFont(String name, float size, OGLSkin group)
	{
		this.name = name;
		this.size = size;
		this.group = group;
		fontMap = new HashMap<Character, FontChar>(100);
		defaultChar = new FontChar('\0', 0, 0, 0, 0, 1, 1, 1, 0, 0);
		directionality = Directionality.LEFT_TO_RIGHT;
	}
	
	@Override
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of this font.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public float getSize()
	{
		return size;
	}

	/**
	 * Sets the size of this font.
	 */
	public void setSize(float size)
	{
		this.size = size;
	}

	@Override
	public OGLSkin getSkin()
	{
		return group;
	}

	/**
	 * Sets the skin to use for this mapping.
	 */
	public void setGroup(OGLSkin group)
	{
		this.group = group;
	}

	/**
	 * Sets the default character. 
	 * @param s0 the starting texture coordinate, s-axis.
	 * @param t0 the starting texture coordinate, t-axis.
	 * @param s1 the ending texture coordinate, s-axis.
	 * @param t1 the ending texture coordinate, t-axis.
	 * @param width the width ratio scalar for this character. 
	 * The final width of the character is the font size times this scalar. 
	 * @param height the height ratio scalar for this character. 
	 * The final height of the character is the font size times this scalar. 
	 * @param advance the advance ratio scalar for this character, or how much to advance the cursor after this is placed. 
	 * The advance of the character is the font size times this scalar. 
	 * @param xofs the x-offset amount scalar to adjust the cursor for painting this character.
	 * The amount of adjustment is the font size times this scalar. 
	 * @param yofs the y-offset amount scalar to adjust the cursor for painting this character.
	 * The amount of adjustment is the font size times this scalar. 
	 */
	public void setDefaultChar(float s0, float t0, float s1, float t1, float width, float height, float advance, float xofs, float yofs)
	{
		defaultChar.s0 = s0;
		defaultChar.t0 = t0;
		defaultChar.s1 = t0;
		defaultChar.t1 = t0;
		defaultChar.width = width;
		defaultChar.height = height;
		defaultChar.advance = advance;
		defaultChar.xofs = xofs;
		defaultChar.yofs = yofs;
	}
	
	/**
	 * Adds a character to the map.
	 * @param c the character to add to the map.
	 * @param s0 the starting texture coordinate, s-axis.
	 * @param t0 the starting texture coordinate, t-axis.
	 * @param s1 the ending texture coordinate, s-axis.
	 * @param t1 the ending texture coordinate, t-axis.
	 * @param width the width ratio scalar for this character. 
	 * The final width of the character is the font size times this scalar. 
	 * @param height the height ratio scalar for this character. 
	 * The final height of the character is the font size times this scalar. 
	 * @param xofs the x-offset amount scalar to adjust the cursor for painting this character.
	 * The amount of adjustment is the font size times this scalar. 
	 * @param yofs the y-offset amount scalar to adjust the cursor for painting this character.
	 * The amount of adjustment is the font size times this scalar. 
	 */
	public void addChar(char c, float s0, float t0, float s1, float t1, 
			float width, float height, float advance, float xofs, float yofs)
	{
		fontMap.put(c, new FontChar(c, s0, t0, s1, t1, width, height, advance, xofs, yofs));
	}
	
	@Override
	public FontChar getChar(char c)
	{
		FontChar out = fontMap.get(c);
		return out != null ? out : defaultChar;
	}
	
	/**
	 * Removes a character from the map.
	 * @param c the character to remove from the map.
	 * @return true if the definition was removed, false otherwise.
	 */
	public boolean removeChar(char c)
	{
		return fontMap.removeUsingKey(c) != null;
	}
	
	@Override
	public Directionality getDirectionality()
	{
		return directionality;
	}

	/**
	 * Sets this font's directionality.
	 */
	public void setDirectionality(Directionality directionality)
	{
		this.directionality = directionality;
	}

	/**
	 * Creates a monospace font that maps the first series of unicode characters
	 * distributed evenly across a single texture, left to right, top to bottom.
	 * <p>The distribution is square-wise, so a number with an integer square root
	 * will be distributed evenly across the texture.
	 * <p> If chars is 9, the codes are:
	 * <table>
	 * <tr><td>0</td><td>1</td><td>2</td></tr>
	 * <tr><td>3</td><td>4</td><td>5</td></tr>
	 * <tr><td>6</td><td>7</td><td>8</td></tr>
	 * </table>
	 * <p> If chars is 15, the codes are:
	 * <table>
	 * <tr><td>0</td><td>1</td><td>2</td><td>3</td></tr>
	 * <tr><td>4</td><td>5</td><td>6</td><td>7</td></tr>
	 * <tr><td>8</td><td>9</td><td>10</td><td>11</td></tr>
	 * <tr><td>12</td><td>13</td><td>14</td><td>&nbsp;</td></tr>
	 * </table>
	 * <p> If chars is 10, the codes are:
	 * <table>
	 * <tr><td>0</td><td>1</td><td>2</td><td>3</td></tr>
	 * <tr><td>4</td><td>5</td><td>6</td><td>7</td></tr>
	 * <tr><td>8</td><td>9</td><td>10</td><td>&nbsp;</td></tr>
	 * <tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
	 * </table>
	 * It's always easier to use a squared value.
	 * 
	 * @param name the name of the font.
	 * @param size the size of the font in units.
	 * @param group the name of the group.
	 * @param chars number of characters.
	 * @return a new font.
	 */
	public static OGLGUIFont makeMonospaceFont(String name, float size, OGLSkin group, int chars)
	{
		return makeMonospaceFont(name, size, group, null, chars);
	}

	/**
	 * Creates a monospace font that maps the first series of unicode characters
	 * from a specified character set distributed evenly across a single texture, 
	 * left to right, top to bottom.
	 * <p>The distribution is square-wise, so a number with an integer square root
	 * will be distributed evenly across the texture.
	 * <p> If chars is 9, the codes are:
	 * <table>
	 * <tr><td>0</td><td>1</td><td>2</td></tr>
	 * <tr><td>3</td><td>4</td><td>5</td></tr>
	 * <tr><td>6</td><td>7</td><td>8</td></tr>
	 * </table>
	 * <p> If chars is 15, the codes are:
	 * <table>
	 * <tr><td>0</td><td>1</td><td>2</td><td>3</td></tr>
	 * <tr><td>4</td><td>5</td><td>6</td><td>7</td></tr>
	 * <tr><td>8</td><td>9</td><td>10</td><td>11</td></tr>
	 * <tr><td>12</td><td>13</td><td>14</td><td>&nbsp;</td></tr>
	 * </table>
	 * <p> If chars is 10, the codes are:
	 * <table>
	 * <tr><td>0</td><td>1</td><td>2</td><td>3</td></tr>
	 * <tr><td>4</td><td>5</td><td>6</td><td>7</td></tr>
	 * <tr><td>8</td><td>9</td><td>10</td><td>&nbsp;</td></tr>
	 * <tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
	 * </table>
	 * It's always easier to use a squared value.
	 * 
	 * @param name the name of the font.
	 * @param size the size of the font in units.
	 * @param group the name of the group.
	 * @param charset the character set to use as a map.
	 * @param chars number of characters.
	 * @return a new font.
	 */
	public static OGLGUIFont makeMonospaceFont(String name, float size, OGLSkin group, Charset charset, int chars)
	{
		OGLGUIFont out = new OGLGUIFont(name, size, group);
		int sq = (int)Math.ceil(Math.sqrt(chars));
		float x_inc = (float)1.0/sq;
		float y_inc = (float)1.0/sq;
		
		char[] chararray = null;
		if (charset != null)
		{
			chararray = new char[chars];
			for (short s = 0; s < chararray.length; s++)
				chararray[s] = (char)s;
			
			CharBuffer cbb = charset.decode(ByteBuffer.wrap(new String(chararray).getBytes()));
			System.arraycopy(cbb.array(), 0, chararray, 0, cbb.capacity());
			Common.noop();
		}
		
		for (char i = 0; i < chars; i++)
		{
			int imod = i%sq;
			int idiv = i/sq;
			out.addChar(
				chararray != null ? chararray[i] : i, 
				x_inc*imod, y_inc*idiv, x_inc*imod+x_inc, y_inc*idiv+y_inc, 
				1, 1, 1, 0, 0
				);
		}
		return out;
	}

	/**
	 * Returns a SAX XML Reader for reading font metadata.
	 */
	public static BMXMLReader getReader() throws SAXException
	{
		return new BMXMLReader();
	}

	/**
	 * Reads in XML-Formatted BM Font (http://www.angelcode.com/products/bmfont/) generated metadata
	 * and creates an OGLGUIFont using the data.
	 * @param file the file that holds the XML data.
	 * @param size the font size in units.
	 * @param group the skin used for this font.
	 * @throws IOException if a read error happens.
	 */
	public static OGLGUIFont readBMFont(File file, float size, OGLSkin group) throws IOException
	{
		FileInputStream fis = new FileInputStream(file);
		OGLGUIFont out = readBMFont(fis, size, group);
		fis.close();
		return out;
	}
	
	/**
	 * Reads in XML-Formatted BM Font (http://www.angelcode.com/products/bmfont/) generated metadata
	 * and creates an OGLGUIFont using the data.
	 * @param in the input stream for the XML data.
	 * @param size the font size in units.
	 * @param group the skin used for this font.
	 * @throws IOException if a read error happens.
	 */
	public static OGLGUIFont readBMFont(InputStream in, float size, OGLSkin group) throws IOException
	{
		OGLGUIFont font = new OGLGUIFont(size, group);
		try {
			BMXMLReader bmxml = getReader();
			bmxml.read(font, in);
		} catch (SAXException ex) {
			throw new IOException(ex);
		}
		return font;
	}

	/**
	 * SAX Reader for BMFont.
	 */
	public static class BMXMLReader
	{
		protected static final String ELEMENT_INFO = "info";
		protected static final String ELEMENT_COMMON = "common";
		protected static final String ELEMENT_CHAR = "char";
		
		XMLReader xmlReader;
		OGLGUIFont font;
		
		BMXMLReader() throws SAXException
		{
			xmlReader = XMLReaderFactory.createXMLReader();
			Handler handler = new Handler();
			xmlReader.setContentHandler(handler);
			xmlReader.setErrorHandler(handler);
		}
		
		public synchronized void read(OGLGUIFont font, InputStream in) throws IOException, SAXException
		{
			InputSource is = new InputSource(in);
			this.font = font;
			xmlReader.parse(is);
			this.font = null;
		}
		
		private class Handler extends DefaultHandler
		{
			float texWidth;
			float texHeight;
			float lineHeight;
			
			public Handler()
			{
				super();
			}
			
			@Override
			public void startDocument() throws SAXException
			{
				font.setDefaultChar(0, 0, 0, 0, 0, 0, 0, 0, 0);
			}

			@Override
			public void endDocument() throws SAXException
			{
			}

			@Override
			public void startElement(String uri, String localName, String qName, Attributes attribs) throws SAXException
			{
				if (localName.equals(ELEMENT_INFO))
					parseInfo(attribs);
				else if (localName.equals(ELEMENT_COMMON))
					parseCommon(attribs);
				else if (localName.equals(ELEMENT_CHAR))
					parseChar(attribs);
			}

			// parses info block.
			private void parseInfo(Attributes attribs) throws SAXException
			{
				for (int i = 0; i < attribs.getLength(); i++)
				{
					String name = attribs.getLocalName(i);
					if (name.equals("face"))
						font.setName(attribs.getValue(i));
					else if (name.equals("size"))
						lineHeight = Float.parseFloat(attribs.getValue(i));
				}
			}
			
			// parses common block.
			private void parseCommon(Attributes attribs) throws SAXException
			{
				for (int i = 0; i < attribs.getLength(); i++)
				{
					String name = attribs.getLocalName(i);
					if (name.equals("scaleW"))
						texWidth = Float.parseFloat(attribs.getValue(i));
					else if (name.equals("scaleH"))
						texHeight = Float.parseFloat(attribs.getValue(i));
				}
			}
			
			// parses common block.
			private void parseChar(Attributes attribs) throws SAXException
			{
				char id = '\0';
				float x = 0f;
				float y = 0f;
				float w = 0f;
				float h = 0f;
				float xofs = 0f;
				float yofs = 0f;
				float adv = 0f;
				
				for (int i = 0; i < attribs.getLength(); i++)
				{
					String name = attribs.getLocalName(i);
					if (name.equals("id"))
						id = (char)Integer.parseInt(attribs.getValue(i));
					else if (name.equals("x"))
						x = Float.parseFloat(attribs.getValue(i));
					else if (name.equals("y"))
						y = Float.parseFloat(attribs.getValue(i));
					else if (name.equals("width"))
						w = Float.parseFloat(attribs.getValue(i));
					else if (name.equals("height"))
						h = Float.parseFloat(attribs.getValue(i));
					else if (name.equals("xadvance"))
						adv = Float.parseFloat(attribs.getValue(i));
					else if (name.equals("xoffset"))
						xofs = Float.parseFloat(attribs.getValue(i));
					else if (name.equals("yoffset"))
						yofs = Float.parseFloat(attribs.getValue(i));
				}
				
				font.addChar(id, 
						x/texWidth, y/texHeight, (x+w)/texWidth, (y+h)/texHeight, 
						w/lineHeight, h/lineHeight, adv/lineHeight, xofs/lineHeight, yofs/lineHeight);
			}

			@Override
			public void endElement(String uri, String localName, String qName) throws SAXException
			{
			}
			
			@Override
			public void error(SAXParseException e) throws SAXException
			{
				throw new SAXException(e);
			}
			
			@Override
			public void fatalError(SAXParseException e) throws SAXException
			{
				throw new SAXException(e);
			}

			@Override
			public void characters(char[] arg0, int arg1, int arg2) throws SAXException
			{
			}
			
		}
	}
}
