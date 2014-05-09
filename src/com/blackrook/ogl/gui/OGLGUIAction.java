/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui;

import com.blackrook.ogl.input.OGLInputConstants;

/**
 * An action that can be invoked later from a GUI object.
 * @author Matthew Tropiano
 */
public abstract class OGLGUIAction implements OGLInputConstants
{
	/** 
	 * The method stub called when this action is invoked.
	 * @param event the event that was passed into this action.
	 */
	public abstract void call(OGLGUIEvent event);
	
}
