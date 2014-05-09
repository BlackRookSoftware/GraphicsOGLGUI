/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui;

import com.blackrook.ogl.util.enums.EasingType;
import com.blackrook.ogl.util.scene2d.OGLScene2DElement;

/**
 * Describes an animation to be performed on an object.
 * @author Matthew Tropiano
 */
public class OGLAnimationGroup<T extends OGLScene2DElement>
{
	/** Object reference. */
	protected T object;
	/** Animation duration in milliseconds. */
	protected float duration;
	/** Animation easing type. */
	protected EasingType easing;
	/** Animation list. */
	protected OGLAnimation<T>[] animations;

	OGLAnimationGroup(T object, float duration, EasingType easing, OGLAnimation<T> ... animations)
	{
		this.object = object;
		this.duration = duration;
		this.easing = easing;
		this.animations = animations;
	}
	
	/**
	 * Gets the object associated with this animation state.
	 */
	public T getObject()
	{
		return object;
	}

	/**
	 * Gets the duration of this animation state.
	 */
	public float getDuration()
	{
		return duration;
	}

	/**
	 * Gets the easing of this animation state.
	 */
	public EasingType getEasing()
	{
		return easing;
	}

	/**
	 * Gets the animations on this animation state.
	 */
	public OGLAnimation<T>[] getAnimations()
	{
		return animations;
	}

}
