/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui.object;

/**
 * Interface for fields that contain a value of some kind.
 * Contains methods for getting and setting that value.
 * @author Matthew Tropiano
 *
 * @param <T> an object type.
 */
public interface OGLGUIValueField<T extends Object>
{
	/** Event type for a value/slider changing. */
	public static final String EVENT_VALUE_CHANGE = "VALUE_CHANGE";

	/** Returns this field's current value. */
	public T getValue();
	
	/** 
	 * Sets this field's value.
	 * This must accept any object, and attempt to set the correct value based on it.
	 * This object must fire an {@link #EVENT_VALUE_CHANGE} event if it changes from its current value.
	 * @param value the value to set.
	 */
	public void setValue(Object value);
	
}
