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

import java.util.*;

import ca.uqac.info.ltl.Constant;
import ca.uqac.info.simplexpath.*;


public class XPathEvent extends Event
{

  /**
   * The XML contents of the event
   */
  protected Element m_xmlContents;

  public XPathEvent()
  {
    super();
  }

  public XPathEvent(String s)
  {
    this();
    m_xmlContents = Element.parse(s);
  }

  @Override
  public Set<Constant> evaluate(SimpleXPathExpression query, Map<String,String> variables) throws EvaluationException
  {
    Set<Constant> out = new HashSet<Constant>();
    List<Element> le = query.evaluate(m_xmlContents, variables);
    for (Element e : le)
    {
      // Convert XML elements into LTL-FO+ constants
      out.add(new Constant(e.toString()));
    }
    return out;
  }

  @Override
  public String toString()
  {
    return m_xmlContents.toString();
  }
}
