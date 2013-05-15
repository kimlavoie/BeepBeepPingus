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

import ca.uqac.info.ltl.Operator;

public class MonitorGreaterThan extends MonitorEquals
{
  public MonitorGreaterThan(Operator left, Operator right)
  {
    super(left, right);
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
      if (f_left > f_right)
        m_verdict = Verdict.TRUE;
      else
        m_verdict = Verdict.FALSE;        
    }
    else
    {
      // Otherwise, we compare them lexicographically
      if (left.compareTo(right) > 0)
        m_verdict = Verdict.TRUE;
      else
        m_verdict = Verdict.FALSE;
    }
    return m_verdict;
  }
  
  @Override
  public Monitor deepClone()
  {
    MonitorEquals me = (MonitorEquals) super.deepClone();
    MonitorGreaterThan gt = new MonitorGreaterThan(me.m_left, me.m_right);
    return gt;
  }
}
