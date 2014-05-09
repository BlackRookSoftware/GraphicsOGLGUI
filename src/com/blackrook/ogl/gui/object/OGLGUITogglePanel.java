/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui.object;

import com.blackrook.commons.Common;
import com.blackrook.ogl.gui.OGLGUIAction;
import com.blackrook.ogl.gui.OGLGUIEvent;
import com.blackrook.ogl.gui.OGLGUIObject;
import com.blackrook.ogl.mesh.MeshView;

/**
 * A checkbox object that holds a boolean switch.
 * <p>
 * This object is very much undecorated. You will have to apply colors and stuff to it.
 * Listening to its state will be useful for setting its colors and look.
 * @author Matthew Tropiano
 */
public abstract class OGLGUITogglePanel extends OGLGUIObject implements OGLGUIToggleable
{
	/** Press action. */
	protected static final OGLGUIAction PRESS_ACTION = new OGLGUIAction()
	{
		@Override
		public void call(OGLGUIEvent event)
		{
			OGLGUITogglePanel tp = (OGLGUITogglePanel)event.getObject();
			tp.requestFocus();
			tp.beingClicked = true;
		};
	};

	/** Release action. */
	protected static final OGLGUIAction RELEASE_ACTION = new OGLGUIAction()
	{
		@Override
		public void call(OGLGUIEvent event)
		{
			OGLGUITogglePanel tp = (OGLGUITogglePanel)event.getObject();
			tp.beingClicked = false;
		};
	};

	/** If true, this is in the middle of being changed. */
	private boolean beingClicked;
	/** Checked state. */
	private boolean state;
	
	/**
	 * Creates a new toggle panel in the unset, false state.
	 */
	public OGLGUITogglePanel()
	{
		this(false);
		bindAction(PRESS_ACTION, EVENT_MOUSE_PRESS, EVENT_KEY_PRESS);
		bindAction(RELEASE_ACTION, EVENT_MOUSE_RELEASE, EVENT_KEY_RELEASE);
	}
	
	/**
	 * Creates a new toggle panel.
	 * @param startState the starting state (true or false).
	 */
	public OGLGUITogglePanel(boolean startState)
	{
		super();
		setState(startState);
	}
	
	@Override
	public boolean isSet()
	{
		return state;
	}

	@Override
	public void toggle()
	{
		setState(!state);
	}

	@Override
	public void setState(boolean state)
	{
		if (this.state != state)
		{
			this.state = state;
			fireEvent(EVENT_VALUE_CHANGE);
		}
	}
	
	@Override
	public Boolean getValue()
	{
		return state;
	}

	@Override
	public void setValue(Object value)
	{
		if (value instanceof Boolean)
			setState((Boolean)value);
		else if (value instanceof Number)
			setState(((Number)value).doubleValue() != 0.0);
		else
			setState(Common.parseBoolean(String.valueOf(value), false));
	}

	@Override
	public MeshView getMeshView()
	{
		return RECTANGLE_VIEW;
	}

	/**
	 * Returns true if this object is in the middle of being clicked.
	 */
	public boolean isBeingClicked()
	{
		return beingClicked;
	}
	
}
