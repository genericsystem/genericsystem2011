package org.genericsystem.cdi;

import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import org.genericsystem.core.Cache;
import org.genericsystem.exception.RollbackException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testenricher.cdi.container.CDIExtension;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.solder.el.Expressions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTest extends Arquillian {

	protected static Logger log = LoggerFactory.getLogger(AbstractTest.class);

	@Deployment
	public static JavaArchive createDeployment() {
		JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class);
		javaArchive.addClasses(CacheProvider.class, SerializableCache.class, MockCdiFactory.class, EngineProvider.class, UserClassesProvider.class, PersitentDirectoryProvider.class, CdiFactory.class);
		javaArchive.addPackage(Expressions.class.getPackage());
		createBeansXml(javaArchive);
		// createArquillianXml(javaArchive);
		javaArchive.addAsServiceProvider(Extension.class, CDIExtension.class);
		return javaArchive;
	}

	private static void createBeansXml(JavaArchive javaArchive) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<beans xmlns=\"http://java.sun.com/xml/ns/javaee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\" http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/beans_1_0.xsd\">");
		stringBuilder.append("<alternatives> ");
		stringBuilder.append("<class>org.genericsystem.cdi.MockCdiFactory</class>");
		stringBuilder.append(" </alternatives>");
		stringBuilder.append("</beans>");
		javaArchive.addAsManifestResource(new StringAsset(stringBuilder.toString()), "beans.xml");
	}

	private static void createArquillianXml(JavaArchive javaArchive) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
		stringBuilder.append("<arquillian xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://jboss.org/schema/arquillian http://jboss.org/schema/arquillian/arquillian_1_0.xsd\">");
		stringBuilder.append("<container qualifier=\"weld\" default=\"true\">");
		stringBuilder.append("<configuration>");
		stringBuilder.append("<property name=\"enableConversationScope\">true</property>");
		stringBuilder.append("</configuration>");
		stringBuilder.append("</container>");
		stringBuilder.append("</arquillian>");
		javaArchive.addAsManifestResource(new StringAsset(stringBuilder.toString()), "arquillian.xml");
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
