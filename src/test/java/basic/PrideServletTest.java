package basic;
/*******************************************************************************
 * Copyright (c) 2001-2007 The PriDE team and MATHEMA Software GmbH
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of the GNU Lesser General Public
 * License (LGPL) which accompanies this distribution, and is available
 * at http://pride.sourceforge.net/LGPL.html
 * 
 * Contributors:
 *     Jan Lessner, MATHEMA Software GmbH - JUnit test suite
 *******************************************************************************/
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Random;

/**
 * This is an automated test client for stress testing the
 * PriDE-based example servlet
 */
public class PrideServletTest {
	
	private void test(int id) throws Exception {
		Random random = new Random(System.currentTimeMillis());
		URL url = new URL("http://bdi09444:8080/pride/servlet/ExampleServlet?id=" + id + "&firstName=Stress&lastName=Test&active=true");
		long start = System.currentTimeMillis();
		URLConnection con = makeConnection(url);
		String response = getResponse(con);
		System.out.println(response);
		long end = System.currentTimeMillis();
		System.out.println("Response time: "+(end-start)+ " ms");
		Thread.sleep(random.nextInt(2000));
	}
	
	private URLConnection makeConnection(URL url) throws Exception {
		HttpURLConnection urlCon = (HttpURLConnection)url.openConnection();
		return urlCon;
	}
	
	private String getResponse(URLConnection urlCon) throws Exception {
		BufferedReader in = new BufferedReader
			(new InputStreamReader(urlCon.getInputStream()));
		StringBuffer out = new StringBuffer();
		String line = null;
		while ((line = in.readLine()) != null)
			out = out.append(line);
		in.close();
		return out.toString();
	}

	public static void main(String[] args) throws Exception {
		int startID = 0;
		int numIDs = 20;
		if (args.length > 0)
			startID = Integer.parseInt(args[0]);
		if (args.length > 1)
			numIDs = Integer.parseInt(args[1]);
		PrideServletTest test = new PrideServletTest();
		for (; numIDs > 0; numIDs--) {
			test.test(startID);
			startID++;
		}
	}
}
