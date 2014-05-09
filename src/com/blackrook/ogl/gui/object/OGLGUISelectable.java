/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui.object;

/**
 * An interface that describes GUI objects that contain
 * a set of objects.
 * @author Matthew Tropiano
 */
public interface OGLGUISelectable<T extends Object>
{
	/** Index value for no selection. */
	public static final int NO_SELECTION = -1;
	
	/**
	 * Gets the selected index on the object.
	 * @return the current selected index. 
	 */
	public int getSelectedIndex();
	
	/**
	 * Sets the selected index on the object.
	 * If the index is out of bounds, it should be set to -1, meaning nothing selected. 
	 * @param index the new selected index.
	 */
	public void setSelectedIndex(int index);
	
	/** 
	 * Returns the current selected value, or null if no value selected.
	 */
	public T getSelectedValue();
	
}
