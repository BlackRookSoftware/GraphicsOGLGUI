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
 * Describes an animation to be performed on an object.
 * @author Matthew Tropiano
 */
public abstract class OGLAnimation<T extends OGLScene2DElement>
{
	/** 
	 * Returns a mutable action state that is used by action queues.
	 * Requires pairing with an object. 
	 */
	public abstract OGLAnimationState<T> createState(T object);
	
}
