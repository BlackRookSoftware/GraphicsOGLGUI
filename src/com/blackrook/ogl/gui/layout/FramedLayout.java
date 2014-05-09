/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui.layout;

import com.blackrook.commons.math.geometry.Rectangle2F;
import com.blackrook.ogl.gui.OGLGUILayout;
import com.blackrook.ogl.gui.OGLGUIObject;

/**
 * A "framed" layout for {@link OGLGUIObject}s similar to {@link java.awt.BorderLayout}.
 * This resizes the (up to) nine children of the object to which this layout is bound into
 * a "frame" around the object with the {@link Attrib#CONTENT} attribute.
 * @author Matthew Tropiano
 */
public class FramedLayout implements OGLGUILayout
{
	/**
	 * Layout position attributes.
	 */
	public static enum Attrib
	{
		TOP_LEFT,
		TOP_CENTER,
		TOP_RIGHT,
		MIDDLE_LEFT,
		CONTENT,
		MIDDLE_RIGHT,
		BOTTOM_LEFT,
		BOTTOM_CENTER,
		BOTTOM_RIGHT;
	}

	/** Frame width in units. */
	protected float frameWidth;
	
	/** Top-left object lifted from resizeChild(). */
	protected OGLGUIObject topLeft; 
	/** Top-center object lifted from resizeChild(). */
	protected OGLGUIObject topCenter; 
	/** Top-right object lifted from resizeChild(). */
	protected OGLGUIObject topRight; 
	/** Middle-left object lifted from resizeChild(). */
	protected OGLGUIObject middleLeft; 
	/** Content object lifted from resizeChild(). */
	protected OGLGUIObject content; 
	/** Middle-right object lifted from resizeChild(). */
	protected OGLGUIObject middleRight; 
	/** Bottom-left object lifted from resizeChild(). */
	protected OGLGUIObject bottomLeft; 
	/** Bottom-center object lifted from resizeChild(). */
	protected OGLGUIObject bottomCenter; 
	/** Bottom-right object lifted from resizeChild(). */
	protected OGLGUIObject bottomRight; 
	
	/**
	 * Creates a new FramedLayout.
	 */
	public FramedLayout(float width)
	{
		this.frameWidth = width;
		
		topLeft = null;
		topCenter = null;
		topRight = null;
		middleLeft = null;
		content = null;
		middleRight = null;
		bottomLeft = null;
		bottomCenter = null;
		bottomRight = null;
	}
	
	@Override
	public void resizeChild(OGLGUIObject object, int index, int childTotal)
	{
		if (index == 0)
		{
			topLeft = null;
			topCenter = null;
			topRight = null;
			middleLeft = null;
			content = null;
			middleRight = null;
			bottomLeft = null;
			bottomCenter = null;
			bottomRight = null;
		}
		
		Object attr = object.getLayoutAttrib();
		
		if (attr == Attrib.TOP_LEFT)
			topLeft = object;
		else if (attr == Attrib.TOP_CENTER)
			topCenter = object;
		else if (attr == Attrib.TOP_RIGHT)
			topRight = object;
		else if (attr == Attrib.MIDDLE_LEFT)
			middleLeft = object;
		else if (attr == Attrib.CONTENT)
			content = object;
		else if (attr == Attrib.MIDDLE_RIGHT)
			middleRight = object;
		else if (attr == Attrib.BOTTOM_LEFT)
			bottomLeft = object;
		else if (attr == Attrib.BOTTOM_CENTER)
			bottomCenter = object;
		else if (attr == Attrib.BOTTOM_RIGHT)
			bottomRight = object;
		else if (attr == null && content == null)
			content = object;

		if (index+1 == childTotal)
			orientChildren();
			
	}
	
	/**
	 * Once objects with the required attributes are found,
	 * this will resize and reposition them.
	 */
	protected void orientChildren()
	{
		float x = 0f;
		float y = 0f;
		float w = 0f;
		float h = 0f;
		
		if (topLeft != null)
		{
			Rectangle2F pb = topLeft.getParent().getBounds();
			x = 0f;
			y = 0f;
			w = topCenter != null ? frameWidth :
				topRight != null ? pb.width - frameWidth :
				pb.width;
			h = frameWidth;
			topLeft.setBounds(x, y, w, h);
		}
		
		if (topCenter != null)
		{
			Rectangle2F pb = topCenter.getParent().getBounds();
			x = topLeft != null ? frameWidth :
				0f;
			y = 0f;
			w = pb.width - (topLeft != null ? frameWidth : 0f) - (topRight != null ? frameWidth : 0f); 
			h = frameWidth;
			topCenter.setBounds(x, y, w, h);
		}

		if (topRight != null)
		{
			Rectangle2F pb = topRight.getParent().getBounds();
			x = topCenter != null ? pb.width - frameWidth : 
				topLeft != null ? pb.width - frameWidth : 
				0f;
			y = 0f;
			w = frameWidth; 
			h = frameWidth;
			topRight.setBounds(x, y, w, h);
		}
		
		if (middleLeft != null)
		{
			Rectangle2F pb = middleLeft.getParent().getBounds();
			boolean hasTop = topLeft != null || topCenter != null || topRight != null;
			boolean hasBottom = bottomLeft != null || bottomCenter != null || bottomRight != null;
			x = 0f;
			y = hasTop ? frameWidth : 0f;
			w = content != null ? frameWidth :
				middleRight != null ? pb.width - frameWidth :
				pb.width; 
			h = pb.height - (hasTop ? frameWidth : 0f) - (hasBottom ? frameWidth : 0f);
			middleLeft.setBounds(x, y, w, h);
		}

		if (content != null)
		{
			Rectangle2F pb = content.getParent().getBounds();
			boolean hasTop = topLeft != null || topCenter != null || topRight != null;
			boolean hasBottom = bottomLeft != null || bottomCenter != null || bottomRight != null;
			x = middleLeft != null ? frameWidth : 0f;
			y = hasTop ? frameWidth : 0f;
			w = pb.width - (middleLeft != null ? frameWidth : 0f) - (middleRight != null ? frameWidth : 0f); 
			h = pb.height - (hasTop ? frameWidth : 0f) - (hasBottom ? frameWidth : 0f);
			content.setBounds(x, y, w, h);
		}

		if (middleRight != null)
		{
			Rectangle2F pb = middleRight.getParent().getBounds();
			boolean hasTop = topLeft != null || topCenter != null || topRight != null;
			boolean hasBottom = bottomLeft != null || bottomCenter != null || bottomRight != null;
			x = content != null ? pb.width - frameWidth :
				middleLeft != null ? pb.width - frameWidth :
				0f;
			y = hasTop ? frameWidth : 0f;
			w = content != null ? frameWidth :
				middleLeft != null ? frameWidth :
				pb.width; 
			h = pb.height - (hasTop ? frameWidth : 0f) - (hasBottom ? frameWidth : 0f);
			middleRight.setBounds(x, y, w, h);
		}

		if (bottomLeft != null)
		{
			Rectangle2F pb = bottomLeft.getParent().getBounds();
			boolean hasTop = topLeft != null || topCenter != null || topRight != null;
			boolean hasMiddle = middleLeft != null || content != null || middleRight != null;
			x = 0f;
			y = hasMiddle ? pb.height - frameWidth : 
				hasTop ? frameWidth : 
				0f;
			w = bottomCenter != null ? frameWidth :
				bottomRight != null ? pb.width - frameWidth :
				pb.width; 
			h = hasMiddle ? frameWidth : 
				hasTop ? pb.height - frameWidth :
				pb.height;
			bottomLeft.setBounds(x, y, w, h);
		}

		if (bottomCenter != null)
		{
			Rectangle2F pb = bottomCenter.getParent().getBounds();
			boolean hasTop = topLeft != null || topCenter != null || topRight != null;
			boolean hasMiddle = middleLeft != null || content != null || middleRight != null;
			x = bottomLeft != null ? frameWidth :
				0f;
			y = hasMiddle ? pb.height - frameWidth : 
				hasTop ? frameWidth : 
				0f;
			w = pb.width - (bottomLeft != null ? frameWidth : 0f) - (bottomRight != null ? frameWidth : 0f); 
			h = hasMiddle ? frameWidth : 
				hasTop ? pb.height - frameWidth :
				pb.height;
			bottomCenter.setBounds(x, y, w, h);
		}

		if (bottomRight != null)
		{
			Rectangle2F pb = bottomRight.getParent().getBounds();
			boolean hasTop = topLeft != null || topCenter != null || topRight != null;
			boolean hasMiddle = middleLeft != null || content != null || middleRight != null;
			x = bottomCenter != null ? pb.width - frameWidth :
				bottomLeft != null ? frameWidth :
				0f;
			y = hasMiddle ? pb.height - frameWidth : 
				hasTop ? frameWidth : 
				0f;
			w = bottomCenter != null ? frameWidth :
				bottomLeft != null ? pb.width - frameWidth :
				pb.width; 
			h = hasMiddle ? frameWidth : 
				hasTop ? pb.height - frameWidth :
				pb.height;
			bottomRight.setBounds(x, y, w, h);
		}
	}

}
