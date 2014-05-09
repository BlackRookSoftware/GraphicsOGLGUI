/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui.object;

import com.blackrook.commons.math.geometry.Rectangle2F;
import com.blackrook.ogl.gui.OGLGUIAnimation;
import com.blackrook.ogl.gui.OGLGUILayout;
import com.blackrook.ogl.gui.OGLGUIObject;
import com.blackrook.ogl.gui.model.RangeModel;
import com.blackrook.ogl.mesh.MeshView;
import com.blackrook.ogl.util.enums.EasingType;

/**
 * Implementation of a progress bar.
 * <p>
 * How much the bar is "filled" is according to the attached model
 * and the current value on the bar. All child components on this bar are
 * inert and do not respond to input, nor can be focused.
 * <p>
 * A transition type can be set on the bar, for animating the bar as it
 * changes.
 * <p>
 * NOTE: The "thumb" object on the bar is NOT VISIBLE (setVisible(false)) by default!
 * <p>
 * This object and its descendants return specific theme keys depending on the current state of the bar.
 * <p>
 * <b>Thumb:</b>
 * <table>
 * <tr>
 * 		<td>Horizontal</td>
 * 		<td>{@link OGLGUIProgressBar#THEME_KEY_PROGRESS_THUMB_HORIZONTAL}</td>
 * </tr>
 * <tr>
 * 		<td>Horizontal Inverse</td>
 * 		<td>{@link OGLGUIProgressBar#THEME_KEY_PROGRESS_THUMB_HORIZONTAL_INVERSE}</td>
 * </tr>
 * <tr>
 * 		<td>Vertical</td>
 * 		<td>{@link OGLGUIProgressBar#THEME_KEY_PROGRESS_THUMB_VERTICAL}</td>
 * </tr>
 * <tr>
 * 		<td>Vertical Inverse</td>
 * 		<td>{@link OGLGUIProgressBar#THEME_KEY_PROGRESS_THUMB_VERTICAL_INVERSE}</td>
 * </tr>
 * </table>
 * <p>
 * <b>Full Bar:</b>
 * <table>
 * <tr>
 * 		<td>Horizontal</td>
 * 		<td>{@link OGLGUIProgressBar#THEME_KEY_PROGRESS_FULL_HORIZONTAL}</td>
 * </tr>
 * <tr>
 * 		<td>Horizontal Inverse</td>
 * 		<td>{@link OGLGUIProgressBar#THEME_KEY_PROGRESS_FULL_HORIZONTAL_INVERSE}</td>
 * </tr>
 * <tr>
 * 		<td>Vertical</td>
 * 		<td>{@link OGLGUIProgressBar#THEME_KEY_PROGRESS_FULL_VERTICAL}</td>
 * </tr>
 * <tr>
 * 		<td>Vertical Inverse</td>
 * 		<td>{@link OGLGUIProgressBar#THEME_KEY_PROGRESS_FULL_VERTICAL_INVERSE}</td>
 * </tr>
 * </table>
 * <p>
 * <b>Empty Bar:</b>
 * <table>
 * <tr>
 * 		<td>Horizontal</td>
 * 		<td>{@link OGLGUIProgressBar#THEME_KEY_PROGRESS_EMPTY_HORIZONTAL}</td>
 * </tr>
 * <tr>
 * 		<td>Horizontal Inverse</td>
 * 		<td>{@link OGLGUIProgressBar#THEME_KEY_PROGRESS_EMPTY_HORIZONTAL_INVERSE}</td>
 * </tr>
 * <tr>
 * 		<td>Vertical</td>
 * 		<td>{@link OGLGUIProgressBar#THEME_KEY_PROGRESS_EMPTY_VERTICAL}</td>
 * </tr>
 * <tr>
 * 		<td>Vertical Inverse</td>
 * 		<td>{@link OGLGUIProgressBar#THEME_KEY_PROGRESS_EMPTY_VERTICAL_INVERSE}</td>
 * </tr>
 * </table>
 * @author Matthew Tropiano
 * @param <T>
 */
public class OGLGUIProgressBar<T extends Object> extends OGLGUIGlassPanel implements OGLGUIValueField<T>
{
	/** Theme key for thumb, horizontal. */
	public static final String THEME_KEY_PROGRESS_THUMB_HORIZONTAL = "progress_thumb_horizontal";
	/** Theme key for full bar, horizontal. */
	public static final String THEME_KEY_PROGRESS_FULL_HORIZONTAL = "progress_full_horizontal";
	/** Theme key for empty bar, horizontal. */
	public static final String THEME_KEY_PROGRESS_EMPTY_HORIZONTAL = "progress_empty_horizontal";
	/** Theme key for thumb, horizontal inverse. */
	public static final String THEME_KEY_PROGRESS_THUMB_HORIZONTAL_INVERSE = "progress_thumb_horizontal_inverse";
	/** Theme key for full bar, horizontal inverse. */
	public static final String THEME_KEY_PROGRESS_FULL_HORIZONTAL_INVERSE = "progress_full_horizontal_inverse";
	/** Theme key for empty bar, horizontal inverse. */
	public static final String THEME_KEY_PROGRESS_EMPTY_HORIZONTAL_INVERSE = "progress_empty_horizontal_inverse";
	/** Theme key for thumb, vertical. */
	public static final String THEME_KEY_PROGRESS_THUMB_VERTICAL = "progress_thumb_vertical";
	/** Theme key for full bar, vertical. */
	public static final String THEME_KEY_PROGRESS_FULL_VERTICAL = "progress_full_vertical";
	/** Theme key for empty bar, vertical. */
	public static final String THEME_KEY_PROGRESS_EMPTY_VERTICAL = "progress_empty_vertical";
	/** Theme key for thumb, vertical inverse. */
	public static final String THEME_KEY_PROGRESS_THUMB_VERTICAL_INVERSE = "progress_thumb_vertical_inverse";
	/** Theme key for full bar, vertical inverse. */
	public static final String THEME_KEY_PROGRESS_FULL_VERTICAL_INVERSE = "progress_full_vertical_inverse";
	/** Theme key for empty bar, vertical inverse. */
	public static final String THEME_KEY_PROGRESS_EMPTY_VERTICAL_INVERSE = "progress_empty_vertical_inverse";

	/** The name given to the thumb between the "full" and "empty" bars. */
	public static final String THUMB_NAME = "thumb";
	/** The name given to the full bar. */
	public static final String FULL_NAME = "full";
	/** The name given to the empty bar. */
	public static final String EMPTY_NAME = "empty";

	/** Bar style enumeration. */
	public static enum Style
	{
		/** Bar fills left to right. */
		HORIZONTAL,
		/** Bar fills right to left. */
		HORIZONTAL_INVERSE,
		/** Bar fills bottom to top. */
		VERTICAL,
		/** Bar fills top to bottom. */
		VERTICAL_INVERSE;
	}
	
	/**
	 * Layout attributes for slider components.
	 */
	private static enum LayoutAttrib
	{
		/** Bar thumb. */
		THUMB,
		/** Full bar. */
		FULL,
		/** Empty bar. */
		EMPTY;
	}
	
	/** Reference to itself. */
	private OGLGUIProgressBar<?> thisRef;

	/** Bar style. */
	protected Style style;
	
	/** Transition type. */
	protected EasingType transitionType;
	/** Transition time (milliseconds). */
	protected float transitionTime;
	/** Rectangle to use for bounds in transitions. */
	private Rectangle2F transRectangle;

	/** Bar model. */
	protected RangeModel<T> model;
	/** Internal object for the thumb. */
	protected OGLGUIObject thumbObject; 
	/** Internal object for the full bar. */
	protected OGLGUIObject fullBarObject; 
	/** Internal object for the empty bar. */
	protected OGLGUIObject emptyBarObject; 

	/** Current value. */
	protected T currentValue;


	/**
	 * Creates a new progress bar using the provided model and style.
	 * @param style the desired style.
	 * @param model the value model.
	 */
	public OGLGUIProgressBar(RangeModel<T> model, Style style)
	{
		thisRef = this;
		this.style = style;
		this.model = model;
		this.currentValue = model.getValueForScalar(0.0);
		this.transitionTime = 500f;
		this.transitionType = null;
		this.transRectangle = new Rectangle2F();
		
		setLayout(new ProgressBarLayout());
		
		thumbObject = new Thumb();
		fullBarObject = new FullBar();
		emptyBarObject = new EmptyBar();
		
		addChild(fullBarObject, LayoutAttrib.FULL);
		addChild(emptyBarObject, LayoutAttrib.EMPTY);
		addChild(thumbObject, LayoutAttrib.THUMB);
	}

	/**
	 * Gets the current transition type.
	 */
	public EasingType getTransitionType()
	{
		return transitionType;
	}

	/**
	 * Sets the current transition type.
	 * This is the transition used when a value changes.
	 */
	public void setTransitionType(EasingType transitionType)
	{
		this.transitionType = transitionType;
	}

	/**
	 * Gets the current transition time in milliseconds.
	 */
	public float getTransitionTime()
	{
		return transitionTime;
	}

	/**
	 * Sets the current transition time in milliseconds.
	 */
	public void setTransitionTime(float transitionTime)
	{
		this.transitionTime = transitionTime;
	}

	/**
	 * Returns a reference to the thumb object.
	 */
	public OGLGUIObject getThumbObject()
	{
		return thumbObject;
	}

	/**
	 * Returns a reference to the full bar object.
	 */
	public OGLGUIObject getFullBarObject()
	{
		return fullBarObject;
	}

	/**
	 * Returns a reference to the empty bar object.
	 */
	public OGLGUIObject getEmptyBarObject()
	{
		return emptyBarObject;
	}

	/** 
	 * Gets the current slider style. 
	 */
	public Style getStyle()
	{
		return style;
	}


	@Override
	public T getValue()
	{
		return currentValue;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setValue(Object value)
	{
		T v = (T)value;
		if (!v.equals(currentValue))
		{
			currentValue = v;
			fireEvent(EVENT_VALUE_CHANGE);
			if (transitionType != null && transitionTime > 0f)
				performTransition();
			else
				resizeChildren();
		}
	}

	/**
	 * Perform the transition to the next value.
	 */
	private void performTransition()
	{
		getFullBounds(transRectangle);
		fullBarObject.animateAbort();
		fullBarObject.animate(
			transitionTime, 
			transitionType, 
			OGLGUIAnimation.bounds(
				transRectangle.x, transRectangle.y, 
				transRectangle.width, transRectangle.height
				));
		getEmptyBounds(transRectangle);
		emptyBarObject.animateAbort();
		emptyBarObject.animate(
			transitionTime, 
			transitionType, 
			OGLGUIAnimation.bounds(
				transRectangle.x, transRectangle.y, 
				transRectangle.width, transRectangle.height
				));
		getThumbBounds(transRectangle);
		thumbObject.animateAbort();
		thumbObject.animate( 
			transitionTime, 
			transitionType, 
			OGLGUIAnimation.bounds(
				transRectangle.x, transRectangle.y, 
				transRectangle.width, transRectangle.height
				));
	}
	
	/**
	 * Gets what the "full" bar's bounds should be according to
	 * the current value and model.
	 */
	private void getFullBounds(Rectangle2F bounds)
	{
		Rectangle2F parentBounds = getBounds();
		float scalar = (float)model.getScalarForValue(currentValue);
		
		switch (style)
		{
			default:
			case HORIZONTAL:
			{
				bounds.x = 0f;
				bounds.y = 0f;
				bounds.width = parentBounds.width * scalar;
				bounds.height = parentBounds.height;
			}
				break;
			case HORIZONTAL_INVERSE:
			{
				bounds.x = parentBounds.width - (parentBounds.width * scalar);
				bounds.y = 0f;
				bounds.width = parentBounds.width * scalar;
				bounds.height = parentBounds.height;
			}
				break;
			case VERTICAL:
			{
				bounds.x = 0f;
				bounds.y = parentBounds.height - (parentBounds.height * scalar);
				bounds.width = parentBounds.width;
				bounds.height = parentBounds.height * scalar;
			}
				break;
			case VERTICAL_INVERSE:
			{
				bounds.x = 0f;
				bounds.y = 0f;
				bounds.width = parentBounds.width;
				bounds.height = parentBounds.height * scalar;
			}
				break;
		}
	}
	
	/**
	 * Gets what the "empty" bar's bounds should be according to
	 * the current value and model.
	 */
	private void getEmptyBounds(Rectangle2F bounds)
	{
		Rectangle2F parentBounds = getBounds();
		float scalar = (float)model.getScalarForValue(currentValue);
		float antiscalar = 1f - scalar;
		
		switch (style)
		{
			case HORIZONTAL:
			{
				bounds.x = parentBounds.width * scalar;
				bounds.y = 0f;
				bounds.width = parentBounds.width * antiscalar;
				bounds.height = parentBounds.height;
			}
				break;
			case HORIZONTAL_INVERSE:
			{
				bounds.x = 0f;
				bounds.y = 0f;
				bounds.width = parentBounds.width * antiscalar;
				bounds.height = parentBounds.height;
			}
				break;
			case VERTICAL:
			{
				bounds.x = 0f;
				bounds.y = 0f;
				bounds.width = parentBounds.width;
				bounds.height = parentBounds.height * antiscalar;
			}
				break;
			case VERTICAL_INVERSE:
			{
				bounds.x = 0f;
				bounds.y = parentBounds.height * scalar;
				bounds.width = parentBounds.width;
				bounds.height = parentBounds.height * antiscalar;
			}
				break;
		}
	}
	
	/**
	 * Gets what the thumb's bounds should be according to
	 * the current value and model.
	 */
	private void getThumbBounds(Rectangle2F bounds)
	{
		Rectangle2F parentBounds = getBounds();
		float scalar = (float)model.getScalarForValue(currentValue);
		
		switch (style)
		{
			case HORIZONTAL:
			{
				bounds.x = parentBounds.width * scalar - (parentBounds.height / 2f);
				bounds.y = 0f;
				bounds.width = parentBounds.height;
				bounds.height = parentBounds.height;
			}
				break;
			case HORIZONTAL_INVERSE:
			{
				bounds.x = parentBounds.width - (parentBounds.width * scalar + (parentBounds.height / 2f));
				bounds.y = 0f;
				bounds.width = parentBounds.height;
				bounds.height = parentBounds.height;
			}
				break;
			case VERTICAL:
			{
				bounds.x = 0f;
				bounds.y = parentBounds.height - (parentBounds.height * scalar + (parentBounds.width / 2f));
				bounds.width = parentBounds.width;
				bounds.height = parentBounds.width;
			}
				break;
			case VERTICAL_INVERSE:
			{
				bounds.x = 0f;
				bounds.y = parentBounds.height * scalar - (parentBounds.width / 2f);
				bounds.width = parentBounds.width;
				bounds.height = parentBounds.width;
			}
				break;
		}
	}
	
	/**
	 * Progress bar thumb object. 
	 */
	private class Thumb extends OGLGUIObject
	{
		Thumb()
		{
			setInert(true);
			setVisible(false);
			addName(THUMB_NAME);
		}
		
		@Override
		public MeshView getMeshView()
		{
			return RECTANGLE_VIEW;
		}

		@Override
		public String getThemeKey()
		{
			switch (thisRef.style)
			{
				default:
				case HORIZONTAL:
					return THEME_KEY_PROGRESS_THUMB_HORIZONTAL;
				case HORIZONTAL_INVERSE:
					return THEME_KEY_PROGRESS_THUMB_HORIZONTAL_INVERSE;
				case VERTICAL:
					return THEME_KEY_PROGRESS_THUMB_VERTICAL;
				case VERTICAL_INVERSE:
					return THEME_KEY_PROGRESS_THUMB_VERTICAL_INVERSE;
			}
		}
	}

	/**
	 * Progress bar full bar object. 
	 */
	private class FullBar extends OGLGUIObject
	{
		FullBar()
		{
			setInert(true);
			setScaleType(ScaleType.ASPECT);
			addName(FULL_NAME);
		}
		
		@Override
		public MeshView getMeshView()
		{
			return RECTANGLE_VIEW;
		}

		@Override
		public String getThemeKey()
		{
			switch (thisRef.style)
			{
				default:
				case HORIZONTAL:
					return THEME_KEY_PROGRESS_FULL_HORIZONTAL;
				case HORIZONTAL_INVERSE:
					return THEME_KEY_PROGRESS_FULL_HORIZONTAL_INVERSE;
				case VERTICAL:
					return THEME_KEY_PROGRESS_FULL_VERTICAL;
				case VERTICAL_INVERSE:
					return THEME_KEY_PROGRESS_FULL_VERTICAL_INVERSE;
			}
		}
	}

	/**
	 * Progress bar empty bar object. 
	 */
	private class EmptyBar extends OGLGUIObject
	{
		EmptyBar()
		{
			setInert(true);
			setScaleType(ScaleType.ASPECT);
			addName(EMPTY_NAME);
		}
		
		@Override
		public MeshView getMeshView()
		{
			return RECTANGLE_VIEW;
		}

		@Override
		public String getThemeKey()
		{
			switch (thisRef.style)
			{
				default:
				case HORIZONTAL:
					return THEME_KEY_PROGRESS_EMPTY_HORIZONTAL;
				case HORIZONTAL_INVERSE:
					return THEME_KEY_PROGRESS_EMPTY_HORIZONTAL_INVERSE;
				case VERTICAL:
					return THEME_KEY_PROGRESS_EMPTY_VERTICAL;
				case VERTICAL_INVERSE:
					return THEME_KEY_PROGRESS_EMPTY_VERTICAL_INVERSE;
			}
		}
	}

	/**
	 * Progress bar layout.
	 */
	private class ProgressBarLayout implements OGLGUILayout
	{
		private Rectangle2F temp = new Rectangle2F();
		
		@Override
		public void resizeChild(OGLGUIObject object, int index, int childTotal)
		{
			object.animateFinish();
			switch ((LayoutAttrib)object.getLayoutAttrib())
			{
				case THUMB:
					getThumbBounds(temp);
					object.setBounds(temp);
					break;
				case FULL:
					getFullBounds(temp);
					object.setBounds(temp);
					break;
				case EMPTY:
					getEmptyBounds(temp);
					object.setBounds(temp);
					break;
			}
		}
		
	}
	
}
