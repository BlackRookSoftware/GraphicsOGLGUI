/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui.model.range;

import com.blackrook.commons.math.RMath;
import com.blackrook.ogl.gui.model.RangeModel;

/**
 * An implementation of {@link RangeModel} that returns a floating-point range.
 * @author Matthew Tropiano
 */
public class FloatRangeModel implements RangeModel<Float>
{
	/** Min value. */
	private float min;
	/** Max value. */
	private float max;
	
	/**
	 * Creates a new model with the provided minimum and maximum values.
	 * @param min the minimum value.
	 * @param max the maximum value.
	 */
	public FloatRangeModel(float min, float max)
	{
		this.min = min;
		this.max = max;
	}

	@Override
	public Float getMinValue()
	{
		return min;
	}

	@Override
	public Float getMaxValue()
	{
		return max;
	}

	@Override
	public Float getValueForScalar(double scalar)
	{
		return (float)RMath.linearInterpolate(scalar, min, max);
	}

	@Override
	public double getScalarForValue(Float value)
	{
		return RMath.getInterpolationFactor(value, min, max);
	}

	@Override
	public boolean isInRange(Float value)
	{
		return value != null && value >= min && value <= max;
	}

}
