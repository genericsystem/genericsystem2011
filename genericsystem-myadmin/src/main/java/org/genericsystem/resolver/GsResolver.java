package org.genericsystem.resolver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import javax.enterprise.inject.spi.BeanManager;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Engine;
import org.genericsystem.file.FileSystem;
import org.genericsystem.file.FileSystem.Directory;
import org.jboss.solder.beanManager.BeanManagerLocator;
import org.jboss.solder.beanManager.BeanManagerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.faces.facelets.impl.DefaultResourceResolver;

public class GsResolver extends DefaultResourceResolver {

	protected static Logger log = LoggerFactory.getLogger(GsResolver.class);

	private BeanManager beanManager = new BeanManagerLocator().getBeanManager();

	public GsResolver() {
		// BoundSessionContext ctx = Container.instance().deploymentManager().instance().select(BoundSessionContext.class).get();
		// Map<String, Object> map = new HashMap<>();
		// ctx.associate(map);
		// ctx.activate();

		Engine engine = BeanManagerUtils.getContextualInstance(beanManager, Engine.class);
		Cache cache = engine.newCache();
		FileSystem directoryTree = cache.<FileSystem> find(FileSystem.class);

		Directory directory = directoryTree.touchRootDirectory(cache, "pages");
		directory.touchFile(cache, "index2.xhtml", "<html><body>coucou Nicolas</body></html>".getBytes());
		directory.touchFile(cache, "index3.xhtml", "<html><body>coucou Michaël</body></html>".getBytes());

		cache.flush();
		// ctx.deactivate();
		// ctx.dissociate(map);
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
				return new ByteArrayInputStream(cache.<FileSystem> find(FileSystem.class).getFileContent(cache, resource));
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
