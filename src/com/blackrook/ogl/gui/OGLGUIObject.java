/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui;

import com.blackrook.commons.hash.Hash;
import com.blackrook.commons.hash.HashedQueueMap;
import com.blackrook.commons.linkedlist.Queue;
import com.blackrook.commons.list.List;
import com.blackrook.commons.math.geometry.Rectangle2F;
import com.blackrook.ogl.OGLMesh;
import com.blackrook.ogl.data.OGLColor;
import com.blackrook.ogl.enums.GeometryType;
import com.blackrook.ogl.gui.OGLGUILayout;
import com.blackrook.ogl.gui.OGLGUI;
import com.blackrook.ogl.gui.OGLGUIEvent.Origin;
import com.blackrook.ogl.mesh.MeshView;
import com.blackrook.ogl.mesh.PolygonMesh;
import com.blackrook.ogl.util.OGLSkin;
import com.blackrook.ogl.util.enums.Easing;
import com.blackrook.ogl.util.enums.EasingType;
import com.blackrook.ogl.util.scene2d.OGLScene2DElement;

/**
 * All GUI Objects inherit this class for creating entry points for GUI events.
 * @author Matthew Tropiano
 */
public abstract class OGLGUIObject implements OGLScene2DElement
{
	/** Unknown type of event. */
	public static final String EVENT_UNKNOWN = "UNKNOWN";
	/** Mouse cursor is (directly) over this object. */
	public static final String EVENT_MOUSE_OVER = "MOUSE_OVER";
	/** Mouse cursor leaves this object (not over it anymore). */
	public static final String EVENT_MOUSE_LEAVE = "MOUSE_LEAVE";
	/** Mouse button is pressed on this object. */
	public static final String EVENT_MOUSE_PRESS = "MOUSE_PRESS";
	/** Mouse button is released on this object. */
	public static final String EVENT_MOUSE_RELEASE = "MOUSE_RELEASE";
	/** Mouse is moved on this object. */
	public static final String EVENT_MOUSE_MOVE = "MOUSE_MOVE";
	/** Mouse is dragged on this object (not moved, button is down). */
	public static final String EVENT_MOUSE_DRAG = "MOUSE_DRAG";
	/** Mouse is clicked on this object. */
	public static final String EVENT_MOUSE_CLICK = "MOUSE_CLICK";
	/** Mouse wheel is scrolled on this object. */
	public static final String EVENT_MOUSE_WHEEL = "MOUSE_WHEEL";
	/** Keyboard key is pressed on this object. */
	public static final String EVENT_KEY_PRESS = "KEY_PRESS";
	/** Keyboard key is released on this object. */
	public static final String EVENT_KEY_RELEASE = "KEY_RELEASE";
	/** Keyboard key is typed on this object. */
	public static final String EVENT_KEY_TYPE = "KEY_TYPE";
	/** Gamepad button is pressed on this object. */
	public static final String EVENT_GAMEPAD_PRESS = "GAMEPAD_PRESS";
	/** Gamepad button is released on this object. */
	public static final String EVENT_GAMEPAD_RELEASE = "GAMEPAD_RELEASE";
	/** Gamepad axis is changed on this object. */
	public static final String EVENT_GAMEPAD_AXIS = "GAMEPAD_AXIS";
	/** Gamepad axis is tapped on this object. */
	public static final String EVENT_GAMEPAD_TAP = "GAMEPAD_TAP";
	/** Object is focused. */
	public static final String EVENT_FOCUS = "FOCUS";
	/** Object loses focus. */
	public static final String EVENT_BLUR = "BLUR";
	/** Object is enabled/disabled. */
	public static final String EVENT_CHANGE_ENABLE_STATE = "CHANGE_ENABLE_STATE";
	/** Object is shown/hidden. */
	public static final String EVENT_CHANGE_VISIBLE_STATE = "CHANGE_VISIBLE_STATE";

	/**
	 * Skin scaling type.
	 */
	public static enum ScaleType
	{
		/** 
		 * No scaling. 
		 */
		NORMAL,
		/** 
		 * Scaling is according to object's dimensional aspect.
		 * Most effective for square textures.
		 * Example: if XY dimensions are [200, 100], ST scaling is [2, 1].
		 * Example: if XY dimensions are [100, 200], ST scaling is [1, 2].
		 */
		ASPECT,
		/** 
		 * Scaling is according to object's height.
		 * Most effective for square textures.
		 * Example: if XY dimensions are [200, 100], ST scaling is [2, 1].
		 * Example: if XY dimensions are [100, 200], ST scaling is [.5, 1].
		 */
		ADJUST_X,
		/** 
		 * Scaling is according to object's width.
		 * Most effective for square textures.
		 * Example: if XY dimensions are [200, 100], ST scaling is [1, .5].
		 * Example: if XY dimensions are [100, 200], ST scaling is [1, 2].
		 */
		ADJUST_Y
	}
	
	/** Rectangular mesh used by all panels. */
	protected static final OGLMesh RECTANGLE = new PolygonMesh(GeometryType.QUADS, 4, 1)
	{{
		setVertex(0, -1, 1, 0);
		setTextureCoordinate(0, 0, 1);
		setVertex(1, -1, -1, 0);
		setTextureCoordinate(1, 0, 0);
		setVertex(2, 1, -1, 0);
		setTextureCoordinate(2, 1, 0);
		setVertex(3, 1, 1, 0);
		setTextureCoordinate(3, 1, 1);
	}};

	/** View for Rectangle. */
	protected static final MeshView RECTANGLE_VIEW = RECTANGLE.getView();

	/** Scene reference. */
	private OGLGUI guiRef;
	/** Reference to parent object. */
	private OGLGUIObject parentRef;
	/** Reference to child objects. */
	private List<OGLGUIObject> children;
	
	/** Object Bounds. */
	private Rectangle2F objectBounds;
	/** Object skin. */
	private OGLSkin skin;
	/** Object color. */
	private OGLColor color;
	/** Object rotation (around z-axis, pointing out of the screen). */
	private float objectRotation;
	/** Is the object visible? */
	private boolean visible;
	/** Does this receive input? */
	private boolean enabled;
	/** Is this considered inert (not receiving input), but not necessarily disabled? */
	private boolean inert;
	/** Constrain position to parent? */
	private boolean constrainToParent;
	/** Layout type for children. */
	private OGLGUILayout layout;
	/** Layout attribute when added to an object. */
	private Object layoutAttrib;
	/** If true, children are not influenced by parent's color. */
	private boolean colorNotInherited;
	
	/** GUI Theme. */
	private OGLGUITheme theme;
	
	/** Object opacity. */
	private float opacity;
	
	/** Scaling type. */
	private ScaleType scaleType;
	
	/** This object's event mapping. */
	private HashedQueueMap<String, OGLGUIAction> eventMap;
	/** This object's name list. */
	private Hash<String> names;
	
	/** This object's absolute bounds. */
	protected Rectangle2F absoluteBounds;

	/** 
	 * Separate object bounds rectangle used for the {@link #getBounds()} call.
	 * This is so that it can be used entirely for feedback, and the manipulation
	 * thereof will not affect this object. 
	 */
	protected Rectangle2F objectBoundsCallback;

	/** 
	 * Separate object color used for the {@link #getColor()} call.
	 * This is so that it can be used entirely for feedback, and the manipulation
	 * thereof will not affect this object. 
	 */
	protected OGLColor objectColorCallback;

	/** 
	 * Separate object bounds rectangle used for the {@link #getAbsoluteBounds()} call.
	 * This is so that it can be used entirely for feedback, and the manipulation
	 * thereof will not affect this object. 
	 */
	protected Rectangle2F objectAbsoluteBoundsCallback;
	
	/** Render order - set during render. */
	private int renderOrder;
	
	/**
	 * Creates a new GUI object.
	 */
	protected OGLGUIObject()
	{
		children = new List<OGLGUIObject>();
		eventMap = new HashedQueueMap<String, OGLGUIAction>(2);
		names = new Hash<String>(3);
		color = new OGLColor(1,1,1,1);
		skin = null;
		visible = true;
		enabled = true;
		inert = false;
		objectBounds = new Rectangle2F();
		absoluteBounds = new Rectangle2F();
		objectBoundsCallback = new Rectangle2F();
		objectColorCallback = new OGLColor();
		objectAbsoluteBoundsCallback = new Rectangle2F();
		renderOrder = -1;
		setConstrainToParent(false);
		setLayout(null);
		setLayoutAttrib(null);
		setBounds(0,0,1,1);
		setScaleType(ScaleType.NORMAL);
		setOpacity(1f);
	}
	
	/**
	 * Gets the reference to which GUI scene this belongs to.
	 */
	public final OGLGUI getGUI()
	{
		return guiRef;
	}
	
	/**
	 * Gets the parent object of this one.
	 * Can be null if this is not a child object.
	 */
	public final OGLGUIObject getParent()
	{
		return parentRef;
	}

	/**
	 * Add a child object to this one and sets a layout attribute to it, as
	 * though {@link #setLayoutAttrib(Object)}
	 * @return true if added, false otherwise.
	 */
	public final boolean addChild(OGLGUIObject obj, Object attrib)
	{
		obj.setLayoutAttrib(attrib);
		if (!children.contains(obj))
		{
			obj.parentRef = this;
			obj.guiRef = guiRef;
			synchronized (children)
			{
				children.add(obj);
				if (guiRef != null)
					guiRef.addObjectTree(obj);
			}
			resizeChildren();
			updateScenePosition();
			return true;
		}
		return false;
	}
	
	/**
	 * Add a child object to this one.
	 * @return true if added, false otherwise.
	 */
	public final boolean addChild(OGLGUIObject obj)
	{
		return addChild(obj, null);
	}
	
	/**
	 * Removes a child object from this one.
	 * @return true if removed, false otherwise.
	 */
	public final boolean removeChild(OGLGUIObject obj)
	{
		if (children.contains(obj))
		{
			obj.parentRef = null;
			obj.guiRef = null;
			synchronized (children)
			{
				if (guiRef != null)
					guiRef.removeObjectTree(obj);
				children.remove(obj);
			}
			resizeChildren();
			updateScenePosition();
			return true;
		}
		return false;
	}
	
	/**
	 * Moves the order of this object up among its siblings,
	 * so that it is rendered later than the one after it 
	 * (bringing it "closer to the camera").
	 * <p>If this is the last one anyway, nothing happens.
	 * <p>If this is not the child of an object, nor does it belong to a GUI,
	 * nothing happens.
	 */
	public final void moveUp()
	{
		List<OGLGUIObject> childList = getParentChildren();
		if (childList == null)
			return;
		
		int index = childList.getIndexOf(this);
		if (index == childList.size() - 1)
			return;
		
		synchronized (childList)
		{
			childList.shift(index, index + 1);
		}
	}

	/**
	 * Moves the order of this object up among its siblings,
	 * so that it is rendered last (bringing it "closest to the camera").
	 * <p>If this is the last one anyway, nothing happens.
	 * <p>If this is not the child of an object, nor does it belong to a GUI,
	 * nothing happens.
	 */
	public final void moveToFront()
	{
		List<OGLGUIObject> childList = getParentChildren();
		if (childList == null)
			return;
		
		int end = childList.size() - 1;
		
		int index = childList.getIndexOf(this);
		if (index == end)
			return;
		
		synchronized (childList)
		{
			childList.shift(index, end);
		}
	}

	/**
	 * Moves the order of this object back among its siblings,
	 * so that it is rendered earlier than the one before it 
	 * (pushing it "further from the camera").
	 * <p>If this is the first one anyway, nothing happens.
	 * <p>If this is not the child of an object, nor does it belong to a GUI,
	 * nothing happens.
	 */
	public final void moveDown()
	{
		List<OGLGUIObject> childList = getParentChildren();
		if (childList == null)
			return;
		
		int index = childList.getIndexOf(this);
		if (index == 0)
			return;
		
		synchronized (childList)
		{
			childList.shift(index, index - 1);
		}
	}

	/**
	 * Moves the order of this object back among its siblings,
	 * so that it is rendered first (pushing it "farthest from the camera").
	 * <p>If this is the first one anyway, nothing happens.
	 * <p>If this is not the child of an object, nor does it belong to a GUI,
	 * nothing happens.
	 */
	public final void moveToBack()
	{
		List<OGLGUIObject> childList = getParentChildren();
		if (childList == null)
			return;
		
		int index = childList.getIndexOf(this);
		if (index == 0)
			return;
		
		synchronized (childList)
		{
			childList.shift(index, 0);
		}
	}

	// Get child list of this object's parent (or GUI, if top of hierarchy).
	private List<OGLGUIObject> getParentChildren()
	{
		OGLGUIObject parent = getParent();
		if (parent == null)
		{
			if (getGUI() == null)
				return null;
			else
				return guiRef.getRootObjects();
		}
		else
			return parent.getChildren();
	}
	
	/**
	 * Checks if an action has at least one binding.
	 * @param type the event type.
	 * @return true if so, false if not.
	 */
	public final boolean hasAction(String type)
	{
		Queue<OGLGUIAction> q = eventMap.get(type);
		return q != null && q.size() > 0;
	}
	
	/**
	 * Adds an action bound to an event type.
	 * The action is enqueued, so the added action will 
	 * happen after other actions bound to this event type.
	 * @param action the action to bind.
	 * @param types the event types.
	 */
	public final void bindAction(OGLGUIAction action, String ... types)
	{
		for (String t : types)
			eventMap.enqueue(t, action);
	}
	
	/**
	 * Removes an action bound to an event type.
	 * @param action the action to unbind.
	 * @param types the event types.
	 */
	public final boolean unbindAction(OGLGUIAction action, String ... types)
	{
		boolean out = false;
		for (String t : types)
			out = out || eventMap.removeValue(t, action);
		return out;
	}
	
	/**
	 * Removes all actions bound to event types.
	 * @param types the event types.
	 */
	public final boolean unbindAllActions(String ... types)
	{
		boolean out = false;
		for (String t : types)
			out = out || eventMap.removeUsingKey(t) != null;
		return out;
	}
	
	/**
	 * Calls an action on this object.
	 * @param action the action to call on this object.
	 */
	public final void callAction(OGLGUIAction action)
	{
		OGLGUIEvent event = new OGLGUIEvent();
		event.reset();
		event.setGUI(getGUI());
		event.setObject(this);
		event.setType(null);
		event.setOrigin(Origin.ACTION);
		action.call(event);
	}
	
	/**
	 * Calls all actions bound to this object by type.
	 * If no action attached to this object is associated with that name,
	 * this does nothing and returns false.
	 * @param event the event to pass.
	 */
	final void callEvent(OGLGUIEvent event)
	{
		if (eventMap.containsKey(event.getType()))
		{
			for (OGLGUIAction action : eventMap.get(event.getType()))
				action.call(event);
		}
	}
	
	/**
	 * Fires a non-specific event to the GUI system.
	 * @param type the event type to fire.
	 */
	protected final void fireEvent(String type)
	{
		if (guiRef != null)
			guiRef.fireGUIEvent(this, type);
	}
	
	/**
	 * Adds a single name or series of names to this object, used for selecting objects.
	 * @param names the names to add.
	 */
	public final void addName(String ... names)
	{
		for (String n : names)
			this.names.put(n);
	}

	/**
	 * Removes a single name or series of names from this object.
	 * @param names the names to remove.
	 */
	public final void removeName(String ... names)
	{
		for (String n : names)
			this.names.remove(n);
	}

	/**
	 * Checks if this object has a particular name.
	 */
	public final boolean hasName(String name)
	{
		return names.contains(name);
	}

	/**
	 * Returns true if one of this object's names matches
	 * at least a piece of the provided pattern, false otherwise.
	 */
	public final boolean hasNamePattern(String pattern)
	{
		for (String n : names)
			if (n.matches(pattern))
				return true;
		return false;
	}

	/**
	 * Returns a copy of this object's bounding rectangle.
	 * This method is NOT THREAD-SAFE.
	 * Any changes to the returned Rectangle2F will not change the bounds of this object,
	 * and thus will NOT trigger any events triggered by a bounds change. 
	 * See {@link #setBounds(float, float, float, float)}.
	 */
	public Rectangle2F getBounds()
	{
		objectBoundsCallback.set(objectBounds);
		return objectBoundsCallback;
	}
	
	/**
	 * Returns a reference to this object's bounds rectangle.
	 * Any changes made to this rectangle will affect the object's bounds,
	 * but will NOT trigger any events triggered by a bounds change. 
	 * See {@link #setBounds(float, float, float, float)}.
	 */
	protected Rectangle2F getNativeBounds()
	{
		return objectBounds;
	}

	/**
	 * Sets this object's rotation in degrees.
	 */
	public void setRotationZ(float rotation)
	{
		objectRotation = rotation;
	}
	
	/**
	 * Rotates this object by a number of degrees.
	 */
	public void rotate(float rotation)
	{
		objectRotation += rotation;
	}
	
	@Override
	public float getRenderRadius()
	{
		float rhh = getRenderHalfHeight();
		float rhw = getRenderHalfWidth();
		return (float)Math.sqrt(rhh * rhh + rhw * rhw);
	}

	@Override
	public boolean useRenderRadius()
	{
		return objectRotation != 0.0f;
	}

	/**
	 * Sets the color of this object.
	 * @param red the red component value for the color. 
	 * @param green the green component value for the color.
	 * @param blue the blue component value for the color.
	 * @param alpha the alpha component value for the color.
	 */
	public void setColor(float red, float green, float blue, float alpha)
	{
		this.color.set(red, green, blue, alpha);
	}

	/**
	 * Sets the object's color.
	 * @param color the color to set.
	 */
	public void setColor(OGLColor color)
	{
		this.color.set(color);
	}

	/**
	 * Gets the object's color.
	 * If the object returned is changed, it will not affect this object's color. 
	 * @return an OGLColor with this object's color components.
	 */
	public OGLColor getColor()
	{
		objectColorCallback.set(color);
		return objectColorCallback;
	}
	
	/**
	 * Sets this object's render skin.
	 */
	public void setSkin(OGLSkin skin)
	{
		this.skin = skin;
	}
	
	@Override
	public float getRenderHalfWidth()
	{
		return objectBounds.width / 2f;
	}

	@Override
	public float getRenderHalfHeight()
	{
		return objectBounds.height / 2f;
	}

	@Override
	public float getRenderHalfDepth()
	{
		return .5f;
	}

	@Override
	public float getRenderRotationZ()
	{
		return objectRotation;
	}

	/**
	 * Sets if the object is visible (and its children).
	 * Fires an event if the visibility state changed.
	 * This attribute affects this object's descendants - they are not 
	 * visible if this is not visible.
	 * Objects that are not visible are ignored when testing for mouse collisions,
	 * nor are they drawn by the renderer.
	 */
	public void setVisible(boolean visible)
	{
		boolean prev = this.visible;
		this.visible = visible;
		if (visible != prev && guiRef != null)
		{
			if (!visible)
				releaseFocus();
			guiRef.fireGUIEvent(this, EVENT_CHANGE_VISIBLE_STATE);
		}
	}
	
	/**
	 * Sets if the object can accept input (and its children).
	 * Fires an event if the enabled state changed.
	 * If this object was focused, it tells the GUI that owns it to
	 * unfocus it.
	 */
	public void setEnabled(boolean enabled)
	{
		boolean prev = this.enabled;
		this.enabled = enabled;
		if (enabled != prev && guiRef != null)
		{
			if (!enabled)
				releaseFocus();
			guiRef.fireGUIEvent(this, EVENT_CHANGE_ENABLE_STATE);
		}
	}

	/**
	 * Sets if the object can accept/intercept input, but does <b>NOT</b>
	 * affect its "enabled" state, unless its parent is enabled/disabled.
	 * Unlike {@link #isVisible()} and {@link #isEnabled()}, this is not
	 * hierarchically significant (inert-ness is set on this and ONLY this object).
	 * Objects that are inert are ignored when testing for mouse collisions.
	 * This fires NO EVENTS on change.
	 * If this object was focused, it tells the GUI that owns it to
	 * unfocus it.
	 */
	public void setInert(boolean inert)
	{
		boolean prev = this.inert;
		this.inert = inert;
		if (inert != prev && guiRef != null)
		{
			if (!inert)
				releaseFocus();
		}
	}

	/**
	 * Sets the object bounds.
	 * @param r			the rectangle to use for bounds.
	 */
	public void setBounds(Rectangle2F r)
	{
		setBounds(r.x, r.y, r.width, r.height);
	}
	
	/**
	 * Sets the object bounds.
	 * @param x			its position x.
	 * @param y			its position y.
	 * @param width		its width.
	 * @param height	its height.
	 */
	public void setBounds(float x, float y, float width, float height)
	{
		objectBounds.x = x;
		objectBounds.y = y;
		objectBounds.width = width;
		objectBounds.height = height;
		correctPosition();
		resizeChildren();
		updateScenePosition();
	}

	/**
	 * Gets this object's ABSOLUTE bounds, i.e. the object's current position after
	 * considering its inherited hierarchy.
	 * The Rectangle2F returned can be changed and it will not affect this object's bounds.
	 * @return a Rectangle2F of the current object bounds.
	 */
	public Rectangle2F getAbsoluteBounds()
	{
		objectAbsoluteBoundsCallback.set(absoluteBounds);
		return objectAbsoluteBoundsCallback;
	}
	
	/**
	 * Sets the object position.
	 * @param x			its position x.
	 * @param y			its position y.
	 */
	public void setPosition(float x, float y)
	{
		objectBounds.x = x;
		objectBounds.y = y;
		correctPosition();
		updateScenePosition();
	}

	/**
	 * Sets the object width and height.
	 * @param width		its width.
	 * @param height	its height.
	 */
	public void setDimensions(float width, float height)
	{
		objectBounds.width = width;
		objectBounds.height = height;
		correctPosition();
		resizeChildren();
		updateScenePosition();
	}

	/**
	 * Changes this object's position by an x or y-coordinate amount.
	 */
	public void translate(float x, float y)
	{
		objectBounds.x += x;
		objectBounds.y += y;
		correctPosition();
		updateScenePosition();
	}

	/**
	 * Changes this object's width/height by an x or y-coordinate amount.
	 */
	public void stretch(float width, float height)
	{
		objectBounds.width += width;
		objectBounds.height += height;
		correctPosition();
		resizeChildren();
		updateScenePosition();
	}

	@Override
	public float getRed()
	{
		return color.getRed() * (parentRef != null && !parentRef.getColorNotInherited() ? parentRef.getRed() : 1f) ;
	}

	@Override
	public float getGreen()
	{
		return color.getGreen() * (parentRef != null && !parentRef.getColorNotInherited() ? parentRef.getGreen() : 1f);
	}

	@Override
	public float getBlue()
	{
		return color.getBlue() * (parentRef != null && !parentRef.getColorNotInherited() ? parentRef.getBlue() : 1f);
	}

	@Override
	public float getAlpha()
	{
		return color.getAlpha() * getFinalOpacity() * (parentRef != null && !parentRef.getColorNotInherited() ? parentRef.getAlpha() : 1f);
	}

	/**
	 * Returns this object's inherited opacity.
	 */
	protected float getFinalOpacity()
	{
		return opacity * (parentRef != null ? parentRef.getFinalOpacity() : 1f);
	}
	
	@Override
	public final float getRenderPositionX()
	{
		return absoluteBounds.x + (absoluteBounds.width / 2f);
	}

	@Override
	public final float getRenderPositionY()
	{
		return absoluteBounds.y + (absoluteBounds.height / 2f);
	}

	@Override
	public final float getRenderPositionZ()
	{
		return 0f;
	}
	
	/**
	 * Returns the order index of when this object was rendered,
	 * or -1 if not rendered.
	 */
	public final int getRenderOrder()
	{
		return renderOrder;
	}

	/**
	 * Sets this object's render order.
	 */
	void setRenderOrder(int renderOrder)
	{
		this.renderOrder = renderOrder;
	}
	
	@Override
	public float getSkinScaleS()
	{
		if (scaleType != null) switch (scaleType)
		{
			case NORMAL:
				return 1f;
			case ASPECT:
			{
				Rectangle2F bounds = getBounds();
				return bounds.width > bounds.height 
					? (bounds.width / (bounds.height == 0 ? 1f : bounds.height)) 
					: 1f;
			}
			case ADJUST_X:
			{
				Rectangle2F bounds = getBounds();
				return bounds.height != 0f ? bounds.width / bounds.height : 0f;
			}
			case ADJUST_Y:
				return 1f;
		}
		return 1f;
	}
	
	@Override
	public float getSkinScaleT()
	{
		if (scaleType != null) switch (scaleType)
		{
			case NORMAL:
				return 1f;
			case ASPECT:
			{
				Rectangle2F bounds = getBounds();
				return bounds.height > bounds.width 
					? (bounds.height / (bounds.width == 0 ? 1f : bounds.width)) 
					: 1f;
			}
			case ADJUST_X:
				return 1f;
			case ADJUST_Y:
			{
				Rectangle2F bounds = getBounds();
				return bounds.width != 0f ? bounds.height / bounds.width : 0f;
			}
		}
		return 1f;
	}
	
	/**
	 * Returns how far down the generation tree this object is.
	 * @return How many parents are above this one.
	 */
	public final int getGeneration()
	{
		return parentRef != null ? 1 + parentRef.getGeneration() : 0;
	}

	/**
	 * Returns this object's skin.
	 * If no skin is set on this, this will take the one from the theme,
	 * if {@link #getThemeKey()} returns a non-null value.
	 * @see #getThemeKey() 
	 */
	@Override
	public OGLSkin getSkin()
	{
		if (skin != null)
			return skin;
		
		String key = getThemeKey();
		
		if (key != null)
		{
			OGLGUITheme t = getTheme();
			return t != null ? t.getSkin(key) : null;
		}

		return null;
	}
	
	/**
	 * Returns this object's theme key.
	 * This is the key that is used to look up the skin
	 * used to render this object. This may return null,
	 * indicating that this object does not have a default skin.
	 */
	public abstract String getThemeKey();
	
	/**
	 * Gets if this object is currently focused in its GUI.
	 * True if so. False if not, or this doesn't belong to a GUI.
	 */
	public boolean isFocused()
	{
		return guiRef != null && guiRef.getFocusedObject() == this;
	}

	/**
	 * Gets if this object is within current camera bounds at all.
	 * True if so. False if not, or this doesn't belong to a GUI.
	 */
	public boolean isOnCamera()
	{
		return guiRef != null && guiRef.isOnCamera(this);
	}
	
	/**
	 * Gets if this object is currently animating
	 * True if so. False if not, or this doesn't belong to a GUI.
	 */
	public boolean isAnimating()
	{
		return guiRef != null && guiRef.isAnimating(this);
	}
	
	/**
	 * Gets if the object is visible, which means that this object has
	 * its "visible" member set to true, and its opacity is nonzero.
	 * This object is not visible if any of its ancestors are not visible.
	 * Objects that are not visible are ignored when testing for mouse collisions.
	 */
	public boolean isVisible()
	{
		return visible && getOpacity() > 0f && (parentRef != null ? parentRef.isVisible() : true);
	}

	/**
	 * Gets if the object can accept/intercept input.
	 * This affects this object's "enabled" state, as the name suggests.
	 * This object is not enabled if any of its ancestors are not enabled.
	 * Objects that are not enabled cannot be focused, and are also ignored
	 * when testing mouse collisions/events. They can still be manipulated
	 * through user-enacted methods that are not direct input.
	 */
	public boolean isEnabled()
	{
		return enabled && (parentRef != null ? parentRef.isEnabled() : true);
	}

	/**
	 * Gets if the object can accept/intercept input, but does <b>NOT</b>
	 * affect its "enabled" state, unless its parent is enabled/disabled.
	 * Unlike {@link #isVisible()} and {@link #isEnabled()}, this is not
	 * hierarchically significant (inert-ness is set on this and ONLY this object).
	 * Objects that are inert are ignored when testing for mouse collisions.
	 */
	public boolean isInert()
	{
		return inert;
	}

	/**
	 * Gets if this constrains its position to its parent.
	 */
	public boolean isConstrainedToParent()
	{
		return constrainToParent;
	}

	/**
	 * Sets if this should constrain its position to its parent.
	 * True if so, false if not.
	 */
	public void setConstrainToParent(boolean constrainToParent)
	{
		this.constrainToParent = constrainToParent;
		correctPosition();
	}

	/**
	 * Gets this object wrapped in an {@link OGLGUIQuery}.
	 * Convenience method for <code>OGLGUIQuery.wrap(this)</code>.
	 * @return a new OGLGUIQuery with this object in it.
	 */
	public OGLGUIQuery getAsQuery()
	{
		return OGLGUIQuery.wrap(this);
	}

	/**
	 * Gets this object's parent wrapped in an {@link OGLGUIQuery}.
	 * Convenience method for <code>OGLGUIQuery.wrap(this.getParent())</code>.
	 * @return a new OGLGUIQuery with this object's parent in it.
	 */
	public OGLGUIQuery getParentAsQuery()
	{
		return OGLGUIQuery.wrap(this.getParent());
	}
	
	/**
	 * Gets this object's children wrapped in an {@link OGLGUIQuery}.
	 * @return a new OGLGUIQuery with this object's children in it.
	 */
	public OGLGUIQuery getChildrenAsQuery()
	{
		return getAsQuery().getChildren();
	}

	/**
	 * Gets this object's entire descendant tree wrapped in an {@link OGLGUIQuery}.
	 * @return a new OGLGUIQuery with this object's descendants in it.
	 */
	public OGLGUIQuery getDescendantsAsQuery()
	{
		return getAsQuery().getDescendants();
	}

	/**
	 * Gets this object's siblings wrapped in an {@link OGLGUIQuery}.
	 * @return a new OGLGUIQuery with this object's siblings in it.
	 */
	public OGLGUIQuery getSiblingsAsQuery()
	{
		return getAsQuery().getSiblings();
	}

	/**
	 * Gets this object's layout type for resizing object children,
	 * should this object get resized itself. By default, this value is null.
	 */
	public OGLGUILayout getLayout()
	{
		return layout;
	}

	/**
	 * Sets this object's layout type for resizing object children,
	 * should this object get resized itself. By default, this value is null.
	 */
	public void setLayout(OGLGUILayout layout)
	{
		this.layout = layout;
		resizeChildren();
	}

	/**
	 * Gets this object's layout attribute, used by some layouts in order to
	 * affect the parent's layout behavior when resizing this component (as a child). 
	 * By default, this value is null.
	 */
	public Object getLayoutAttrib()
	{
		return layoutAttrib;
	}

	/**
	 * Sets this object's layout attribute, used by some layouts in order to
	 * affect the parent's layout behavior when resizing this component (as a child). 
	 * By default, this value is null.
	 * Changing this value while this object is a part of its parent's layout
	 */
	public void setLayoutAttrib(Object attrib)
	{
		this.layoutAttrib = attrib;
	}

	/**
	 * Gets if this object DOESN'T pass on its color to its children.
	 */
	public boolean getColorNotInherited()
	{
		return colorNotInherited;
	}

	/**
	 * Sets if this object DOESN'T pass on its color to its children.
	 */
	public void setColorNotInherited(boolean enabled)
	{
		this.colorNotInherited = enabled;
	}

	/**
	 * Returns the current theme used by this object.
	 * If no current theme, this returns the parent's theme.
	 * If no parent, this returns the theme attached the owning GUI.
	 */
	public final OGLGUITheme getTheme()
	{
		return theme != null 
			? theme 
			: (parentRef != null 
				? parentRef.getTheme() 
				: (guiRef != null 
					? guiRef.getTheme() 
					: null
				)
			);
	}
	
	/**
	 * Sets the theme used by this object, and its descendants if they do not
	 * have a theme set. Setting this to null allows this object to inherit the current theme
	 * from its parents.
	 * @param theme the theme to use. Can be null.
	 */
	public void setTheme(OGLGUITheme theme)
	{
		this.theme = theme;
	}
	
	/**
	 * Gets this object's opacity (0 to 1).
	 * Opacity is always inherited by children, regardless 
	 * of if {@link #getColorNotInherited()} is true or false.
	 * This affects visibility - if this is 0.0, this object is
	 * considered NOT VISIBLE.
	 * @see #isVisible()
	 */
	public float getOpacity()
	{
		return opacity;
	}

	/**
	 * Gets this object's opacity (0 to 1).
	 * Opacity is always inherited by children, regardless 
	 * of if {@link #getColorNotInherited()} is true or false.
	 * This affects visibility - setting this to 0.0 is like setting
	 * {@link #setVisible(boolean)} to <code>false</code>.
	 */
	public void setOpacity(float opacity)
	{
		this.opacity = opacity;
	}

	/**
	 * Gets this object's scaling type.
	 */
	public ScaleType getScaleType()
	{
		return scaleType;
	}

	/**
	 * Sets this object's scaling type.
	 */
	public void setScaleType(ScaleType scaleType)
	{
		this.scaleType = scaleType;
	}

	/**
	 * Sets this object's width and height based on the bounds of its children.
	 * If it has no children, nothing happens.
	 */
	public void setBoundsByChildren()
	{
		Rectangle2F objectBounds = getBounds();

		if (children.size() > 0) 
		{
			float w = 0f;
			float h = 0f;
			for (OGLGUIObject child : children)
			{
				Rectangle2F childBounds = child.getBounds();

				float cw = childBounds.x + childBounds.width;
				float ch = childBounds.y + childBounds.height;
				w = Math.max(w, cw);
				h = Math.max(h, ch);
			}
			setBounds(objectBounds.x, objectBounds.y, w, h);
		}
	}
	
	/**
	 * Requests focus on this object in the scene it belongs to.
	 */
	public void requestFocus()
	{
		if (guiRef != null)
			guiRef.requestObjectFocus(this);
	}

	/**
	 * Releases focus on this object if this object currently has focus
	 * in the scene it belongs to.
	 */
	public void releaseFocus()
	{
		if (guiRef != null)
			guiRef.requestObjectUnfocus(null);
	}

	/**
	 * Calls upon the layouts to resize the children.
	 */
	public void resizeChildren()
	{
		if (layout == null)
			return;
		
		int i = 0;
		for (OGLGUIObject child : children)
		{
			layout.resizeChild(child, i++, children.size());
			child.correctPosition();
			child.resizeChildren();
		}
		
		updateScenePosition();
	}

	/**
	 * Enqueues an animation on this GUI Object, no duration.
	 * @param animations the animations to perform (at once) for this animation.
	 */
	public void animate(OGLGUIAnimation ... animations)
	{
		animate(0, Easing.LINEAR, animations);
	}

	/**
	 * Enqueues an animation on this GUI Object, linear transition.
	 * @param duration the duration of the action in milliseconds.
	 * @param animations the animations to perform (at once) for this animation.
	 */
	public void animate(float duration, OGLGUIAnimation ... animations)
	{
		animate(duration, Easing.LINEAR, animations);
	}

	/**
	 * Enqueues an animation on this GUI Object.
	 * @param duration the duration of the action in milliseconds.
	 * @param transition the transition type for the action.
	 * @param animations the animations to perform (at once) for this animation.
	 */
	public void animate(float duration, EasingType transition, OGLGUIAnimation ... animations)
	{
		if (guiRef != null)
			guiRef.addAnimation(this, duration, transition, animations);
	}

	/**
	 * Enqueues a delay between animations on this GUI Object.
	 * @param duration the duration of the action in milliseconds.
	 */
	public void animateDelay(float duration)
	{
		if (guiRef != null)
			guiRef.addAnimation(this, duration, Easing.LINEAR);
	}

	/**
	 * Aborts the animation on this object, abandoning it mid-animation.
	 */
	public void animateAbort()
	{
		if (guiRef != null)
			guiRef.endAnimation(this, false);
	}

	/**
	 * Finishes the animation on this object all the way to the end.
	 */
	public void animateFinish()
	{
		if (guiRef != null)
			guiRef.endAnimation(this, true);
	}

	/**
	 * Corrects the position of this object, if this is constrained to the parent's position.
	 */
	protected void correctPosition()
	{
		if (!constrainToParent || parentRef == null)
			return;
		
		Rectangle2F objectBounds = getNativeBounds();
		Rectangle2F parentBounds = parentRef.getBounds();
		
		if (objectBounds.x < 0)
			objectBounds.x = 0;
		else if (objectBounds.x + objectBounds.width > parentBounds.width)
			objectBounds.x = parentBounds.width - objectBounds.width;
		
		if (objectBounds.y < 0)
			objectBounds.y = 0;
		else if (objectBounds.y + objectBounds.height > parentBounds.height)
			objectBounds.y = parentBounds.height - objectBounds.height;
	}

	/**
	 * Updates this GUI Object's position and absolute bounds in the scene's collision field.
	 */
	protected void updateScenePosition()
	{
		Rectangle2F bounds = getNativeBounds();
		Rectangle2F parentAbsoluteBounds = parentRef != null ? parentRef.absoluteBounds : null;
		
		absoluteBounds.x = bounds.x + (parentAbsoluteBounds != null ? parentAbsoluteBounds.x : 0f);
		absoluteBounds.y = bounds.y + (parentAbsoluteBounds != null ? parentAbsoluteBounds.y : 0f);
		absoluteBounds.width = bounds.width;
		absoluteBounds.height = bounds.height;
		
		for (OGLGUIObject obj : children)
			obj.updateScenePosition();

		if (guiRef != null)
		{
			// Do something here to scene.
		}

	}

	/**
	 * Called when this object is added to a GUI.
	 * Does nothing, unless overridden.
	 * @param gui the reference to the GUI that this was added to.
	 */
	protected void onGUIChange(OGLGUI gui)
	{
		// Do nothing.
	}
	
	/**
	 * Sets the reference to which GUI scene this belongs to.
	 */
	final void setGUI(OGLGUI ref)
	{
		guiRef = ref;
		onGUIChange(guiRef);
	}

	/**
	 * Returns a reference to the list of this object's children.
	 */
	final List<OGLGUIObject> getChildren()
	{
		return children;
	}

	/** Gets a reference to the name hash. */
	final Hash<String> getNameHash()
	{
		return names;
	}

	/**
	 * Gets all objects in the tree.
	 * @param query the output query result.
	 */
	final void getAllInTree(OGLGUIQuery query)
	{
		query.add(this);
		for (OGLGUIObject child : getChildren())
			child.getAllInTree(query);
	}

	/**
	 * Gets all name matches in this object's tree.
	 * @param name the name to look for.
	 * @param query the output query result.
	 */
	final void getNameMatchesInTree(String name, OGLGUIQuery query)
	{
		if (hasName(name))
			query.add(this);
		for (OGLGUIObject child : getChildren())
			child.getNameMatchesInTree(name, query);
	}

	/**
	 * Gets all name pattern matches in this object's tree.
	 * @param name the name to look for.
	 * @param query the output query result.
	 */
	final void getNamePatternMatchesInTree(String pattern, OGLGUIQuery query)
	{
		if (hasNamePattern(pattern))
			query.add(this);
		for (OGLGUIObject child : getChildren())
			child.getNamePatternMatchesInTree(pattern, query);
	}

	/**
	 * Gets all class matches in this object's tree.
	 * @param clazz the class type to look for.
	 * @param query the output query result.
	 */
	final void getTypeMatchesInTree(Class<?> clazz, OGLGUIQuery query)
	{
		if (clazz.isInstance(this))
			query.add(this);
		for (OGLGUIObject child : getChildren())
			child.getTypeMatchesInTree(clazz, query);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName()).append(' ');
		Rectangle2F bounds = getBounds();
		if (names.size() > 0)
		{
			sb.append('[');
			int x = 0;
			for (String name : names)
			{
				sb.append(name);
				if (x < names.size() - 1)
					sb.append(", ");
				x++;
			}
			sb.append(']');
		}
		else
			sb.append("(UNNAMED)");
		sb.append(' ');
		sb.append(String.format("R[%.03f, %.03f, %.03f, %.03f]", 
			bounds.x, bounds.y, bounds.width, bounds.height));
		sb.append(' ');
		sb.append(String.format("RGBA(%.03f, %.03f, %.03f, %.03f) O: %.03f", 
			color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha(), opacity));
		sb.append(' ');
		if (!isEnabled())
			sb.append("DISABLED").append(' ');
		if (!isVisible())
			sb.append("INVISIBLE").append(' ');
		if (getLayout() != null)
			sb.append(getLayout().getClass().getSimpleName()).append(' ');
		if (getLayoutAttrib() != null)
			sb.append(getLayoutAttrib().getClass().getSimpleName()).append(' ');
		return sb.toString();
	}
	
	
}
