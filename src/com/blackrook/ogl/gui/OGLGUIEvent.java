/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.blackrook.ogl.input.OGLInputConstants;

/**
 * Event class for things that happen in the GUI.
 * Be warned that event objects may be pooled - this should only be used
 * for feedback purposes. Do not store references to these objects, 
 * as they be may be reused by the GUI system. They are also NOT THREAD-SAFE.
 * @author Matthew Tropiano
 */
public final class OGLGUIEvent implements OGLInputConstants
{
	/**
	 * Origin of event.
	 */
	public static enum Origin
	{
		/** Event has unknown origin. */
		UNKNOWN,
		/** Event originated from an action call. */
		ACTION,
		/** Event originated from Input. */
		INPUT,
		/** Event originated from GUI System. */
		GUI,
	}
	
	/**
	 * Input type on event.
	 */
	private static enum InputType
	{
		/** Not an input event. */
		NONE,
		/** Keyboard event. */
		KEYBOARD,
		/** Mouse event. */
		MOUSE,
		/** Mouse wheel event. */
		MOUSE_WHEEL,
		/** Gamepad event. */
		GAMEPAD,
		/** Gamepad axis event. */
		GAMEPAD_AXIS,
		/** Gamepad axis tap event. */
		GAMEPAD_AXIS_TAP;
	}
	
	/** GUI system node that this event occurred on. */
	private OGLGUI gui;
	/** Object that this event occurred on. */
	private OGLGUIObject object;
	/** Event type name. */
	private String type;
	/** Event API origin. */
	private Origin origin;
	/** Input type for event. */
	private InputType inputType;
	/** Keyboard modifier. */
	private int imod;
	/** Integer argument 0. */
	private int iarg0;
	/** Integer argument 1. */
	private int iarg1;
	/** Float argument 0. */
	private float farg0;
	/** Float argument 1. */
	private float farg1;
	/** Float argument 2. */
	private float farg2;
	/** Float argument 3. */
	private float farg3;
	/** Float argument 4. */
	private float farg4;
	/** Float argument 5. */
	private float farg5;
	/** Float argument 6. */
	private float farg6;
	/** Float argument 7. */
	private float farg7;
	
	/** Creates a completely new event. */
	OGLGUIEvent()
	{
		reset();
	}
	
	/** Resets this event's fields. */
	void reset()
	{
		gui = null;
		object = null;
		type = OGLGUIObject.EVENT_UNKNOWN;
		origin = Origin.UNKNOWN;
		iarg0 = -1;
		iarg1 = -1;
		farg0 = 0f;
		farg1 = 0f;
		farg2 = 0f;
		farg3 = 0f;
		farg4 = 0f;
		farg5 = 0f;
		farg6 = 0f;
		farg7 = 0f;
	}
	
	/**
	 * Returns a reference to the GUI node that this event happened on. 
	 */
	public OGLGUI getGUI()
	{
		return gui;
	}
	
	/** 
	 * Returns a reference to the object that is the source of this event.
	 * May be null if this event was not fired by an object. 
	 */
	public OGLGUIObject getObject()
	{
		return object;
	}
	
	/**
	 * Returns the type of event that this is.
	 */
	public String getType()
	{
		return type;
	}
	
	/**
	 * Returns what system originated this event.
	 */
	public Origin getOrigin()
	{
		return origin;
	}
	
	/**
	 * Is this event a keyboard event?
	 * @return true if so, false if not.
	 */
	public boolean isKeyboardEvent()
	{
		return inputType == InputType.KEYBOARD;
	}
	
	/**
	 * Is this event a mouse event?
	 * @return true if so, false if not.
	 */
	public boolean isMouseEvent()
	{
		return inputType == InputType.MOUSE;
	}
	
	/**
	 * Is this event a mouse wheel event?
	 * @return true if so, false if not.
	 */
	public boolean isMouseWheelEvent()
	{
		return inputType == InputType.MOUSE_WHEEL;
	}
	
	/**
	 * Is this event a gamepad axis event?
	 * @return true if so, false if not.
	 */
	public boolean isGamepadEvent()
	{
		return inputType == InputType.GAMEPAD;
	}
	
	/**
	 * Is this event a gamepad axis event?
	 * @return true if so, false if not.
	 */
	public boolean isGamepadAxisEvent()
	{
		return inputType == InputType.GAMEPAD_AXIS;
	}
	
	/**
	 * Is this event a gamepad axis tap event?
	 * @return true if so, false if not.
	 */
	public boolean isGamepadAxisTapEvent()
	{
		return inputType == InputType.GAMEPAD_AXIS_TAP;
	}
	
	/**
	 * Is this event an object event?
	 * @return true if so, false if not.
	 */
	public boolean isObjectEvent()
	{
		return inputType == InputType.NONE;
	}
	
	/**
	 * Returns the key modifier mask associated with this event.
	 * If this originated from SWING, this is an {@link InputEvent} MASK bitmask.
	 * @see InputEvent
	 */
	public int getKeyModifier()
	{
		return imod;
	}
	
	/**
	 * Returns the key code associated with this event, if this was a keyboard event.
	 * If this originated from SWING, this is a VK constant.
	 * If this is NOT a keyboard event, this returns -1.
	 * @see KeyEvent
	 */
	public int getKeyCode()
	{
		if (!isKeyboardEvent())
			return -1;
		return iarg0;
	}
	
	/**
	 * Returns the mouse button associated with this event, if this was a mouse event.
	 * If this originated from SWING, this is a MOUSE_BUTTON constant.
	 * If this is NOT a keyboard event, this returns -1.
	 * @see MouseEvent
	 */
	public int getMouseButton()
	{
		if (!isMouseEvent())
			return -1;
		return iarg0;
	}

	/**
	 * Returns the canvas mouse cursor position, x-axis, associated with this event, if this was a mouse event.
	 * This is in units native to the canvas, not the GUI system!
	 */
	public float getMouseCanvasPositionX()
	{
		if (!isMouseEvent())
			return -1f;
		return farg0;
	}
	
	/**
	 * Returns the canvas mouse cursor position, y-axis, associated with this event, if this was a mouse event.
	 * This is in units native to the canvas, not the GUI system!
	 */
	public float getMouseCanvasPositionY()
	{
		if (!isMouseEvent())
			return -1f;
		return farg1;
	}
	
	/**
	 * Returns the mouse cursor position inside the current object that it is over, 
	 * x-axis, associated with this event, if this was a mouse event.
	 * This is in units native to the GUI system!
	 */
	public float getMouseObjectPositionX()
	{
		if (!isMouseEvent())
			return -1f;
		return farg2;
	}
	
	/**
	 * Returns the mouse cursor position inside the current object that it is over, 
	 * y-axis, associated with this event, if this was a mouse event.
	 * This is in units native to the GUI system!
	 */
	public float getMouseObjectPositionY()
	{
		if (!isMouseEvent())
			return -1f;
		return farg3;
	}

	/**
	 * Returns the mouse cursor position, x-axis, associated with this event, if this was a mouse event.
	 * This is in units native to the GUI system!
	 */
	public float getMousePositionX()
	{
		if (!isMouseEvent())
			return -1f;
		return farg4;
	}
	
	/**
	 * Returns the mouse cursor position, y-axis, associated with this event, if this was a mouse event.
	 * This is in units native to the GUI system!
	 */
	public float getMousePositionY()
	{
		if (!isMouseEvent())
			return -1f;
		return farg5;
	}
	
	/**
	 * Returns the amount of units that the mouse moved, x-axis, associated with this event, if this was a mouse event.
	 * This is in units native to the GUI system!
	 */
	public float getMouseMovementX()
	{
		if (!isMouseEvent())
			return -1f;
		return farg6;
	}
	
	/**
	 * Returns the amount of units that the mouse moved, y-axis, associated with this event, if this was a mouse event.
	 * This is in units native to the GUI system!
	 */
	public float getMouseMovementY()
	{
		if (!isMouseEvent())
			return -1f;
		return farg7;
	}

	/**
	 * Returns the amount of units that the mouse wheel moved, if this is a mouse wheel event.
	 * If this is NOT a mouse wheel event, this returns 0.
	 */
	public int getMouseWheelMovement()
	{
		if (!isMouseWheelEvent())
			return 0;
		return iarg0;
	}
	
	/**
	 * Returns the id of the controller that this event happened on.
	 * If this is NOT a gamepad event, the returns -1.
	 */
	public int getGamepadId()
	{
		if (!isGamepadEvent() && !isGamepadAxisEvent() && !isGamepadAxisTapEvent())
			return -1;
		return iarg0;
	}
	
	/**
	 * Returns the axis on the controller that this event happened on.
	 * If this is NOT a gamepad event, the returns 0.
	 */
	public int getGamepadAxisId()
	{
		if (!isGamepadAxisEvent() && !isGamepadAxisTapEvent())
			return AXIS_UNDEFINED;
		return iarg1;
	}
	
	/**
	 * Returns the axis on the controller that this event happened on.
	 * If this is NOT a gamepad event, the returns 0.
	 */
	public int getGamepadButton()
	{
		if (!isGamepadEvent())
			return GAMEPAD_UNDEFINED;
		return iarg1;
	}
	
	/**
	 * Returns the value on the controller axis that this event happened on.
	 * If this is NOT a gamepad axis event, the returns 0f.
	 */
	public float getGamepadAxisValue()
	{
		if (!isGamepadAxisEvent())
			return 0f;
		return farg0;
	}
	
	/**
	 * Returns the positivity on the controller axis that a tap event happened on.
	 * If this is NOT a gamepad axis tap event, the returns false.
	 */
	public boolean getGamepadAxisTapValue()
	{
		if (!isGamepadAxisTapEvent())
			return false;
		return farg0 > 0f;
	}
	
	/** Sets the GUI reference. */
	void setGUI(OGLGUI gui)
	{
		this.gui = gui;
	}
	
	/** Sets the object reference. */
	void setObject(OGLGUIObject object)
	{
		this.object = object;
	}
	
	/** Sets the type. */
	void setType(String type)
	{
		this.type = type;
	}
	
	/** Sets the origin. */
	void setOrigin(Origin origin)
	{
		this.origin = origin;
	}
	
	/** Sets the key modifier. */
	void setKeyModifier(int keyModifier)
	{
		this.imod = keyModifier;
	}
	
	/** Sets the key code. */
	void setKeyCode(int keyCode)
	{
		inputType = InputType.KEYBOARD;
		this.iarg0 = keyCode;
	}
	
	/** Sets the mouse button. */
	void setMouseButton(int button)
	{
		inputType = InputType.MOUSE;
		this.iarg0 = button;
	}
	
	/** Sets the mouse wheel movement units. */
	void setMouseWheelUnits(int units)
	{
		inputType = InputType.MOUSE_WHEEL;
		this.iarg0 = units;
	}

	/**
	 * Sets the mouse movement/location units. 
	 * @param canvasX canvas position X in canvas units.
	 * @param canvasY canvas position Y in canvas units.
	 * @param objectX object position X in GUI units (camera).
	 * @param objectY object position Y in GUI units (camera).
	 * @param positionX position X in GUI units (camera).
	 * @param positionY position Y in GUI units (camera).
	 * @param moveX mouse movement X in GUI units (camera).
	 * @param moveY mouse movement Y in GUI units (camera).
	 */
	void setMouseUnits(
		float canvasX, float canvasY, float objectX, float objectY,
		float positionX, float positionY, float moveX, float moveY)
	{
		inputType = InputType.MOUSE;
		farg0 = canvasX;
		farg1 = canvasY;
		farg2 = objectX;
		farg3 = objectY;
		farg4 = positionX;
		farg5 = positionY;
		farg6 = moveX;
		farg7 = moveY;
	}
	
	/**
	 * Sets the gamepad button.
	 * @param gamepadId the id of the gamepad that this happened on.
	 * @param gamepadButton the gamepad button pressed.
	 */
	void setGamepadButton(int gamepadId, int gamepadButton)
	{
		inputType = InputType.GAMEPAD;
		iarg0 = gamepadId;
		iarg1 = gamepadButton;
	}
	
	/**
	 * Sets the gamepad axis value.
	 * @param gamepadId the id of the gamepad that this happened on.
	 * @param gamepadAxisId the axis id.
	 * @param value the new axis value.
	 */
	void setGamepadAxes(int gamepadId, int gamepadAxisId, float value)
	{
		inputType = InputType.GAMEPAD_AXIS;
		iarg0 = gamepadId;
		iarg1 = gamepadAxisId;
		farg0 = value;
	}
	
	/**
	 * Sets the gamepad axis value.
	 * @param gamepadId the id of the gamepad that this happened on.
	 * @param gamepadAxisId the axis id.
	 * @param positive if true, tap was at positive axis.
	 */
	void setGamepadAxisTap(int gamepadId, int gamepadAxisId, boolean positive)
	{
		inputType = InputType.GAMEPAD_AXIS_TAP;
		iarg0 = gamepadId;
		iarg1 = gamepadAxisId;
		farg0 = positive ? 1f : -1f;
	}
	
}
