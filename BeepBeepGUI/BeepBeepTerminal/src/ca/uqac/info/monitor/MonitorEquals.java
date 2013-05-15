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

import ca.uqac.info.ltl.*;
import ca.uqac.info.simplexpath.SimpleXPathExpression;

import java.util.Set;

public class MonitorEquals extends Monitor
{ 
  protected Operator m_left;
  protected Operator m_right;
  
  protected boolean m_firstEvent = true; 
  
  public MonitorEquals(Operator left, Operator right)
  {
    super();
    m_left = left;
    m_right = right;
  }

  @Override
  public void processEvent(Event e) throws MonitorException
  {
    if (!m_firstEvent)
      return; // Do nothing except on first event
    m_firstEvent = false;
    if (m_left instanceof XPathAtom)
    {
      SimpleXPathExpression xp = SimpleXPathExpression.parse(m_left.toString());
      Set<Constant> domain = e.evaluate(xp);
      for (Constant c : domain)
      {
        m_left = c;
        break;
      }
    }
    if (m_right instanceof XPathAtom)
    {
      SimpleXPathExpression xp = SimpleXPathExpression.parse(m_right.toString());
      Set<Constant> domain = e.evaluate(xp);
      for (Constant c : domain)
      {
        m_right = c;
        break;
      }
    }
  }

  @Override
  public Verdict getVerdict()
  {
    String left = m_left.toString();
    String right = m_right.toString();
    if (isNumeric(left) && isNumeric(right))
    {
      // If the two operands are numbers, we compare them numerically
      float f_left = Float.parseFloat(left);
      float f_right = Float.parseFloat(right);
      if (f_left == f_right)
        m_verdict = Verdict.TRUE;
      else
        m_verdict = Verdict.FALSE;        
    }
    else
    {
      // Otherwise, we compare them lexicographically
      if (left.compareTo(right) == 0)
        m_verdict = Verdict.TRUE;
      else
        m_verdict = Verdict.FALSE;
    }
    return m_verdict;
  }

  @Override
  public Monitor deepClone()
  {
    Operator left, right;
    if (m_left instanceof Constant)
      left = new Constant(m_left.toString());
    else if (m_left instanceof XPathAtom)
      left = new XPathAtom(m_left.toString());
    else
      left = new Atom(m_left.toString());
    if (m_right instanceof Constant)
      right = new Constant(m_right.toString());
    else if (m_right instanceof XPathAtom)
      right = new XPathAtom(m_right.toString());
    else
      right = new Atom(m_right.toString());    
    return new MonitorEquals(left, right);
  }

  @Override
  public void setValue(Atom a, Atom v)
  {
    if (!(m_left instanceof Constant) && m_left.equals(a))
    {
      m_left = new Constant(v.toString());
    }
    if (!(m_right instanceof Constant) && m_right.equals(a))
    {
      m_right = new Constant(v.toString());
    }
  }
  
  @Override
  public String toString()
  {
    return m_left + "=" + m_right;
  }
  
  @Override
  public void reset()
  {
	  super.reset();
	  m_firstEvent = true;
  }
  
  protected static boolean isNumeric(String s)
  {
    return s.matches("[\\d\\.]+");
  }

}
