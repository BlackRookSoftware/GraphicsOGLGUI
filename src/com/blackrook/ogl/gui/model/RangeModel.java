/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui.model;

/**
 * A data model to use with objects that have a range of values and
 * a way to select them by varying input along a floating-point scale.
 * @author Matthew Tropiano
 * @param <T> the object that this model returns.
 */
public interface RangeModel<T extends Object>
{
	/** Returns the minimum value of this model. */
	public T getMinValue();
	
	/** Returns the maximum value of this model. */
	public T getMaxValue();

	/** Returns if the input value is in the range. */
	public boolean isInRange(T value);

	/** 
	 * Returns a value in this model using a variable scalar.
	 * @param scalar a value from 0 to 1, inclusive. 
	 */
	public T getValueForScalar(double scalar);
	
	/** 
	 * Returns a scalar from 0 to 1 using a value that could be in this model.
	 * @param value a value that could conceivably fit in the model.
	 */
	public double getScalarForValue(T value);
	
}
