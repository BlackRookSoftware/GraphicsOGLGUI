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
 * An action that just requests focus on the object that it happened on.
 * @author Matthew Tropiano
 */
public class FocusAction extends OGLGUIAction
{
	/** A FocusAction instance. */
	public static final FocusAction INSTANCE = new FocusAction();
	
	private FocusAction() {}
	
	@Override
	public void call(OGLGUIEvent event)
	{
		event.getObject().requestFocus();
	}

}
