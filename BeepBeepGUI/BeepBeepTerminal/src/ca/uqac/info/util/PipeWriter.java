/******************************************************************************
Runtime monitor for pipe-based events
Copyright (C) 2013 Sylvain Halle et al.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 ******************************************************************************/
package ca.uqac.info.util;
import java.io.*;

public class PipeWriter
{
	public static void main(String[] args)
	{
		File pipe = new File("/tmp/mapipe");
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		try
		{
			fos = new FileOutputStream(pipe);
			osw = new OutputStreamWriter(fos, "UTF8");
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Starting writer\nType anything+Return to send to pipe; type quit to quit.");
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);
		System.out.print("> ");
		String msg = "";
		try
		{
			msg = in.readLine();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		while (msg.compareTo("quit") != 0)
		{
			try
			{
				osw.write(msg);
				osw.flush();
			}
			catch (IOException e)
			{
				System.out.println("Pipe closed");
				break;
			}
			System.out.print("> ");
			try
			{
				msg = in.readLine();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				System.exit(1);
			}
		}
		System.out.println("Terminated");
		System.exit(0);
	}

}
