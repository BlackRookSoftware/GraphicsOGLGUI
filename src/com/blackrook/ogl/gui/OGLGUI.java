/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui;

import java.util.Iterator;

import javax.swing.KeyStroke;

import com.blackrook.commons.ObjectPair;
import com.blackrook.commons.ResettableIterator;
import com.blackrook.commons.hash.HashMap;
import com.blackrook.commons.hash.HashedQueueMap;
import com.blackrook.commons.linkedlist.Queue;
import com.blackrook.commons.list.List;
import com.blackrook.commons.math.geometry.Rectangle2F;
import com.blackrook.ogl.OGLGraphics;
import com.blackrook.ogl.gui.OGLGUIEvent.Origin;
import com.blackrook.ogl.gui.input.GUIKeyStroke;
import com.blackrook.ogl.util.OGL2DCamera;
import com.blackrook.ogl.util.OGLSkin;
import com.blackrook.ogl.util.OGLResourceLoader;
import com.blackrook.ogl.util.enums.EasingType;
import com.blackrook.ogl.util.scene2d.OGLScene2DNode;

/**
 * 2D scene for GUI stuff.
 * @author Matthew Tropiano
 */
public class OGLGUI extends OGLScene2DNode<OGLGUIObject>
{
	private static final int MOUSE_CANVASX = 0; 
	private static final int MOUSE_CANVASY = 1; 
	private static final int MOUSE_OBJECTX = 2; 
	private static final int MOUSE_OBJECTY = 3; 
	private static final int MOUSE_POSITIONX = 4; 
	private static final int MOUSE_POSITIONY = 5; 
	private static final int MOUSE_MOVEX = 6; 
	private static final int MOUSE_MOVEY = 7; 
	
	/** Is this layer enabled (responds to input)? */
	private boolean acceptsInput;
	/** If true, this updates animations during redraw, not independently. */
	private boolean updateAnimationsDuringDisplay;
	
	/** Current moused-over object. */
	private OGLGUIObject objectMouseOver;
	/** Current object the mouse button is pressed on. */
	private OGLGUIObject objectMouseDown;
	/** Current pressed mouse button. */
	private int objectMouseDownButton;
	/** Current "drag state." */
	private boolean objectMouseDragState;
	/** Current focused object. */
	private OGLGUIObject objectFocus;
	
	/** GUI Theme. */
	private OGLGUITheme theme;
	
	/** Mouse coordinates. */
	private float[] calculatedMouseCoordinates;
	
	/** The action queue. */
	private HashMap<OGLGUIObject, OGLAnimationQueue<OGLGUIObject>> actionQueueMap;
	/** The action iterator. */
	private ResettableIterator<ObjectPair<OGLGUIObject, OGLAnimationQueue<OGLGUIObject>>> actionQueueIterator;

	/** List of broadcast actions - keyboard. */
	private HashedQueueMap<GUIKeyStroke, OGLGUIAction> keyBroadcastMap;

	/** Current modifiers for keys (used with broadcast). */
	private int currentKeyModifiers;
	
	/** Render count "pointer." */
	private int renderCount;
	
	/** List of Root GUI Objects. */
	private List<OGLGUIObject> rootObjects;

	/**
	 * Creates a new OGLGUIScene instance to be bound to a graphics system.
	 * Requires a resource loader. 
	 */
	public OGLGUI(OGLResourceLoader loader)
	{
		this(loader, new OGL2DCamera());
	}
	
	/**
	 * Creates a new OGLGUIScene instance to be bound to a graphics system.
	 * Requires a resource loader and camera instance.
	 * This GUI's camera listener is automatically added to the camera.
	 */
	public OGLGUI(OGLResourceLoader loader, OGL2DCamera camera)
	{
		super(loader, camera);
		acceptsInput = true;
		updateAnimationsDuringDisplay = true;
		objectFocus = null;
		objectMouseOver = null;
		objectMouseDown = null;
		objectMouseDownButton = 0;
		objectMouseDragState = false;
		calculatedMouseCoordinates = new float[8];
		rootObjects = new List<OGLGUIObject>(5);
		renderCount = 0;
		actionQueueMap = new HashMap<OGLGUIObject, OGLAnimationQueue<OGLGUIObject>>();
		keyBroadcastMap = new HashedQueueMap<GUIKeyStroke, OGLGUIAction>(4);
		theme = null;
	}

	/**
	 * Adds a GUI Object to this GUI (plus children).
	 */
	public synchronized void addObject(OGLGUIObject obj)
	{
		obj.setGUI(this);
		rootObjects.add(obj);
		addObjectTree(obj);
	}
	
	/**
	 * Adds the object tree to the GUI.
	 */
	synchronized void addObjectTree(OGLGUIObject obj)
	{
		super.addObject(obj);
		for (OGLGUIObject child : obj.getChildren())
		{
			child.setGUI(this);
			super.addObject(child);
			addObjectTree(child);
		}
	}

	/**
	 * Removes a GUI Object from this GUI (plus children).
	 */
	public synchronized boolean removeObject(OGLGUIObject obj)
	{
		if (super.removeObject(obj))
		{
			obj.setGUI(null);
			rootObjects.remove(obj);
			removeObjectTree(obj);
			return true;
		}
		return false;
	}

	/**
	 * Removes an the object tree from the GUI.
	 */
	synchronized void removeObjectTree(OGLGUIObject obj)
	{
		super.removeObject(obj);
		for (OGLGUIObject child : obj.getChildren())
		{
			obj.setGUI(null);
			super.removeObject(child);
			removeObjectTree(child);
		}
	}

	/**
	 * Gets the root objects.
	 */
	List<OGLGUIObject> getRootObjects()
	{
		return rootObjects;
	}

	/**
	 * Adds an action to the action queue.
	 * @param object the object that called this request.
	 * @param duration the duration of the actions in milliseconds.
	 * @param type the transition/easing type.
	 * @param actions the action to add.
	 */
	void addAnimation(OGLGUIObject object, float duration, EasingType type, OGLGUIAnimation ... actions)
	{
		synchronized(actionQueueMap)
		{
			OGLAnimationQueue<OGLGUIObject> animQueue = actionQueueMap.get(object);
			if (animQueue == null)
			{
				animQueue = new OGLAnimationQueue<OGLGUIObject>(object);
				actionQueueMap.put(object, animQueue);
			}
			animQueue.add(duration, type, actions);
		}
	}

	/**
	 * Adds an action to the action queue.
	 * @param object the object that called this request.
	 * @param complete if true, completes the animation.
	 */
	void endAnimation(OGLGUIObject object, boolean complete)
	{
		synchronized(actionQueueMap)
		{
			OGLAnimationQueue<OGLGUIObject> animQueue = actionQueueMap.get(object);
			if (animQueue != null)
			{
				if (complete)
					animQueue.finish();
				else
					animQueue.abort();
				actionQueueMap.removeUsingKey(object);
			}
		}
	}

	/**
	 * Gets if this GUI accepts input.
	 * If this is true, then this GUI can receive 
	 * input from the keyboard and mouse. If false,
	 * All objects in this GUI will not respond to input.
	 */
	public boolean isAcceptingInput()
	{
		return acceptsInput;
	}
	
	/**
	 * Sets if this GUI accepts input.
	 * If this is true, then this GUI can receive 
	 * input from the keyboard and mouse. If false,
	 * All objects in this GUI will not respond to input.
	 */
	public void setAcceptingInput(boolean acceptsInput)
	{
		this.acceptsInput = acceptsInput;
	}

	/**
	 * Adds a broadcast key event to this GUI.
	 * All keystrokes made to this GUI perform the associated {@link KeyStroke}.
	 * Actions are called in the order that they were bound. 
	 * @param keystroke the keystroke to add.
	 */
	public void bindBroadcastAction(GUIKeyStroke keystroke, OGLGUIAction action)
	{
		keyBroadcastMap.enqueue(keystroke, action);
	}
	
	/**
	 * Removes a specific broadcast key event from this GUI.
	 * @param keystroke the keystroke to remove.
	 * @return true if removed successfully, false otherwise.
	 */
	public boolean unbindBroadcastAction(GUIKeyStroke keystroke, OGLGUIAction action)
	{
		return keyBroadcastMap.removeValue(keystroke, action);
	}
	
	/**
	 * Removes all specific broadcast key events from this GUI for a keystroke.
	 * @param keystroke the keystroke to remove.
	 * @return true if removed successfully, false otherwise.
	 */
	public boolean unbindAllBroadcastActions(GUIKeyStroke keystroke)
	{
		return keyBroadcastMap.removeUsingKey(keystroke) != null;
	}
	
	/**
	 * Removes all specific broadcast key events from this GUI for a keystroke.
	 */
	public void unbindAllBroadcastActions()
	{
		keyBroadcastMap.clear();
	}
	
	/**
	 * Requests an object focus and fires object events.
	 * Fires events if and only if <code>object</code> is not
	 * the currently-focused object.
	 */
	public void requestObjectFocus(OGLGUIObject object)
	{
		if (object != objectFocus)
		{
			if (objectFocus != null)
				fireGUIEvent(objectFocus, OGLGUIObject.EVENT_BLUR);
			
			objectFocus = object;
		
			if (objectFocus != null)
				fireGUIEvent(objectFocus, OGLGUIObject.EVENT_FOCUS);
		}
	}
	
	/**
	 * Requests that an object be unfocused, but only if it
	 * was the one currently in focus. 
	 */
	public void requestObjectUnfocus(OGLGUIObject object)
	{
		if (getFocusedObject() == object)
			requestObjectFocus(null);
	}
	
	/**
	 * Returns the object that currently has focus in this GUI.
	 * Can be null if no object has focus.
	 */
	public OGLGUIObject getFocusedObject()
	{
		return objectFocus;
	}
	
	/**
	 * Returns the current theme used by the GUI.
	 * If no current theme, this returns null.
	 */
	public OGLGUITheme getTheme()
	{
		return theme;
	}
	
	/**
	 * Sets the theme used by all objects.
	 * @param theme
	 */
	public void setTheme(OGLGUITheme theme)
	{
		this.theme = theme;
	}
	
	/**
	 * Gets all GUI Objects. 
	 * @return a query result.
	 */
	public synchronized OGLGUIQuery getAll()
	{
		OGLGUIQuery out = new OGLGUIQuery();
		for (OGLGUIObject obj : rootObjects)
			obj.getAllInTree(out);
		return out;
	}
	
	/**
	 * Gets all GUI Objects. 
	 * @return a query result.
	 */
	public synchronized OGLGUIQuery getOnCamera()
	{
		OGLGUIQuery out = new OGLGUIQuery();
		for (OGLGUIObject obj : sceneObjects)
			if (isOnCamera(obj)) 
				out.add(obj);
		return out;
	}
	
	/**
	 * Gets all GUI Objects that are in an animation. 
	 * @return a query result.
	 */
	public synchronized OGLGUIQuery getAnimating()
	{
		OGLGUIQuery out = new OGLGUIQuery();
		synchronized (actionQueueMap)
		{
			Iterator<OGLGUIObject> it = actionQueueMap.keyIterator(); 
			while (it.hasNext())
				out.add(it.next());
		}
		return out;
	}
	
	/**
	 * Gets all GUI Objects with a matching name. 
	 * @param name the name to use.
	 * @return a query result.
	 */
	public synchronized OGLGUIQuery getByName(String name)
	{
		OGLGUIQuery out = new OGLGUIQuery();
		for (OGLGUIObject obj : rootObjects)
			obj.getNameMatchesInTree(name, out);
		return out;
	}
	
	/**
	 * Gets all GUI Objects with a matching name regex pattern. 
	 * @param name the name to use.
	 * @return a query result.
	 */
	public synchronized OGLGUIQuery getByPattern(String name)
	{
		OGLGUIQuery out = new OGLGUIQuery();
		for (OGLGUIObject obj : rootObjects)
			obj.getNamePatternMatchesInTree(name, out);
		return out;
	}
	
	/**
	 * Gets all GUI Objects with a matching name.
	 * @param clazz the class type to search on. 
	 * @return a query result.
	 */
	public synchronized OGLGUIQuery getByType(Class<?> clazz)
	{
		OGLGUIQuery out = new OGLGUIQuery();
		for (OGLGUIObject obj : rootObjects)
			obj.getTypeMatchesInTree(clazz, out);
		return out;
	}
	
	/**
	 * This is always flipped Y. 
	 * For this object, this always returns true.
	 */
	public boolean getFlipY()
	{
		return true;
	}
	
	@Override
	public boolean glKeyPress(int keycode)
	{
		if (!isAcceptingInput())
			return false;
		
		setKeyMask(keycode, false);
		
		boolean handled = fireBroadcastKeyEvent(keycode, false);
		
		if (objectFocus != null)
		{
			fireKeyboardEvent(objectFocus, OGLGUIObject.EVENT_KEY_PRESS, keycode);
			handled = true;
		}
		return handled;
	}
	
	@Override
	public boolean glKeyRelease(int keycode)
	{
		if (!isAcceptingInput())
			return false;

		setKeyMask(keycode, true);

		boolean handled = fireBroadcastKeyEvent(keycode, true);

		if (objectFocus != null)
		{
			fireKeyboardEvent(objectFocus, OGLGUIObject.EVENT_KEY_RELEASE, keycode);
			handled = true;
		}
		return handled;
	}
	
	@Override
	public boolean glKeyTyped(int keycode)
	{
		if (!isAcceptingInput())
			return false;
		
		if (objectFocus != null)
		{
			fireKeyboardEvent(objectFocus, OGLGUIObject.EVENT_KEY_TYPE, keycode);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean glGamepadPress(int gamepadId, int gamepadButton)
	{
		if (!isAcceptingInput())
			return false;
		
		boolean handled = fireBroadcastGamepadButtonEvent(gamepadId, gamepadButton, false);

		if (objectFocus != null)
		{
			fireGamepadEvent(objectFocus, OGLGUIObject.EVENT_GAMEPAD_PRESS, gamepadId, gamepadButton);
			handled = true;
		}
		
		return handled;
	}

	@Override
	public boolean glGamepadRelease(int gamepadId, int gamepadButton)
	{
		if (!isAcceptingInput())
			return false;
		
		boolean handled = fireBroadcastGamepadButtonEvent(gamepadId, gamepadButton, true);

		if (objectFocus != null)
		{
			fireGamepadEvent(objectFocus, OGLGUIObject.EVENT_GAMEPAD_RELEASE, gamepadId, gamepadButton);
			handled = true;
		}
		
		return handled;
	}

	@Override
	public boolean glGamepadAxisChange(int gamepadId, int gamepadAxisId, float value)
	{
		if (!isAcceptingInput())
			return false;
		
		if (objectFocus != null)
		{
			fireGamepadAxisEvent(objectFocus, OGLGUIObject.EVENT_GAMEPAD_AXIS, gamepadId, gamepadAxisId, value);
			return true;
		}
		return false;
	}

	@Override
	public boolean glGamepadAxisTap(int gamepadId, int gamepadAxisId, boolean position)
	{
		if (!isAcceptingInput())
			return false;
		
		boolean handled = fireBroadcastGamepadTapEvent(gamepadId, gamepadAxisId, position);

		if (objectFocus != null)
		{
			fireGamepadTapEvent(objectFocus, OGLGUIObject.EVENT_GAMEPAD_TAP, gamepadId, gamepadAxisId, position);
			handled = true;
		}
		
		return handled;
	}

	@Override
	public boolean glMousePress(int mousebutton)
	{
		if (!isAcceptingInput())
			return false;
		
		objectMouseDragState = false;
		
		if (objectMouseOver != null)
		{
			fireMouseEvent(objectMouseOver, OGLGUIObject.EVENT_MOUSE_PRESS, mousebutton);
			objectMouseDown = objectMouseOver;
			objectMouseDownButton = mousebutton;
			return true;
		}
		objectMouseDown = null;
		return false;
	}
	
	@Override
	public boolean glMouseRelease(int mousebutton)
	{
		if (!isAcceptingInput())
			return false;
		
		if (objectMouseDown != null)
		{
			if (objectMouseOver != null)
				fireMouseEvent(objectMouseOver, OGLGUIObject.EVENT_MOUSE_RELEASE, mousebutton);
			if (objectMouseDown == objectMouseOver && !objectMouseDragState)
				fireMouseEvent(objectMouseDown, OGLGUIObject.EVENT_MOUSE_CLICK, mousebutton);
			objectMouseDown = null;
			objectMouseDownButton = -1;
			objectMouseDragState = false;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean glMouseWheel(int units)
	{
		if (!isAcceptingInput())
			return false;
		
		if (objectFocus != null)
			fireMouseWheelEvent(objectFocus, units);
		return false;
	}
	
	@Override
	public void glMouseMove(int unitsX, int coordinateX, int unitsY, int coordinateY)
	{
		if (!isAcceptingInput()) 
			return;

		super.glMouseMove(unitsX, coordinateX, unitsY, coordinateY);
		setMouseCoordinates(unitsX, coordinateX, unitsY, coordinateY, calculatedMouseCoordinates);
		
		if (objectMouseDown != null)
		{
			objectMouseDragState = objectMouseDown.hasAction(OGLGUIObject.EVENT_MOUSE_DRAG);
			if (objectMouseDragState)
				fireMouseEvent(objectMouseDown, OGLGUIObject.EVENT_MOUSE_DRAG, objectMouseDownButton);
		}
		else if (objectMouseOver != null)
			fireMouseEvent(objectMouseOver, OGLGUIObject.EVENT_MOUSE_MOVE);
	}

	@Override
	public void glMouseExit()
	{
		if (objectMouseOver != null)
			fireMouseEvent(objectMouseOver, OGLGUIObject.EVENT_MOUSE_LEAVE);
	
		objectMouseOver = null;
	}
	
	/**
	 * Updates all of the animation nodes in the GUI by an amount of time.
	 * @param millis amount of milliseconds.
	 */
	public void updateAnimations(float millis)
	{
		if (millis == 0f) return;
		
		synchronized (actionQueueMap)
		{
			if (actionQueueIterator == null)
				actionQueueIterator = actionQueueMap.iterator();
			else
				actionQueueIterator.reset();
			
			while (actionQueueIterator.hasNext())
			{
				ObjectPair<OGLGUIObject, OGLAnimationQueue<OGLGUIObject>> pair = actionQueueIterator.next();
				pair.getValue().update(millis);
				if (pair.getValue().isDone())
					actionQueueIterator.remove();
			}
		}
	}

	/**
	 * Sets if this GUI updates its animations automatically via {@link #display(OGLGraphics)}
	 * calling {@link #updateAnimations(float)} with {@link OGLGraphics#currentTimeStepMillis()} as the
	 * argument. 
	 * <p>If true, this does. If false, {@link #updateAnimations(float)} must be called independently
	 * to update animations.
	 * <p> By default, this is true.
	 * @param value the new value.
	 */
	public void setUpdateAnimationsDuringDisplay(boolean value)
	{
		updateAnimationsDuringDisplay = value;
	}
	
	/**
	 * Calculates mouse coordinates for events.
	 */
	private void setMouseCoordinates(int unitsX, int coordinateX, int unitsY, int coordinateY, float[] output)
	{
		float widthScale = camera.getWidth() / canvasWidth;
		float heightScale = camera.getHeight() / canvasHeight;

		output[MOUSE_MOVEX] = widthScale * unitsX;
		output[MOUSE_MOVEY] = heightScale * unitsY;

		output[MOUSE_CANVASX] = coordinateX;
		output[MOUSE_CANVASY] = coordinateY;

		output[MOUSE_POSITIONX] = getMousePositionX();
		output[MOUSE_POSITIONY] = getMousePositionY();
		
		OGLGUIObject obj = objectMouseDown != null 
			? objectMouseDown
			: (objectMouseOver != null ? objectMouseOver : null);
		
		output[MOUSE_OBJECTX] = -1f;
		output[MOUSE_OBJECTY] = -1f;
		if (obj != null)
		{
			Rectangle2F rect = obj.getBounds();
			output[MOUSE_OBJECTX] = output[MOUSE_POSITIONX] - rect.x; 
			output[MOUSE_OBJECTY] = output[MOUSE_POSITIONY] - rect.y; 
		}
		
	}
	
	/**
	 * Fires a GUI-specific event.
	 * @param object the object central to the event.
	 */
	void fireGUIEvent(OGLGUIObject object, String type)
	{
		OGLGUIEvent guiEvent = new OGLGUIEvent();
		guiEvent.setGUI(this);
		guiEvent.setObject(object);
		guiEvent.setType(type);
		guiEvent.setOrigin(Origin.GUI);
		object.callEvent(guiEvent);
	}
	
	/**
	 * Returns true if the object is on camera.
	 */
	final boolean isOnCamera(OGLGUIObject object)
	{
		return objectIsOnCamera(object);
	}

	/**
	 * Returns true if the object is in the middle of an animation.
	 */
	final boolean isAnimating(OGLGUIObject object)
	{
		boolean out = false;
		synchronized (actionQueueMap)
		{
			out = actionQueueMap.containsKey(object);
		}
		return out;
	}

	/**
	 * Sets/releases a key mask, if it is a maskable key (CTRL, SHIFT, ALT, ALTGRAPH, MASK).
	 * @param keycode the input keycode.
	 * @param release true if key released, false otherwise.
	 */
	protected void setKeyMask(int keycode, boolean release)
	{
		if (release) switch (keycode)
		{
			case KEY_CONTROL:
				currentKeyModifiers &= ~GUIKeyStroke.MASK_CTRL; 
				break;
			case KEY_ALT:
				currentKeyModifiers &= ~GUIKeyStroke.MASK_ALT; 
				break;
			case KEY_ALT_GRAPH:
				currentKeyModifiers &= ~GUIKeyStroke.MASK_ALT_GRAPH; 
				break;
			case KEY_META:
				currentKeyModifiers &= ~GUIKeyStroke.MASK_META; 
				break;
			case KEY_SHIFT:
				currentKeyModifiers &= ~GUIKeyStroke.MASK_SHIFT; 
				break;
			case KEY_WINDOWS:
				currentKeyModifiers &= ~GUIKeyStroke.MASK_WIN; 
				break;
		}
		else switch (keycode)
		{
			case KEY_CONTROL:
				currentKeyModifiers |= GUIKeyStroke.MASK_CTRL; 
				break;
			case KEY_ALT:
				currentKeyModifiers |= GUIKeyStroke.MASK_ALT; 
				break;
			case KEY_ALT_GRAPH:
				currentKeyModifiers |= GUIKeyStroke.MASK_ALT_GRAPH; 
				break;
			case KEY_META:
				currentKeyModifiers |= GUIKeyStroke.MASK_META; 
				break;
			case KEY_SHIFT:
				currentKeyModifiers |= GUIKeyStroke.MASK_SHIFT; 
				break;
			case KEY_WINDOWS:
				currentKeyModifiers |= GUIKeyStroke.MASK_WIN; 
				break;
		}
		
	}
	
	/**
	 * Fires a keyboard typed event.
	 * @param object the object central to the event.
	 */
	protected void fireKeyboardEvent(OGLGUIObject object, String type, int keyCode)
	{
		OGLGUIEvent guiEvent = new OGLGUIEvent();
		guiEvent.setGUI(this);
		guiEvent.setObject(object);
		guiEvent.setType(type);
		guiEvent.setOrigin(Origin.INPUT);
		guiEvent.setKeyModifier(currentKeyModifiers);
		guiEvent.setKeyCode(keyCode);
		object.callEvent(guiEvent);
	}
	
	/**
	 * Fires a gamepad pressed event.
	 * @param object the object central to the event.
	 */
	protected void fireGamepadEvent(OGLGUIObject object, String type, int gamepadId, int buttonCode)
	{
		OGLGUIEvent guiEvent = new OGLGUIEvent();
		guiEvent.setGUI(this);
		guiEvent.setObject(object);
		guiEvent.setType(type);
		guiEvent.setOrigin(Origin.INPUT);
		guiEvent.setGamepadButton(gamepadId, buttonCode);
		object.callEvent(guiEvent);
	}
	
	/**
	 * Fires a gamepad axis change event.
	 * @param object the object central to the event.
	 */
	protected void fireGamepadAxisEvent(OGLGUIObject object, String type, int gamepadId, int axisTypeId, float value)
	{
		OGLGUIEvent guiEvent = new OGLGUIEvent();
		guiEvent.setGUI(this);
		guiEvent.setObject(object);
		guiEvent.setType(type);
		guiEvent.setOrigin(Origin.INPUT);
		guiEvent.setGamepadAxes(gamepadId, axisTypeId, value);
		object.callEvent(guiEvent);
	}
	
	/**
	 * Fires a gamepad axis change event.
	 * @param object the object central to the event.
	 */
	protected void fireGamepadTapEvent(OGLGUIObject object, String type, int gamepadId, int axisTypeId, boolean positive)
	{
		OGLGUIEvent guiEvent = new OGLGUIEvent();
		guiEvent.setGUI(this);
		guiEvent.setObject(object);
		guiEvent.setType(type);
		guiEvent.setOrigin(Origin.INPUT);
		guiEvent.setGamepadAxisTap(gamepadId, axisTypeId, positive);
		object.callEvent(guiEvent);
	}
	
	/**
	 * Fires a broadcast key event.
	 * @param keycode the keycode used.
	 * @param release true if release, false if not.
	 * @return true if a broadcast event was sent, false if not.
	 */
	protected boolean fireBroadcastKeyEvent(int keycode, boolean release)
	{
		OGLGUIEvent guiEvent = new OGLGUIEvent();
		guiEvent.setGUI(this);
		guiEvent.setObject(null);
		guiEvent.setType(release ? OGLGUIObject.EVENT_KEY_RELEASE : OGLGUIObject.EVENT_KEY_PRESS);
		guiEvent.setOrigin(Origin.INPUT);
		guiEvent.setKeyModifier(currentKeyModifiers);
		guiEvent.setKeyCode(keycode);
		
		Queue<OGLGUIAction> queue = keyBroadcastMap.get(GUIKeyStroke.createKey(currentKeyModifiers, keycode, release));
		if (queue != null && !queue.isEmpty()) 
		{
			for (OGLGUIAction action : queue)
				action.call(guiEvent);
			return true;
		}
		return false;
	}
	
	/**
	 * Fires a broadcast gamepad button event.
	 * @param id the gamepad id used.
	 * @param button the button used.
	 * @param release true if release, false if not.
	 * @return true if a broadcast event was sent, false if not.
	 */
	protected boolean fireBroadcastGamepadButtonEvent(int id, int button, boolean release)
	{
		OGLGUIEvent guiEvent = new OGLGUIEvent();
		guiEvent.setGUI(this);
		guiEvent.setObject(null);
		guiEvent.setType(release ? OGLGUIObject.EVENT_GAMEPAD_RELEASE : OGLGUIObject.EVENT_GAMEPAD_PRESS);
		guiEvent.setOrigin(Origin.INPUT);
		guiEvent.setGamepadButton(id, button);
		
		Queue<OGLGUIAction> queue = keyBroadcastMap.get(GUIKeyStroke.createGamepad(currentKeyModifiers, button, release));
		if (queue != null && !queue.isEmpty()) 
		{
			for (OGLGUIAction action : queue)
				action.call(guiEvent);
			return true;
		}
		return false;
	}
	
	/**
	 * Fires a broadcast gamepad axis tap event.
	 * @param id the gamepad id used.
	 * @param axisId the axis used.
	 * @param positive true if axis tap was in a "positive" valued direction, false if not.
	 * @return true if positive axis, false if negative.
	 */
	protected boolean fireBroadcastGamepadTapEvent(int id, int axisId, boolean positive)
	{
		OGLGUIEvent guiEvent = new OGLGUIEvent();
		guiEvent.setGUI(this);
		guiEvent.setObject(null);
		guiEvent.setType(OGLGUIObject.EVENT_GAMEPAD_TAP);
		guiEvent.setOrigin(Origin.INPUT);
		guiEvent.setGamepadAxisTap(id, axisId, positive);
		
		Queue<OGLGUIAction> queue = keyBroadcastMap.get(GUIKeyStroke.createGamepadTap(axisId, positive));
		if (queue != null && !queue.isEmpty()) 
		{
			for (OGLGUIAction action : queue)
				action.call(guiEvent);
			return true;
		}
		return false;
	}
	
	/**
	 * Fires a mouse pressed event.
	 * @param object the object central to the event.
	 */
	protected void fireMouseEvent(OGLGUIObject object, String type)
	{
		OGLGUIEvent guiEvent = new OGLGUIEvent();
		guiEvent.setGUI(this);
		guiEvent.setObject(object);
		guiEvent.setType(type);
		guiEvent.setOrigin(Origin.INPUT);
		guiEvent.setKeyModifier(currentKeyModifiers);
		guiEvent.setMouseUnits(
			calculatedMouseCoordinates[MOUSE_CANVASX], 
			calculatedMouseCoordinates[MOUSE_CANVASY], 
			calculatedMouseCoordinates[MOUSE_OBJECTX], 
			calculatedMouseCoordinates[MOUSE_OBJECTY], 
			calculatedMouseCoordinates[MOUSE_POSITIONX], 
			calculatedMouseCoordinates[MOUSE_POSITIONY], 
			calculatedMouseCoordinates[MOUSE_MOVEX], 
			calculatedMouseCoordinates[MOUSE_MOVEY]
			);
		object.callEvent(guiEvent);
	}
	
	/**
	 * Fires a mouse pressed event.
	 * @param object the object central to the event.
	 */
	protected void fireMouseEvent(OGLGUIObject object, String type, int button)
	{
		OGLGUIEvent guiEvent = new OGLGUIEvent();
		guiEvent.setGUI(this);
		guiEvent.setObject(object);
		guiEvent.setType(type);
		guiEvent.setOrigin(Origin.INPUT);
		guiEvent.setKeyModifier(currentKeyModifiers);
		guiEvent.setMouseButton(button);
		guiEvent.setMouseUnits(
			calculatedMouseCoordinates[MOUSE_CANVASX], 
			calculatedMouseCoordinates[MOUSE_CANVASY], 
			calculatedMouseCoordinates[MOUSE_OBJECTX], 
			calculatedMouseCoordinates[MOUSE_OBJECTY], 
			calculatedMouseCoordinates[MOUSE_POSITIONX], 
			calculatedMouseCoordinates[MOUSE_POSITIONY], 
			calculatedMouseCoordinates[MOUSE_MOVEX], 
			calculatedMouseCoordinates[MOUSE_MOVEY]
			);
		object.callEvent(guiEvent);
	}
	
	/**
	 * Fires a mouse pressed event.
	 * @param object the object central to the event.
	 */
	protected void fireMouseWheelEvent(OGLGUIObject object, int units)
	{
		OGLGUIEvent guiEvent = new OGLGUIEvent();
		guiEvent.setGUI(this);
		guiEvent.setObject(object);
		guiEvent.setOrigin(Origin.INPUT);
		guiEvent.setKeyModifier(currentKeyModifiers);
		guiEvent.setMouseWheelUnits(units);
		object.callEvent(guiEvent);
	}

	@Override
	public synchronized void display(OGLGraphics g)
	{
		if (updateAnimationsDuringDisplay)
			updateAnimations(g.currentTimeStepMillis());
		
		super.display(g);
	}

	/**
	 * Updates objects affected by mouse movement.
	 */
	protected void updateObjectEvents()
	{
		OGLGUIObject finalObject = null;
		
		for (OGLGUIObject object : sceneObjects)
		{
			if (!object.isVisible())
				continue;

			if (!object.isEnabled())
				continue;
			
			if (object.isInert())
				continue;

			if (object.getRenderOrder() < 0)
				continue;
			
			if (!doMouseTest(object))
				continue;
			
			if (finalObject == null)
				finalObject = object;
			else if (object.getRenderOrder() >= finalObject.getRenderOrder())
				finalObject = object;
			
		}
		
		if (finalObject != objectMouseOver && objectMouseOver != null)
			fireMouseEvent(objectMouseOver, OGLGUIObject.EVENT_MOUSE_LEAVE);
	
		if (finalObject != objectMouseOver && finalObject != null)
			fireMouseEvent(finalObject, OGLGUIObject.EVENT_MOUSE_OVER);
	
		objectMouseOver = finalObject;
	}

	/**
	 * Checks if the mouse cursor is inside a particular object (rendered area).
	 * @param object the object to test.
	 * @return <code>true</code> if inside, <code>false</code> if not.
	 */
	protected boolean doMouseTest(OGLGUIObject object)
	{
		float mx = getMousePositionX();
		float my = getMousePositionY();
		return mx > object.getRenderPositionX() - object.getRenderHalfWidth() 
			&& mx < object.getRenderPositionX() + object.getRenderHalfWidth()
			&& my < object.getRenderPositionY() + object.getRenderHalfHeight()
			&& my > object.getRenderPositionY() - object.getRenderHalfHeight();
	}
	
	/**
	 * Sets the mouse coordinates.
	 */
	protected void setMouseCoordinates()
	{
		super.setMouseCoordinates();
		updateObjectEvents();
	}

	/**
	 * Creates the render list entries for an object.
	 */
	protected void displayRecreateRenderListForObjects(OGLGraphics g)
	{
		renderCount = 0;
		displayRecreateRenderListForObjectsRecurse(g, rootObjects);
	}
	
	private void displayRecreateRenderListForObjectsRecurse(OGLGraphics g, List<OGLGUIObject> objects)
	{
		for (OGLGUIObject obj : objects)
		{
			if (excludeObjectFromVisibility(obj))
			{
				obj.setRenderOrder(-1);
				continue;
			}
			
			OGLSkin group = obj.getSkin();
			if (group != null) 
			{
				for (int p = 0; p < group.size(); p++)
					renderListAddNode(g, loader, obj, renderListObjects, group.get(p), 0, 0);
			}
			else
				renderListAddNode(g, loader, obj, renderListObjects, DEFAULT_STEP, 0, 0);
			obj.setRenderOrder(renderCount++);
			displayRecreateRenderListForObjectsRecurse(g, obj.getChildren());
		}
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for (OGLGUIObject object : rootObjects)
			toStringRecurse("", sb, object);
		return sb.toString();
	}
	
	private void toStringRecurse(String tabString, StringBuilder sb, OGLGUIObject object)
	{
		sb.append(tabString).append(object.toString()).append('\n');
		for (OGLGUIObject child : object.getChildren())
			toStringRecurse(tabString + "\t", sb, child);
	}

}
