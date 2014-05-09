/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui;

import com.blackrook.commons.math.RMath;
import com.blackrook.commons.math.geometry.Rectangle2F;
import com.blackrook.ogl.data.OGLColor;
import com.blackrook.ogl.util.OGLSkin;

/**
 * Describes an animation to be performed on an object in the GUI system.
 * @author Matthew Tropiano
 */
public abstract class OGLGUIAnimation extends OGLAnimation<OGLGUIObject>
{
	/**
	 * Creates a new GUI animation that performs a color transition.
	 * @param color the new color.
	 */
	public static OGLGUIAnimation color(OGLColor color)
	{
		return new ColorAnim(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	/**
	 * Creates a new GUI animation that performs a color transition.
	 * @param red the red color component (0 to 1).
	 * @param green the green color component (0 to 1).
	 * @param blue the blue color component (0 to 1).
	 * @param alpha the alpha color component (0 to 1).
	 */
	public static OGLGUIAnimation color(float red, float green, float blue, float alpha)
	{
		return new ColorAnim(red, green, blue, alpha);
	}

	/**
	 * Creates a new GUI animation that performs an opacity transition.
	 * @param opacity the new opacity (0 to 1).
	 */
	public static OGLGUIAnimation opacity(float opacity)
	{
		return new OpacityAnim(opacity);
	}

	/**
	 * Creates a new GUI animation that performs an object position transition.
	 * Either of the following parameters can be null to designate no change.
	 * @param x the new x-coordinate.
	 * @param y the new y-coordinate.
	 */
	public static OGLGUIAnimation position(Float x, Float y)
	{
		return new PositionAnim(x, y, null, null);
	}

	/**
	 * Creates a new GUI animation that performs an object dimension transition.
	 * Either of the following parameters can be null to designate no change.
	 * @param width the new width.
	 * @param height the new height.
	 */
	public static OGLGUIAnimation dimension(Float width, Float height)
	{
		return new PositionAnim(null, null, width, height);
	}

	/**
	 * Creates a new GUI animation that performs an object bounds transition.
	 * Any of the following parameters can be null to designate no change.
	 * @param x the new x-coordinate.
	 * @param y the new y-coordinate.
	 * @param width the new width.
	 * @param height the new height.
	 */
	public static OGLGUIAnimation bounds(Float x, Float y, Float width, Float height)
	{
		return new PositionAnim(x, y, width, height);
	}

	/**
	 * Creates a new GUI animation that performs a rotation transition.
	 * @param rotation the new rotation (in degrees).
	 */
	public static OGLGUIAnimation rotation(float rotation)
	{
		return new RotationAnim(rotation);
	}

	/**
	 * Creates a new GUI animation that performs an object visibility change.
	 * @param visible true for visible, false for invisible.
	 */
	public static OGLGUIAnimation visible(boolean visible)
	{
		return new VisibleAnim(visible);
	}

	/**
	 * Creates a new GUI animation that sets a skin on the object.
	 * @param skins the OGLSkins to set (in order). Can be null.
	 */
	public static OGLGUIAnimation skin(OGLSkin ... skins)
	{
		return new SkinAnim(skins);
	}

	/**
	 * Creates a new GUI animation that calls an action on the object.
	 * @param action the OGLGUIAction to call.
	 */
	public static OGLGUIAnimation action(OGLGUIAction action)
	{
		return new ActionAnim(action);
	}

	/**
	 * An action that creates a color transition.
	 */
	private static class ColorAnim extends OGLGUIAnimation
	{
		/** Ending color. */
		private float r1, g1, b1, a1;

		/**
		 * Creates a new GUI action that performs a color transition.
		 */
		public ColorAnim(float red, float green, float blue, float alpha)
		{
			r1 = red;
			g1 = green;
			b1 = blue;
			a1 = alpha;
		}

		@Override
		public OGLAnimationState<OGLGUIObject> createState(OGLGUIObject object)
		{
			return new State(object);
		}
		
		public class State extends OGLAnimationState<OGLGUIObject>
		{
			/** Starting color. */
			private float r0, g0, b0, a0;
			
			State(OGLGUIObject object)
			{
				super(object);
				OGLColor color = object.getColor();
				r0 = color.getRed();
				g0 = color.getGreen();
				b0 = color.getBlue();
				a0 = color.getAlpha();
			}

			@Override
			public void update(float percentProgress)
			{
				object.setColor(
					(float)RMath.linearInterpolate(percentProgress, r0, r1),
					(float)RMath.linearInterpolate(percentProgress, g0, g1),
					(float)RMath.linearInterpolate(percentProgress, b0, b1),
					(float)RMath.linearInterpolate(percentProgress, a0, a1)
					);
			}
		}
	}
	
	/**
	 * An action that creates a color transition.
	 */
	private static class OpacityAnim extends OGLGUIAnimation
	{
		/** Ending opacity. */
		private float op1;

		/**
		 * Creates a new GUI action that performs an opacity transition.
		 */
		public OpacityAnim(float opacity)
		{
			op1 = opacity;
		}

		@Override
		public OGLAnimationState<OGLGUIObject> createState(OGLGUIObject object)
		{
			return new State(object);
		}
		
		public class State extends OGLAnimationState<OGLGUIObject>
		{
			/** Starting color. */
			private float op0;
			
			State(OGLGUIObject object)
			{
				super(object);
				op0 = object.getOpacity();
			}

			@Override
			public void update(float percentProgress)
			{
				object.setOpacity((float)RMath.linearInterpolate(percentProgress, op0, op1));
			}
		}
	}
	
	/**
	 * An action that creates a position transition.
	 */
	private static class PositionAnim extends OGLGUIAnimation
	{
		/** Ending position. */
		private Float x1, y1, width1, height1;

		/**
		 * Creates a new GUI action that performs a position transition.
		 * @param x the new x-coordinate.
		 * @param y the new y-coordinate.
		 * @param width the new width.
		 * @param height the new height.
		 */
		public PositionAnim(Float x, Float y, Float width, Float height)
		{
			x1 = x;
			y1 = y;
			width1 = width;
			height1 = height;
		}

		@Override
		public OGLAnimationState<OGLGUIObject> createState(OGLGUIObject object)
		{
			return new State(object);
		}
		
		public class State extends OGLAnimationState<OGLGUIObject>
		{
			/** Starting position. */
			private float x0, y0, width0, height0;

			State(OGLGUIObject object)
			{
				super(object);
				Rectangle2F r = object.getBounds();
				x0 = r.x;
				y0 = r.y;
				width0 = r.width;
				height0 = r.height;
			}

			@Override
			public void update(float percentProgress)
			{
				object.setBounds(
					x1 != null ? (float)RMath.linearInterpolate(percentProgress, x0, x1) : x0,
					y1 != null ? (float)RMath.linearInterpolate(percentProgress, y0, y1) : y0,
					width1 != null ? (float)RMath.linearInterpolate(percentProgress, width0, width1) : width0,
					height1 != null ? (float)RMath.linearInterpolate(percentProgress, height0, height1) : height0
					);
			}
		}
	}

	/**
	 * An action that sets visible state.
	 */
	private static class VisibleAnim extends OGLGUIAnimation
	{
		/** Ending flag. */
		private boolean visible;
		/** Do once flag. */
		private boolean flag;

		/**
		 * Creates a new GUI action that sets visible state or not.
		 * @param visible the new state.
		 */
		public VisibleAnim(boolean visible)
		{
			this.visible = visible;
			flag = false;
		}

		@Override
		public OGLAnimationState<OGLGUIObject> createState(OGLGUIObject object)
		{
			return new State(object);
		}
		
		public class State extends OGLAnimationState<OGLGUIObject>
		{
			State(OGLGUIObject object)
			{
				super(object);
			}

			@Override
			public void update(float percentProgress)
			{
				if (!flag)
				{
					object.setVisible(visible);
					flag = !flag;
				}
			}
		}
	}
	
	/**
	 * Action that sets a skin.
	 */
	private static class SkinAnim extends OGLGUIAnimation
	{
		/** New skin to set. */
		private OGLSkin[] skins;

		/**
		 * Creates a new animation that sets skins.
		 */
		public SkinAnim(OGLSkin ... skins)
		{
			this.skins = new OGLSkin[skins.length];
			System.arraycopy(skins, 0, this.skins, 0, skins.length);
		}
		
		@Override
		public OGLAnimationState<OGLGUIObject> createState(OGLGUIObject object)
		{
			return new State(object);
		}
		
		public class State extends OGLAnimationState<OGLGUIObject>
		{
			State(OGLGUIObject object)
			{
				super(object);
			}

			@Override
			public void update(float percentProgress)
			{
				object.setSkin(skins[(int)(percentProgress * skins.length)]);
			}
		}
	}
	
	/**
	 * Action that sets a skin.
	 */
	private static class ActionAnim extends OGLGUIAnimation
	{
		/** New skin to set. */
		private OGLGUIAction action;
		/** Do once flag. */
		private boolean flag;

		/**
		 * Creates a new animation that sets a skin.
		 */
		public ActionAnim(OGLGUIAction action)
		{
			this.action = action;
			flag = false;
		}
		
		@Override
		public OGLAnimationState<OGLGUIObject> createState(OGLGUIObject object)
		{
			return new State(object);
		}
		
		public class State extends OGLAnimationState<OGLGUIObject>
		{
			State(OGLGUIObject object)
			{
				super(object);
			}

			@Override
			public void update(float percentProgress)
			{
				if (!flag)
				{
					object.callAction(action);
					flag = !flag;
				}
			}
		}
	}

	/**
	 * An action that does a rotation.
	 */
	private static class RotationAnim extends OGLGUIAnimation
	{
		/** Ending rotation. */
		private float rotation1;
		
		/**
		 * Creates a new Cinema action that sets rotation.
		 * @param rotation the new rotation.
		 */
		public RotationAnim(float rotation)
		{
			rotation1 = rotation;
		}

		@Override
		public OGLAnimationState<OGLGUIObject> createState(OGLGUIObject object)
		{
			return new State(object);
		}
		
		public class State extends OGLAnimationState<OGLGUIObject>
		{
			/** Starting rotation. */
			private float rotation0;
			
			State(OGLGUIObject object)
			{
				super(object);
				rotation0 = object.getRenderRotationZ();
			}

			@Override
			public void update(float percentProgress)
			{
				object.setRotationZ((float)RMath.linearInterpolate(percentProgress, rotation0, rotation1));
			}

		}
	}

}
