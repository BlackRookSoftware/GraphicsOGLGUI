/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui.object;

import com.blackrook.ogl.gui.OGLGUIAction;
import com.blackrook.ogl.gui.OGLGUIEvent;
import com.blackrook.ogl.gui.OGLGUIObject;
import com.blackrook.ogl.gui.layout.FramedLayout;

/**
 * A "framed" pane object that already adds the frame
 * elements around the object, as well as a means to
 * make the frame resizable around its edges, and draggable
 * at its top.  
 * @author Matthew Tropiano
 */
public class OGLGUIFrame extends OGLGUIGlassPanel
{
	/** Name of top-left panel. */
	public static final String TOP_LEFT_NAME = "top-left"; 
	/** Name of top-center panel. */
	public static final String TOP_CENTER_NAME = "top-center"; 
	/** Name of top-right panel. */
	public static final String TOP_RIGHT_NAME = "top-right"; 
	/** Name of bottom-left panel. */
	public static final String BOTTOM_LEFT_NAME = "bottom-left"; 
	/** Name of bottom-center panel. */
	public static final String BOTTOM_CENTER_NAME = "bottom-center"; 
	/** Name of bottom-right panel. */
	public static final String BOTTOM_RIGHT_NAME = "bottom-right"; 
	/** Name of middle-left panel. */
	public static final String MIDDLE_LEFT_NAME = "middle-left"; 
	/** Name of middle-right panel. */
	public static final String MIDDLE_RIGHT_NAME = "middle-right"; 
	/** Name of content panel. */
	public static final String CONTENT_NAME = "content"; 
	
	/** Top-left Panel. */
	private OGLGUIPanel topLeftPanel;
	/** Top-center Panel. */
	private OGLGUIPanel topCenterPanel;
	/** Top-right Panel. */
	private OGLGUIPanel topRightPanel;
	/** Bottom-left Panel. */
	private OGLGUIPanel bottomLeftPanel;
	/** Bottom-center Panel. */
	private OGLGUIPanel bottomCenterPanel;
	/** Bottom-right Panel. */
	private OGLGUIPanel bottomRightPanel;
	/** Middle-left Panel. */
	private OGLGUIPanel middleLeftPanel;
	/** Middle-right Panel. */
	private OGLGUIPanel middleRightPanel;
	/** Content Panel. */
	private OGLGUIPanel contentPanel;
	
	/** Frame thickness. */
	private float thickness;
	/** Is this frame resizable? */
	private boolean resizable;
	/** Is this frame draggable? */
	private boolean draggable;
	
	/**
	 * Horizontal resize action.
	 */
	private final OGLGUIAction RESIZABLE_RIGHT = new OGLGUIAction()
	{
		@Override
		public void call(OGLGUIEvent event)
		{
			if (resizable)
			{
				OGLGUIObject obj = event.getObject().getParent();
				float mouseX = event.getMouseMovementX();
				float change = mouseX < 0f ? correctMouseMoveX(mouseX, obj.getBounds().width) : mouseX; 
				obj.stretch(change, 0);
			}
		}
	};
	
	/**
	 * Horizontal resize action.
	 */
	private final OGLGUIAction RESIZABLE_LEFT = new OGLGUIAction()
	{
		@Override
		public void call(OGLGUIEvent event)
		{
			if (resizable)
			{
				OGLGUIObject obj = event.getObject().getParent();
				float mouseX = event.getMouseMovementX();
				float change = mouseX > 0f ? correctMouseMoveX(mouseX, obj.getBounds().width) : mouseX; 
				obj.stretch(-change, 0);
				obj.translate(change, 0);
			}
		}
	};
	
	/**
	 * Vertical resize action.
	 */
	private final OGLGUIAction RESIZABLE_BOTTOM = new OGLGUIAction()
	{
		@Override
		public void call(OGLGUIEvent event)
		{
			if (resizable)
			{
				OGLGUIObject obj = event.getObject().getParent();
				float mouseY = event.getMouseMovementY();
				float change = mouseY < 0f ? correctMouseMoveY(mouseY, obj.getBounds().height) : mouseY; 
				obj.stretch(0, change);
			}
		}
	};
	
	/**
	 * Vertical resize action.
	 */
	private final OGLGUIAction RESIZABLE_TOP = new OGLGUIAction()
	{
		@Override
		public void call(OGLGUIEvent event)
		{
			if (resizable)
			{
				OGLGUIObject obj = event.getObject().getParent();
				float mouseY = event.getMouseMovementY();
				float change = mouseY > 0f ? correctMouseMoveY(mouseY, obj.getBounds().height) : mouseY; 
				obj.stretch(0, -change);
				obj.translate(0, change);
			}
		}
	};
	
	/**
	 * Vertical resize action.
	 */
	private final OGLGUIAction RESIZABLE_TOP_OR_DRAG = new OGLGUIAction()
	{
		@Override
		public void call(OGLGUIEvent event)
		{
			if (draggable)
			{
				OGLGUIObject obj = event.getObject().getParent();
				obj.translate(event.getMouseMovementX(), event.getMouseMovementY());
			}
			else if (resizable)
			{
				OGLGUIObject obj = event.getObject().getParent();
				float mouseY = event.getMouseMovementY();
				float change = mouseY > 0f ? correctMouseMoveY(mouseY, obj.getBounds().height) : mouseY; 
				obj.stretch(0, -change);
				obj.translate(0, change);
			}
		}
	};
	
	/**
	 * Creates a new GUI panel.
	 * @param thickness the thickness of the outer frame in units.
	 */
	public OGLGUIFrame(float thickness)
	{
		this(0, 0, 0, 0, thickness);
	}
	
	/**
	 * Creates a new GUI object.
	 * @param width		its width.
	 * @param height	its height.
	 */
	public OGLGUIFrame(float width, float height, float thickness)
	{
		this(0, 0, width, height, thickness);
	}

	/**
	 * Creates a new GUI object.
	 * @param x			its position x.
	 * @param y			its position y.
	 * @param width		its width.
	 * @param height	its height.
	 */
	public OGLGUIFrame(float x, float y, float width, float height, float thickness)
	{
		super();
		setBounds(x, y, width, height);
		setLayout(new FramedLayout(thickness));
		
		this.thickness = thickness;
		this.resizable = false;
		this.draggable = false;
		
		topLeftPanel = new OGLGUIPanel();
		topLeftPanel.bindAction(RESIZABLE_TOP, EVENT_MOUSE_DRAG);
		topLeftPanel.bindAction(RESIZABLE_LEFT, EVENT_MOUSE_DRAG);
		addChild(topLeftPanel, FramedLayout.Attrib.TOP_LEFT);
		
		topCenterPanel = new OGLGUIPanel();
		topCenterPanel.bindAction(RESIZABLE_TOP_OR_DRAG, EVENT_MOUSE_DRAG);
		addChild(topCenterPanel, FramedLayout.Attrib.TOP_CENTER);

		topRightPanel = new OGLGUIPanel();
		topRightPanel.bindAction(RESIZABLE_TOP, EVENT_MOUSE_DRAG);
		topRightPanel.bindAction(RESIZABLE_RIGHT, EVENT_MOUSE_DRAG);
		addChild(topRightPanel, FramedLayout.Attrib.TOP_RIGHT);
		
		middleLeftPanel = new OGLGUIPanel();
		middleLeftPanel.bindAction(RESIZABLE_LEFT, EVENT_MOUSE_DRAG);
		addChild(middleLeftPanel, FramedLayout.Attrib.MIDDLE_LEFT);
		
		middleRightPanel = new OGLGUIPanel();
		middleRightPanel.bindAction(RESIZABLE_RIGHT, EVENT_MOUSE_DRAG);
		addChild(middleRightPanel, FramedLayout.Attrib.MIDDLE_RIGHT);
		
		bottomLeftPanel = new OGLGUIPanel();
		bottomLeftPanel.bindAction(RESIZABLE_BOTTOM, EVENT_MOUSE_DRAG);
		bottomLeftPanel.bindAction(RESIZABLE_LEFT, EVENT_MOUSE_DRAG);
		addChild(bottomLeftPanel, FramedLayout.Attrib.BOTTOM_LEFT);

		bottomCenterPanel = new OGLGUIPanel();
		bottomCenterPanel.bindAction(RESIZABLE_BOTTOM, EVENT_MOUSE_DRAG);
		addChild(bottomCenterPanel, FramedLayout.Attrib.BOTTOM_CENTER);

		bottomRightPanel = new OGLGUIPanel();
		bottomRightPanel.bindAction(RESIZABLE_BOTTOM, EVENT_MOUSE_DRAG);
		bottomRightPanel.bindAction(RESIZABLE_RIGHT, EVENT_MOUSE_DRAG);
		addChild(bottomRightPanel, FramedLayout.Attrib.BOTTOM_RIGHT);
		
		contentPanel = new OGLGUIPanel();
		addChild(contentPanel, FramedLayout.Attrib.CONTENT);
	}
	
	/**
	 * Correct X
	 */
	protected float correctMouseMoveX(float mouseX, float width)
	{
		float contentWidth = width - (thickness * 2);
		return (mouseX < 0 ? -1 : 1) * Math.min(contentWidth, Math.abs(mouseX)); 
	}
	
	/**
	 * Correct Y
	 */
	protected float correctMouseMoveY(float mouseY, float height)
	{
		float contentHeight = height - (thickness * 2);
		return (mouseY < 0 ? -1 : 1) * Math.min(contentHeight, Math.abs(mouseY)); 
	}
	
	/**
	 * Sets if this is resizable by dragging the mouse at the edges.
	 * @param resizable
	 */
	public void setResizable(boolean resizable)
	{
		this.resizable = resizable;
	}
	
	/**
	 * Sets if this is draggable by dragging the mouse at the top center panel.
	 * @param draggable
	 */
	public void setDraggable(boolean draggable)
	{
		this.draggable = draggable;
	}
	
	/**
	 * Gets the reference to the top-left panel in the frame.
	 */
	public OGLGUIPanel getTopLeftPanel()
	{
		return topLeftPanel;
	}

	/**
	 * Gets the reference to the top-center panel in the frame.
	 */
	public OGLGUIPanel getTopCenterPanel()
	{
		return topCenterPanel;
	}

	/**
	 * Gets the reference to the top-right panel in the frame.
	 */
	public OGLGUIPanel getTopRightPanel()
	{
		return topRightPanel;
	}

	/**
	 * Gets the reference to the bottom-left panel in the frame.
	 */
	public OGLGUIPanel getBottomLeftPanel()
	{
		return bottomLeftPanel;
	}

	/**
	 * Gets the reference to the bottom-center panel in the frame.
	 */
	public OGLGUIPanel getBottomCenterPanel()
	{
		return bottomCenterPanel;
	}

	/**
	 * Gets the reference to the bottom-right panel in the frame.
	 */
	public OGLGUIPanel getBottomRightPanel()
	{
		return bottomRightPanel;
	}

	/**
	 * Gets the reference to the middle-left panel in the frame.
	 */
	public OGLGUIPanel getMiddleLeftPanel()
	{
		return middleLeftPanel;
	}

	/**
	 * Gets the reference to the middle-right panel in the frame.
	 */
	public OGLGUIPanel getMiddleRightPanel()
	{
		return middleRightPanel;
	}

	/**
	 * Gets the reference to the content (middle) panel in the frame.
	 */
	public OGLGUIPanel getContentPanel()
	{
		return contentPanel;
	}

	
}
