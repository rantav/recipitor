/**
 * NAME: Base64.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.datain;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.geronimo.mail.util.Base64Encoder;
import org.apache.geronimo.mail.util.SessionUtil;
import org.apache.log4j.Logger;

/**
 * @author ymaman
 * created: Feb 22, 2011
 * Associated Bugs: 
 */
public class BASE64DecoderStream_ extends FilterInputStream {

	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(BASE64DecoderStream_.class);

	public BASE64DecoderStream_(final InputStream inp) {
		super(inp);
		decoder = new Base64Encoder();
		ignoreErrors = false;
		encodedChars = new byte[8000];
		decodedChars = new byte[6000];
		decodedCount = 0;
		decodedIndex = 0;
		ignoreErrors = SessionUtil.getBooleanProperty("mail.mime.base64.ignoreerrors", false);
	}

	private boolean dataAvailable() {
		return decodedCount != 0;
	}

	//	private byte getBufferedChar() {
	//		decodedCount--;
	//		return decodedChars[decodedIndex++];
	//	}
	private boolean decodeStreamData() throws IOException {
		decodedIndex = 0;
		final int readCharacters = fillEncodedBuffer();
		if (readCharacters > 0) {
			decodedCount = decoder.decode(encodedChars, 0, readCharacters, decodedChars);
			return true;
		} //else 
		return false;
	}

	private int getByte() throws IOException {
		if (!dataAvailable() && !decodeStreamData()) return -1;
//		else {
			decodedCount--;
			return decodedChars[decodedIndex++] & 255;
//		}
	}

	private int getBytes(final byte data[], int offset, int length) throws IOException {
		int readCharacters;
		int copyCount;
		for (readCharacters = 0; length > 0; readCharacters += copyCount) {
			if (!dataAvailable() && !decodeStreamData()) return readCharacters <= 0 ? -1 : readCharacters;
			copyCount = Math.min(decodedCount, length);
			System.arraycopy(decodedChars, decodedIndex, data, offset, copyCount);
			decodedIndex += copyCount;
			decodedCount -= copyCount;
			offset += copyCount;
			length -= copyCount;
		}
		return readCharacters;
	}

	private int fillEncodedBuffer() throws IOException {
		int readCharacters = 0;
		do {
			int ch;
			do {
				ch = in.read();
				if (ch == -1) if (readCharacters % 4 != 0) {
					if (!ignoreErrors) throw new IOException("Base64 encoding error, data truncated");
					else return readCharacters / 4 * 4;
				} else return readCharacters;
			} while (!decoder.isValidBase64(ch));
			encodedChars[readCharacters++] = (byte) ch;
		} while (readCharacters < encodedChars.length);
		return readCharacters;
	}

	@Override
	public int read() throws IOException {
		return getByte();
	}

	@Override
	public int read(final byte buffer[], final int offset, final int length) throws IOException {
		return getBytes(buffer, offset, length);
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	@Override
	public int available() throws IOException {
		return in.available() / 4 * 3 + decodedCount;
	}

	protected static final String MAIL_BASE64_IGNOREERRORS = "mail.mime.base64.ignoreerrors";
	protected static final int BUFFERED_UNITS = 2000;
	protected Base64Encoder decoder;
	protected boolean ignoreErrors;
	protected byte encodedChars[];
	protected byte decodedChars[];
	protected int decodedCount;
	protected int decodedIndex;
}
