package org.genericsystem.cdi;

import javax.inject.Inject;

import org.genericsystem.core.Cache;
import org.genericsystem.exception.RollbackException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
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
		javaArchive.addClasses(CacheProvider.class, CacheSerializable.class, EngineProvider.class, UserClassesProvider.class, PersitentDirectoryProvider.class, CdiFactory.class);
		javaArchive.addPackage(Expressions.class.getPackage());
		javaArchive.addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));
		return javaArchive;
	}

	@Inject
	Cache cache;

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
