/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui.object;

import com.blackrook.commons.linkedlist.Queue;
import com.blackrook.commons.math.geometry.Rectangle2F;
import com.blackrook.ogl.enums.GeometryType;
import com.blackrook.ogl.gui.OGLGUI;
import com.blackrook.ogl.gui.OGLGUIFont;
import com.blackrook.ogl.gui.OGLGUIFontType;
import com.blackrook.ogl.gui.OGLGUIObject;
import com.blackrook.ogl.gui.OGLGUITheme;
import com.blackrook.ogl.gui.OGLGUIFontType.Directionality;
import com.blackrook.ogl.gui.OGLGUIFontType.FontChar;
import com.blackrook.ogl.mesh.MeshView;
import com.blackrook.ogl.mesh.PolygonMesh;
import com.blackrook.ogl.util.OGLSkin;

/**
 * GUI Object that holds and displays text data.
 * @author Matthew Tropiano
 */
public class OGLGUILabel extends OGLGUIObject
{
	// No polygons.
	private static final PolygonMesh NO_POLYS = new PolygonMesh(GeometryType.QUADS, 0, 0);
	
	/**
	 * List of font text alignment/justification types.
	 */
	public static enum Justification
	{
		/** Text will be left-aligned inside the object bounds. */
		LEFT,
		/** Text will be right-aligned inside the object bounds. */
		RIGHT,
		/** Text will be centered inside the object bounds. */
		CENTER,
		/** Font's default justification will be used, depending on its directionality. */
		DEFAULT;
	}
	
	/**
	 * List of font text vertical alignment types.
	 */
	public static enum Alignment
	{
		/** Text will be aligned to the top of the object. */
		TOP,
		/** Text will be aligned to the middle of the object. */
		MIDDLE,
		/** Text will be aligned to the bottom of the object. */
		BOTTOM;
	}
	
	/**
	 * List of auto-resizing methods.
	 */
	public static enum ResizeMode
	{
		/** Y-coordinate bound does not change (shrink down/expand up). */
		PIN_Y,
		/** Y-coordinate bound changes (shrink up/expand down). */
		CHANGE_Y
	}
	
	/** The font definition to use. */
	private OGLGUIFontType font;
	
	/** The text to use for this object. */
	private String text;
	
	/** The current theme key. */
	private String themeKey;

	/** Use word wrapping. */
	private boolean wordWrap; 
	/** Type of type justification to use. */
	private Justification justification; 
	/** Type of type vertical alignment to use. */
	private Alignment alignment;

	/** Resizing mode to use when text is changed (can be null). */
	private ResizeMode resizeMode;
	/** Maximum width of the resize. */
	private float maxWidth;

	/** Starting line number. */
	private int startingLine;
	
	//==== RESULTANT TEXT DATA ====================

	/** The broken-up text data for generating the polygonal data. */
	private MeshContext textData;
	/** The polygon data to render the text. */
	private PolygonMesh textMesh;
	/** The polygon data to render the text. */
	private MeshView textMeshView;
	
	/**
	 * Creates a new GUI Text object.
	 */
	public OGLGUILabel()
	{
		this(null, "", Justification.DEFAULT, Alignment.TOP);
	}
	
	/**
	 * Creates a new GUI Text object.
	 */
	public OGLGUILabel(String text)
	{
		this(null, text, Justification.DEFAULT, Alignment.TOP);
	}
	
	/**
	 * Creates a new GUI Text object.
	 */
	public OGLGUILabel(OGLGUIFont font)
	{
		this(font, "", Justification.DEFAULT, Alignment.TOP);
	}
	
	/**
	 * Creates a new GUI Text object.
	 */
	public OGLGUILabel(OGLGUIFont font, String text)
	{
		this(font, text, Justification.DEFAULT, Alignment.TOP);
	}
	
	/**
	 * Creates a new GUI Text object.
	 */
	public OGLGUILabel(OGLGUIFont font, String text, Justification justification)
	{
		this(font, text, justification, Alignment.TOP);
	}
	
	/**
	 * Creates a new GUI Text object.
	 */
	public OGLGUILabel(OGLGUIFont font, String text, Justification justification, Alignment alignment)
	{
		this.justification = justification;
		this.alignment = alignment;
		this.font = font;
		setText(text);
	}
	
	/**
	 * Sets the object bounds.
	 * @param x			its position x.
	 * @param y			its position y.
	 * @param width		its width.
	 * @param height	its height.
	 */
	public void setBounds(float x, float y, float width, float height)
	{
		super.setBounds(x, y, width, height);
		refreshMesh();
	}

	/**
	 * Changes this object's width/height by an x or y-coordinate amount.
	 */
	public void stretch(float width, float height)
	{
		super.stretch(width, height);
		refreshMesh();
	}

	/**
	 * Returns this object's skin, which is taken from the selected font.
	 * If no font is set on this, this will take the one from the theme,
	 * if this also has a theme key set on it.
	 */
	@Override
	public OGLSkin getSkin()
	{
		OGLGUIFontType font = getFont();
		return font != null ? font.getSkin() : null;
	}
	
	@Override
	public void setTheme(OGLGUITheme theme)
	{
		super.setTheme(theme);
		refreshMesh();
	}

	/**
	 * Gets the font for this text object.
	 * If no font is set on this object, it will attempt to get it from the theme,
	 * if the appropriate theme key is set.
	 */
	public OGLGUIFontType getFont()
	{
		if (font != null)
			return font;
		
		String key = getThemeKey();
		if (key != null)
		{
			OGLGUITheme t = getTheme();
			return t != null ? t.getFont(key) : null;
		}

		return null;
	}

	/**
	 * Sets the font for this text object.
	 */
	public void setFont(OGLGUIFontType font)
	{
		this.font = font;
		refreshMesh();
	}

	/**
	 * Sets this object's theme key. 
	 * This is the key that is used to look up the font used to render this object
	 * if a theme is applied to this object or the GUI that owns it. 
	 */
	public void setThemeKey(String themeKey)
	{
		this.themeKey = themeKey;
	}
	
	@Override
	public String getThemeKey()
	{
		return themeKey;
	}
	
	/**
	 * Sets the text on this text object.
	 * Default is the empty string.
	 */
	public void setText(String text)
	{
		if (this.text != null && this.text.equals(text))
			return;
		this.text = text == null ? "" : text;
		refreshMesh();
	}
	
	/**
	 * Gets the text on this text object.
	 * Default is the empty string.
	 */
	public String getText()
	{
		return text;
	}
	
	/**
	 * Checks if word wrapping is enabled.
	 * True if so, false if not.
	 */
	public boolean isWordWrappingEnabled()
	{
		return wordWrap;
	}

	/**
	 * Sets if word wrapping is enabled.
	 * True if so, false if not.
	 */
	public void setWordWrap(boolean wordWrap)
	{
		this.wordWrap = wordWrap;
		refreshMesh();
	}

	/**
	 * Gets the text alignment/justification.
	 */
	public Justification getJustification()
	{
		return justification;
	}

	/**
	 * Sets the text alignment/justification.
	 */
	public void setJustification(Justification justification)
	{
		this.justification = justification;
		refreshMesh();
	}

	/**
	 * Gets the current style of vertical text alignment.
	 */
	public Alignment getAlignment()
	{
		return alignment;
	}

	/**
	 * Sets the current style of vertical text alignment.
	 */
	public void setAlignment(Alignment alignment)
	{
		this.alignment = alignment;
		refreshMesh();
	}

	/**
	 * Gets the current resize mode for this text object.
	 * If this is not null, the object will resize itself 
	 * according to the max width and resize mode parameters.
	 */
	public ResizeMode getResizeMode()
	{
		return resizeMode;
	}

	/**
	 * Sets the current resize mode for this text object.
	 * If this is not null, the object will resize itself 
	 * according to the max width and resize mode parameters.
	 */
	public void setResizeMode(ResizeMode resizeMode)
	{
		this.resizeMode = resizeMode;
	}

	/**
	 * Gets the max width in units for the resizing capability of this object.
	 */
	public float getMaxWidth()
	{
		return maxWidth;
	}

	/**
	 * Sets the max width in units for the resizing capability of this object.
	 */
	public void setMaxWidth(float maxWidth)
	{
		this.maxWidth = maxWidth;
	}

	/**
	 * Gets the starting line that the mesh data will be generated for
	 * in the text field. By default, this is zero (first line). 
	 */
	public int getStartingLine()
	{
		return startingLine;
	}

	/**
	 * Sets the starting line that the mesh data will be generated for
	 * in the text field. By default, this is zero (first line). 
	 */
	public void setStartingLine(int startingLine)
	{
		this.startingLine = startingLine;
		textMesh = constructMesh();
		textMeshView = textMesh.getView();
	}
	
	@Override
	protected void onGUIChange(OGLGUI gui)
	{
		super.onGUIChange(gui);
		refreshMesh();
	}

	@Override
	public MeshView getMeshView()
	{
		return textMeshView;
	}
	
	private void refreshMesh()
	{
		refreshTextData(getText());
		textMesh = constructMesh();
		textMeshView = textMesh != null ? textMesh.getView() : null;
	}

	/**
	 * Creates and returns a new mesh for the new text.
	 * @param text the text data.
	 */
	private void refreshTextData(String text)
	{	
		OGLGUIFontType font = getFont();
		
		if (font == null)
			return;
		
		float width = resizeMode != null ? maxWidth : getRenderHalfWidth()*2;
		float height = resizeMode != null ? Float.MAX_VALUE : getRenderHalfHeight()*2;

		textData = new MeshContext();
		if (text == null || text.length() == 0 || font.getSize() <= 0f || width == 0 || height == 0)
			return;
		
		float size = font.getSize();
		char[] chars = text.toCharArray();
		for (int i = 0; i < chars.length; i++)
		{
			char c = chars[i];
			FontChar fc = font.getChar(c);
			// newlines
			if (fc == null)
			{
				// Do nothing.
			}
			else if (c == '\n')
			{
				textData.addWordToLine();
				textData.nextLine();
			}
			// spaces and other.
			else if (Character.isWhitespace(c))
			{
				FontChar spacefc = font.getChar(' ');
				float spwidth = (spacefc.width * size);
				textData.addWordToLine();
				if (textData.lineBuffer.lineWidth + spwidth < width)
					textData.lineBuffer.addChar(spacefc);
			}
			// character will breach the line.
			else if (textData.lineBuffer.lineWidth + textData.wordWidth + (fc.advance * size) >= width)
			{
				if (textData.lineBuffer.words == 0)
					textData.addWordToLine();
				textData.nextLine();
				textData.addChar(fc);
			}
			else
			{
				textData.addChar(fc);
				if (characterIsBreakingDelimiter(c))
					textData.addWordToLine();
			}
		}
		textData.addWordToLine();
		textData.nextLine();
	}

	/**
	 * Checks if a particular character is a breaking delimiter for word wrapping.
	 */
	private boolean characterIsBreakingDelimiter(char c)
	{
		switch (c)
		{
			case '-':
			case '\u2013':
			case '\u2014':
				return true;
		}
		return false;
	}
	
	// Creates the mesh itself.
	private PolygonMesh constructMesh()
	{
		OGLGUIFontType font = getFont();
		
		if (font == null || textData.quads == 0)
			return NO_POLYS;
		
		int maxLines = textData.textBlock.size();
		int visibleLines = maxLines - Math.max(startingLine, 0);
		float size = font.getSize();
		float width = resizeMode != null ? maxWidth : getRenderHalfWidth()*2;
		float height = resizeMode != null ? visibleLines * size : getRenderHalfHeight() * 2;
		int totalLines = (int)(size != 0.0f ? height / size : 0);
		float heightBasis = size / height * 2;
		float widthBasis = size / width * 2;

		PolygonMesh out = new PolygonMesh(GeometryType.QUADS, textData.quads*4, 1);
		
		int i = 0;
		float y = 1f;
		if (alignment == Alignment.BOTTOM)
			y = -1f + (visibleLines < totalLines ? visibleLines * heightBasis : totalLines * heightBasis);
		else if (alignment == Alignment.MIDDLE)
			y = 1f - ((2f - (visibleLines < totalLines ? visibleLines * heightBasis : totalLines * heightBasis))/2);
		
		Directionality dir = font.getDirectionality();
		int linenum = 0;
		int linesRendered = 0;
		for (MeshContext.Line line : textData.textBlock)
		{
			if (linesRendered >= totalLines)
				break;

			if (linenum < startingLine)
			{
				linenum++;
				continue;
			}
			
			linenum++;
			
			float x = 0f;
			
			Justification just = null;
			
			if (justification == Justification.DEFAULT)
				just = font.getDirectionality() == 
				Directionality.LEFT_TO_RIGHT ? Justification.LEFT : Justification.RIGHT;
			else
				just = justification;
			
			if (just == Justification.CENTER)
				x = -(line.lineWidth / width);
			else if (just == Justification.RIGHT)
				x = (-(line.lineWidth / width)) * 2 + 1;
			else // LEFT
				x = -1f;

			if (dir == Directionality.RIGHT_TO_LEFT)
				x += (line.lineWidth / width) * 2;
			
			for (FontChar fc : line.line)
			{
				if (dir == Directionality.RIGHT_TO_LEFT)
					x -= fc.advance * widthBasis;
				
				float wx = fc.width * widthBasis;
				float hy = fc.height * heightBasis;
				float xofs = fc.xofs * widthBasis;
				float yofs = fc.yofs * heightBasis;
				
				int idx = (i*4);

				float x0 = x+xofs;
				float x1 = x+wx+xofs;
				float y0 = yofs - (y - hy);
				float y1 = yofs - y;
				
				out.setVertex(idx+0, x0, y0, 0);
				out.setVertex(idx+1, x0, y1, 0);
				out.setVertex(idx+2, x1, y1, 0);
				out.setVertex(idx+3, x1, y0, 0);
				
				out.setTextureCoordinate(idx+0, fc.s0, fc.t1);
				out.setTextureCoordinate(idx+1, fc.s0, fc.t0);
				out.setTextureCoordinate(idx+2, fc.s1, fc.t0);
				out.setTextureCoordinate(idx+3, fc.s1, fc.t1);
				
				if (dir == Directionality.LEFT_TO_RIGHT)
					x += fc.advance * widthBasis;
				i++;
			}
			y -= heightBasis;
			linesRendered++;
		}
		
		Rectangle2F objectBounds = getNativeBounds();
		
		// resize!
		if (resizeMode != null) switch (resizeMode)
		{
			case PIN_Y:
				objectBounds.height = height;
				if (linesRendered <= 1)
					objectBounds.width = textData.textBlock.head().lineWidth;
				else
					objectBounds.width = maxWidth;
				break;
			case CHANGE_Y:
				float old_height = objectBounds.height;
				objectBounds.height = height;
				objectBounds.y += old_height - objectBounds.height;
				if (linesRendered <= 1)
					objectBounds.width = textData.textBlock.head().lineWidth;
				else
					objectBounds.width = maxWidth;
				break;
		}

		return out;
	}
	
	/**
	 * Mesh context for creating a mesh.
	 */
	private class MeshContext
	{
		public Queue<Line> textBlock;
		public Line lineBuffer;
		public Queue<FontChar> wordBuffer;
		public float wordWidth;
		public int quads;
		
		MeshContext()
		{
			textBlock = new Queue<Line>();
			lineBuffer = new Line();
			wordBuffer = new Queue<FontChar>();
			wordWidth = 0f;
			quads = 0;
		}
		public void nextLine()
		{
			lineBuffer.trim();
			quads += lineBuffer.line.size();
			textBlock.add(lineBuffer);
			lineBuffer = new Line();
		}
		public void addWordToLine()
		{
			for (FontChar fc : wordBuffer)
				lineBuffer.addChar(fc);
			lineBuffer.words++;
			wordBuffer = new Queue<FontChar>();
			wordWidth = 0;
		}
		public void addChar(FontChar fc)
		{
			OGLGUIFontType font = getFont();
			wordBuffer.add(fc);
			wordWidth += (fc.advance * font.getSize());
		}
		
		protected class Line
		{
			public Queue<FontChar> line;
			public float lineWidth;
			public int words;
			
			public Line()
			{
				line = new Queue<FontChar>();
				lineWidth = 0;
				words = 0;
			}
			public void addChar(FontChar fc)
			{
				OGLGUIFontType font = getFont();
				line.add(fc);
				lineWidth += (fc.advance * font.getSize());
			}
			public void trim()
			{
				OGLGUIFontType font = getFont();
				float size = font.getSize();
				while (!line.isEmpty() && Character.isWhitespace(line.tail().c))
					lineWidth -= line.removeIndex(line.size()-1).advance * size;
				while (!line.isEmpty() && Character.isWhitespace(line.head().c))
					lineWidth -= line.dequeue().advance * size;
			}
		}
	}
	
	
}
