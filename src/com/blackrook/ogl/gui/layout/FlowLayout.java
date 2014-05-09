/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui.layout;

import com.blackrook.commons.linkedlist.Queue;
import com.blackrook.commons.list.List;
import com.blackrook.commons.math.geometry.Rectangle2F;
import com.blackrook.ogl.gui.OGLGUILayout;
import com.blackrook.ogl.gui.OGLGUIObject;
import com.blackrook.ogl.gui.object.OGLGUIGlassPanel;

/**
 * A layout for OGLGUIObjects that sets the position of its components inside
 * the parent. This does not resize components.
 * @author Matthew Tropiano
 */
public class FlowLayout implements OGLGUILayout
{
	/** Horizontal alignment types. */
	public static enum HAlignment
	{
		/** Objects are aligned to the left of the inside of the parent. */
		LEFT,
		/** Objects are aligned to the center of the inside of the parent. */
		CENTER,
		/** Objects are aligned to the right of the inside of the parent. */
		RIGHT;
	}
	
	/** Vertical alignment types. */
	public static enum VAlignment
	{
		/** Objects are aligned to the top of the inside of the parent. */
		TOP,
		/** Objects are aligned to the middle of the inside of the parent. */
		MIDDLE,
		/** Objects are aligned to the bottom of the inside of the parent. */
		BOTTOM;
	}

	/** Default gap size. */
	public static final float DEFAULT_GAP = 4f;
	
	/** Horizontal alignment type. */
	protected HAlignment hAlignment;
	/** Vertical alignment type. */
	protected VAlignment vAlignment;
	/** Horizontal gap between objects. */
	protected float horizontalGap;
	/** Vertical gap between objects. */
	protected float verticalGap;
	
	/** Current X space. */
	protected float sizeX;
	/** Current Y space. */
	protected float sizeY;
	
	/** Vector of objects. */
	protected List<Flow> currentLineup;
	
	/**
	 * Creates a new FlowLayout with a LEFT horizontal alignment, 
	 * TOP vertical alignment, and default gaps.
	 */
	public FlowLayout()
	{
		this(HAlignment.LEFT, VAlignment.TOP, DEFAULT_GAP, DEFAULT_GAP);
	}

	/**
	 * Creates a new FlowLayout with a specific horizontal alignment, 
	 * TOP vertical alignment, and default gaps.
	 * @param h the horizontal alignment of this layout.
	 */
	public FlowLayout(HAlignment h)
	{
		this(h, VAlignment.TOP, DEFAULT_GAP, DEFAULT_GAP);
	}

	/**
	 * Creates a new FlowLayout with specific alignments and default gaps.
	 * @param h the horizontal alignment of this layout.
	 * @param v the vertical alignment of this layout.
	 */
	public FlowLayout(HAlignment h, VAlignment v)
	{
		this(h, v, DEFAULT_GAP, DEFAULT_GAP);
	}

	/**
	 * Creates a new FlowLayout with specific alignments and horizontal gap.
	 * @param h the horizontal alignment of this layout.
	 * @param v the vertical alignment of this layout.
	 * @param hGap the gap between horizontal elements.
	 */
	public FlowLayout(HAlignment h, VAlignment v, float hGap)
	{
		this(h, v, hGap, DEFAULT_GAP);
	}

	/**
	 * Creates a new FlowLayout with specific alignments and gaps.
	 * @param h the horizontal alignment of this layout.
	 * @param v the vertical alignment of this layout.
	 * @param hGap the gap between horizontal elements.
	 * @param vGap the gap between vertical elements.
	 */
	public FlowLayout(HAlignment h, VAlignment v, float hGap, float vGap)
	{
		hAlignment = h;
		vAlignment = v;
		horizontalGap = hGap;
		verticalGap = vGap;
		currentLineup = new List<Flow>(3);
	}
	
	/**
	 * Creates a special panel that is used for deciding to 
	 * create a new line in the flow layout. 
	 */
	public static OGLGUIObject createNewLinePanel()
	{
		return new NewLinePanel();
	}

	@Override
	public void resizeChild(OGLGUIObject object, int index, int childTotal)
	{
		OGLGUIObject parent = object.getParent();
		Rectangle2F parentBounds = parent.getBounds();

		boolean firstChild = index == 0;
		boolean lastChild = index+1 == childTotal;
		
		// first child.
		if (firstChild)
			prepareLayout(parent);
		
		Rectangle2F bounds = object.getBounds();
		boolean added = false;
		
		while (!added && sizeY > 0)
		{
			Flow flow = currentLineup.getByIndex(currentLineup.size()-1);
			if (!(object instanceof NewLinePanel) && bounds.width <= sizeX)
			{
				flow.maxHeight = Math.max(flow.maxHeight, bounds.height);
				flow.totalWidth += bounds.width + (flow.queue.size() > 0 ? horizontalGap : 0f);
				flow.queue.add(object);
				sizeX -= bounds.width + horizontalGap;
				
				if (lastChild)
					sizeY -= flow.maxHeight;

				added = true;
			}
			else
			{
				currentLineup.add(new Flow());
				sizeY -= flow.maxHeight + (!lastChild ? verticalGap : 0f);
				sizeX = parentBounds.width;
				
				// skip newline
				if (object instanceof NewLinePanel)
					added = true;
			}
			
		}
		
		// last child.
		if (lastChild)
			orientChildren(parent);
		
	}

	protected void prepareLayout(OGLGUIObject parent)
	{
		Rectangle2F parentBounds = parent.getBounds();

		currentLineup.clear();
		currentLineup.add(new Flow());
		sizeX = parentBounds.width;
		sizeY = parentBounds.height;
	}
	
	protected void orientChildren(OGLGUIObject parent)
	{
		Rectangle2F parentBounds = parent.getBounds();
		
		float uy = parentBounds.height - sizeY;
		float oy = 
			vAlignment == VAlignment.TOP ? 0f :
			vAlignment == VAlignment.MIDDLE ? (parentBounds.height - uy) / 2f :
			vAlignment == VAlignment.BOTTOM ? parentBounds.height - uy : 0f;
			
		for (int i = 0; i < currentLineup.size(); i++)
		{
			Flow flow = currentLineup.getByIndex(i);
			float ox = 
				hAlignment == HAlignment.LEFT ? 0f : 
				hAlignment == HAlignment.CENTER ? (parentBounds.width - flow.totalWidth) / 2f : 
				hAlignment == HAlignment.RIGHT ? (parentBounds.width - flow.totalWidth) : 0f;
			
			for (OGLGUIObject obj : flow.queue)
			{
				obj.setPosition(ox, oy);
				ox += horizontalGap + obj.getBounds().width;
			}
			oy += verticalGap + flow.maxHeight;
		}
	}
	
	private static class Flow
	{
		public Queue<OGLGUIObject> queue;
		public float totalWidth;
		public float maxHeight;
		
		public Flow()
		{
			queue = new Queue<OGLGUIObject>();
			totalWidth = 0f;
			maxHeight = 0f;
		}
	}

	/**
	 * Special panel that acts as a "new line" in a flow.
	 * It's characteristics are never examined.
	 */
	protected static class NewLinePanel extends OGLGUIGlassPanel
	{
		private NewLinePanel()
		{
			super(0, 0, 0, 0);
		}

		@Override
		public boolean isInert()
		{
			return true;
		}

		@Override
		public boolean isVisible()
		{
			return true;
		}
	}
	
}
