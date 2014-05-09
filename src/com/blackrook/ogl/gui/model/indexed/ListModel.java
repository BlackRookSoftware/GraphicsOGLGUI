/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.ogl.gui.model.indexed;

import com.blackrook.commons.list.List;
import com.blackrook.ogl.gui.model.IndexedModel;

/**
 * A model that uses a list backing.
 * @author Matthew Tropiano
 * @param <T> the object type to use.
 */
public class ListModel<T extends Object> implements IndexedModel<T>
{
	/** Underlying data set for model. */
	private List<T> dataSet;
	
	/**
	 * Creates a new list model using a set of objects.
	 * @param objects the objects to put in the list.
	 */
	public ListModel(T ... objects)
	{
		dataSet = new List<T>(objects.length);
		for (T t : objects)
			dataSet.add(t);
	}
	
	/**
	 * Creates a new list model using an iterable set of objects.
	 * @param iterable the iterable list of objects to put in the list.
	 */
	public ListModel(Iterable<T> iterable)
	{
		dataSet = new List<T>();
		for (T t : iterable)
			dataSet.add(t);
	}
	
	@Override
	public T getValueByIndex(int index)
	{
		return dataSet.getByIndex(index);
	}

	@Override
	public int getIndexByValue(T value)
	{
		return dataSet.getIndexOf(value);
	}

	@Override
	public int size()
	{
		return dataSet.size();
	}

}
