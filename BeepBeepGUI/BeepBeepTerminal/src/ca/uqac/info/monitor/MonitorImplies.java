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

public class MonitorImplies extends MonitorAnd
{
  public MonitorImplies(Monitor l, Monitor r)
  {
    super(l, r);
  }
  
  @Override
  public Verdict getVerdict()
  {
    Verdict left = m_left.getVerdict();
    Verdict right = m_right.getVerdict();
    return threeValuedImplies(left, right);
  }

  @Override
  public Monitor deepClone()
  {
    return new MonitorImplies(m_left.deepClone(), m_right.deepClone());
  }
  
  @Override
  public String toString()
  {
    return "(" + m_left + ") → (" + m_right + ")";
  }
}