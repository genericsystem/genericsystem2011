package org.genericsystem.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Nicolas Feybesse
 * 
 */
class Vertex {

	private HomeTreeNode homeTreeNode;

	private GList supers;

	private GList components;

	public Vertex(HomeTreeNode homeTreeNode, GList supers, GList components) {
		this.homeTreeNode = homeTreeNode;
		this.supers = supers;
		this.components = components;
	}

	public HomeTreeNode getHomeTreeNode() {
		return homeTreeNode;
	}

	public List<Generic> getSupers() {
		return supers;
	}

	public List<Generic> getComponents() {
		return components;
	}

	public static class GList extends ArrayList<Generic> implements List<Generic> {

		private static final long serialVersionUID = 6487730580551161546L;

		public GList(Collection<? extends Generic> list) {
			super(list);
		}

		public GList(Generic... array) {
			this(Arrays.asList(array));
		}

		@Override
		public Generic set(int index, Generic element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(int index, Generic element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Generic remove(int index) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(int index, Collection<? extends Generic> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ListIterator<Generic> listIterator() {
			return listIterator(0);
		}

		@Override
		public ListIterator<Generic> listIterator(final int index) {
			return new ListIterator<Generic>() {
				private final ListIterator<? extends Generic> i = GList.super.listIterator(index);

				@Override
				public boolean hasNext() {
					return i.hasNext();
				}

				@Override
				public Generic next() {
					return i.next();
				}

				@Override
				public boolean hasPrevious() {
					return i.hasPrevious();
				}

				@Override
				public Generic previous() {
					return i.previous();
				}

				@Override
				public int nextIndex() {
					return i.nextIndex();
				}

				@Override
				public int previousIndex() {
					return i.previousIndex();
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}

				@Override
				public void set(Generic e) {
					throw new UnsupportedOperationException();
				}

				@Override
				public void add(Generic e) {
					throw new UnsupportedOperationException();
				}
			};
		}

		@Override
		public GList subList(int fromIndex, int toIndex) {
			return new GList(super.subList(fromIndex, toIndex));
		}

		@Override
		public Generic[] toArray() {
			return toArray(new Generic[size()]);
		}
	}

}
