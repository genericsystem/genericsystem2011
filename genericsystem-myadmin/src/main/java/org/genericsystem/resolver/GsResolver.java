package org.genericsystem.resolver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import javax.enterprise.inject.spi.BeanManager;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Engine;
import org.genericsystem.file.FileSystem;
import org.jboss.solder.beanManager.BeanManagerLocator;
import org.jboss.solder.beanManager.BeanManagerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.faces.facelets.impl.DefaultResourceResolver;

public class GsResolver extends DefaultResourceResolver {

	protected static Logger log = LoggerFactory.getLogger(GsResolver.class);

	private BeanManager beanManager = new BeanManagerLocator().getBeanManager();

	public GsResolver() {
		Engine engine = BeanManagerUtils.getContextualInstance(beanManager, Engine.class);
		Cache cache = engine.newCache();
		FileSystem directoryTree = cache.<FileSystem> find(FileSystem.class);

		StringBuilder entete = new StringBuilder();
		entete.append("<html xmlns='http://www.w3.org/1999/xhtml' xmlns:h='http://java.sun.com/jsf/html'>");
		entete.append("<body>");
		StringBuilder basPage = new StringBuilder();
		basPage.append("</body>");
		basPage.append("</html>");
		String content1 = entete.toString() + "<h:outputText value='coucou Nicolas' />" + basPage.toString();
		String content2 = entete.toString() + "<h:outputText value='coucou Michaël' />" + basPage.toString();

		directoryTree.touchFile(cache, "/pages/index2.xhtml", content1.getBytes());
		directoryTree.touchFile(cache, "/pages/index3.xhtml", content2.getBytes());
		directoryTree.touchFile(cache, "/pages/index4.xhtml");

		cache.flush();
	}

	@Override
	public URL resolveUrl(String resource) {
		try {
			return new URL("", "", 0, resource, new GsStreamHandler());
		} catch (MalformedURLException e) {
			throw new IllegalStateException();
		}
	}

	public class GsStreamHandler extends URLStreamHandler {

		@Override
		protected URLConnection openConnection(URL url) throws IOException {
			return new GsURLConnection(url.getFile());
		}

		public class GsURLConnection extends URLConnection {

			private String resource;

			public GsURLConnection(String resource) {
				super(null);
				this.resource = resource;
			}

			@Override
			public synchronized InputStream getInputStream() throws IOException {
				log.info("getInputStream");
				Cache cache = BeanManagerUtils.getContextualInstance(beanManager, Cache.class);
				FileSystem directoryTree = cache.<FileSystem> find(FileSystem.class);
				byte[] fileContent = directoryTree.getFileContent(cache, resource);
				if (fileContent == null) {
					Object ctx = FacesContext.getCurrentInstance().getExternalContext().getContext();
					if (ctx instanceof ServletContext) {
						InputStream stream = ((ServletContext) ctx).getResourceAsStream(resource);
						if (stream != null)
							return stream;
					}
					throw new IllegalStateException("Cannot open resource " + resource);
				}
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
