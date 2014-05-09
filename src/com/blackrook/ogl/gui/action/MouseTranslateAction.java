/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui.action;

import com.blackrook.ogl.gui.OGLGUIAction;
import com.blackrook.ogl.gui.OGLGUIEvent;

/**
 * An action that translates the object by mouse movement.
 * @author Matthew Tropiano
 */
public class MouseTranslateAction extends OGLGUIAction
{
	/** A DragAction instance. */
	public static final MouseTranslateAction INSTANCE = new MouseTranslateAction();
	
	private MouseTranslateAction() {}
	
	@Override
	public void call(OGLGUIEvent event)
	{
		event.getObject().translate(event.getMouseMovementX(), event.getMouseMovementY());
	}

}
