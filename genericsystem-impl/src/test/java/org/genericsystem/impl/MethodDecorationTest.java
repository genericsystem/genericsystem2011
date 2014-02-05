package org.genericsystem.impl;

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
import org.genericsystem.generic.Attribute;
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
				return !m.getName().equals("power");
			}
		});
		return f;
	}

	private static MethodHandler initHandler() {
		return new MethodHandler() {
			@Override
			public Object invoke(Object self, Method m, Method proceed, Object[] args) throws Throwable {
				// log.info("Before method : " + m.getName());
				Object o = proceed.invoke(self, args);
				// log.info("After method : " + m.getName());
				// return ((GenericImpl) self).getValue(((GenericImpl) self).getCurrentCache().<Holder> find(m.getAnnotation(Attribute.class).value()));
				return o;
			}
		};
	}

	static <T> T newProxyInstance(Class<T> clazz) throws InstantiationException, IllegalAccessException {
		f.setSuperclass(clazz);
		Class<T> proxyClass = f.createClass();
		T instance = proxyClass.newInstance();
		((ProxyObject) instance).setHandler(handler);
		return instance;
	}

	@SystemGeneric
	@InstanceGenericClass(Car.class)
	public static class Cars extends GenericImpl {
	}

	@SystemGeneric
	@Components(Cars.class)
	@InstanceValueClassConstraint(Integer.class)
	public static class Power extends GenericImpl {
	}

	// @Retention(RetentionPolicy.RUNTIME)
	// @Target({ ElementType.METHOD })
	// public @interface Attribute {
	// Class<?> value();
	// }

	// --------------------------------------------------------------------------------------
	// public static abstract class Car extends GenericImpl {
	//
	// @Attribute(Power.class)
	// public abstract Integer getPower();

	// {
	// return getValue(getCurrentCache().<Attribute> find(Power.class));
	// }

	// }
	// --------------------------------------------------------------------------------------
	public static class Car extends GenericImpl {

		public Integer getPower() {
			return getValue(getCurrentCache().<Attribute> find(Power.class));
		}

		public void setPower(Integer power) {

		}

	}

	// --------------------------------------------------------------------------------------

	public void testCurrentFunctionGS() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(new DefaultFactory() {

			@Override
			public Generic newGeneric(Class<?> clazz) {
				try {
					if (clazz != null && clazz.equals(Car.class)) {
						return (Generic) newProxyInstance(clazz);
					} else
						return (Generic) (clazz != null && genericClass.isAssignableFrom(clazz) ? clazz.newInstance() : genericClass.newInstance());
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
					throw new IllegalStateException(e);
				}
			}

		}, Power.class).start();

		Type cars = cache.find(Cars.class);
		Car myBmw = cars.setInstance("myBmw");
		log.info("myBmw.getPower(): " + myBmw.getPower());

	}
}
