/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui.action;

import com.blackrook.commons.hash.Hash;
import com.blackrook.commons.math.Pair;
import com.blackrook.ogl.gui.OGLGUIAction;
import com.blackrook.ogl.gui.OGLGUIEvent;
import com.blackrook.ogl.input.OGLInputConstants;

/**
 * An action that validates HOW it was activated before
 * it is actually called. This is meant to be attached to more than one
 * type of event, especially if both mouse and keyboard keys are used for validation.
 * @author Matthew Tropiano
 */
public abstract class ValidInputAction extends OGLGUIAction
{
	/** Set of valid keys. */
	private Hash<Integer> validKeyCodes;
	/** Set of valid mouse buttons. */
	private Hash<Integer> validMouseButtons;
	/** Set of valid gamepad buttons. */
	private Hash<Integer> validGamepadButtons;
	/** Set of valid gamepad buttons. */
	private Hash<Pair> validGamepadTaps;
	
	/** Protected constructor. */
	protected ValidInputAction()
	{
	}
	
	/**
	 * Adds a "valid" key code to this action.
	 * Key codes are defined by the ones in {@link OGLInputConstants}.
	 * @param keyCodes the key codes to add.
	 */
	public void addKeyCode(int ... keyCodes)
	{
		if (validKeyCodes == null)
			validKeyCodes = new Hash<Integer>(2);
		
		for (int i : keyCodes)
			validKeyCodes.put(i);
	}
	
	/**
	 * Removes a "valid" key code from this action.
	 * Key codes are defined by the ones in {@link OGLInputConstants}.
	 * @param keyCodes the key codes to remove.
	 */
	public void removeKeyCode(int ... keyCodes)
	{
		if (validKeyCodes == null)
			return;
		
		for (int i : keyCodes)
			validKeyCodes.remove(i);
	}
	
	/**
	 * Adds a "valid" mouse button to this action.
	 * Mouse buttons are defined by the ones in {@link OGLInputConstants}.
	 * @param buttons the mouse buttons to add.
	 */
	public void addMouseButton(int ... buttons)
	{
		if (validMouseButtons == null)
			validMouseButtons = new Hash<Integer>(2);
		
		for (int i : buttons)
			validMouseButtons.put(i);
	}
	
	/**
	 * Removes a "valid" mouse button from this action.
	 * Mouse buttons are defined by the ones in {@link OGLInputConstants}.
	 * @param buttons the mouse buttons to remove.
	 */
	public void removeMouseButton(int ... buttons)
	{
		if (validMouseButtons == null)
			return;

		for (int i : buttons)
			validMouseButtons.remove(i);
	}
	
	/**
	 * Adds a "valid" gamepad button to this action.
	 * Gamepad buttons are defined by the ones in {@link OGLInputConstants}.
	 * the equivalents in {@link OGLInputConstants}.  
	 * @param buttons the gamepad buttons to add.
	 */
	public void addGamepadButton(int ... buttons)
	{
		if (validGamepadButtons == null)
			validGamepadButtons = new Hash<Integer>(2);

		for (int i : buttons)
			validGamepadButtons.put(i);
	}
	
	/**
	 * Removes a "valid" gamepad button from this action.
	 * Gamepad buttons are defined by the ones in {@link OGLInputConstants}.
	 * @param buttons the gamepad buttons to remove.
	 */
	public void removeGamepadButton(int ... buttons)
	{
		if (validGamepadButtons == null)
			return;
		
		for (int i : buttons)
			validGamepadButtons.remove(i);
	}
	
	/**
	 * Adds a "valid" gamepad axis tap to this action.
	 * Gamepad axes are defined by the ones in {@link OGLInputConstants}.
	 * the equivalents in {@link OGLInputConstants}.
	 * @param axisId the id value of a valid axis.  
	 * @param positive if true, "positive" axis value, otherwise, negative.
	 */
	public void addGamepadAxisTap(int axisId, boolean positive)
	{
		if (validGamepadTaps == null)
			validGamepadTaps = new Hash<Pair>(2);
		
		validGamepadTaps.put(new Pair(axisId, positive ? 1 : 0));
	}
	
	/**
	 * Removes a "valid" gamepad axis tap from this action.
	 * Gamepad axes are defined by the ones in {@link OGLInputConstants}.
	 * @param axisId the id value of a valid axis.  
	 * @param positive if true, "positive" axis value, otherwise, negative.
	 */
	public void removeGamepadAxisTap(int axisId, boolean positive)
	{
		if (validGamepadTaps == null)
			return;
		
		validGamepadTaps.remove(new Pair(axisId, positive ? 1 : 0));
	}
	
	@Override
	public final void call(OGLGUIEvent event)
	{
		if (event.isKeyboardEvent())
		{
			if (validKeyCodes != null && validKeyCodes.contains(event.getKeyCode()))
				validCall(event);
		}
		else if (event.isMouseEvent())
		{
			if (validMouseButtons != null && validMouseButtons.contains(event.getMouseButton()))
				validCall(event);
		}
		else if (event.isGamepadEvent())
		{
			if (validGamepadButtons != null && validGamepadButtons.contains(event.getGamepadButton()))
				validCall(event);
		}
		else if (event.isGamepadAxisTapEvent())
		{
			if (validGamepadTaps != null && validGamepadTaps.contains(new Pair(event.getGamepadAxisId(), event.getGamepadAxisTapValue() ? 1 : 0)))
				validCall(event);
		}
	}
	
	/**
	 * If the input is validated, this is called with the
	 * same event as {@link #call(OGLGUIEvent)}
	 * @param event the same event that was passed to this action.
	 */
	public abstract void validCall(OGLGUIEvent event);
	
}
