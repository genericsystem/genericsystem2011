package org.genericsystem.jsf.resolver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import javax.enterprise.context.ContextNotActiveException;
import javax.faces.view.facelets.ResourceResolver;

import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.genericsystem.core.Cache;
import org.genericsystem.file.FileSystem;

public class GsResolver extends ResourceResolver {

	static byte[] asByteArray(final InputStream in) throws IllegalArgumentException {
		if (in == null)
			throw new IllegalArgumentException("stream must be specified");

		final ByteArrayOutputStream out = new ByteArrayOutputStream(8192);
		final int len = 4096;
		final byte[] buffer = new byte[len];
		int read = 0;
		try {
			while (((read = in.read(buffer)) != -1))
				out.write(buffer, 0, read);
		} catch (final IOException ioe) {
			throw new RuntimeException("Error in obtainting bytes from " + in, ioe);
		} finally {
			try {
				in.close();
			} catch (final IOException ignore) {
			}
		}
		return out.toByteArray();
	}

	@Override
	public URL resolveUrl(String resource) {
		try {
			Cache cache = BeanProvider.getContextualReference(Cache.class);
			FileSystem fileSystem = cache.<FileSystem> find(FileSystem.class);
			byte[] fileContent = fileSystem.getFileContent(resource);
			if (fileContent != null)
				return new URL("", "", 0, resource, new GsStreamHandler(fileContent));
			// URL url = super.resolveUrl(resource);
			// if (url != null) {
			// File file = fileSystem.setFile(resource, asByteArray(((ByteArrayInputStream) url.getContent())));
			// return new URL("", "", 0, resource, new GsStreamHandler(file.getContent()));
			// }
		} catch (ContextNotActiveException ignore) {
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return null;// super.resolveUrl(resource);
	}

	public class GsStreamHandler extends URLStreamHandler {

		byte[] fileContent;

		public GsStreamHandler(byte[] fileContent) {
			this.fileContent = fileContent;
		}

		@Override
		protected URLConnection openConnection(URL url) throws IOException {
			return new GsURLConnection(url.getFile());
		}

		public class GsURLConnection extends URLConnection {

			public GsURLConnection(String resource) {
				super(null);
			}

			@Override
			public synchronized InputStream getInputStream() throws IOException {
				return new ByteArrayInputStream(fileContent);
			}

			@Override
			public String getContentType() {
				return "text/html";
			}

			@Override
			public synchronized void connect() throws IOException {
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
	}
}
