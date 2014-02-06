package org.genericsystem.impl;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.InstanceGenericClass;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.InstanceValueClassConstraint;
import org.genericsystem.core.Cache;
import org.genericsystem.core.Factory.DefaultFactory;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class MethodDecorationTest extends AbstractTest {

	private static ProxyFactory f = initProxyFactory();

	private static MethodHandler handler = initHandler();

	private static ProxyFactory initProxyFactory() {
		ProxyFactory f = new ProxyFactory();
		f.setFilter(new MethodFilter() {
			@Override
			public boolean isHandled(Method m) {
				return m.getAnnotation(Attribute.class) != null;
			}
		});
		return f;
	}

	private static MethodHandler initHandler() {
		return new MethodHandler() {
			@Override
			public Object invoke(Object self, Method m, Method proceed, Object[] args) throws Throwable {
				if (!void.class.equals(m.getReturnType()))
					return ((GenericImpl) self).getValue(((GenericImpl) self).getCurrentCache().<Holder> find(m.getAnnotation(Attribute.class).value()));
				((GenericImpl) self).setValue(((GenericImpl) self).getCurrentCache().<Holder> find(m.getAnnotation(Attribute.class).value()), (Serializable) args[0]);
				return null;
			}
		};
	}

	static <T> T newProxyInstance(Class<T> clazz) throws InstantiationException, IllegalAccessException {
		f.setSuperclass(clazz);
		@SuppressWarnings("unchecked")
		Class<T> proxyClass = f.createClass();
		T instance = proxyClass.newInstance();
		((ProxyObject) instance).setHandler(handler);
		return instance;
	}

	@SystemGeneric
	@InstanceGenericClass(Car.class)
	public static class Cars extends GenericImpl {}

	@SystemGeneric
	@Components(Cars.class)
	@InstanceValueClassConstraint(Integer.class)
	public static class Power extends GenericImpl {}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	public @interface Attribute {
		Class<?> value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE })
	public @interface Entity {}

	@Entity
	public static abstract class Car extends GenericImpl {
		@Attribute(Power.class)
		public abstract Integer getPower();

		@Attribute(Power.class)
		public abstract void setPower(Integer power);
	}

	public static class JavassistFactory extends DefaultFactory {
		private static final long serialVersionUID = 6550937541771332963L;

		@Override
		public Generic newGeneric(Class<?> clazz) {
			try {
				return clazz != null && clazz.getAnnotation(Entity.class) != null ? (Generic) newProxyInstance(clazz) : super.newGeneric(clazz);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	public void testCurrentFunctionGS() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(new JavassistFactory(), Power.class).start();
		Type cars = cache.find(Cars.class);
		Car myBmw = cars.setInstance("myBmw");
		myBmw.setPower(235);
		assert myBmw.getPower().equals(235);

	}
}
