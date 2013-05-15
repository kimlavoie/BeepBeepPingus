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

import ca.uqac.info.ltl.Atom;

public abstract class Monitor
{
  public enum Verdict {TRUE, FALSE, INCONCLUSIVE};
  
  /**
   * The monitor's verdict, by default "inconclusive"; once
   * the monitor concludes either true or false, it keeps its value 
   * forever
   */
  protected Verdict m_verdict = Verdict.INCONCLUSIVE;
  
  private String name, caption, formula;
  /**
   * Processes a new event.
   * @param e The event to process. This can be any JavaScript object;
   *   the monitor merely passes the event to its internal monitors.
   */
  public abstract void processEvent(Event e) throws MonitorException;
  
  /**
   * Computes the verdict of the monitor.
   * @return The verdict. The return value can be either
   *   true, false, or inconclusive, according to the constants
   *   defined earlier in this file.
   */
  public abstract Verdict getVerdict();
  
  /**
   * Clones a monitor (deeply).
   */
  public abstract Monitor deepClone();
  
  /**
   * Replaces the occurrence of a quantified variable by some value
   * @param a The variable to look for
   * @param v The value to replace it by
   */
  public abstract void setValue(Atom a, Atom v);
  
  /**
   * Computes the three-valued conjunction
   * @param x Left operand
   * @param y Right operand
   * @return True, false or inconclusive
   */
  public static Verdict threeValuedAnd(Verdict x, Verdict y)
  {
    if (x == Verdict.FALSE || y == Verdict.FALSE)
      return Verdict.FALSE;
    if (x == Verdict.TRUE && y == Verdict.TRUE)
      return Verdict.TRUE;
    return Verdict.INCONCLUSIVE;
  }
  
  /**
   * Computes the three-valued disjunction
   * @param x Left operand
   * @param y Right operand
   * @return True, false or inconclusive
   */
  public static Verdict threeValuedOr(Verdict x, Verdict y)
  {
    if (x == Verdict.FALSE && y == Verdict.FALSE)
      return Verdict.FALSE;
    if (x == Verdict.TRUE || y == Verdict.TRUE)
      return Verdict.TRUE;
    return Verdict.INCONCLUSIVE;
  }
  
  /**
   * Computes the three-valued implication
   * @param x Left operand
   * @param y Right operand
   * @return True, false or inconclusive
   */
  public static Verdict threeValuedImplies(Verdict x, Verdict y)
  {
    if (x == Verdict.TRUE && y == Verdict.FALSE)
      return Verdict.FALSE;
    if (x == Verdict.FALSE || y == Verdict.TRUE)
      return Verdict.TRUE;
    return Verdict.INCONCLUSIVE;
  }
  
  /**
   * Computes the three-valued exclusive disjunction
   * @param x Left operand
   * @param y Right operand
   * @return True, false or inconclusive
   */
  public static Verdict threeValuedXor(Verdict x, Verdict y)
  {
    if (x == Verdict.FALSE && y == Verdict.FALSE)
      return Verdict.FALSE;
    if (x == Verdict.TRUE || y == Verdict.TRUE)
      return Verdict.FALSE;
    return Verdict.INCONCLUSIVE;
  }
  
  /**
   * Computes the three-valued negation
   * @param x Operand
   * @return True, false or inconclusive
   */
  public static Verdict threeValuedNot(Verdict x)
  {
    if (x == Verdict.FALSE)
      return Verdict.TRUE;
    if (x == Verdict.TRUE)
      return Verdict.FALSE;
    return Verdict.INCONCLUSIVE;
  }
  
  /**
   * Resets the monitor's state to its initial settings
   */
  public void reset()
  {
	  m_verdict = Verdict.INCONCLUSIVE;
  }
}
