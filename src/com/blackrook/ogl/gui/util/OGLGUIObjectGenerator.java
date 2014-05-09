/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui.util;

import com.blackrook.commons.math.geometry.Rectangle2F;
import com.blackrook.ogl.data.OGLColor;

/**
 * A descriptor class that details how the {@link OGLGUIObjectFactory} should
 * automatically construct objects.
 * @author Matthew Tropiano
 */
public interface OGLGUIObjectGenerator
{
	/**
	 * Retrieves the next set of bounds for a generated object.
	 * <p>
	 * The policy of this method is to generate a non-null {@link Rectangle2F}
	 * that, if altered somehow, will not alter future objects. The {@link Rectangle2F} created
	 * is allowed to share a reference with the previously generated Rectangle, since
	 * the intent is to read it, not write.
	 * @param sequence the sequence number for the generated object.
	 * @return a {@link Rectangle2F} of the bounds for the next object.
	 */
	public Rectangle2F nextBounds(int sequence);
	
	/**
	 * Retrieves the next color for a generated object.
	 * <p>
	 * The policy of this method is to generate a non-null {@link OGLColor}
	 * that, if altered somehow, will not alter future objects. The {@link OGLColor} created
	 * is allowed to share a reference with the previously generated color object, since
	 * the intent is to read it, not write.
	 * @param sequence the sequence number for the generated object.
	 * @return an {@link OGLColor} for the next color.
	 */
	public OGLColor nextColor(int sequence);
	
	/**
	 * Retrieves the next name or names to apply to a generated object.
	 * <p>
	 * The policy of this method is to generate a list of strings. It may
	 * be null, which is to be interpreted as no names. 
	 * The {@link OGLColor} created is allowed to share a reference with 
	 * the previously generated list of Strings, since the intent is to 
	 * read it, not write.
	 * @param sequence the sequence number for the generated object.
	 * @return an array of names to add.
	 */
	public String[] nextNames(int sequence);
	
}
