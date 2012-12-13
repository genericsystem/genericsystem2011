package org.genericsystem.cdi;

import javax.enterprise.context.Conversation;
import javax.inject.Inject;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.impl.cdi.CacheProvider;
import org.genericsystem.impl.cdi.EngineProvider;
import org.genericsystem.impl.cdi.StartupBean;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.solder.el.Expressions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTest extends Arquillian {

	protected static Logger log = LoggerFactory.getLogger(AbstractTest.class);

	@Deployment
	public static JavaArchive createDeployment() {
		JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class);
		javaArchive.addClasses(StartupBean.class, CacheProvider.class, EngineProvider.class);
		javaArchive.addPackage(Expressions.class.getPackage());
		javaArchive.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
		return javaArchive;
	}

	@Inject
	protected Engine engine;

	@Inject
	protected Cache cache;

	@Inject
	Conversation conversation;

	@Inject
	Expressions expressions;

	public abstract static class RollbackCatcher {
		public void assertIsCausedBy(Class<? extends Throwable> clazz) {
			try {
				intercept();
			} catch (RollbackException ex) {
				if (ex.getCause() == null)
					throw new IllegalStateException("Rollback Exception has not any cause", ex);
				if (!clazz.isAssignableFrom(ex.getCause().getClass()))
					throw new IllegalStateException("Cause of rollback exception is not of type : " + clazz.getSimpleName(), ex);

				log.info("Caught exception : " + ex.getCause());
				return;
			} catch (Exception ex) {
				if (!clazz.isAssignableFrom(ex.getClass()))
					throw new IllegalStateException("Cause of exception is not of type : " + clazz.getSimpleName(), ex);
				return;
			}
			assert false : "Unable to catch any rollback exception!";
		}

		public abstract void intercept();
	}
}
