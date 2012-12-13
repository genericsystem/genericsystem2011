package org.genericsystem.impl;

import java.lang.reflect.Method;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import org.testng.annotations.Test;

@Test
public class JavassistTest {

	static <T> T newInstance(Class<T> clazz) throws InstantiationException, IllegalAccessException {
		ProxyFactory f = new ProxyFactory();
		f.setSuperclass(clazz);
		f.setFilter(new MethodFilter() {
			@Override
			public boolean isHandled(Method m) {
				return !m.getName().equals("finalize");
			}
		});
		Class<T> proxyClass = f.createClass();
		MethodHandler handler = new MethodHandler() {
			@Override
			public Object invoke(Object self, Method m, Method proceed, Object[] args) throws Throwable {
				System.out.println("Before method : " + m.getName());
				Object o = proceed.invoke(self, args);
				System.out.println("After method : " + m.getName());
				return o;
			}
		};
		T instance = proxyClass.newInstance();
		((ProxyObject) instance).setHandler(handler);
		return instance;
	}

	public static class Entity {
		public void test() {
			System.out.println("Call method");
		}
	}

	public void testJavassist() throws Exception {
		Entity entity = newInstance(Entity.class);
		assert (entity instanceof Entity);
		assert !Entity.class.equals(entity.getClass());
		assert Entity.class.isAssignableFrom(entity.getClass());
		entity.test();
	}
}