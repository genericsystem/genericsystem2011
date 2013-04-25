package org.genericsystem.cdi;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserClasses {

	private Set<Class<?>> userClasses = new HashSet<>();

	public void addUserClasse(Class<?> userClasse) {
		userClasses.add(userClasse);
	}

	public Class<?>[] getUserClassesArray() {
		return userClasses.toArray(new Class[userClasses.size()]);
	}
}
