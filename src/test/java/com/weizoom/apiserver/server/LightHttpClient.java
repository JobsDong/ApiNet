/**
 * Copyright    : Copyright (c) 2006. Wintim Corp. All rights reserved
 * File Summary : 
 * Create time  : 2011-12-29
 */
package com.weizoom.apiserver.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;


/**
 * 
 * @author chuter
 * 
 */
public class LightHttpClient {

	static final Logger LOG = Logger.getLogger(LightHttpClient.class);

	public static enum Method {GET, POST};
	
	static int MAX_CONTENT = 64 * 1024;
	
	static {
		if (MAX_CONTENT < 0) {
			MAX_CONTENT = Integer.MAX_VALUE;
		}
	}

	static final int BUFFER_SIZE = 8 * 1024;

	static int TIMEOUT = 10000;
	static int UPLOAD_IMG_TIMEFACTOR = 5;
	
	boolean onlyReadHeader = true; 

	public void setToOnlyReadHeader(boolean isTrue) {
		onlyReadHeader = isTrue;
	}
	
	/** Returns the value of a named header. */
	public String getHeader(Properties headers, String name) {
		name = name.toLowerCase();
		return (String) headers.get(name);
	}
	
	/**
	 * @see #execute(URL, Map, Map, Method, HttpProxy)
	 */
	public HttpResponse execute(URL url, Map<String, String> paramsMap, Method method)
			throws IOException, ConnectException {
		return execute(url, null, paramsMap, method);
	}

	public HttpResponse execute(
			URL url, 
			Map<String, String> headersMap, 
			Map<String, String> paramsMap,
			Method method
			) throws IOException, ConnectException {
		if (! "http".equals(url.getProtocol())) {
			throw new IllegalArgumentException("Not an HTTP url:" + url);
		}

		HttpResponse response = null;
		Throwable cause = null;
		
		int code = ProtocolStatus.SUCCESS;
		Properties headers = new Properties();
		byte[] content = null;
		Socket socket = null;
		try {
			socket = new Socket(); 
			socket.setSoTimeout(LightHttpClient.TIMEOUT);
			InetSocketAddress sockAddr = null;
			sockAddr = new InetSocketAddress(url.getHost(), (-1 == url.getPort()) ? 80 : url.getPort());
			socket.connect(sockAddr, LightHttpClient.TIMEOUT);

			// make request
			OutputStream reqestStream = socket.getOutputStream();

			String request = buildRequest(url, method, headersMap, paramsMap);
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("send request:\n" + request);
			}
			
			reqestStream.write(request.getBytes());
			reqestStream.flush();

			// process response
			PushbackInputStream in = new PushbackInputStream(new BufferedInputStream(socket.getInputStream(), BUFFER_SIZE), BUFFER_SIZE);
			
			StringBuilder line = new StringBuilder();
			boolean haveSeenNonContinueStatus = false;
			while (!haveSeenNonContinueStatus) {
				// parse status code line
				code = parseStatusLine(in, line);
				// parse headers
				headers.putAll(parseHeaders(in, line));
				haveSeenNonContinueStatus = code != 100; // 100 is "Continue"
			}

			if (onlyReadHeader || (code >= 300 && code < 400)) { //���ҳ�淢����ת������ȡcontent
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				content = new byte[]{};
				//if configured to only get the headers, no need to read more
				return new HttpResponse(code, content, headers);
			}
			
			String transferCodeType = getHeader(headers, "Transfer-Encoding");
			if (transferCodeType != null && transferCodeType.equalsIgnoreCase("chunked")) {
				content = readChunkedContent(in, line);
			} else {
				content = readPlainContent(in, headers);
			}

			String contentEncoding = getHeader(headers, "Content-Encoding");
			if ("gzip".equals(contentEncoding) || "x-gzip".equals(contentEncoding)) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("uncompressing....");
				}
				byte[] compressed = content;

				content = GZIPUtils.unzipBestEffort(compressed, MAX_CONTENT);
				if (content == null) {
					throw new IOException("unzipBestEffort returned null");
				}

				if (LOG.isDebugEnabled()) {
					LOG.debug("fetched " + compressed.length + " bytes of compressed content (expanded to "
							+ content.length + " bytes) from " + url);
				}
			} else {
				if (LOG.isDebugEnabled()) {
					LOG.debug("fetched " + content.length + " bytes from " + url);
				}
			}
		} catch (SocketTimeoutException e) {
			LOG.warn("Timeout when connect to " + url.toString());
			throw new ConnectException(url, "Time out", e);
		} catch (SocketException e) {
			LOG.warn(String.format("Failed to connect to '%s' because of '%s'", url.toString(), e.getMessage()));
			throw new ConnectException(url, e);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			code = ProtocolStatus.EXCEPTION;
			cause = e;
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		}
		
		response = new HttpResponse(code, content, headers);
		if (null != cause) {
			response.setCause(cause);
		}
		return response;
	}

	/**
	 */
	public HttpResponse upload(
			URL url, 
			Map<String, String> headersMap, 
			byte[] uploadData
			) throws IOException, ConnectException {
		if (! "http".equals(url.getProtocol())) {
			throw new IllegalArgumentException("Not an HTTP url:" + url);
		}

		HttpResponse response = null;
		Throwable cause = null;
		
		int code = ProtocolStatus.SUCCESS;
		Properties headers = new Properties();
		byte[] content = null;
		Socket socket = null;
		try {
			socket = new Socket(); 
			socket.setSoTimeout(LightHttpClient.TIMEOUT*UPLOAD_IMG_TIMEFACTOR);
			InetSocketAddress sockAddr = null;
			sockAddr = new InetSocketAddress(url.getHost(), (-1 == url.getPort()) ? 80 : url.getPort());
			socket.connect(sockAddr, LightHttpClient.TIMEOUT*UPLOAD_IMG_TIMEFACTOR);

			// make request
			OutputStream reqestStream = socket.getOutputStream();

			byte[] reqestBytes = buildUploadRequest(url, uploadData, headersMap);

			if (LOG.isDebugEnabled()) {
				LOG.debug("send request:\n" + new String(reqestBytes));
			}
			
			reqestStream.write(reqestBytes);
			reqestStream.flush();

			// process response
			PushbackInputStream in = new PushbackInputStream(new BufferedInputStream(socket.getInputStream(), BUFFER_SIZE), BUFFER_SIZE);
			
			StringBuilder line = new StringBuilder();
			boolean haveSeenNonContinueStatus = false;
			while (!haveSeenNonContinueStatus) {
				// parse status code line
				code = parseStatusLine(in, line);
				// parse headers
				headers.putAll(parseHeaders(in, line));
				haveSeenNonContinueStatus = code != 100; // 100 is "Continue"
			}

			if (onlyReadHeader || (code >= 300 && code < 400)) { //���ҳ�淢����ת������ȡcontent
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				content = new byte[]{};
				//if configured to only get the headers, no need to read more
				return new HttpResponse(code, content, headers);
			}
			
			String transferCodeType = getHeader(headers, "Transfer-Encoding");
			if (transferCodeType != null && transferCodeType.equalsIgnoreCase("chunked")) {
				content = readChunkedContent(in, line);
			} else {
				content = readPlainContent(in, headers);
			}

			String contentEncoding = getHeader(headers, "Content-Encoding");
			if ("gzip".equals(contentEncoding) || "x-gzip".equals(contentEncoding)) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("uncompressing....");
				}
				byte[] compressed = content;

				content = GZIPUtils.unzipBestEffort(compressed, MAX_CONTENT);
				if (content == null) {
					throw new IOException("unzipBestEffort returned null");
				}

				if (LOG.isDebugEnabled()) {
					LOG.debug("fetched " + compressed.length + " bytes of compressed content (expanded to "
							+ content.length + " bytes) from " + url);
				}
			} else {
				if (LOG.isDebugEnabled()) {
					LOG.debug("fetched " + content.length + " bytes from " + url);
				}
			}
		} catch (SocketTimeoutException e) {
			LOG.warn("Timeout when connect to " + url.toString());
			throw new ConnectException(url, "Time out", e);
		} catch (SocketException e) {
			LOG.warn(String.format("Failed to connect to '%s' because of '%s'", url.toString(), e.getMessage()));
			throw new ConnectException(url, e);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			code = ProtocolStatus.EXCEPTION;
			cause = e;
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		}
		
		response = new HttpResponse(code, content, headers);
		if (null != cause) {
			response.setCause(cause);
		}
		return response;
	}
	
	private String buildRequest(
			URL url, 
			Method method, 
			Map<String, String> headers,
			Map<String, String> paramMap
			) {
		String host = url.getHost();
		String portString;
		
		int port = url.getPort();
		if (-1 == url.getPort()) {
			portString = "";
		} else {
			portString = ":" + port;
		}
		
		String paramStr = encodeParamStr(paramMap);
		String path = modifyUrlPath(url, paramStr, method); 
		StringBuilder reqestBuffer = new StringBuilder(method.name()+" ");
		reqestBuffer.append(path).append(" HTTP/1.1\r\n");
		
		reqestBuffer.append("Host: "+host).append(portString+"\r\n");
		
		if (null == headers) {
			headers = HttpRequestParamUtil.getCommonHttpRequestHeaders();
		}
		for (Entry<String, String> headerEntry : headers.entrySet()) {
			reqestBuffer.append(String.format("%s: %s\r\n", headerEntry.getKey(), headerEntry.getValue()));
		}
		
		if (Method.POST == method) { 
			if (null != paramStr && paramStr.length() > 0) {
				reqestBuffer.append(String.format("Content-Length: %d\r\n\r\n", paramStr.length()));
				reqestBuffer.append(paramStr);
			} else {
				reqestBuffer.append("\r\n");
			}
		} else {
			reqestBuffer.append("\r\n");
		}
		
		return reqestBuffer.toString();
	}
	
	private byte[] buildUploadRequest(
			URL url, 
			byte[] uploadData, 
			Map<String, String> headers
			) {
		if (null == uploadData || uploadData.length == 0) {
			throw new IllegalArgumentException("The post data bytes can not be null.");
		}
		
		String host = url.getHost();
		String portString;
		
		int port = url.getPort();
		if (-1 == url.getPort()) {
			portString = "";
		} else {
			portString = ":" + port;
		}
		
		String path = modifyUrlPath(url, null, Method.POST);
		StringBuilder reqestBuffer = new StringBuilder(Method.POST.name()+" ");
		reqestBuffer.append(path).append(" HTTP/1.1\r\n");
		
		reqestBuffer.append("Host: "+host).append(portString+"\r\n");
		
		if (null == headers) {
			headers = HttpRequestParamUtil.getCommonHttpRequestHeaders();
		}
		for (Entry<String, String> headerEntry : headers.entrySet()) {
			reqestBuffer.append(String.format("%s: %s\r\n", headerEntry.getKey(), headerEntry.getValue()));
		}
		reqestBuffer.append(String.format("Content-Length: %d\r\n\r\n", uploadData.length));
		
		byte[] requestHeaderBytes = reqestBuffer.toString().getBytes();
		byte[] requestBytes = new byte[requestHeaderBytes.length+uploadData.length];
		System.arraycopy(requestHeaderBytes, 0, requestBytes, 0, requestHeaderBytes.length);
		System.arraycopy(uploadData, 0, requestBytes, requestHeaderBytes.length, uploadData.length);
		
		return requestBytes;
	}
	
	private String modifyUrlPath(URL url, String paramStr, Method method) {
		String path = "".equals(url.getFile()) ? "/" : url.getFile();

		if (null == paramStr || paramStr.trim().length() == 0) {
			return path;
		}
		
		if (Method.GET == method) {
			if (path.length() > url.getPath().length()) { 
				path = path + "&" + paramStr;
			} else {
				if ('/' == path.charAt(path.length()-1)) {
					path = path.substring(0, path.length()-1);
				} 
				path += "?" + paramStr; 
			}
		} 
		
		return path;
	}
	
	private String encodeParamStr(Map<String, String> paramMap) {
		if (null == paramMap) {
			return null;
		}
		
		StringBuilder paramStrBuffer = new StringBuilder();
		
		for (Entry<String, String> entry : paramMap.entrySet()) {
			try {
				paramStrBuffer.append(String.format("%s=%s", URLEncoder.encode(entry.getKey(), "UTF-8"), 
						URLEncoder.encode(entry.getValue(), "UTF-8")));
			} catch (Exception e) {
				throw new IllegalArgumentException(String.format("Invalid param for web request %s=%s", entry.getKey(), entry.getValue()));
			}
			paramStrBuffer.append('&');
		}
		
		if (paramStrBuffer.length() > 1) {
			paramStrBuffer.deleteCharAt(paramStrBuffer.length()-1);
		}
		
		return paramStrBuffer.toString();
	}
	
	private byte[] readChunkedContent(PushbackInputStream in, StringBuilder line) throws IOException {
		boolean doneChunks = false;
		int contentBytesRead = 0;
		byte[] bytes = new byte[LightHttpClient.BUFFER_SIZE];
		ByteArrayOutputStream out = new ByteArrayOutputStream(LightHttpClient.BUFFER_SIZE);

		while (!doneChunks) {
			readLine(in, line, false);

			String chunkLenStr;
			// LOG.fine("chunk-header: '" + line + "'");

			int pos = line.indexOf(";");
			if (pos < 0) {
				chunkLenStr = line.toString();
			} else {
				chunkLenStr = line.substring(0, pos);
				// LOG.fine("got chunk-ext: " + line.substring(pos+1));
			}
			chunkLenStr = chunkLenStr.trim();
			int chunkLen;
			try {
				chunkLen = Integer.parseInt(chunkLenStr, 16);
			} catch (NumberFormatException e) {
				throw new IOException("bad chunk length: " + line.toString());
			}

			if (chunkLen == 0) {
				doneChunks = true;
				break;
			}

			if ((contentBytesRead + chunkLen) > LightHttpClient.MAX_CONTENT) {
				chunkLen = LightHttpClient.MAX_CONTENT - contentBytesRead;
			}

			// read one chunk
			int chunkBytesRead = 0;
			while (chunkBytesRead < chunkLen) {

				int toRead = (chunkLen - chunkBytesRead) < LightHttpClient.BUFFER_SIZE ? (chunkLen - chunkBytesRead)
						: LightHttpClient.BUFFER_SIZE;
				int len = in.read(bytes, 0, toRead);

				if (len == -1)
					throw new IOException("chunk eof after " + contentBytesRead
							+ " bytes in successful chunks" + " and "
							+ chunkBytesRead + " in current chunk");

				// DANGER!!! Will printed GZIPed stuff right to your
				// terminal!
				// LOG.fine("read: " + new String(bytes, 0, len));

				out.write(bytes, 0, len);
				chunkBytesRead += len;
			}

			readLine(in, line, false);
		}

		if (!doneChunks) {
			if (contentBytesRead != LightHttpClient.MAX_CONTENT) {
				throw new IOException("chunk eof: !doneChunk && didn't max out");
			}
		}

		return out.toByteArray();
	}

	private byte[] readPlainContent(InputStream in, Properties headers) throws IOException {

		int contentLength = Integer.MAX_VALUE; // get content length
		String contentLengthString = getHeader(headers, "Content-Length");
		if (contentLengthString != null) {
			contentLengthString = contentLengthString.trim();
			try {
				contentLength = Integer.parseInt(contentLengthString);
			} catch (NumberFormatException e) {
				throw new IOException("bad content length: "
						+ contentLengthString);
			}
		}
		if (LightHttpClient.MAX_CONTENT >= 0 && contentLength > LightHttpClient.MAX_CONTENT) {
			// limit download size
			contentLength = LightHttpClient.MAX_CONTENT;
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream(LightHttpClient.BUFFER_SIZE);
		byte[] bytes = new byte[LightHttpClient.BUFFER_SIZE];
		int length = 0; // read content
		for (int i = in.read(bytes); i != -1; i = in.read(bytes)) {
			out.write(bytes, 0, i);
			length += i;
			if (length >= contentLength) {
				break;
			}
		}
		return out.toByteArray();
	}

	private int parseStatusLine(PushbackInputStream in, StringBuilder line) throws IOException {
		readLine(in, line, false);

		int codeStart = line.indexOf(" ");
		int codeEnd = line.indexOf(" ", codeStart + 1);

		// handle lines with no plaintext result code, ie:
		// "HTTP/1.1 200" vs "HTTP/1.1 200 OK"
		if (codeEnd == -1) {
			codeEnd = line.length();
		}

		int code;
		try {
			code = Integer.parseInt(line.substring(codeStart + 1, codeEnd));
		} catch (NumberFormatException e) {
			throw new IOException("bad status line '" + line + "': " + e.getMessage(), e);
		}

		return code;
	}

	private void processHeaderLine(StringBuilder line, TreeMap headers) throws IOException {
		int colonIndex = line.indexOf(":"); // key is up to colon
		if (colonIndex == -1) {
			int i;
			for (i = 0; i < line.length(); i++) {
				if (!Character.isWhitespace(line.charAt(i))) {
					break;
				}
			}
			if (i == line.length()) {
				return;
			}
			throw new IOException("No colon in header:" + line);
		}
		String key = line.substring(0, colonIndex);

		int valueStart = colonIndex + 1; // skip whitespace
		while (valueStart < line.length()) {
			int c = line.charAt(valueStart);
			if (c != ' ' && c != '\t') {
				break;
			}
			valueStart++;
		}
		String value = line.substring(valueStart);
		key = key.toLowerCase();
		
		if ("set-cookie".equals(key)) {
			int valueEnd = value.indexOf(';');
			if (valueEnd > -1) {
				value = value.substring(0, valueEnd+1);
			} else {
				value = value+";";
			}
			
			if (headers.containsKey(key)) {
				value = headers.get(key).toString()+value;
			}
		}
		
		headers.put(key, value);
	}

	private Map parseHeaders(PushbackInputStream in, StringBuilder line) throws IOException {
		TreeMap headers = new TreeMap(String.CASE_INSENSITIVE_ORDER);
		return parseHeaders(in, line, headers);
	}

	// Adds headers to an existing TreeMap
	private Map parseHeaders(PushbackInputStream in, StringBuilder line, TreeMap headers) throws IOException {
		while (readLine(in, line, true) != 0) {
			// handle HTTP responses with missing blank line after headers
			int pos;
			if (((pos = line.indexOf("<!DOCTYPE")) != -1)
					|| ((pos = line.indexOf("<HTML")) != -1)
					|| ((pos = line.indexOf("<html")) != -1)) {

				in.unread(line.substring(pos).getBytes("UTF-8"));
				line.setLength(pos);

				try {
					processHeaderLine(line, headers);
				} catch (Exception e) {
					// fixme:
					e.printStackTrace();
				}

				return headers;
			}

			processHeaderLine(line, headers);
		}
		return headers;
	}

	private static int readLine(PushbackInputStream in, StringBuilder line,
			boolean allowContinuedLine) throws IOException {
		line.setLength(0);
		for (int c = in.read(); c != -1; c = in.read()) {
			switch (c) {
			case '\r':
				if (peek(in) == '\n') {
					in.read();
				}
			case '\n':
				if (line.length() > 0) {
					// at EOL -- check for continued line if the current
					// (possibly continued) line wasn't blank
					if (allowContinuedLine) {
						switch (peek(in)) {
						case ' ':
						case '\t': // line is continued
							in.read();
							continue;
						}
					}
				}
				return line.length(); // else complete
			default:
				line.append((char) c);
			}
		}
		throw new EOFException();
	}

	private static int peek(PushbackInputStream in) throws IOException {
		int value = in.read();
		in.unread(value);
		return value;
	}
	
}
