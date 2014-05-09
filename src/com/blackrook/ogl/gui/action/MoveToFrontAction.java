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
 * An action that just moves this object to the front of its siblings.
 * @author Matthew Tropiano
 */
public class MoveToFrontAction extends OGLGUIAction
{
	/** A FocusAction instance. */
	public static final MoveToFrontAction INSTANCE = new MoveToFrontAction();
	
	private MoveToFrontAction() {}
	
	@Override
	public void call(OGLGUIEvent event)
	{
		event.getObject().moveToFront();
	}

}
