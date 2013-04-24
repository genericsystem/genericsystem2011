package org.genericsystem.resolver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class GsURLConnection extends URLConnection {

	private String content = null;

	public GsURLConnection(URL u) {
		super(u);
	}

	@Override
	public synchronized InputStream getInputStream() throws IOException {
		if (!connected)
			connect();
		// TODO
		return null;

	}

	@Override
	public String getContentType() {
		return "text/html";
	}

	@Override
	public synchronized void connect() throws IOException {
		if (!connected) {
			// TODO
			this.connected = true;
		}
	}

	@Override
	public long getExpiration() {
		return -1l;
	}

	@Override
	public long getLastModified() {
		return -1l;
	}
}
