package org.genericsystem.test;

import java.util.HashSet;
import java.util.Set;

import org.apache.deltaspike.core.api.message.MessageContext;
import org.genericsystem.cdi.CacheProvider;
import org.genericsystem.cdi.CdiFactory;
import org.genericsystem.cdi.EngineProvider;
import org.genericsystem.cdi.PersistentDirectoryProvider;
import org.genericsystem.cdi.UserClassesProvider;
import org.genericsystem.example.Example.MyVehicle;
import org.genericsystem.example.Example.Vehicle;
import org.genericsystem.jsf.util.FacesContextProvider;
import org.genericsystem.jsf.util.GsMessages;
import org.genericsystem.jsf.util.GsRedirect;
import org.genericsystem.myadmin.beans.GenericBean;
import org.genericsystem.myadmin.beans.GuiGenericsTreeBean;
import org.genericsystem.myadmin.beans.WrapperBean;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTest extends Arquillian {

	static protected Logger log = LoggerFactory.getLogger(AbstractTest.class);

	@Deployment
	public static JavaArchive createTestArchive() {
		return new Archiver(GenericBean.class, CacheProvider.class, GsMessages.class, GsRedirect.class, EngineProvider.class, UserClassesProvider.class, PersistentDirectoryProvider.class, CdiFactory.class, GuiGenericsTreeBean.class, WrapperBean.class,
				MyVehicle.class, Vehicle.class).archive();
	}

	public static class Archiver {
		JavaArchive archive = ShrinkWrap.create(JavaArchive.class);

		public Archiver(Class<?>... classes) {
			BeansXml beanXml = new BeansXml();
			archive.addClasses(classes);
			archive.addPackage("org.apache.deltaspike.core.impl.scope.window");
			archive.addPackage("org.genericsystem.cdi.event");
			archive.addPackage("org.apache.deltaspike.core.impl.message");
			archive.addPackage("org.apache.deltaspike.core.impl.scope.conversation");
			archive.addPackage("org.apache.deltaspike.core.impl.scope.viewaccess");
			archive.addPackage(MessageContext.class.getPackage());
			archive.addPackage(FacesContextProvider.class.getPackage());
			archive.addAsManifestResource(beanXml.byteArraySet(), ArchivePaths.create("beans.xml"));
		}

		public JavaArchive archive() {
			return archive;
		}
	}

	public static class BeansXml {

		private Set<String> interceptors = new HashSet<String>();
		private Set<String> alternativeStereotypes = new HashSet<String>();

		public BeansXml addInterceptors(Class<?>... classes) {
			for (Class<?> clazz : classes)
				interceptors.add(clazz.getName());
			return this;
		}

		public BeansXml addAlternativeSterotypes(Class<?>... classes) {
			for (Class<?> clazz : classes)
				alternativeStereotypes.add(clazz.getName());
			return this;
		}

		@Override
		public String toString() {
			String xml = "<beans>\n";
			for (String interceptor : interceptors)
				xml += "<interceptors><class>" + interceptor + "</class></interceptors>\n";
			xml += "<alternatives>\n";
			if (!alternativeStereotypes.isEmpty())
				for (String alternativeStereotype : alternativeStereotypes)
					xml += "<stereotype>" + alternativeStereotype + "</stereotype>\n";
			xml += "</alternatives>\n";
			return xml += "</beans>\n";
		}

		public ByteArrayAsset byteArraySet() {
			return new ByteArrayAsset(toString().getBytes());
		}
	}

}