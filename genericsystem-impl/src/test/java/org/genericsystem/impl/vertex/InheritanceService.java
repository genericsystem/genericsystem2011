package org.genericsystem.impl.vertex;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;

interface InheritanceService extends AncestorsService {

	default boolean isSuperOf(Vertex subMeta, Vertex[] overrides, Serializable subValue, Vertex... subComponents) {
		return Arrays.asList(overrides).stream().anyMatch(override -> override.inheritsFrom((Vertex) this)) || inheritsFrom(subMeta, subValue, subComponents, getMeta(), getValue(), getComponents());
	}

	public static boolean inheritsFrom(Vertex subMeta, Serializable subValue, Vertex[] subComponents, Vertex superMeta, Serializable superValue, Vertex[] superComponents) {
		if (!subMeta.inheritsFrom(superMeta))
			return false;
		if (!componentsDepends(subMeta.getSingulars(), subComponents, superComponents))
			return false;
		return subMeta.isProperty() || Objects.equals(subValue, superValue);
	}

	static boolean componentsDepends(boolean[] singulars, Vertex[] subComponents, Vertex[] superComponents) {
		int subIndex = 0;
		loop: for (int superIndex = 0; superIndex < superComponents.length; superIndex++) {
			Vertex superComponent = superComponents[superIndex];
			for (; subIndex < subComponents.length; subIndex++) {
				Vertex subComponent = subComponents[subIndex];
				if (subComponent.inheritsFrom(superComponent) || subComponent.isInstanceOf(superComponent)) {
					if (singulars[subIndex])
						return true;
					subIndex++;
					continue loop;
				}
			}
			return false;
		}
		return true;
	}

	default Vertex[] getSupers(Vertex[] overrides) {
		class SupersComputer extends LinkedHashSet<Vertex> {

			private static final long serialVersionUID = -1078004898524170057L;

			private final Vertex[] overrides;
			private final Map<Vertex, Boolean> alreadyComputed = new HashMap<>();

			private SupersComputer(Vertex[] overrides) {
				this.overrides = overrides;
				isSelected(getMeta().getEngine());
			}

			private boolean isSelected(Vertex candidate) {
				Boolean result = alreadyComputed.get(candidate);
				if (result != null)
					return result;
				if ((!isEngine() && candidate.equals(getMeta(), getValue(), getComponents()))) {
					alreadyComputed.put(candidate, false);
					return false;
				}
				boolean isMeta = getMeta().inheritsFrom(candidate) || candidate.isEngine();
				boolean isSuper = candidate.isSuperOf(getMeta(), overrides, getValue(), getComponents());
				if (!isMeta && !isSuper) {
					alreadyComputed.put(candidate, false);
					return false;
				}
				boolean selectable = true;
				for (Vertex inheriting : candidate.getInheritings())
					if (isSelected(inheriting))
						selectable = false;
				if (isMeta) {
					selectable = false;
					for (Vertex instance : candidate.getInstances())
						isSelected(instance);
				}
				result = alreadyComputed.put(candidate, selectable);
				assert result == null;
				if (selectable)
					add(candidate);
				return selectable;
			}

			@Override
			public Vertex[] toArray() {
				return toArray(new Vertex[size()]);
			}

			@Override
			public boolean add(Vertex candidate) {
				for (Vertex vertex : this) {
					if (vertex.equals(candidate)) {
						assert false : "Candidate already exists : " + candidate.info();
					} else if (vertex.inheritsFrom(candidate)) {
						assert false : vertex.info() + candidate.info();
					}
				}
				Iterator<Vertex> it = iterator();
				while (it.hasNext())
					if (candidate.inheritsFrom(it.next())) {
						assert false;
						it.remove();
					}
				boolean result = super.add(candidate);
				assert result;
				return true;
			}
		}
		return new SupersComputer(overrides).toArray();
	}
}
