/**
 * NAME: ProcessExecutorTest.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.textextractor;

import java.io.BufferedReader;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * @author ymaman
 * created: Mar 16, 2011
 * Associated Bugs: 
 */
public class ProcessExecutorTest {

	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(ProcessExecutorTest.class);

	@Test
	public void test() throws Exception {
		final ProcessExecutor $ = new ProcessExecutor();
		final BufferedReader br = $.run("/bin/echo", "hello world!");
		Assert.assertEquals("hello world!", br.readLine());
	}
}
