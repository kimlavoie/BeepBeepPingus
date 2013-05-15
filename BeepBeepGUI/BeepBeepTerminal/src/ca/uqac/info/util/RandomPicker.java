/******************************************************************************
Runtime monitor for pipe-based events
Copyright (C) 2013 Sylvain Halle et al.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 ******************************************************************************/
package ca.uqac.info.util;

import java.util.*;

public class RandomPicker<T>
{
	private Random m_random;
	
	public RandomPicker(Random r)
	{
		super();
		assert r != null;
		m_random = r;
	}
	
	/**
	 * Randomly picks an element from a vector
	 * @param v Input vector
	 * @return Chosen element
	 */
	public T pick(Vector<T> v)
	{
		assert v.size() > 0;
		int index = m_random.nextInt(v.size());
		assert index >= 0 && index < v.size();
		return v.elementAt(index);
	}
	
	/**
	 * Randomly picks an element from a set
	 * @param v Input set
	 * @return Chosen element
	 */
	public T pick(Set<T> s)
	{
		Vector<T> out = new Vector<T>(s);
		return pick(out);
	}
}
