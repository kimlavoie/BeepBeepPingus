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

public class MonitorG extends UnaryMonitor
{
  /**
   * An array of monitors, one for each state of the trace to read
   */
  List<Monitor> m_mons;
  
  public MonitorG(Monitor m)
  {
    super(m);
    m_mons = new LinkedList<Monitor>();
  }
  
  @Override
  public Monitor deepClone()
  {
    MonitorG out = new MonitorG(m_phi.deepClone());
    return out;
  }

  @Override
  public void processEvent(Event e) throws MonitorException
  {
    // Optimization: if we already reached a verdict,
    // don't care about new events
    if (this.m_verdict != Verdict.INCONCLUSIVE)
    {
      return;
    }
    Monitor new_mon = (Monitor) m_phi.deepClone();
    this.m_mons.add(new_mon);
    for (Monitor mon : m_mons)
    {
      mon.processEvent(e);
    }
  }

  @Override
  public Verdict getVerdict()
  {
    // If we already reached a verdict, return it
    if (this.m_verdict != Verdict.INCONCLUSIVE)
    {
      return this.m_verdict;
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
    return this.m_verdict;
  }
  
  @Override
  public String toString()
  {
    return "G (" + m_phi + ")";
  }
  
  @Override
  public void reset()
  {
	  super.reset();
	  m_mons.clear();
  }
}
