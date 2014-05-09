/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui;

import com.blackrook.commons.linkedlist.Queue;
import com.blackrook.ogl.util.enums.EasingType;

/**
 * Queue that holds the current action state plus
 * the rest in the list.
 * @author Matthew Tropiano
 */
public class OGLAnimationQueue<T extends OGLGUIObject>
{
	/** Object reference. */
	protected T object;
	/** Current action. */
	protected OGLAnimationGroupState<T> currentAction;
	/** Rest of actions. */
	protected Queue<OGLAnimationGroup<T>> animationList;
	
	/**
	 * Creates the action state queue.
	 */
	OGLAnimationQueue(T object)
	{
		this.object = object;
		animationList = new Queue<OGLAnimationGroup<T>>();
		currentAction = null;
	}
	
	public void add(float duration, EasingType type, OGLAnimation<T> ... actions)
	{
		animationList.add(new OGLAnimationGroup<T>(object, duration, type, actions));
	}
	
	/**
	 * Updates action states.
	 * @param millis the amount of milliseconds to update.
	 */
	public synchronized void update(float millis)
	{
		float leftover = millis;
		while (leftover > 0f && !isDone())
		{
			if (currentAction == null)
			{
				if (!animationList.isEmpty())
					currentAction = new OGLAnimationGroupState<T>(animationList.dequeue());
			}
			leftover = currentAction.update(millis);
			if (leftover > 0f)
				currentAction = null;
		}
	}
	
	/**
	 * Finishes out the animation.
	 */
	public synchronized void finish()
	{
		update(Integer.MAX_VALUE);
	}
	
	/**
	 * Stops the animation.
	 */
	public synchronized void abort()
	{
		currentAction = null;
		animationList.clear();
	}
	
	/**
	 * Is this done?
	 */
	public boolean isDone()
	{
		return currentAction == null && animationList.isEmpty();
	}
	
}
