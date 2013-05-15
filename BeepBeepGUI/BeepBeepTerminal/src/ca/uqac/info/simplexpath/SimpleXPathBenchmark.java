package ca.uqac.info.simplexpath;

import java.io.StringReader;
import java.util.*;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.xml.sax.InputSource;

public class SimpleXPathBenchmark
{
  public static void main(String[] args)
  {
    String event = "<character><id>3</id><position><x>10</x></position></character>";
    String query = "character[id='4']/position/x";
    Map<String,String> resolver = new HashMap<String,String>();
    resolver.put("$i", "3");
    long beg = System.nanoTime();
    SimpleXPathExpression exp = SimpleXPathExpression.parse(query);
    Element el = Element.parse(event);
    exp.evaluate(el);
    long end = System.nanoTime();
    System.out.println((end - beg) / 1000000f);
    XPath m_xPath = XPathFactory.newInstance().newXPath();
    beg = System.nanoTime();
    try
    {
      /*NodeList result = (NodeList)*/ m_xPath.evaluate(query, 
          new InputSource(new StringReader(event)),
          XPathConstants.NODESET);
    } catch (XPathExpressionException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    end = System.nanoTime();
    System.out.println((end - beg) / 1000000f);
    //System.out.println(exp);
  }
}
