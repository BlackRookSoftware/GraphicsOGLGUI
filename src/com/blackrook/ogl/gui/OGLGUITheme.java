/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui;

import com.blackrook.commons.hash.HashMap;
import com.blackrook.ogl.util.OGLSkin;

/**
 * GUI theme for objects and whole GUI layers. 
 * @author Matthew Tropiano
 */
public class OGLGUITheme
{
	/** Theme name. */
	protected String name;
	/** Key map. */
	protected HashMap<String, Object> skinMap;
	
	/**
	 * Creates a new theme.
	 * @param name the name of the theme.
	 */
	public OGLGUITheme(String name)
	{
		this.name = name;
		skinMap = new HashMap<String, Object>(8);
	}
	
	/**
	 * Sets a theme skin by key.
	 * Re-setting an existing key replaces it.
	 * @param key the theme key.
	 * @param skin the skin to use. if null, the key is removed.
	 */
	public void set(String key, OGLSkin skin)
	{
		if (skin == null)
			skinMap.removeUsingKey(key);
		else
			skinMap.put(key, skin);
	}
	
	/**
	 * Sets a theme font by key.
	 * Re-setting an existing key replaces it.
	 * @param key the theme key.
	 * @param font the skin to use. if null, the key is removed.
	 */
	public void set(String key, OGLGUIFontType font)
	{
		if (font == null)
			skinMap.removeUsingKey(key);
		else
			skinMap.put(key, font);
	}
	
	/**
	 * Returns a skin associated with a theme key, 
	 * or null if not a skin or none is associated.
	 * @param key the key to use.
	 */
	public OGLSkin getSkin(String key)
	{
		Object obj = skinMap.get(key);
		return obj != null && obj instanceof OGLSkin ? (OGLSkin)obj : null;
	}
	
	/**
	 * Returns a font associated with a theme key, 
	 * or null if not a font or none is associated.
	 * @param key the key to use.
	 */
	public OGLGUIFontType getFont(String key)
	{
		Object obj = skinMap.get(key);
		return obj != null && obj instanceof OGLGUIFontType ? (OGLGUIFontType)obj : null;
	}
	
}
