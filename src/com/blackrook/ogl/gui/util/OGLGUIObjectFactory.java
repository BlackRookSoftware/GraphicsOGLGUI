/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui.util;

import com.blackrook.commons.list.List;
import com.blackrook.ogl.gui.OGLGUIObject;
import com.blackrook.ogl.gui.object.OGLGUIPanel;

/**
 * A factory class for generating GUI objects.
 * @author Matthew Tropiano
 */
public final class OGLGUIObjectFactory
{
	/**
	 * Generates a bunch of {@link OGLGUIObject}s for use. 
	 * @param generator the generator to use.
	 * @param objectCount the amount of objects to generate. 
	 * @return the objects created.
	 */
	public static OGLGUIPanel[] generatePanels(OGLGUIObjectGenerator generator, int objectCount)
	{
		List<OGLGUIPanel> outList = new List<OGLGUIPanel>();
		
		for (int i = 0; i < objectCount; i++)
		{
			OGLGUIPanel obj = new OGLGUIPanel();
			obj.setBounds(generator.nextBounds(i));
			obj.setColor(generator.nextColor(i));
			String[] names = generator.nextNames(i);
			if (names != null) obj.addName(names);
		}
		
		OGLGUIPanel[] out = new OGLGUIPanel[outList.size()];
		outList.toArray(out);
		return out;
	}
	
}
