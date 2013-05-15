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
package ca.uqac.info.simplexpath;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Performs queries on XML documents. The queries are written using the XPath
 * syntax, with the following restrictions:
 * <ul>
 * <li>Transitive children (<tt>//</tt>) and parent (<tt>../</tt>) axes are
 *   not supported</li>
 * <li>Attributes (<tt>@att</tt>) are not supported</li>
 * <li>The only operator allowed in a predicate is equality between a path
 * and a constant or a variable</li>
 * <li>A variable may only stand for a leaf value (i.e. a number or a String),
 *   not an arbitrary subdocument</li>
 * </ul>
 * Normal usage involves instantiating an expression from a String using 
 * the {@link parse} method, and then querying a document using the
 * {@link evaluate} method. If the expression contains variables (denoted by
 * the <tt>$</tt> prefix, and occurring only in predicates), the value for
 * these variables is to be fetched from a <tt>Map&lt;String,String&gt;</tt>
 * associating variable names to values.
 * <p>
 * Examples of valid queries:
 * <ol>
 * <li><tt>/abc/def</tt></li>
 * <li><tt>/abc[ghi=3]/def</tt></li>
 * <li><tt>/abc[ghi=3][q=0]/def[xyz='hello']</tt></li>
 * </ol>
 */
public class SimpleXPathExpression
{
  static final Pattern m_tapPattern = Pattern.compile("^(\\w+?)(\\[.*\\])*/");
  Vector<TagAndPredicate> m_taps;
  
  protected SimpleXPathExpression()
  {
    super();
    m_taps = new Vector<TagAndPredicate>();
  }
  
  /**
   * Builds a SimpleXPathExpression from a string representation. See
   * the class documentation for restrictions on the allowed format.
   * @param s The string representing the query
   * @return The expression
   */
  public static SimpleXPathExpression parse(String s)
  {
    SimpleXPathExpression exp = new SimpleXPathExpression();
    if (s.startsWith("/"))
      s = s.substring(1);
    while (!s.isEmpty())
    {
      Matcher match = m_tapPattern.matcher(s);
      if (match.find())
      {
        String tagname = match.group(1).trim();
        String predicates = match.group(2);
        TagAndPredicate n_tap = new TagAndPredicate(tagname, predicates);
        exp.m_taps.add(n_tap);
        s = s.substring(match.end()).trim();
      }
      else
      {
        TagAndPredicate n_tap = new TagAndPredicate(s, null);
        exp.m_taps.add(n_tap);
        break;
      }
    }
    return exp;
  }

  @Override
  public String toString()
  {
    StringBuilder out = new StringBuilder();
    for (int i = 0; i < m_taps.size(); i++)
    {
      TagAndPredicate tap = m_taps.elementAt(i);
      if (i > 0)
        out.append("/");
      out.append(tap);
    }
    return out.toString();
  }
  
  /**
   * Evaluates the query on a given XML document.
   * @param root The document to evaluate the query on
   * @return A list of Elements, representing the result set of that query
   */
  public List<Element> evaluate(Element root)
  {
    return evaluate(root, new HashMap<String,String>(), 0);
  }
  
  /**
   * Evaluates the query on a given XML document.
   * @param root The document to evaluate the query on
   * @param variables A map that associates variable names to values.
   * If the query contains occurrences of variables (denoted by the <tt>$</tt>
   * prefix), the variable will be replaced by its value as defined in this map,
   * and the query will then be evaluated.
   * @return A list of Elements, representing the result set of that query
   */
  public List<Element> evaluate(Element root, Map<String,String> variables)
  {
    return evaluate(root, variables, 0);
  }
  
  protected List<Element> evaluate(Element root, Map<String,String> variables, int tap_index)
  {
    List<Element> out = new LinkedList<Element>();
    if (tap_index > m_taps.size()) // We are beyond the path length
      return out;
    if (tap_index == m_taps.size())
    {
      // We are at the tip of the path: return current node as result
      out.add(root);
      return out;
    }
    TagAndPredicate tap = m_taps.elementAt(tap_index);
    if (tap.m_tag.compareTo(root.m_name) != 0) // Element name doesn't match
      return out;
    for (Predicate pred : tap.m_predicates)
    {
      if (!pred.evaluate(root, variables, 0))
        return out;
    }
    // If we are here, all predicates evaluated to true, we continue
    for (Element child : root.m_children)
    {
      List<Element> results = evaluate(child, variables, tap_index + 1);
      out.addAll(results);
    }
    return out;
  }
  
  /**
   * Represents a part of an XPath query, namely an element name followed by
   * one or more predicates rooted in this element:
   * <tt>p[predicate]...[predicate]</tt>
   * @author sylvain
   *
   */
  protected static class TagAndPredicate
  {
    String m_tag;
    static final Pattern m_predicatePattern = Pattern.compile("\\[([\\w\\d=\\$'/]+?)\\]");
    List<Predicate> m_predicates;
    
    /**
     * Builds a TagAndPredicate from an element name and a list of predicates
     * @param tagname The name of the element (i.e. <tt>p</tt> in the class definition)
     * @param predicates A (possibly empty) string containing a sequence of
     * predicates of the form <tt>[...][...]...[...]</tt>
     */
    TagAndPredicate(String tagname, String predicates)
    {
      super();
      m_predicates = new LinkedList<Predicate>();
      m_tag = tagname;
      if (predicates != null)
      {
        Matcher match = m_predicatePattern.matcher(predicates);
        while (match.find())
        {
          String contents = match.group(1).trim();
          Predicate pred = new Predicate(contents);
          m_predicates.add(pred);
        }
      }
    }
    
    @Override
    public String toString()
    {
      StringBuffer out = new StringBuffer(m_tag);
      for (Predicate pred : m_predicates)
      {
        out.append("[").append(pred).append("]");
      }
      return out.toString();
    }
  }
  
  /**
   * Represents a single XPath predicate. As per the restrictions mentioned 
   * in the class documentation, a predicate must be of the form
   * <tt>p/p/.../p = value</tt>, where the left-hand side of the equality is
   * a path containing only element names (no nested predicates), and the
   * right-hand side is either a constant or a variable.
   * @author sylvain
   *
   */
  protected static class Predicate
  {
    String[] m_path;
    static final Pattern m_equalPattern = Pattern.compile("^(.*)\\s*=\\s*[']{0,1}(.*?)[']{0,1}$");
    String m_value;
    
    Predicate(String s)
    {
      super();
      Matcher match = m_equalPattern.matcher(s);
      if (match.matches())
      {
        String left = match.group(1).trim();
        m_path = left.split("/");
        m_value = match.group(2).trim();
      }
    }
    
    boolean evaluate(Element root)
    {
      return evaluate(root, new HashMap<String,String>(), 0);
    }
    
    boolean evaluate(Element root, Map<String,String> variables)
    {
      return evaluate(root, variables, 0);
    }
    
    protected boolean evaluate(Element root, Map<String,String> variables, int pathindex)
    {
      if (pathindex > m_path.length || pathindex < 0)
        return false;
      if (pathindex == m_path.length)
      {
        for (Element e : root.m_children)
        {
          if (e.m_isLeaf)
          {
            if (m_value.startsWith("$"))
            {
              String value = variables.get(m_value.substring(1));
              if (value == null)
                return false;
              return e.m_name.compareTo(value) == 0;
            }
            else
            {
              return e.m_name.compareTo(m_value) == 0;
            }
          }
        }
        return false;
      }
      String el_name = m_path[pathindex];
      for (Element child : root.m_children)
      {
        if (el_name.compareTo(child.m_name) != 0)
          continue;
        if (evaluate(child, variables, pathindex + 1))
          return true;
      }
      return false;
    }
    
    @Override
    public String toString()
    {
      StringBuffer out = new StringBuffer();
      for (int i = 0; i < m_path.length; i++)
      {
        if (i > 0)
          out.append("/");
        out.append(m_path[i]);
      }
      out.append("=").append(m_value);
      return out.toString();
    }
  }
}
