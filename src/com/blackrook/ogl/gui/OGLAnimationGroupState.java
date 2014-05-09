/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui;

import com.blackrook.ogl.util.enums.EasingType;
import com.blackrook.ogl.util.scene2d.OGLScene2DElement;

/**
 * Describes an animation to be performed on an object.
 * @author Matthew Tropiano
 */
public class OGLAnimationGroupState<T extends OGLScene2DElement>
{
	/** Animation group. */
	protected OGLAnimationGroup<T> animationGroup;
	/** Animation states. */
	protected OGLAnimationState<T>[] animationStates;
	/** Animation progress in milliseconds. */
	protected float progress;
	
	/** 
	 * Returns a mutable action state that is used by action queues.
	 * Requires pairing with an object. 
	 */
	@SuppressWarnings("unchecked")
	OGLAnimationGroupState(OGLAnimationGroup<T> animationGroup)
	{
		this.progress = 0f;
		this.animationGroup = animationGroup;

		OGLAnimation<T>[] anims = animationGroup.getAnimations();
		this.animationStates = new OGLAnimationState[anims.length];
		int x = 0;
		for (OGLAnimation<T> anim : anims)
			this.animationStates[x++] = anim.createState(animationGroup.getObject());
	}
	
	/**
	 * Gets the animation group associated with this state.
	 */
	public OGLAnimationGroup<T> getAnimationGroup()
	{
		return animationGroup;
	}

	/**
	 * Gets the progress in milliseconds on this state.
	 */
	public float getProgress()
	{
		return progress;
	}
	
	/**
	 * Updates the object animations on this group state.
	 * @param millis the milliseconds.
	 * @return the leftover time.
	 */
	public float update(float millis)
	{
		float duration = animationGroup.getDuration();
		
		float next = Math.min(progress + millis, duration);
		float leftover = (progress + millis) - next;
		progress = next;
		
		EasingType trans = animationGroup.getEasing();
		float p = trans.getSample(duration > 0f ? Math.min(progress / duration, 1f) : 1f);
		
		for (OGLAnimationState<T> state : animationStates)
			state.update(p);
			
		return leftover;
	}

}
