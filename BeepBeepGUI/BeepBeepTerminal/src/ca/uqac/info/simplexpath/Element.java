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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Representation of an XML document as a tree of named elements.
 * The current implementation of this class handles XML documents,
 * barring these few restrictions:
 * <ul>
 * <li>Elements may not have attributes</li>
 * <li>Self-closing elements are not recognized</li>
 * <li>The schema of the document may not be recursive, i.e. an
 *   element named <tt>&lt;a&gt;</tt> must not <em>contain</em> another element
 *   named <tt>&lt;a&gt;</tt> at any level of nesting</li>
 * <li>Entities and CDATA sections are not supported</li>
 * </ul>
 * An Element can be built from a String by using {@link Element.parse},
 * and then queried using XPath by creating some {@link SimpleXPathExpression}
 * <tt>exp</tt> and calling {@link SimpleXPathExpression.evaluate}.
 * @author sylvain
 *
 */
public class Element
{
  String m_name;
  List<Element> m_children;
  boolean m_isLeaf = false;
  protected static final Pattern m_tagPattern = Pattern.compile("^<(\\w+?)>(.*?)</\\1>", Pattern.MULTILINE + Pattern.DOTALL);
  
  public Element()
  {
    super();
    m_name = "";
    m_children = new LinkedList<Element>();
  }
  
  public static Element parse(String s)
  {
    List<Element> out = parseChildren(s);
    if (!out.isEmpty())
    {
      for (Element e : out)
      {
        return e;
      }
    }
    return null;
  }
  
  protected static List<Element> parseChildren(String s)
  {
    s = s.trim();
    List<Element> out_set = new LinkedList<Element>();
    Matcher match = m_tagPattern.matcher(s);
    while (!s.isEmpty())
    {
      Element new_e = new Element();
      if (match.find())
      {
        new_e.m_name = match.group(1);
        new_e.m_children = Element.parseChildren(match.group(2));
        int end = match.end();
        s = s.substring(end).trim();
        match = m_tagPattern.matcher(s);          
      }
      else
      {
        new_e.m_name = s;
        new_e.m_isLeaf = true;
        s = "";
      }
      out_set.add(new_e);
    }
    return out_set;
  }
  
  @Override
  public String toString()
  {
    return toStringBuilder(new StringBuilder()).toString();
  }
  
  protected StringBuilder toStringBuilder(StringBuilder indent)
  {
    StringBuilder out = new StringBuilder();
    if (m_isLeaf)
    {
      out.append(indent).append(m_name);
    }
    else
    {
      out.append(indent).append("<").append(m_name).append(">\n");
      StringBuilder n_indent = new StringBuilder(indent).append(" ");
      for (Element e : m_children)
      {
        out.append(e.toStringBuilder(n_indent));
        if (e.m_isLeaf)
          out.append("\n");
      }
      out.append(indent).append("</").append(m_name).append(">\n");
    }
    return out;
  }
  
  public static void main(String[] args)
  {
    Element e = Element.parse("<root>\n<abc>ddd</abc><abc>ded</abc></root>");
    System.out.println(e);
  }
}