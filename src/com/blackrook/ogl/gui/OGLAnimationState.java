/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui;

import com.blackrook.ogl.util.scene2d.OGLScene2DElement;

/**
 * Animation state.
 * @author Matthew Tropiano
 */
public abstract class OGLAnimationState<T extends OGLScene2DElement>
{
	/** Object reference. */
	protected T object;
	
	protected OGLAnimationState(T object)
	{
		this.object = object;
	}
	
	/**
	 * Updates the object on this state using the progress
	 * factor provided.
	 * @param progressPercent percentage progress (0 to 1).
	 */
	public abstract void update(float progressPercent);
	
}
