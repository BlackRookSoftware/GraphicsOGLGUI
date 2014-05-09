/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui.action;

import com.blackrook.commons.list.List;
import com.blackrook.ogl.gui.OGLGUIAction;
import com.blackrook.ogl.gui.OGLGUIEvent;

/**
 * An action encapsulates and calls other actions.
 * Actions can be added to or removed from this one at will.
 * @author Matthew Tropiano
 */
public class CompoundValidInputAction extends ValidInputAction
{
	/** Action list. */
	private List<OGLGUIAction> actionList;
	
	/**
	 * Creates a new compound action with no actions attached.
	 */
	public CompoundValidInputAction()
	{
		actionList = new List<OGLGUIAction>(3);
	}
	
	/**
	 * Adds one or more actions to this compound action.
	 * The actions passed in are appended to the internal list in
	 * the order provided. Actions are executed in the order in 
	 * which they were added.
	 * @param actions the actions to add.
	 */
	public void addAction(OGLGUIAction ... actions)
	{
		for (OGLGUIAction action : actions)
			actionList.add(action);
	}

	/**
	 * Removes one or more actions from this compound action.
	 * The actions passed in are appended to the internal list in
	 * the order provided. Actions are executed in the order in 
	 * which they were added.
	 * @param actions the actions to remove.
	 */
	public void removeAction(OGLGUIAction ... actions)
	{
		for (OGLGUIAction action : actions)
			actionList.remove(action);
	}

	/**
	 * Clears all actions added to this compound action.
	 */
	public void clearActions()
	{
		actionList.clear();
	}

	/**
	 * Clears and then adds one or more actions to this compound action.
	 * The actions passed in are appended to the internal list in
	 * the order provided. Actions are executed in the order in 
	 * which they were added.
	 * @param actions the actions to add.
	 */
	public void setAction(OGLGUIAction ... actions)
	{
		clearActions();
		addAction(actions);
	}

	@Override
	public final void validCall(OGLGUIEvent event)
	{
		for (OGLGUIAction action : actionList)
			action.call(event);
	}

}
