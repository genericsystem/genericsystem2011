package org.genericsystem.impl.vertex.services;

import org.genericsystem.impl.vertex.Statics;

public interface SystemPropertiesService {

	public static interface Constraint {

	}

	public static class SingularConstraint implements Constraint {

	}

	public static class PropertyConstraint implements Constraint {

	}

	default boolean isSingularConstraint(int pos) {
		return isEnabled(SingularConstraint.class, pos);
	};

	default boolean isPropertyConstraint() {
		return isEnabled(PropertyConstraint.class);
	};

	default boolean isEnabled(Class<? extends Constraint> clazz) {
		return isEnabled(clazz, Statics.NO_POSITION);
	}

	default boolean isEnabled(Class<?> clazz, int pos) {
		return false;
	}
}
