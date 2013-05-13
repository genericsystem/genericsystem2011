package org.genericsystem.resolver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.inject.spi.BeanManager;
import org.genericsystem.core.Cache;
import org.genericsystem.file.FileSystem;
import org.genericsystem.file.FileSystem.FileType.File;
import org.jboss.solder.beanManager.BeanManagerLocator;
import org.jboss.solder.beanManager.BeanManagerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.faces.facelets.impl.DefaultResourceResolver;

public class GsResolver extends DefaultResourceResolver {

	protected static Logger log = LoggerFactory.getLogger(GsResolver.class);

	private BeanManager beanManager = new BeanManagerLocator().getBeanManager();

	public GsResolver() {
		// Engine engine = BeanManagerUtils.getContextualInstance(beanManager, Engine.class);
		// Cache cache = engine.newCache();
		// FileSystem directoryTree = cache.<FileSystem> find(FileSystem.class);
		//
		// StringBuilder entete = new StringBuilder();
		// entete.append("<html xmlns='http://www.w3.org/1999/xhtml' xmlns:h='http://java.sun.com/jsf/html'>");
		// entete.append("<body>");
		// StringBuilder basPage = new StringBuilder();
		// basPage.append("</body>");
		// basPage.append("</html>");
		// String content1 = entete.toString() + "<h:outputText value='coucou Nicolas' />" + basPage.toString();
		// String content2 = entete.toString() + "<h:outputText value='coucou Michaël' />" + basPage.toString();
		//
		// directoryTree.touchFile(cache, "/pages/test/index2.xhtml", content1.getBytes());
		// directoryTree.touchFile(cache, "/pages/index3.xhtml", content2.getBytes());
		// directoryTree.touchFile(cache, "/pages/index4.xhtml");
		//
		// cache.flush();
	}

	static byte[] asByteArray(final InputStream in) throws IllegalArgumentException {
		// Precondition check
		if (in == null) {
			throw new IllegalArgumentException("stream must be specified");
		}

		// Get content as an array of bytes
		final ByteArrayOutputStream out = new ByteArrayOutputStream(8192);
		final int len = 4096;
		final byte[] buffer = new byte[len];
		int read = 0;
		try {
			while (((read = in.read(buffer)) != -1)) {
				out.write(buffer, 0, read);
			}
		} catch (final IOException ioe) {
			throw new RuntimeException("Error in obtainting bytes from " + in, ioe);
		} finally {
			try {
				in.close();
			} catch (final IOException ignore) {

			}
			// We don't need to close the outstream, it's a byte array out
		}

		// Represent as byte array
		final byte[] content = out.toByteArray();

		// Return
		return content;
	}

	@Override
	public URL resolveUrl(String resource) {
		try {

			Cache cache = BeanManagerUtils.getContextualInstance(beanManager, Cache.class);
			FileSystem fileSystem = cache.<FileSystem> find(FileSystem.class);
			byte[] fileContent = fileSystem.getFileContent(cache, resource);
			if (fileContent != null) {
				log.info("GS : Resolved resource : " + resource);
				return new URL("", "", 0, resource, new GsStreamHandler(fileContent));
			}
			URL url = super.resolveUrl(resource);
			if (url != null) {
				File file = fileSystem.touchFile(cache, resource, asByteArray(((ByteArrayInputStream) url.getContent())));
				file.log(cache);
				log.info("Old resolver : Resolved resource : " + resource);
				log.info("Content : " + new String(file.getContent(cache)));
				return new URL("", "", 0, resource, new GsStreamHandler(file.getContent(cache)));
			}
		} catch (ContextNotActiveException ignore) {

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return super.resolveUrl(resource);
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
				log.info("getInputStream");
				return new ByteArrayInputStream(fileContent);
			}

			@Override
			public String getContentType() {
				log.info("getContentType");
				return "text/html";
			}

			@Override
			public synchronized void connect() throws IOException {
				log.info("connect");
			}

			@Override
			public long getExpiration() {
				log.info("getExpiration");
				return -1l;
			}

			@Override
			public long getLastModified() {
				log.info("getLastModified");
				return -1l;
			}
		}
	}
}
