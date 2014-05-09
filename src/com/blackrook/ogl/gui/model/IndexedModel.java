/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui.model;

/**
 * A data model for use with objects that contain lists of predefined objects stored in a list.
 * @author Matthew Tropiano
 * @param <T> the object that this model contains.
 */
public interface IndexedModel<T extends Object>
{
	/**
	 * Returns the value at a specific index number in this model.
	 * If this index number is invalid, this returns null.
	 */
	public T getValueByIndex(int index);
	
	/**
	 * Returns a specific index number of a value in this model.
	 * If the value is not in this model, this returns -1. 
	 */
	public int getIndexByValue(T value);

	/**
	 * Returns how many objects are in this model.
	 */
	public int size();
}
