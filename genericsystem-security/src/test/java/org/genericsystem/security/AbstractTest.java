package org.genericsystem.security;

import org.genericsystem.exception.RollbackException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractTest extends Arquillian {

	protected static Logger log = LoggerFactory.getLogger(AbstractTest.class);

	@Deployment
	public static JavaArchive createDeployment() {
		JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class);
		javaArchive.addPackage("org.genericsystem.security.beans");
		javaArchive.addPackage("org.genericsystem.security.initialisation");
		javaArchive.addPackage("org.genericsystem.security.structure");
		javaArchive.addPackage("org.genericsystem.security.exception");
		javaArchive.addPackage("org.genericsystem.security.hachage");
		javaArchive.addPackage("org.genericsystem.security.manager");
		javaArchive.addPackage("org.genericsystem.security");
		javaArchive.addPackage("org.genericsystem.cdi.event");
		javaArchive.addPackage("org.genericsystem.cdi");
		javaArchive.addPackage("org.genericsystem.core");
		javaArchive.addAsManifestResource(new StringAsset(""), "beans.xml");
		return javaArchive;
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
