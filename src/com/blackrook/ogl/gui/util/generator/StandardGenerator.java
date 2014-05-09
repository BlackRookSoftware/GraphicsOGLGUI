/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui.util.generator;

import com.blackrook.commons.math.geometry.Rectangle2F;
import com.blackrook.ogl.data.OGLColor;
import com.blackrook.ogl.gui.util.OGLGUIObjectGenerator;

/**
 * A generator that will generate white-colored objects with a specific bounds.
 * @author Matthew Tropiano
 */
public class StandardGenerator implements OGLGUIObjectGenerator
{
	/** Used for all colors. */
	private OGLColor color; 

	/** The saved bounds. */
	private Rectangle2F bounds;

	/** Returned bounds. */
	private Rectangle2F returnedBounds; 
	
	/**
	 * Creates a new StandardGenerator that generates objects with a specific bounds.
	 * @param x Starting X-coordinate.
	 * @param y Starting Y-coordinate.
	 * @param width the object width.
	 * @param height the object height.
	 */
	public StandardGenerator(int x, int y, int width, int height)
	{
		color = new OGLColor();
		bounds = new Rectangle2F();
		returnedBounds = new Rectangle2F();
		setBounds(x, y, width, height);
	}
	
	/**
	 * Sets the bounds of generated objects.
	 * @param bounds a rectangle that describes the desired bounds.
	 */
	public void setBounds(Rectangle2F bounds)
	{
		this.bounds.set(bounds);
	}
	
	/**
	 * Sets the bounds of generated objects.
	 * @param x Starting X-coordinate.
	 * @param y Starting Y-coordinate.
	 * @param width the object width.
	 * @param height the object height.
	 */
	public void setBounds(int x, int y, int width, int height)
	{
		bounds.set(x, y, width, height);
	}
	
	@Override
	public Rectangle2F nextBounds(int sequence)
	{
		returnedBounds.set(bounds);
		return returnedBounds;
	}

	@Override
	public OGLColor nextColor(int sequence)
	{
		color.set(OGLColor.WHITE);
		return color;
	}

	@Override
	public String[] nextNames(int sequence)
	{
		return null;
	}

}
