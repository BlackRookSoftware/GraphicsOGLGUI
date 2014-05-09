/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui.object;

import com.blackrook.ogl.mesh.MeshView;

/**
 * A special panel whose {@link #getMeshView()} method always returns null, 
 * so it is not rendered itself, but still is a panel otherwise.
 * @author Matthew Tropiano
 */
public class OGLGUIGlassPanel extends OGLGUIPanel
{
	/**
	 * Creates a new GUI panel.
	 */
	public OGLGUIGlassPanel()
	{
		this(0, 0, 0, 0);
	}
	
	/**
	 * Creates a new GUI object.
	 * @param width		its width.
	 * @param height	its height.
	 */
	public OGLGUIGlassPanel(float width, float height)
	{
		super();
		setBounds(0, 0, width, height);
	}

	/**
	 * Creates a new GUI object.
	 * @param x			its position x.
	 * @param y			its position y.
	 * @param width		its width.
	 * @param height	its height.
	 */
	public OGLGUIGlassPanel(float x, float y, float width, float height)
	{
		super();
		setBounds(x, y, width, height);
	}
	
	@Override
	public MeshView getMeshView()
	{
		return null;
	}
	
}
