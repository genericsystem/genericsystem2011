package org.genericsystem.impl.vertex;

public interface SystemPropertiesService {

	public static interface Constraint {

	}

	public static class SingularConstraint implements Constraint {

	}

	public static class PropertyConstraint implements Constraint {

	}

	default boolean isSingularConstraint(int pos) {
		return isEnabled(SingularConstraint.class);
	};

	default boolean isPropertyConstraint(int pos) {
		return isEnabled(PropertyConstraint.class, pos);
	};

	default boolean isEnabled(Class<? extends Constraint> clazz) {
		return isEnabled(clazz, Statics.NO_POSITION);
	}

	default boolean isEnabled(Class<?> clazz, int pos) {
		return false;
	}
}
