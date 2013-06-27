package org.genericsystem.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Factory.DefaultFactory;

@ApplicationScoped
public class CdiFactory extends DefaultFactory {

	private static final long serialVersionUID = 8659134551411453652L;

	@Inject
	private Instance<Cache> cache;

	@Override
	public Cache getCacheLocal() {
		return cache.get();
	}
}
