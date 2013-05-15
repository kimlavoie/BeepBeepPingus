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

public class MonitorX extends UnaryMonitor
{
  public int m_eventsSeen = 0;
  
  public MonitorX(Monitor m)
  {
    super(m);
  }

  @Override
  public void processEvent(Event e) throws MonitorException
  {
    m_eventsSeen++;
    // We pass all events to the internal monitor, except the first
    if (m_eventsSeen < 2)
    {
      return;
    }
    this.m_phi.processEvent(e);
  }

  @Override
  public Verdict getVerdict()
  {
    // If we already reached a verdict or we are not at next state, return it
    if (m_verdict != Verdict.INCONCLUSIVE || m_eventsSeen < 2)
    {
      return m_verdict;
    }
    Verdict verd = m_phi.getVerdict();
    if (verd != Verdict.INCONCLUSIVE)
    {
      m_verdict = verd;
    }
    return m_verdict;
  }

  @Override
  public Monitor deepClone()
  {
    return new MonitorX(m_phi.deepClone());
  }
  
  @Override
  public String toString()
  {
    return "X (" + m_phi + ")";
  }


}
