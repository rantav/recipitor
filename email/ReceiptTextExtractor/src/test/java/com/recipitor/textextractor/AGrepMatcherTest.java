/**
 * NAME: AGrepMatcherTest.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.textextractor;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ymaman
 * created: Mar 20, 2011
 * Associated Bugs: 
 */
public class AGrepMatcherTest {

	@SuppressWarnings("unused")
	private static Logger LGR = LoggerFactory.getLogger(AGrepMatcherTest.class);
	static AGrepMatcher _;

	@BeforeClass
	public static void init() {
		_ = new AGrepMatcher();
	}

	@Test
	public void checkWord() throws Exception {
		final ProcessExecutor peMock = createMock();
		_.setProcessExecutor(peMock);
		_.isExist("hello", "world");
		Mockito.verify(peMock).runAndGetResltsAsString(AGrepMatcher.AGREP_SCRIPT_WRAPPER, "hello", "0", "world");
	}

	@Test
	public void stopAfterMathcZeroError() throws Exception {
		final ProcessExecutor peMock = Mockito.mock(ProcessExecutor.class);
		Mockito.when(peMock.runAndGetResltsAsString(AGrepMatcher.AGREP_SCRIPT_WRAPPER, "hello", "0", "world"))
				.thenReturn("1");
		_.setProcessExecutor(peMock);
		_.isExist("hello", "world");
		verify(peMock, 0);
	}

	@Test
	public void stopAfterMathcOneError() throws Exception {
		final ProcessExecutor peMock = Mockito.mock(ProcessExecutor.class);
		Mockito.when(peMock.runAndGetResltsAsString(AGrepMatcher.AGREP_SCRIPT_WRAPPER, "hello", "0", "world"))
				.thenReturn("0");
		Mockito.when(peMock.runAndGetResltsAsString(AGrepMatcher.AGREP_SCRIPT_WRAPPER, "hello", "1", "world"))
				.thenReturn("1");
		_.setProcessExecutor(peMock);
		_.isExist("hello", "world");
		verify(peMock, 1);
	}

	@Test
	public void stopAfterMathcTwoError() throws Exception {
		final ProcessExecutor peMock = Mockito.mock(ProcessExecutor.class);
		Mockito.when(peMock.runAndGetResltsAsString(AGrepMatcher.AGREP_SCRIPT_WRAPPER, "hello", "0", "world"))
				.thenReturn("0");
		Mockito.when(peMock.runAndGetResltsAsString(AGrepMatcher.AGREP_SCRIPT_WRAPPER, "hello", "1", "world"))
				.thenReturn("0");
		Mockito.when(peMock.runAndGetResltsAsString(AGrepMatcher.AGREP_SCRIPT_WRAPPER, "hello", "2", "world"))
				.thenReturn("1");
		_.setProcessExecutor(peMock);
		_.isExist("hello", "world");
		verify(peMock, 2);
	}

	@Test
	public void stopAfterMathcThreeError() throws Exception {
		final ProcessExecutor peMock = Mockito.mock(ProcessExecutor.class);
		Mockito.when(peMock.runAndGetResltsAsString(AGrepMatcher.AGREP_SCRIPT_WRAPPER, "hello", "0", "world"))
				.thenReturn("0");
		Mockito.when(peMock.runAndGetResltsAsString(AGrepMatcher.AGREP_SCRIPT_WRAPPER, "hello", "1", "world"))
				.thenReturn("0");
		Mockito.when(peMock.runAndGetResltsAsString(AGrepMatcher.AGREP_SCRIPT_WRAPPER, "hello", "2", "world"))
				.thenReturn("0");
		_.setProcessExecutor(peMock);
		Assert.assertEquals(null, _.isExist("hello", "world"));
	}

	/**
	* @param peMock
	* @param i
	* @throws Exception 
	*/
	private void verify(final ProcessExecutor peMock, final int i) throws Exception {
		Mockito.verify(peMock, Mockito.times(i >= 0 ? 1 : 0)).runAndGetResltsAsString(
				AGrepMatcher.AGREP_SCRIPT_WRAPPER, "hello", "0", "world");
		Mockito.verify(peMock, Mockito.times(i >= 1 ? 1 : 0)).runAndGetResltsAsString(
				AGrepMatcher.AGREP_SCRIPT_WRAPPER, "hello", "1", "world");
		Mockito.verify(peMock, Mockito.times(i >= 2 ? 1 : 0)).runAndGetResltsAsString(
				AGrepMatcher.AGREP_SCRIPT_WRAPPER, "hello", "2", "world");
	}

	/**
	 * @return
	 * @throws Exception 
	 */
	private ProcessExecutor createMock() throws Exception {
		final ProcessExecutor $ = Mockito.mock(ProcessExecutor.class);
		Mockito.when(
				$.runAndGetResltsAsString(Matchers.anyString(), Matchers.eq(AGrepMatcher.AGREP_SCRIPT_WRAPPER),
						Matchers.anyString(), Matchers.anyString())).thenReturn("1");
		return $;
	}
}
