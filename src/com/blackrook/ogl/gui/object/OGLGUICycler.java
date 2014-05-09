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
import com.blackrook.ogl.gui.model.IndexedModel;

/**
 * Option cycler abstract.
 * A cycler changes its value each click.
 * An {@link #EVENT_VALUE_CHANGE} event is fired each value change.
 */
public class OGLGUICycler<T extends Object> extends OGLGUILabel implements OGLGUIValueField<T>, OGLGUISelectable<T>
{
	/** The model to use. */
	protected IndexedModel<T> model;
	/** The current choice index. */
	protected int currentIndex;
	
	protected static final OGLGUIAction BASIC_ACTION = new OGLGUIAction()
	{
		@Override
		public void call(OGLGUIEvent event)
		{
			OGLGUICycler<?> obj = (OGLGUICycler<?>)event.getObject();
			
			if (event.isMouseEvent())
			{
				if (event.getMouseButton() == MOUSE_LEFT)
					obj.advanceSelection();
			}
			else if (event.isKeyboardEvent())
			{
				int keyCode = event.getKeyCode();
				if (keyCode == KEY_RIGHT)
					obj.advanceSelection();
				else if (keyCode == KEY_LEFT)
					obj.reverseSelection();
			}
			else if (event.isGamepadEvent())
			{
				if (event.getGamepadButton() == GAMEPAD_1)
					obj.advanceSelection();
				else if (event.getGamepadButton() == GAMEPAD_2)
					obj.reverseSelection();
			}
			else if (event.isGamepadAxisTapEvent())
			{
				if (event.getGamepadAxisId() == AXIS_X) // also = XBOX LEFT STICK X
				{
					if (event.getGamepadAxisTapValue())
						obj.advanceSelection();
					else
						obj.reverseSelection();
				}
			}
			
		}
	};
		
	/**
	 * Creates a new cycler with a set of items to cycle through.
	 * @param model the model that this cycler uses for values.
	 */
	public OGLGUICycler(IndexedModel<T> model)
	{
		super(null, "", Justification.CENTER);
		this.model = model;
		this.currentIndex = NO_SELECTION;

		bindAction(BASIC_ACTION, EVENT_KEY_PRESS, EVENT_MOUSE_CLICK, EVENT_GAMEPAD_PRESS, EVENT_GAMEPAD_TAP);
		setSelectedIndex(0);
	}

	@Override
	public void setSelectedIndex(int index)
	{
		index = index < 0 || index >= model.size() ? NO_SELECTION : index;
		
		if (currentIndex != index)
		{
			currentIndex = index;
			if (currentIndex != NO_SELECTION)
				setText(String.valueOf(model.getValueByIndex(currentIndex)));
			else
				setText("");
			fireEvent(EVENT_VALUE_CHANGE);
		}
	}

	@Override
	public int getSelectedIndex()
	{
		return currentIndex;
	}

	@Override
	public T getSelectedValue()
	{
		if (currentIndex == NO_SELECTION)
			return null;
		return model.getValueByIndex(currentIndex);
	}

	@Override
	public T getValue()
	{
		return getSelectedValue();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setValue(Object value)
	{
		setSelectedIndex(model.getIndexByValue((T)value));
	}
	
	/**
	 * Advances the cycler's selected index.
	 */
	protected void advanceSelection()
	{
		setSelectedIndex((currentIndex + 1) % model.size());
	}

	/**
	 * Reverses the cycler's selected index.
	 */
	protected void reverseSelection()
	{
		setSelectedIndex(currentIndex > 0 ? (currentIndex - 1) : model.size() - 1);
	}
	
}

