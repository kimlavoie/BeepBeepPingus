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

public class MonitorFactory implements OperatorVisitor
{
  protected Stack<Monitor> m_stack;
  
  public MonitorFactory()
  {
    super();
    m_stack = new Stack<Monitor>();
  }
  
  public Monitor getMonitor()
  {
    assert !m_stack.isEmpty();
    return m_stack.peek();
  }

  @Override
  public void visit(OperatorAnd o)
  {
    Monitor m_right = m_stack.pop();
    Monitor m_left = m_stack.pop();
    Monitor mon = new MonitorAnd(m_left, m_right);
    m_stack.push(mon);
  }

  @Override
  public void visit(OperatorOr o)
  {
    Monitor m_right = m_stack.pop();
    Monitor m_left = m_stack.pop();
    Monitor mon = new MonitorOr(m_left, m_right);
    m_stack.push(mon);
  }
  
  @Override
  public void visit(OperatorXor o)
  {
    Monitor m_right = m_stack.pop();
    Monitor m_left = m_stack.pop();
    Monitor mon = new MonitorXor(m_left, m_right);
    m_stack.push(mon);
  }

  @Override
  public void visit(OperatorNot o)
  {
    Monitor m_op = m_stack.pop();
    Monitor mon = new MonitorNot(m_op);
    m_stack.push(mon);
  }

  @Override
  public void visit(OperatorF o)
  {
    Monitor m_op = m_stack.pop();
    Monitor mon = new MonitorF(m_op);
    m_stack.push(mon);
  }

  @Override
  public void visit(OperatorX o)
  {
    Monitor m_op = m_stack.pop();
    Monitor mon = new MonitorX(m_op);
    m_stack.push(mon);
  }

  @Override
  public void visit(OperatorG o)
  {
    Monitor m_op = m_stack.pop();
    Monitor mon = new MonitorG(m_op);
    m_stack.push(mon);
  }

  @Override
  public void visit(OperatorEquals o)
  {
    Operator l = o.getLeft();
    Operator r = o.getRight();
    Monitor mon = new MonitorEquals(l, r);
    m_stack.push(mon);
  }
  
  @Override
  public void visit(OperatorGreaterThan o)
  {
    Operator l = o.getLeft();
    Operator r = o.getRight();
    Monitor mon = new MonitorGreaterThan(l, r);
    m_stack.push(mon);
  }

  @Override
  public void visit(OperatorImplies o)
  {
    Monitor m_right = m_stack.pop();
    Monitor m_left = m_stack.pop();
    Monitor mon = new MonitorImplies(m_left, m_right);
    m_stack.push(mon);
  }

  @Override
  public void visit(OperatorEquiv o)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void visit(OperatorU o)
  {
    Monitor m_right = m_stack.pop();
    Monitor m_left = m_stack.pop();
    Monitor mon = new MonitorU(m_left, m_right);
    m_stack.push(mon);
  }

  @Override
  public void visit(Exists o)
  {
    Monitor m_op = m_stack.pop();
    Atom a = o.getVariable();
    XPathAtom p = o.getPath();
    SimpleXPathExpression xp = SimpleXPathExpression.parse(p.toString());
    Monitor mon = new MonitorExists(a, xp, m_op);
    m_stack.push(mon);    
  }

  @Override
  public void visit(ForAll o)
  {
    Monitor m_op = m_stack.pop();
    Atom a = o.getVariable();
    XPathAtom p = o.getPath();
    SimpleXPathExpression xp = SimpleXPathExpression.parse(p.toString());
    Monitor mon = new MonitorForAll(a, xp, m_op);
    m_stack.push(mon);
  }

  @Override
  public void visit(Atom o)
  {
    // Don't do anything
  }

  @Override
  public void visit(OperatorTrue o)
  {
    //m_stack.pop();
    m_stack.push(new MonitorTrue());
    
  }

  @Override
  public void visit(OperatorFalse o)
  {
    //m_stack.pop();
    m_stack.push(new MonitorFalse());
  }

  @Override
  public void visit(XPathAtom o)
  {
    // Don't do anything
  }

}
