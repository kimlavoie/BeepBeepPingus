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

import java.util.*;

public class MonitorForAll extends MonitorQuantifier
{
  
  public MonitorForAll(Atom x, SimpleXPathExpression p, Monitor phi)
  {
    super(x, p, phi);
  }
  
  protected MonitorForAll(Atom x, SimpleXPathExpression p, Monitor phi, Map<String,String> res)
  {
    super(x, p, phi, res);
  }

  @Override
  public void processEvent(Event e) throws MonitorException
  {
    // Optimization: if we already reached a verdict,
    // don't care about new events
    if (m_verdict != Verdict.INCONCLUSIVE)
    {
      return;
    }
    // Fetch domain for quantified variable
    Set<Constant> domain = e.evaluate(m_p, m_variableResolver);
    if (m_firstEvent == true)
    {
      m_firstEvent = false;
      for (Constant value : domain)
      {
        // For each value, instantiate inner monitor, replacing the variable
        // by this value
        Monitor new_mon = m_phi.deepClone();
        new_mon.setValue(m_x, value);
        new_mon.processEvent(e);
        m_mons.add(new_mon);
      }
    }
    else
    {
      for (Monitor mon : m_mons)
      {
        mon.processEvent(e);
      }
    }
  }

  @Override
  public Verdict getVerdict()
  {
    // If we already reached a verdict, return it
    if (m_verdict != Verdict.INCONCLUSIVE)
    {
      return m_verdict;
    }
    Iterator<Monitor> it = m_mons.iterator();
    while (it.hasNext())
    {
      Monitor mon = it.next();
      Verdict verd = mon.getVerdict();
      if (verd != Verdict.INCONCLUSIVE)
      {
        // We remove this monitor from the array, as it
        // will never change the verdict from now on
        it.remove();
      }
      if (verd == Verdict.FALSE)
      {
        this.m_verdict = Verdict.FALSE;
        // We don't break right away, so that the monitor can
        // continue its housekeeping on its internal monitors
      }
    }
    // If no internal monitor exists in the list, verdict is true
    if (m_mons.isEmpty() && this.m_verdict != Verdict.FALSE)
    {
      this.m_verdict = Verdict.TRUE;
    }
    return this.m_verdict;
  }

  @Override
  public Monitor deepClone()
  {
    // Clones share the same XPath expression and resolver
    MonitorForAll out = new MonitorForAll(new Atom(m_x.toString()), 
       m_p, m_phi.deepClone(), m_variableResolver);
    return out;
  }
  
  @Override
  public String toString()
  {
    return "∀ " + m_x + " ∈ " + m_p + " : (" + m_phi + ")";
  }

}
