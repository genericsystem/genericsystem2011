package org.genericsystem.resolver;

import java.net.MalformedURLException;
import java.net.URL;
import com.sun.faces.facelets.impl.DefaultResourceResolver;

public class GsResolver extends DefaultResourceResolver {

	URL resourceUrl = null;

	@Override
	public URL resolveUrl(String resource) {
		resourceUrl = super.resolveUrl(resource);
		if (resourceUrl == null) {
			if (resource.startsWith("/")) {
				resource = resource.substring(1);
				try {
					resourceUrl = new URL(null, resource, new GsStreamHandler());
				} catch (MalformedURLException e) {
					throw new IllegalStateException();
				}
			}
		}
		return resourceUrl;
	}
}
