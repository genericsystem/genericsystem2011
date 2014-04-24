package org.genericsystem.security;

import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import org.genericsystem.cdi.CacheProvider;
import org.genericsystem.cdi.CdiFactory;
import org.genericsystem.cdi.EngineProvider;
import org.genericsystem.cdi.GenericProvider;
import org.genericsystem.cdi.PersistentDirectoryProvider;
import org.genericsystem.cdi.SerializableCache;
import org.genericsystem.cdi.UserClassesProvider;
import org.genericsystem.cdi.event.EventLauncher;
import org.genericsystem.core.Cache;
import org.genericsystem.exception.RollbackException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testenricher.cdi.container.CDIExtension;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractTest extends Arquillian {

	protected static Logger log = LoggerFactory.getLogger(AbstractTest.class);

	@Inject
	Cache cache;

	@Deployment
	public static JavaArchive createDeployment() {
		JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class);
		javaArchive.addClasses(CacheProvider.class, SerializableCache.class, MockCdiFactory.class, GenericProvider.class, EngineProvider.class, UserClassesProvider.class, PersistentDirectoryProvider.class, CdiFactory.class, EventLauncher.class);
		javaArchive.addPackage("org.genericsystem.security.beans");
		javaArchive.addPackage("org.genericsystem.security.initialisation");
		javaArchive.addPackage("org.genericsystem.security.structure");
		javaArchive.addPackage("org.genericsystem.security.exception");
		javaArchive.addPackage("org.genericsystem.security.hachage");
		javaArchive.addPackage("org.genericsystem.security.manager");
		javaArchive.addAsServiceProvider(Extension.class, CDIExtension.class);
		createBeansXml(javaArchive);
		return javaArchive;
	}

	private static void createBeansXml(JavaArchive javaArchive) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<beans xmlns=\"http://java.sun.com/xml/ns/javaee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\" http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/beans_1_0.xsd\">");
		stringBuilder.append("<alternatives> ");
		stringBuilder.append("<class>org.genericsystem.security.MockCdiFactory</class>");
		stringBuilder.append(" </alternatives>");
		stringBuilder.append("</beans>");
		javaArchive.addAsManifestResource(new StringAsset(stringBuilder.toString()), "beans.xml");
	}

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
