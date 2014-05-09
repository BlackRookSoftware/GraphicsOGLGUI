/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui;

import java.util.Iterator;
import java.util.Random;

import com.blackrook.commons.ResettableIterable;
import com.blackrook.commons.ResettableIterator;
import com.blackrook.commons.hash.Hash;
import com.blackrook.commons.hash.HashedQueueMap;
import com.blackrook.commons.list.List;
import com.blackrook.commons.math.geometry.Rectangle2F;
import com.blackrook.ogl.data.OGLColor;
import com.blackrook.ogl.gui.object.OGLGUILabel;
import com.blackrook.ogl.gui.object.OGLGUIToggleable;
import com.blackrook.ogl.gui.object.OGLGUIValueField;
import com.blackrook.ogl.util.OGLSkin;
import com.blackrook.ogl.util.enums.Easing;
import com.blackrook.ogl.util.enums.EasingType;

/**
 * The results of a scene query.
 * @author Matthew Tropiano
 */
public final class OGLGUIQuery implements ResettableIterable<OGLGUIObject>
{
	/** Hash of objects. */
	private Hash<OGLGUIObject> objectHash;
	/** Map of name to object. */
	private HashedQueueMap<String, OGLGUIObject> nameMap;
	/** List of objects. */
	private List<OGLGUIObject> objectList;

	/**
	 * Creates a new query set. 
	 */
	OGLGUIQuery()
	{
		objectHash = new Hash<OGLGUIObject>(5, 1);
		objectList = new List<OGLGUIObject>(5);
		nameMap = new HashedQueueMap<String, OGLGUIObject>(5, 1);
	}
	
	/** Adds an object to the result, but only if it isn't in the set. */
	final void add(OGLGUIObject object)
	{
		if (object != null && !objectHash.contains(object))
		{
			objectHash.put(object);
			objectList.add(object);
			for (String name : object.getNameHash())
				nameMap.enqueue(name, object);
		}
	}
	
	/**
	 * Returns the amount of objects in this query.
	 */
	public int size()
	{
		return objectList.size();
	}

	/**
	 * Returns true if there are no objects in this query.
	 */
	public boolean isEmpty()
	{
		return objectList.isEmpty();
	}

	@Override
	public ResettableIterator<OGLGUIObject> iterator()
	{
		return objectList.iterator();
	}

	/**
	 * Wraps a set of objects in a new query, where its contents
	 * consist of the provided objects, in the same order.
	 */
	public static OGLGUIQuery wrap(OGLGUIObject ... objects)
	{
		OGLGUIQuery out = new OGLGUIQuery();
		if (objects != null) for (OGLGUIObject object : objects)
			out.add(object);
		return out;
	}
	
	/**
	 * Wraps a set of objects in a new query, where its contents
	 * consist of the provided objects, in the same order.
	 */
	public static OGLGUIQuery wrap(Iterable<OGLGUIObject> objects)
	{
		OGLGUIQuery out = new OGLGUIQuery();
		if (objects != null) for (OGLGUIObject object : objects)
			out.add(object);
		return out;
	}
	
	/**
	 * Gets an object in this query wrapped in a query.
	 * The order of the objects in the query is the order in which they were added.
	 * @param index the index of the desired object.
	 * @return a new query result that is the desired object.
	 */
	public OGLGUIQuery get(int index)
	{
		return wrap(getObject(index));
	}
	
	/**
	 * Gets an OGLGUIObject in this query.
	 * The order of the objects in the query is the order in which they were added.
	 * @param index the index of the desired object.
	 * @return the OGLGUIObject at the desired index, or null if no object at that index.
	 */
	public OGLGUIObject getObject(int index)
	{
		return objectList.getByIndex(index);
	}
	
	/**
	 * Gets every n-th object in this query as a new query.
	 * For example, if <code>n</code> is 3, this returns every multiple-of-3-indexed object.
	 * Conveience method for <code>getOffsetAndModulo(0, n)</code>. 
	 * @param n the multiple stepping.
	 * @return a new query result.
	 */
	public OGLGUIQuery getEveryMultiple(int n)
	{
		return getOffsetAndModulo(0, n);
	}
	
	/**
	 * Gets each even-indexed object in this query as a new query.
	 * Conveience method for <code>getOffsetAndModulo(0, 2)</code>. 
	 * @return a new query result.
	 */
	public OGLGUIQuery getEvens()
	{
		return getOffsetAndModulo(0, 2);
	}
	
	/**
	 * Gets each odd-indexed object in this query as a new query.
	 * Conveience method for <code>getOffsetAndModulo(1, 2)</code>. 
	 * @return a new query result.
	 */
	public OGLGUIQuery getOdds()
	{
		return getOffsetAndModulo(1, 2);
	}
	
	/**
	 * Gets every object at the index pattern specified in this query as a new query.
	 * For example, if <code>offset</code> is 1 and <code>step</code> is 3, this returns every 3rd object
	 * shifted after the first object.
	 * @param offset the starting offset.
	 * @param modulo the modulo after the offset.
	 * @return a new query result.
	 */
	public OGLGUIQuery getOffsetAndModulo(int offset, int modulo)
	{
		OGLGUIQuery out = new OGLGUIQuery();
		for (int i = 0; i < size(); i++)
			if ((i - offset) % modulo == 0)
				out.add(getObject(i));
		return out;
	}
	
	/**
	 * Returns the first object in this query wrapped in a query.
	 * Convenience method for <code>get(0)</code>.
	 * If there are no objects in this query, it will return an empty query. 
	 * @return a new query result with the first object in this query.
	 */
	public OGLGUIQuery getFirst()
	{
		return get(0);
	}
	
	/**
	 * Returns the last object in this query wrapped in a query.
	 * Convenience method for <code>get(size()-1)</code>. 
	 * @return a new query result with the last object in this query.
	 */
	public OGLGUIQuery getLast()
	{
		return get(size()-1);
	}
	
	/**
	 * Gets all GUI Objects that match all of the names provided in this query.
	 * IF no names are provided, this returns itself. 
	 * @param names the names to search for.
	 * @return a new query result.
	 */
	public OGLGUIQuery getByName(String ... names)
	{
		OGLGUIQuery out = this;
		for (String name : names)
			 out = wrap(out.nameMap.get(name));
		return out;
	}

	/**
	 * Gets all GUI Objects with a matching name regex pattern in this query. 
	 * @param pattern the pattern to use.
	 * @return a new query result.
	 */
	public OGLGUIQuery getByPattern(String pattern)
	{
		OGLGUIQuery out = new OGLGUIQuery();
		Iterator<String> it = nameMap.keyIterator();
		String name = null;
		while (it.hasNext())
		{
			name = it.next();
			if (name.matches(pattern)) for (OGLGUIObject object : nameMap.get(name))
				out.add(object);
		}
		return out;
	}
	
	/**
	 * Gets a new query result of all objects in the query with the same class.
	 * @param clazz the class type to scan for.
	 * @return a new query result where all of the results are castable as <code>clazz</code>.
	 */
	public OGLGUIQuery getByType(Class<?> clazz)
	{
		OGLGUIQuery out = new OGLGUIQuery();
		for (OGLGUIObject object : this)
			if (clazz.isInstance(object))
				out.add(object);
		return out;
	}

	/**
	 * Gets a new query result of all objects in the query with a specific layout attribute.
	 * @param layoutAttribute the layout attribute to test for.
	 * @return a new query result.
	 */
	public OGLGUIQuery getByAttrib(Object layoutAttribute)
	{
		OGLGUIQuery out = new OGLGUIQuery();
		for (OGLGUIObject object : this)
		{
			boolean test = layoutAttribute == null || object.getLayoutAttrib() == null 
					? layoutAttribute == object.getLayoutAttrib()
					: layoutAttribute.equals(object.getLayoutAttrib());
			if (test)
				out.add(object);
		}
		return out;		
	}

	/**
	 * Gets a new query result containing objects that are currently animating.
	 * @return a new query result that has objects that are in an animation.
	 */
	public OGLGUIQuery getAnimating()
	{
		OGLGUIQuery out = new OGLGUIQuery();
		for (OGLGUIObject object : this)
			if (object.isAnimating())
				out.add(object);
		return out;
	}

	/**
	 * Gets a new query result containing objects that are visible on camera.
	 * @return a new query result that has objects that are in the camera's bounds.
	 */
	public OGLGUIQuery getOnCamera()
	{
		OGLGUIQuery out = new OGLGUIQuery();
		for (OGLGUIObject object : this)
			if (object.isOnCamera())
				out.add(object);
		return out;
	}

	/**
	 * Gets all children of all of the objects in this query.
	 * @return a new query result.
	 */
	public OGLGUIQuery getChildren()
	{
		OGLGUIQuery out = new OGLGUIQuery();
		for (OGLGUIObject object : this)
			for (OGLGUIObject child : object.getChildren())
				out.add(child);
		return out;
	}
	
	/**
	 * Gets all descendants of all of the objects in this query.
	 * @return a new query result.
	 */
	public OGLGUIQuery getDescendants()
	{
		OGLGUIQuery out = new OGLGUIQuery();
		for (OGLGUIObject object : this)
			getDescendantsRecurse(out, object);
		return out;
	}
	
	// recursive accumulator for getDescendants.
	private void getDescendantsRecurse(OGLGUIQuery query, OGLGUIObject parent)
	{
		query.add(parent);
		for (OGLGUIObject child : parent.getChildren())
			getDescendantsRecurse(query, child);
	}
	
	/**
	 * Gets all siblings of all of the objects in this query.
	 * Does not get siblings of objects without parents.
	 * @return a new query result.
	 */
	public OGLGUIQuery getSiblings()
	{
		OGLGUIQuery out = new OGLGUIQuery();
		for (OGLGUIObject object : this)
		{
			if (object.getParent() != null)
			{
				for (OGLGUIObject child : object.getParent().getChildren())
					if (child != object) 
						out.add(child);
			}
		}
		return out;
	}
	
	/**
	 * Gets all parents of all of the objects in this query.
	 * @return a new query result.
	 */
	public OGLGUIQuery getParents()
	{
		OGLGUIQuery out = new OGLGUIQuery();
		for (OGLGUIObject object : this)
			out.add(object.getParent());
		return out;
	}
	
	/**
	 * Creates a new query result that is the union of two queries.
	 * NOTE: There are never duplicates in OGLGUIQueries.
	 * @return a new query result with both sets of objects.
	 */
	public OGLGUIQuery getUnion(OGLGUIQuery query)
	{
		OGLGUIQuery out = new OGLGUIQuery();
		for (OGLGUIObject object : this)
			out.add(object);
		for (OGLGUIObject object : query)
			out.add(object);
		return out;
	}

	/**
	 * Creates a new query result that is the intersection of two queries
	 * (both objects must exist in this query and the other to be included).
	 * @return a new query result with just objects that are in both queries.
	 */
	public OGLGUIQuery getIntersection(OGLGUIQuery query)
	{
		OGLGUIQuery out = new OGLGUIQuery();
		for (OGLGUIObject object : this)
			if (query.objectHash.contains(object))
				out.add(object);
		return out;
	}

	/**
	 * Creates a new query result that is the objects in this query
	 * minus the ones present in another.
	 * @return a new query result with objects in this query minus another query.
	 */
	public OGLGUIQuery getDifference(OGLGUIQuery query)
	{
		OGLGUIQuery out = new OGLGUIQuery();
		for (OGLGUIObject object : this)
			if (!query.objectHash.contains(object))
				out.add(object);
		return out;
	}

	/**
	 * Creates a new query result that is the union of the objects in this query and another,
	 * minus the ones present in both. The is essentially the union minus the intersection.
	 * @return a new query result with just objects in this query that are not in the provided query.
	 */
	public OGLGUIQuery getXOr(OGLGUIQuery query)
	{
		OGLGUIQuery out = new OGLGUIQuery();
		for (OGLGUIObject object : this)
			if (!query.objectHash.contains(object))
				out.add(object);
		for (OGLGUIObject object : query)
			if (!this.objectHash.contains(object))
				out.add(object);
		return out;
	}

	/**
	 * Gets a sublist of this query result as another query.
	 * @param startIndex the starting index of this query, INCLUSIVE.
	 * @param endIndex the ending index, EXCLUSIVE. If this is past the end of the query, this is {@link #size()}.
	 * @return a new query result that is a sublist of the objects in this result.
	 */
	public OGLGUIQuery getSubQuery(int startIndex, int endIndex)
	{
		OGLGUIQuery out = new OGLGUIQuery();
		int end = Math.min(endIndex, size());
		for (int i = startIndex; i < end; i++)
			out.add(getObject(i));
		return out;
	}

	/**
	 * Gets a new query result that is a random sub-selection of 
	 * objects in this query. 
	 * @param random the random number generator to use.
	 * @return a new query result that is a random sub-selection of objects
	 * in this query. 
	 */
	public OGLGUIQuery getRandom(Random random)
	{
		return getRandom(random, 0.5f);
	}

	/**
	 * Gets a new query result that is a random sub-selection of 
	 * objects in this query, weighted by a chance to select each one.
	 * @param random the random number generator to use.
	 * @param chance the random chance factor. if 0 or less, picks nothing.
	 * if 1 or greater, picks EVERYTHING. No random numbers are generated in these cases.
	 * @return a new query result that is a random sub-selection of objects
	 * in this query. 
	 */
	public OGLGUIQuery getRandom(Random random, float chance)
	{
		if (chance <= 0f)
			return wrap();
		else if (chance >= 1f)
			return wrap(this);
		
		OGLGUIQuery out = new OGLGUIQuery();
		for (int i = 0; i < size(); i++)
			if (random.nextFloat() < chance) 
				out.add(getObject(i));
		return out;
	}

	/**
	 * Gets a new query result that is a random sub-selection of 
	 * a specific amount of objects in this query. 
	 * @param random the random number generator to use.
	 * @param objects the number of objects to pick at random.
	 * @return a new query result that is a random sub-selection of objects
	 * in this query, with size <code>objects</code>.
	 */
	public OGLGUIQuery getRandom(Random random, int objects)
	{
		OGLGUIQuery out = wrap(this);
		if (objects <= 0)
			return new OGLGUIQuery();

		out.shuffle(random);
		
		int start = random.nextInt((size() - objects) + 1);
		return out.getSubQuery(start, start + objects);
	}

	/**
	 * Gets a new query result containing objects with a matching toggle state. 
	 * Only works on OGLGUIToggleable objects.
	 * @param state the set state to test for.
	 * @return a new query result that has objects with a matching toggle state.
	 */
	public OGLGUIQuery getHavingState(boolean state)
	{
		OGLGUIQuery out = new OGLGUIQuery();
		for (int i = 0; i < size(); i++)
		{
			OGLGUIObject obj = getObject(i);
			if (obj instanceof OGLGUIToggleable && ((OGLGUIToggleable)obj).isSet() == state)
				out.add(obj);
		}
		return out;
	}
	
	/**
	 * Gets a new query result containing objects that are visible.
	 * @return a new query result that has objects that are visible.
	 * @see OGLGUIObject#isVisible()
	 */
	public OGLGUIQuery getVisible()
	{
		OGLGUIQuery out = new OGLGUIQuery();
		for (OGLGUIObject object : this)
			if (object.isVisible())
				out.add(object);
		return out;
	}
	
	/**
	 * Randomizes the order of the objects in this query.
	 * @param random the random number generator to use.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery shuffle(Random random)
	{
		objectList.shuffle(random);
		return this;
	}

	/**
	 * Requests focus on the first object in the query.
	 * If the set is empty, this does nothing.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery requestFocus()
	{
		if (size() > 0)
			getObject(0).requestFocus();
		return this;
	}
	
	/**
	 * Requests release of focus on all of the objects in the query.
	 * Only one object at a time can be focused, so this will actually
	 * unfocus at most one object in the query set.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery releaseFocus()
	{
		for (OGLGUIObject object : this)
			object.releaseFocus();
		return this;
	}
	
	/**
	 * Sets if the object can accept input on all objects in this query result.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery setEnabled(boolean enabled)
	{
		for (OGLGUIObject object : this)
			object.setEnabled(enabled);
		return this;
	}

	/**
	 * Gets if the first object in the result can accept input.
	 * Note that this can be affected by parents in the object's lineage.
	 * If the set is empty, this returns false.
	 * @return true if so, false otherwise.
	 * @see OGLGUIObject#isEnabled()
	 */
	public boolean isEnabled()
	{
		return size() > 0 ? getObject(0).isEnabled() : false;
	}

	/**
	 * Sets if the object is visible on all objects in this query result.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery setVisible(boolean visible)
	{
		for (OGLGUIObject object : this)
			object.setVisible(visible);
		return this;
	}

	/**
	 * Gets if the first object in the result is visible.
	 * Note that this can be affected by parents in the object's lineage.
	 * If the set is empty, this returns false.
	 * @return true if so, false otherwise.
	 * @see OGLGUIObject#isVisible()
	 */
	public boolean isVisible()
	{
		return size() > 0 ? getObject(0).isVisible() : false;
	}

	/**
	 * Sets the theme on all objects in this query result.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery setTheme(OGLGUITheme theme)
	{
		for (OGLGUIObject object : this)
			object.setTheme(theme);
		return this;
	}

	/**
	 * Gets the theme on the first object in the query result.
	 * Note that this can be affected by the GUI that the object is attached to.
	 * If the set is empty, this returns null.
	 * @return the attached theme or null if no theme.
	 * @see OGLGUIObject#getTheme()
	 */
	public OGLGUITheme getTheme()
	{
		return size() > 0 ? getObject(0).getTheme() : null;
	}

	/**
	 * Gets the current theme key on the first object in the query.
	 * If the set is empty, this returns null.
	 * @return the key name or null if no key is set.
	 * @see OGLGUIObject#getThemeKey()
	 */
	public String getThemeKey()
	{
		return size() > 0 ? getObject(0).getThemeKey() : null;
	}
	
	/**
	 * Sets this object's skin on all objects in this query result.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery setSkin(OGLSkin group)
	{
		for (OGLGUIObject object : this)
			object.setSkin(group);
		return this;
	}

	/**
	 * Gets the skin on the first object in this query result.
	 * If the set is empty, this returns null.
	 * @return the set skin or null if no skin is set.
	 */
	public OGLSkin getSkin()
	{
		return size() > 0 ? getObject(0).getSkin() : null;
	}

	/**
	 * Sets the color on all objects in this query result.
	 * @param red the red component value for the color. 
	 * @param green the green component value for the color.
	 * @param blue the blue component value for the color.
	 * @param alpha the alpha component value for the color.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery setColor(float red, float green, float blue, float alpha)
	{
		for (OGLGUIObject object : this)
			object.setColor(red, green, blue, alpha);
		return this;
	}

	/**
	 * Sets the color on all objects in this query result.
	 * @param color the color to set.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery setColor(OGLColor color)
	{
		return setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	/**
	 * Gets the color on the first object in this query result.
	 * If the set is empty, this returns null.
	 * @return itself, in order to chain queries.
	 */
	public OGLColor getColor()
	{
		return size() > 0 ? getObject(0).getColor() : null;
	}

	/**
	 * Gets the red color component of the first object in this query result.
	 * This color is a calculated color - it is affected by its parent's color components.
	 * If the set is empty, this returns 0.
	 * @return the red color component. 
	 * @see OGLGUIObject#getRed()
	 * @see OGLGUIObject#getColorNotInherited()
	 */
	public float getRed()
	{
		return size() > 0 ? getObject(0).getRed() : 0f;
	}
	
	/**
	 * Gets the green color component of the first object in this query result.
	 * This color is a calculated color - it is affected by its parent's color components.
	 * If the set is empty, this returns 0.
	 * @return the green color component. 
	 * @see OGLGUIObject#getGreen()
	 * @see OGLGUIObject#getColorNotInherited()
	 */
	public float getGreen()
	{
		return size() > 0 ? getObject(0).getGreen() : 0f;
	}
	
	/**
	 * Gets the blue color component of the first object in this query result.
	 * This color is a calculated color - it is affected by its parent's color components.
	 * If the set is empty, this returns 0.
	 * @return the blue color component. 
	 * @see OGLGUIObject#getBlue()
	 * @see OGLGUIObject#getColorNotInherited()
	 */
	public float getBlue()
	{
		return size() > 0 ? getObject(0).getBlue() : 0f;
	}
	
	/**
	 * Gets the alpha color component of the first object in this query result.
	 * This color is a calculated color - it is affected by its parent's color components.
	 * If the set is empty, this returns 0.
	 * @return the alpha color component. 
	 * @see OGLGUIObject#getAlpha()
	 * @see OGLGUIObject#getColorNotInherited()
	 */
	public float getAlpha()
	{
		return size() > 0 ? getObject(0).getAlpha() : 0f;
	}
	
	/**
	 * Sets the opacity on all objects in this query result.
	 * @param opacity the opacity to set.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery setOpacity(float opacity)
	{
		for (OGLGUIObject object : this)
			object.setOpacity(opacity);
		return this;
	}

	/**
	 * Gets the opacity on the first object in this query result.
	 * If the set is empty, this returns 0.
	 * @return the object's opacity value.
	 */
	public float getOpacity()
	{
		return size() > 0 ? getObject(0).getOpacity() : 0f;
	}

	/**
	 * Sets the object bounds on all objects in this query result.
	 * @param x			its position x.
	 * @param y			its position y.
	 * @param width		its width.
	 * @param height	its height.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery setBounds(float x, float y, float width, float height)
	{
		for (OGLGUIObject object : this)
			object.setBounds(x, y, width, height);
		return this;
	}

	/**
	 * Gets the object bounds on the first object in this query result.
	 * If the set is empty, this returns null.
	 * @return a Rectangle4F containing the object bounds. If the contents of the Rectangle2F change,
	 * it will not affect the object.
	 */
	public Rectangle2F getBounds()
	{
		return size() > 0 ? getObject(0).getBounds() : null;
	}
	
	/**
	 * Gets the object absolute bounds on the first object in this query result.
	 * If the set is empty, this returns null.
	 * @return a Rectangle4F containing the object bounds. If the contents of the Rectangle2F change,
	 * it will not affect the object.
	 */
	public Rectangle2F getAbsoluteBounds()
	{
		return size() > 0 ? getObject(0).getAbsoluteBounds() : null;
	}
	
	/**
	 * Sets the object position on all objects in this query result.
	 * @param x			its position x.
	 * @param y			its position y.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery setPosition(float x, float y)
	{
		for (OGLGUIObject object : this)
			object.setPosition(x, y);
		return this;
	}

	/**
	 * Sets the object width and height on all objects in this query result.
	 * @param width		its width.
	 * @param height	its height.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery setDimensions(float width, float height)
	{
		for (OGLGUIObject object : this)
			object.setDimensions(width, height);
		return this;
	}

	/**
	 * Changes this object's position by an x or y-coordinate amount on all objects in this query result.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery translate(float x, float y)
	{
		for (OGLGUIObject object : this)
			object.translate(x, y);
		return this;
	}

	/**
	 * Changes this object's width/height by an x or y-coordinate amount on all objects in this query result.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery stretch(float width, float height)
	{
		for (OGLGUIObject object : this)
			object.stretch(width, height);
		return this;
	}

	/**
	 * Sets the text of all objects in this query result.
	 * Only works on OGLGUILabel objects.
	 * @param text the text message to set.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery setText(String text)
	{
		for (OGLGUIObject object : this)
			if (object instanceof OGLGUILabel) 
				((OGLGUILabel)object).setText(text);
		return this;
	}
	
	/**
	 * Gets the text on the first object in this query result,
	 * or null if the object is not a OGLGUILabel type.
	 * If the set is empty, this returns null.
	 * @return the text as a String.
	 */
	public String getText()
	{
		if (size() > 0 && getObject(0) instanceof OGLGUILabel)
			return ((OGLGUILabel)getObject(0)).getText();
		return null;
	}	
	
	/**
	 * Sets the value on all objects in this query result.
	 * Only works on {@link OGLGUIValueField} objects.
	 * @param value the value to set.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery setValue(Object value)
	{
		for (OGLGUIObject object : this)
			if (object instanceof OGLGUIValueField<?>) 
				((OGLGUIValueField<?>)object).setValue(value);
		return this;
	}
	
	/**
	 * Gets the value on the first object in this query result,
	 * or null if the object is not a OGLGUIValueField type.
	 * If the set is empty, this returns null.
	 * @return the text as a String.
	 */
	public Object getValue()
	{
		if (size() > 0 && getObject(0) instanceof OGLGUIValueField)
			return ((OGLGUIValueField<?>)getObject(0)).getValue();
		return null;
	}	
	
	/**
	 * Toggles the state of all objects in this query result.
	 * Only works on {@link OGLGUIToggleable} objects.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery toggle()
	{
		for (OGLGUIObject object : this)
			if (object instanceof OGLGUIToggleable) 
				((OGLGUIToggleable)object).toggle();
		return this;
	}
	
	/**
	 * Sets the state of all objects in this query result.
	 * Only works on {@link OGLGUIToggleable} objects.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery setState(boolean state)
	{
		for (OGLGUIObject object : this)
			if (object instanceof OGLGUIToggleable) 
				((OGLGUIToggleable)object).setState(state);
		return this;
	}
	
	/**
	 * Gets if the toggle state on the first object in this query result is "set,"
	 * or false if the object is not an OGLGUIToggleable type.
	 * If the set is empty, this returns false.
	 * @return if the toggleable is set or false otherwise.
	 */
	public boolean isSet()
	{
		if (size() > 0 && getObject(0) instanceof OGLGUIToggleable)
			return ((OGLGUIToggleable)getObject(0)).isSet();
		return false;
	}	
	
	/**
	 * Sets this object's rotation in degrees on all objects in this query result.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery setRotationZ(float rotation)
	{
		for (OGLGUIObject object : this)
			object.setRotationZ(rotation);
		return this;
	}

	/**
	 * Rotates this object by an amount of degrees on all objects in this query result.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery rotate(float rotation)
	{
		for (OGLGUIObject object : this)
			object.rotate(rotation);
		return this;
	}

	/**
	 * Adds a name (or several) to the objects in this query.
	 * @param names the names to add to the objects.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery addName(String ... names)
	{
		for (OGLGUIObject object : this)
			object.addName(names);
		return this;
	}
	
	/**
	 * Removes a name (or several) from the objects in this query.
	 * @param names the names to remove from the objects.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery removeName(String ... names)
	{
		for (OGLGUIObject object : this)
			object.removeName(names);
		return this;
	}
	
	/**
	 * Enqueues an animation on this GUI Object, no duration.
	 * @param animations the animations to perform (at once) for this animation.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery animate(OGLGUIAnimation ... animations)
	{
		return animate(0, Easing.LINEAR, animations);
	}

	/**
	 * Enqueues an animation on this GUI Object, linear transition.
	 * @param duration the duration of the action in milliseconds.
	 * @param animations the animations to perform (at once) for this animation.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery animate(float duration, OGLGUIAnimation ... animations)
	{
		return animate(duration, Easing.LINEAR, animations);
	}

	/**
	 * Enqueues an animation on this GUI Object.
	 * @param duration the duration of the action in milliseconds.
	 * @param transition the transition type for the action.
	 * @param animations the animations to perform (at once) for this animation.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery animate(float duration, EasingType transition, OGLGUIAnimation ... animations)
	{
		for (OGLGUIObject object : this)
			object.animate(duration, transition, animations);
		return this;
	}

	/**
	 * Enqueues an animation on this GUI Object, linear transition, and with
	 * a cumulative delay added to each object.<p>
	 * For example, if <code>cumulativeDelay</code> is 200, then the first object
	 * will receive 0 delay, but the next will receive 200ms, then the next will
	 * get 400ms, then the next 600ms, and so on.
	 * @param cumulativeDelay the cumulative delay to add to each object beyond the first.
	 * @param duration the duration of the action in milliseconds.
	 * @param animations the animations to perform (at once) for this animation.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery animate(float cumulativeDelay, float duration, OGLGUIAnimation ... animations)
	{
		return animate(cumulativeDelay, duration, Easing.LINEAR, animations);
	}

	/**
	 * Enqueues an animation on this GUI Object, and with
	 * a cumulative delay added to each object.<p>
	 * For example, if <code>cumulativeDelay</code> is 200, then the first object
	 * will receive 0 delay, but the next will receive 200ms, then the next will
	 * get 400ms, then the next 600ms, and so on.
	 * @param cumulativeDelay the cumulative delay to add to each object beyond the first.
	 * @param duration the duration of the action in milliseconds.
	 * @param transition the transition type for the action.
	 * @param animations the animations to perform (at once) for this animation.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery animate(float cumulativeDelay, float duration, EasingType transition, OGLGUIAnimation ... animations)
	{
		float delay = 0f;
		for (OGLGUIObject object : this)
		{
			object.animateDelay(delay);
			object.animate(duration, transition, animations);
			delay += cumulativeDelay;
		}
		return this;
	}

	/**
	 * Enqueues a delay between animations on this GUI Object.
	 * @param duration the duration of the action in milliseconds.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery animateDelay(float duration)
	{
		for (OGLGUIObject object : this)
			object.animateDelay(duration);
		return this;
	}

	/**
	 * Aborts the animation on this object, abandoning it mid-animation.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery animateAbort()
	{
		for (OGLGUIObject object : this)
			object.animateAbort();
		return this;
	}

	/**
	 * Finishes the animation on this object all the way to the end.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery animateFinish()
	{
		for (OGLGUIObject object : this)
			object.animateFinish();
		return this;
	}
	
	/**
	 * Binds an action of a particular type to the objects inside this query.
	 * @param action the action to bind.
	 * @param types the action type to bind.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery bindAction(OGLGUIAction action, String ... types)
	{
		for (OGLGUIObject object : this)
			object.bindAction(action, types);
		return this;
	}
	
	/**
	 * Unbinds an action of a particular type from the objects inside this query.
	 * @param action the action to bind.
	 * @param types the action type to bind.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery unbindAction(OGLGUIAction action, String ... types)
	{
		for (OGLGUIObject object : this)
			object.unbindAction(action, types);
		return this;
	}
	
	/**
	 * Unbinds all actions of a particular type from the objects inside this query.
	 * @param type the action type to bind.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery unbindAllActions(String type)
	{
		for (OGLGUIObject object : this)
			object.unbindAllActions(type);
		return this;
	}
	
	/**
	 * Calls an action on the objects inside the query.
	 * @param action the action to call.
	 * @return itself, in order to chain queries.
	 */
	public OGLGUIQuery callAction(OGLGUIAction action)
	{
		for (OGLGUIObject object : this)
			object.callAction(action);
		return this;
	}
	
	@Override
	public String toString()
	{
		return objectList.toString();
	}
	
}
