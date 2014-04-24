package org.genericsystem.impl.vertex.services;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import org.genericsystem.impl.vertex.Snapshot;
import org.genericsystem.impl.vertex.Vertex;
import org.genericsystem.iterator.AbstractConcateIterator;
import org.genericsystem.iterator.SingletonIterator;

public interface ComponentsInheritanceService {

	default Snapshot<Vertex> getAttributes(Vertex attribute) {
		return getInheritings(attribute, 1);
	}

	default Snapshot<Vertex> getHolders(Vertex attribute) {
		return getInheritings(attribute, 2);
	}

	default Snapshot<Serializable> getValues(Vertex attribute) {
		return getInheritings(attribute, 2).project(holder -> holder.getValue());
	}

	default Snapshot<Vertex> getInheritings(final Vertex origin, final int level) {
		return new Snapshot<Vertex>() {
			@Override
			public Iterator<Vertex> iterator() {
				return inheritingsIterator(origin, level);
			}
		};
	}

	default Iterator<Vertex> inheritingsIterator(final Vertex origin, final int level) {
		class Inheritings {

			private final Vertex base;
			private final Vertex origin;
			private final int level;

			private Inheritings(Vertex base, Vertex origin, int level) {
				this.base = base;
				this.origin = origin;
				this.level = level;
			}

			private Iterator<Vertex> inheritanceIterator() {
				return projectIterator(fromAboveIterator());
			};

			private Iterator<Vertex> supersIterator() {
				return base.getSupersStream().filter(next -> base.getMeta().equals(next.getMeta()) && origin.isAttributeOf(next)).iterator();
			}

			private Iterator<Vertex> fromAboveIterator() {
				if (!origin.isAttributeOf(base))
					return Collections.emptyIterator();
				Iterator<Vertex> supersIterator = supersIterator();
				if (!supersIterator.hasNext())
					return (base.isEngine() || !origin.isAttributeOf(base.getMeta())) ? new SingletonIterator<Vertex>(origin) : new Inheritings(base.getMeta(), origin, level).inheritanceIterator();

					return new AbstractConcateIterator<Vertex, Vertex>(supersIterator) {
						@Override
						protected Iterator<Vertex> getIterator(final Vertex superVertex) {
							return new Inheritings(superVertex, origin, level).inheritanceIterator();
						}
					};
			}

			private Iterator<Vertex> projectIterator(Iterator<Vertex> iteratorToProject) {
				return new AbstractConcateIterator<Vertex, Vertex>(iteratorToProject) {
					@Override
					protected Iterator<Vertex> getIterator(final Vertex holder) {
						Iterator<Vertex> indexIterator = holder.getLevel() < level ? new ConcateIterator<>(base.getMetaComposites(holder).iterator(), base.getSuperComposites(holder).iterator()) : base.getSuperComposites(holder).iterator();
						if (!((Vertex) ComponentsInheritanceService.this).equals(base)) {
							return indexIterator.hasNext() ? new ConcateIterator<Vertex>(new SingletonIterator<Vertex>(holder), projectIterator(indexIterator)) : new SingletonIterator<Vertex>(holder);
						}
						return indexIterator.hasNext() ? projectIterator(indexIterator) : holder.getLevel() == level ? new SingletonIterator<Vertex>(holder) : Collections.emptyIterator();
					}
				};
			}
		}
		return new Inheritings((Vertex) ComponentsInheritanceService.this, origin, level).inheritanceIterator();
	}
}
