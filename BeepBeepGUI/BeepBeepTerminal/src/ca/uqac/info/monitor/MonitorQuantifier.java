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

import ca.uqac.info.ltl.*;
import ca.uqac.info.simplexpath.SimpleXPathExpression;

public abstract class MonitorQuantifier extends Monitor
{ 
  /**
   * The internal monitor for the inside property
   */
  protected Monitor m_phi;
  
  /**
   * The quantified variable
   */
  protected Atom m_x;
  
  /**
   * The XPath expression to quantify over
   */
  protected final SimpleXPathExpression m_p;
  
  /**
   *  An array of monitors, one for each value of a
   */
  protected List<Monitor> m_mons;
  
  /**
   * If this is the first event processed by this monitor
   */
  protected boolean m_firstEvent = true;
  
  /**
   * A mapping of previously-quantified variables to actual values
   */
  protected Map<String,String> m_variableResolver;
  
  public MonitorQuantifier(Atom x, SimpleXPathExpression p, Monitor phi)
  {
    super();
    m_phi = phi;
    m_p = p;
    m_x = x;
    m_mons = new LinkedList<Monitor>();
    m_variableResolver = new HashMap<String,String>();
  }
  
  protected MonitorQuantifier(Atom x, SimpleXPathExpression p, Monitor phi, Map<String,String> variables)
  {
    this(x, p, phi);
    m_variableResolver.putAll(variables);
  }

  @Override
  public void setValue(Atom a, Atom v)
  {
    m_phi.setValue(a, v);
    m_variableResolver.put(a.toString(), v.toString());
  }
  
  @Override
  public void reset()
  {
	  super.reset();
	  m_phi.reset();
	  m_mons = new LinkedList<Monitor>();
	  m_variableResolver = new HashMap<String,String>();
  }
}
