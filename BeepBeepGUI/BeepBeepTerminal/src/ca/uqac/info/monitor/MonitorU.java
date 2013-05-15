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

public class MonitorU extends BinaryMonitor
{
  /**
   * We monitor left U right by watching G left and F right.
   */
  protected Monitor m_Gleft;
  protected Monitor m_Fright;
  
  public MonitorU(Monitor l, Monitor r)
  {
    super(l, r);
    m_Gleft = new MonitorG(m_left);
    m_Fright = new MonitorF(m_right);
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
    m_Gleft.processEvent(e);
    m_Fright.processEvent(e);
    // We update the verdict here
    Verdict v_left = m_Gleft.getVerdict();
    Verdict v_right = m_Fright.getVerdict();
    if (v_left == Verdict.FALSE && v_right != Verdict.TRUE)
      m_verdict = Verdict.FALSE;
    if (v_left != Verdict.TRUE && v_right == Verdict.TRUE)
      m_verdict = Verdict.TRUE;
  }

  @Override
  public Verdict getVerdict()
  {
    return m_verdict;
  }

  @Override
  public Monitor deepClone()
  {
    return new MonitorU(m_left.deepClone(), m_right.deepClone());
  }

}
