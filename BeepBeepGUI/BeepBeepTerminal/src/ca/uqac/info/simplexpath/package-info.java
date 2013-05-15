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

/**
 * Package for handling of simple XML documents and simple XPath queries.
 * Although Java provides classes for handling XML documents and XPath
 * queries, they have been found to be slow for monitoring purposes.
 * Within the limits of the current package (see the
 * documentation for {@link Element} and {@link SimpleXPathExpression},
 * the present classes have been found to run 8-10 times faster than
 * the JDK classes for the same queries. 
*/
package ca.uqac.info.simplexpath;