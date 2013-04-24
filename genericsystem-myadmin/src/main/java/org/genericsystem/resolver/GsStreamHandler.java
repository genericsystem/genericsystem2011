package org.genericsystem.resolver;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class GsStreamHandler extends URLStreamHandler {

	@Override
	protected URLConnection openConnection(URL url) throws IOException {
		return new GsURLConnection(url);
	}
}
