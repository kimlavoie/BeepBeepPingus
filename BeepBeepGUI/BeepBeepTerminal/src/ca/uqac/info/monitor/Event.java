/*
	BeepBeep, an LTL-FO+ runtime monitor with XML events
	Copyright (C) 2008-2013 Sylvain Hallé

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.info.monitor;

import ca.uqac.info.ltl.Constant;
import ca.uqac.info.simplexpath.SimpleXPathExpression;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Representation of an Event for the monitor. Typically, an event
 * contains data that can be queried by means of a query string.
 * @author sylvain
 */
public abstract class Event
{
	/**
	 * Evaluate a query on a given event. This query itself may
	 * return multiple results, and may be written in various languages
	 * depending on the actual implementation of class Event.
	 * @param query The query to evaluate on the current event.
	 * @return The set of results corresponding to that query. Since
	 *   we assume results to be pieces from the original event, we
	 *   type them as events themselves for simplicity.
	 */
    public Set<Constant> evaluate(SimpleXPathExpression query) throws EvaluationException
    {
      return evaluate(query, new HashMap<String,String>());
    }
    
	public abstract Set<Constant> evaluate(SimpleXPathExpression query, Map<String,String> variables) throws EvaluationException;

	public static class EvaluationException extends MonitorException
	{
		/**
		 * Dummy UID
		 */
		private static final long serialVersionUID = 1L;

		public EvaluationException(String message)
		{
			super(message);
		}
	}
}
