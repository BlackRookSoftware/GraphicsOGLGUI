/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui.group;

import java.util.Arrays;

import com.blackrook.ogl.gui.OGLGUIAction;
import com.blackrook.ogl.gui.OGLGUIEvent;
import com.blackrook.ogl.gui.OGLGUIObject;
import com.blackrook.ogl.gui.object.OGLGUIToggleable;

/**
 * This is a set that describes a set of toggleable buttons that
 * are associated in a group such that when one gets set, the others are unset.
 * @author Matthew Tropiano
 */
public class ToggleGroup
{
	/** Current panel. */
	protected OGLGUIToggleable current;
	
	/**
	 * Creates a new {@link ToggleGroup} and binds the required actions
	 * to the objects provided.
	 * @param objects the objects to include in the group.
	 */
	public ToggleGroup(OGLGUIObject ... objects)
	{
		this(Arrays.asList(objects));
	}

	/**
	 * Creates a new {@link ToggleGroup} and binds the required actions
	 * to the objects provided.
	 * @param objects the objects to include in the group.
	 */
	public ToggleGroup(Iterable<OGLGUIObject> objects)
	{
		OGLGUIAction action = new OGLGUIAction()
		{
			@Override
			public void call(OGLGUIEvent event)
			{
				toggle(event.getObject());
			}
		};
			
		for (OGLGUIObject obj : objects)
			obj.bindAction(action, OGLGUIToggleable.EVENT_VALUE_CHANGE);
	}
	
	/**
	 * Performs toggle.
	 */
	private void toggle(OGLGUIObject object)
	{
		if (object != null && object instanceof OGLGUIToggleable)
		{
			OGLGUIToggleable tog = (OGLGUIToggleable)object;
			if (current != null && current != tog) 
				current.setState(false);
			current = tog;
		}
	}
	
}
