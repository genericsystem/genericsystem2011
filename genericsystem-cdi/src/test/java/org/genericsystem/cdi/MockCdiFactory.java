package org.genericsystem.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Specializes;
import javax.enterprise.inject.spi.BeanManager;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Engine;
import org.jboss.arquillian.testenricher.cdi.container.CDIExtension;
import org.jboss.solder.core.Veto;

@Specializes
@Alternative
@ApplicationScoped
public class MockCdiFactory extends CdiFactory {

	private static final long serialVersionUID = 6131943314303158368L;

	@Override
	protected Class<? extends Cache> getCacheClass() throws ClassNotFoundException {
		return MockSerializableCache.class;
	}

	@Veto
	public static class MockSerializableCache extends SerializableCache {

		public MockSerializableCache() {
			super((Cache) null);
		}

		public MockSerializableCache(Cache cache) {
			super(cache);
		}

		public MockSerializableCache(Engine engine) {
			super(engine);
		}

		@Override
		protected BeanManager getBeanManager() {
			return CDIExtension.getBeanManager();
		}
	}

}
